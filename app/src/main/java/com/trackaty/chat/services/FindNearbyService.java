package com.trackaty.chat.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavDeepLinkBuilder;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.trackaty.chat.App;
import com.trackaty.chat.BuildConfig;
import com.trackaty.chat.Interface.FirebaseRelationCallback;
import com.trackaty.chat.R;
import com.trackaty.chat.activities.MainActivity;
import com.trackaty.chat.models.Relation;
import com.trackaty.chat.models.User;
import com.trackaty.chat.receivers.MicMuteChangedReceiver;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import io.chirp.chirpsdk.ChirpSDK;
import io.chirp.chirpsdk.models.ChirpError;
import io.chirp.chirpsdk.interfaces.ChirpEventListener;

import static com.trackaty.chat.App.FIND_NEARBY_CHANNEL_ID;
import static com.trackaty.chat.Utils.PendingIntentFlags.pendingIntentUpdateCurrentFlag;

public class FindNearbyService extends Service {

    private final static String TAG = FindNearbyService.class.getSimpleName();

    private NotificationManagerCompat notificationManager;

    private FirebaseUser mFirebaseCurrentUser;
    private String mCurrentUserId; //get current to get uid
    private int receivedSoundID;
    private App mApplication;
    // Binder given to clients
    //private final IBinder binder = new LocalBinder();
    // Random number generator
    //private final Random mGenerator = new Random();


    private final static int PENDING_INTENT_REQUEST_CODE = 45; // For the notification
    private final static int SEARCHING_PERIOD = 10*60*1000; //1*60*1000;

    private static final int LIKES_NOTIFICATION_ID = 1;
    private static final int PICK_UPS_NOTIFICATION_ID = 2;
    private static final int MESSAGES_NOTIFICATION_ID = 3;
    private static final int REQUESTS_SENT_NOTIFICATION_ID = 4;
    private static final int REQUESTS_APPROVED_NOTIFICATION_ID = 5;
    private static final int FIND_NOTIFICATION_ID = 6;
    private static final int VISIBILITY_NOTIFICATION_ID = 7;

    private static final String SHOW_MUTED_MIC = "showMutedMic"; // to show muted mic alert fragment
    private static final String ALERT_MESSAGE_EXTRA_KEY = "AudioChangeKey";

    private Notification mNotification;
    private ChirpSDK chirp;
    private CountDownTimer mSearchingTimer; // A timer to stop service when finished
    private long mTimeLiftInMillis;// Remaining time till the search ends

    private DatabaseReference mDatabaseRef, mUsersRef, mSearchRef, mRelationsRef;

    private static final String RELATION_STATUS_BLOCKING = "blocking"; // the selected user is blocking me (current user)
    private static final String RELATION_STATUS_BLOCKED= "blocked"; // the selected user is blocked by me (current user)


    //Get chirp secret keys from the keystore
    String CHIRP_APP_KEY = BuildConfig.CHIRP_APP_KEY;
    String CHIRP_APP_SECRET = BuildConfig.CHIRP_APP_SECRET;
    String CHIRP_APP_CONFIG = BuildConfig.CHIRP_APP_CONFIG;

    // To listen to mic mute changes
    private IntentFilter mMuteChangedIntentFilter;
    private MicMuteChangedReceiver mMicMuteChangedReceiver;
    private AudioManager mAudioManager;

    private int checkMicMuteWindow = 0; // to listen for mic mute on api < 28 P

    // A listener for sound id
    ChirpEventListener chirpEventListener = new ChirpEventListener() {
        @Override
        public void onSent(byte[] data, int channel) {
            Log.v("ChirpSDK", "Sent data");
        }

        @Override
        public void onSending(@NotNull byte[] bytes, int i) {

        }

        // After we received a sound Id
        @Override
        public void onReceived(@Nullable byte[] data, int channel) {
            if (data != null && data.length > 0) {
                //String identifier = new String(bytes);
                receivedSoundID = bytesToInt(data); // get soundId from received bytes
                // get user from it's sound id, if there is no blocking relation, update search results
                getUser(receivedSoundID);
                Log.d(TAG , "onReceived= " + receivedSoundID + " bytes length ="+data.length + " channel= "+ channel);
            } else {
                Log.e(TAG, "onReceived= ChirpError: Decode failed");
            }
        }

        @Override
        public void onReceiving(int channel) {
            Log.v("ChirpSDK", "Receiving data...");
        }

        @Override
        public void onStateChanged(int oldState, int newState) {
            Log.v("onStateChanged", " oldState= "+oldState + " newState="+ newState);
        }

        @Override
        public void onSystemVolumeChanged(float oldVolume, float newVolume) {
            Log.v("ChirpSDK", "Volume changed from: " + oldVolume +" to: " + newVolume);
        }
    };

    public FindNearbyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return null;
        //return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "FindNearbyService onCreate");

        if (CHIRP_APP_KEY.equals("") || CHIRP_APP_SECRET.equals("")) {
            Log.e(TAG, "APP_KEY or APP_SECRET is not set. " +
                    "Please update with your APP_KEY/APP_SECRET from admin.chirp.io");
            return;
        }

        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mApplication = ((App)this.getApplicationContext());
        // To listen to mic mute changes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mMuteChangedIntentFilter = new IntentFilter(AudioManager.ACTION_MICROPHONE_MUTE_CHANGED);
            mMicMuteChangedReceiver = new MicMuteChangedReceiver(){
                @Override
                public void onReceive(Context context, Intent intent) {
                    super.onReceive(context, intent);
                    Log.d(TAG, "AudioManager microphone mute onReceive");

                    if (AudioManager.ACTION_MICROPHONE_MUTE_CHANGED.equals(intent.getAction())) {
                        Log.d(TAG, "AudioManager microphone mute changed");
                        // To check if mic is muted or not
                        if(mAudioManager != null && mAudioManager.isMicrophoneMute()) {
                            sendAlertMessage();
                            // Stop timer and service
                            notifyUi();
                            stopSelf();
                        }
                    }
                }
            };

            // register the receiver listen for mic mute changes
            registerReceiver(mMicMuteChangedReceiver,mMuteChangedIntentFilter);
        }
        // Create new chirp instance
        chirp = new ChirpSDK(this, CHIRP_APP_KEY, CHIRP_APP_SECRET);

        //Get current logged in user
        mFirebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mCurrentUserId = mFirebaseCurrentUser != null ? mFirebaseCurrentUser.getUid() : null;

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mUsersRef = mDatabaseRef.child("users");
        mSearchRef = mDatabaseRef.child("search");
        mRelationsRef = mDatabaseRef.child("relations");

        notificationManager = NotificationManagerCompat.from(this);
        mTimeLiftInMillis = SEARCHING_PERIOD;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //userID = intent.getStringExtra("userID");
        Log.d(TAG, "onStartCommand");

        if (chirp != null) {
            Log.d(TAG, "chirp Connect Version: " + chirp.getVersion());
            //return super.onStartCommand(intent, flags, startId);
            ChirpError setConfigError = chirp.setConfig(CHIRP_APP_CONFIG);
            if (setConfigError.getCode() > 0) {
                Log.e(TAG, setConfigError.getMessage());
            }else{
                // If configuration succeeded, let's start the Sdk and attach the listener
                startSdk();
            }
        }

        Intent NotificationClickIntent = new Intent(this, MainActivity.class);
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, PENDING_INTENT_REQUEST_CODE, NotificationClickIntent, pendingIntentUpdateCurrentFlag());
        PendingIntent pendingIntent = new NavDeepLinkBuilder(this)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.mainFragment)
                .createPendingIntent();

        mNotification = new NotificationCompat.Builder(this, FIND_NEARBY_CHANNEL_ID)
                .setContentTitle(getString(R.string.notification_nearby_title))
                .setContentText(getString(R.string.notification_nearby_body))
                .setSmallIcon(R.mipmap.ic_notification)
                .setColor(getResources().getColor(R.color.color_primary))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_SOCIAL)
                .setContentIntent(pendingIntent)
                .build();


        startForeground(FIND_NOTIFICATION_ID, mNotification);

        return START_REDELIVER_INTENT;
    }


    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        CancelTimer();
        try {
            chirp.stop();
            chirp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // unregister Mic Mute Changed Receiver
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            unregisterReceiver(mMicMuteChangedReceiver);
        }
    }

    public void stopSdk() {
        if (chirp == null){
            return;
        }
        ChirpError error = chirp.stop();
        if (error.getCode() > 0) {
            Log.e(TAG, error.getMessage());
        }
    }

    public void startSdk() {
        if (chirp == null) {
            return;
        }
        ChirpError error = chirp.start(false, true);
        if (error.getCode() > 0) {
            Log.e("ChirpError: ", error.getMessage());
        }else{
            Log.d(TAG, "chirp started. lets start timer");
            chirp.setListener(chirpEventListener);
            StartTimer();
        }
    }

    private void StartTimer() {

        // check if it's a negative number
        if(mTimeLiftInMillis < 0){
            return;
        }
        // Cancel any previous timer
        if (mSearchingTimer != null) {
            CancelTimer();
        }

        mSearchingTimer = new CountDownTimer( mTimeLiftInMillis,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //Log.d(TAG, "onTick.  millisUntilFinished= "+ millisUntilFinished);
                //mTimeLiftInMillis = millisUntilFinished;
                // Only user this method if api is less than 28. We already listen to ACTION_MICROPHONE_MUTE_CHANGED in API >= 28
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                    checkMicMuteWindow ++; // increase the check window to check every 10 seconds
                    if(checkMicMuteWindow >= 10){
                        // To check if mic is muted or not
                        //Log.d(TAG, "10 seconds passed, lets check if mic is muted or not. checkMicMuteWindow= "+checkMicMuteWindow);
                        if(mAudioManager != null && mAudioManager.isMicrophoneMute()) {
                            sendAlertMessage();
                            // Stop timer and service
                            notifyUi();
                            stopSelf();
                        }

                        checkMicMuteWindow = 0; // reset the counter to start checking again when it reaches 10 seconds
                    }
                }
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "mRemainingTimer onFinish.");
                CancelTimer();
                notifyUi();
                stopSelf();
            }
        }.start();
    }
    // Cancel active countdown timer
    private void CancelTimer() {
        if(mSearchingTimer != null){
            // cancel timer
            mSearchingTimer.cancel();
            Log.d(TAG, "mSearchingTimer canceled");
        }
    }

    /** method for clients */
    /*public long getTimeLift() {
        return mTimeLiftInMillis;
    }*/


    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    /*public class LocalBinder extends Binder {
        public FindNearbyService getService() {
            // Return this instance of LocalService so clients can call public methods
            return FindNearbyService.this;
        }
    }*/

    public int bytesToInt (byte[] bytes) {

        /*ByteBuffer wrapped = ByteBuffer.wrap(bytes); // big-endian by default
        return wrapped.getInt(); // 1*/

        //return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);

        /*return ((bytes[0] & 0xFF) << 24) |
                ((bytes[1] & 0xFF) << 16) |
                ((bytes[2] & 0xFF) << 8 ) |
                ((bytes[3] & 0xFF) << 0 );*/

        return ByteBuffer.wrap(bytes).getInt();
    }

    private void notifyUi() {
        Log.d(TAG, "Broadcasting message");
        Intent intent = new Intent("com.basbes.dating.serviceStopped");
        // You can also include some extra data.
        intent.putExtra("isServiceStopped", true);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendAlertMessage() {
        if(mApplication.isInForeground){
            // App is in foreground, show dialog instead of toast
            Intent intent = new Intent("com.basbes.dating.audioChanged");
            intent.putExtra(ALERT_MESSAGE_EXTRA_KEY, SHOW_MUTED_MIC);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }else{
            Toast.makeText(this, R.string.mic_muted_toast, Toast.LENGTH_LONG).show();
        }
    }

    private void getUser(int soundID) {
        Query query = mUsersRef.orderByChild("soundId").equalTo(soundID).limitToFirst(1);
        query.keepSynced(true); // without it, the data of found user doesn't update at the fist time.
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User exist, loop throw users value
                    List<User> usersList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            Log.d(TAG, "User exist. user key= "+snapshot.getKey() + " name= "+ user.getName() + " bio="+ user.getBiography());
                            if(!TextUtils.equals(mCurrentUserId, snapshot.getKey())){
                                // It's not current logged on user, add it to the results list
                                user.setKey(snapshot.getKey());
                                user.setCreated(ServerValue.TIMESTAMP);
                                //user.setCreated(System.currentTimeMillis());
                                usersList.add(user);
                            }

                        }
                    }// end of for loop
                    // return if there are no results
                    if(usersList.size()<= 0){
                        return;
                    }

                    // Check if there is a blocking relation with this user or not
                    isUserBlocked(usersList.get(0).getKey(), new FirebaseRelationCallback() {
                        @Override
                        public void onCallback(Relation relation) {
                            if (relation == null){
                                Log.d(TAG , "isUserBlocked Callback is null. Update user search");
                                DatabaseReference mCurrentSearchRef = mSearchRef.child(mCurrentUserId).child(usersList.get(0).getKey());
                                mCurrentSearchRef.setValue(usersList.get(0)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "updated search ref successfully");
                                        }else{
                                            Log.w(TAG, "error during updating search ref");
                                        }

                                    }
                                });
                            }
                        }
                    });

                }else{
                    // User doesn't exist
                    Log.w(TAG, "getUserOnce User is null, no such user");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "getUserOnce. User onCancelled" +databaseError);
            }
        });

    }

    private void isUserBlocked(String userID, final FirebaseRelationCallback callback) {
        if(mCurrentUserId == null){
            callback.onCallback(null);
           return;
        }
        Log.d(TAG, "isUserBlocked. userID= "+userID+ " mCurrentUserId= "+mCurrentUserId);
        DatabaseReference currentRelationRef = mRelationsRef.child(mCurrentUserId).child(userID);
        currentRelationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Relation relation = dataSnapshot.getValue(Relation.class);
                    if (relation != null) {
                        if(TextUtils.equals(relation.getStatus(), RELATION_STATUS_BLOCKING)
                        || (TextUtils.equals(relation.getStatus(), RELATION_STATUS_BLOCKED))){
                            // User is blocking me r blocked by me
                            Log.d(TAG, "User is blocking me or blocked by me= "+dataSnapshot.getKey());
                            callback.onCallback(relation);
                        }else{
                            Log.d(TAG, "There is a relation but not blocking: "+dataSnapshot.getKey());
                            callback.onCallback(null);
                        }

                    }

                } else {
                    Log.d(TAG, "there is no relation");
                    callback.onCallback(null);
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "getUserOnce. User onCancelled" +databaseError);
            }
        });

    }

}


