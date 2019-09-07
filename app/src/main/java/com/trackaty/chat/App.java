package com.trackaty.chat;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDexApplication;
import androidx.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode;
//import androidx.multidex.MultiDexApplication;


/**
 * Created on 25/03/2017.
 */

public class App extends MultiDexApplication {

    private final static String TAG = App.class.getSimpleName();
    private static Context sApplicationContext;

    // Since I can connect from multiple devices, we store each connection instance separately
    // any time that connectionsRef's value is null (i.e. has no children) I am offline
    //initialize the FirebaseAuth instance
    private FirebaseAuth  mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private static FirebaseUser currentUser;
    private static String currentUserId;

    private FirebaseDatabase database ;
    private DatabaseReference myConnectionsRef;
    private DatabaseReference mUserRef;

    // Stores the timestamp of my last disconnect (the last time I was seen online)
    private DatabaseReference lastOnlineRef;

    private DatabaseReference connectedRef;//  = database.getReference(".info/connected");
    //private DatabaseReference connection;

    /*private static final String PICK_UPS_CHANNEL_NAME = "Pick-up lines";
    private static final String MESSAGES_CHANNEL_NAME = "Messages";
    private static final String LIKES_CHANNEL_NAME = "Likes";
    private static final String REQUESTS_CHANNEL_NAME = "Reveal requests";*/

    public static final String PICK_UPS_CHANNEL_ID = "Pickup_id";
    public static final String MESSAGES_CHANNEL_ID = "Messages_id";
    public static final String LIKES_CHANNEL_ID = "Likes_id";
    public static final String REQUESTS_CHANNEL_ID = "Reveal_id";

    private static final String PREFERENCE_KEY_NIGHT = "night" ;
    private static final String PREFERENCE_KEY_RINGTONE = "ringtone";
    private static final String PREFERENCE_KEY_VERSION = "version";

    private static final String NIGHT_VALUE_LIGHT = "light";
    private static final String NIGHT_VALUE_DARK = "dark";
    private static final String NIGHT_VALUE_BATTERY = "battery";
    private static final String NIGHT_VALUE_SYSTEM = "system";

    private SharedPreferences sharedPreferences;

   /* private ValueEventListener onlineListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            Log.i(TAG, "onDataChange");
            if(snapshot.exists()){
                Log.i(TAG, "snapshot.exists()");
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    Log.i(TAG, "connected");

                    DatabaseReference connection = myConnectionsRef.push();

                    // Add this device to my connections list
                    // this value could contain info about the device or a timestamp too
                    connection.setValue(Boolean.TRUE);
                    lastOnlineRef.setValue(0);

                    // When this device disconnects, remove it
                    connection.onDisconnect().removeValue();

                    // When I disconnect, update the last time I was seen online
                    lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP );
                }else{
                    Log.i(TAG, "not connected");
                }
            }else{
                Log.i(TAG, "snapshot don't exist");
            }
        }

        @Override
        public void onCancelled(DatabaseError error) {
            Log.w(TAG, "Listener was cancelled at .info/connected");
        }
    };*/

    @Override
    public void onCreate() {

        super.onCreate();

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        // Activate the saved theme in preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this /* Activity context */);
        String darkModeValue = sharedPreferences.getString(PREFERENCE_KEY_NIGHT, "");

        switch (darkModeValue){
            case NIGHT_VALUE_LIGHT:
                setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case NIGHT_VALUE_DARK:
                setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case NIGHT_VALUE_BATTERY:
                setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                break;
            case NIGHT_VALUE_SYSTEM:
                setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;

                default:
                    Log.i(TAG, "darkModeValue is not set yet");
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // Set the default value to FOLLOW_SYSTEM because it's API 29 and above
                        setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        // Set the default value to AUTO_BATTERY because we are below api 29
                        setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                    }else{
                        // Set the default value to NIGHT_NO because
                        // "system default" and "Battery Saver" not supported on api below 21
                        setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                    break;
        }



        sApplicationContext = getApplicationContext();

        Log.i(TAG, "Application class onCreate");
        // Initialize the SDK before executing any other operations,
        //FacebookSdk.sdkInitialize(sApplicationContext);

        // [START Firebase Database enable persistence]
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        // [END rtdb_enable_persistence]
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = currentUser != null ? currentUser.getUid() : null;

        createNotificationsChannels();
        /*database = FirebaseDatabase.getInstance();

        //initialize the AuthStateListener method
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in: user userId " + user.getUid());
                    isUserExist(user.getUid()); // if not start complete profile
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener); //*/
        // [START Picasso enable persistence]
        /*Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);*/
        // [END Picasso enable persistence]
    }

    private void createNotificationsChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library

            NotificationChannel LikesChannel = new NotificationChannel(
                    LIKES_CHANNEL_ID,
                    getString(R.string.likes_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
            );
            LikesChannel.setDescription(getString(R.string.likes_notification_channel_description));

            NotificationChannel PickupsChannel = new NotificationChannel(
                    PICK_UPS_CHANNEL_ID,
                    getString(R.string.pickups_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
            );
            PickupsChannel.setDescription(getString(R.string.pickups_notification_channel_description));

            NotificationChannel MessagesChannel = new NotificationChannel(
                    MESSAGES_CHANNEL_ID,
                    getString(R.string.messages_notification_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            MessagesChannel.setDescription(getString(R.string.messages_notification_channel_description));

            NotificationChannel RevealChannel = new NotificationChannel(
                    REQUESTS_CHANNEL_ID,
                    getString(R.string.reveal_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
            );
            RevealChannel.setDescription(getString(R.string.reveal_notification_channel_description));

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(LikesChannel);
                manager.createNotificationChannel(PickupsChannel);
                manager.createNotificationChannel(MessagesChannel);
                manager.createNotificationChannel(RevealChannel);
            }
        }
    }

    public static Context getContext() {
        return sApplicationContext;
        //return instance.getApplicationContext();
    }

    public static String getCurrentUserId() {
        Log.i(TAG, "Application currentUserId= "+currentUserId);
        return currentUserId;
        //return instance.getApplicationContext();
    }

    /*private void isUserExist(String currentUserId) {

        // Read from the database just once
        Log.d(TAG, "currentUserId Value is: " + currentUserId);
        mUserRef = database.getReference().child("users").child(currentUserId);

        // database references for online
        myConnectionsRef = database.getReference().child("users").child(currentUserId).child("connections");
        lastOnlineRef  = database.getReference().child("users").child(currentUserId).child("lastOnline");

        connectedRef  = FirebaseDatabase.getInstance().getReference(".info/connected");

        // [START single_value_read]
        //ValueEventListener postListener = new ValueEventListener() {
        //mUserRef.addValueEventListener(postListener);
        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // [START_EXCLUDE]
                if (dataSnapshot.exists()) {
                    // Get user value
                    Log.i(TAG, "onDataChange dataSnapshot user exist");
                    //connectedRef.addValueEventListener(onlineListener);
                } else {
                    // User is null, error out
                    Log.w(TAG, "User is null, no such user");
                    //completeProfile(currentUserId, currentUserName, currentUserEmail);
                    //completeProfile(mUser);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                //setEditingEnabled(true);
                // [END_EXCLUDE]
            }
        });
        // [END single_value_read]
    }*/

}

