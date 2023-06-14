package com.trackaty.chat.Fragments;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavDeepLinkBuilder;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.king.view.radarview.RadarView;
import com.trackaty.chat.Adapters.UsersAdapter;
import com.trackaty.chat.Interface.FirebaseUserCallback;
import com.trackaty.chat.Interface.ItemClickListener;
import com.trackaty.chat.R;
import com.trackaty.chat.Utils.CheckPermissions;
import com.trackaty.chat.ViewModels.UsersViewModel;
import com.trackaty.chat.activities.MainActivity;
import com.trackaty.chat.models.User;
import com.trackaty.chat.receivers.SoundIdAlarm;
import com.trackaty.chat.services.FindNearbyService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.POWER_SERVICE;
import static com.trackaty.chat.App.VISIBILITY_CHANNEL_ID;
import static com.trackaty.chat.Utils.PendingIntentFlags.*;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements ItemClickListener{

    private final static String TAG = MainFragment.class.getSimpleName();


    private RecyclerView mUsersRecycler;
    private ArrayList<User> mUserArrayList;
    private UsersAdapter mUsersAdapter;
    private  LinearLayoutManager mLinearLayoutManager;

    private FragmentManager fragmentManager;
    private  static final String PERMISSION_RATIONALE_FRAGMENT = "permissionFragment";
    private  static final String BATTERY_ALERT_FRAGMENT = "batteryFragment";
    private  static final int RESULT_REQUEST_RECORD_AUDIO = 21; // for record audio permission
    private static final int REQUEST_CODE_ALARM = 13; // To detect if alarm is already set or not
    private static final String USER_SOUND_ID_KEY = "userSoundId";
    private static final String USER_ID_KEY = "userId";
    private Intent serviceIntent;

    private Context mActivityContext;
    private Activity activity;

    private UsersViewModel viewModel;
    private RadarView mRadarView;
    private ConstraintLayout mRadarLayout;
    private TextView mTimerText;

    private AlarmManager alarmManager;
    private Intent alarmIntent;
    private PendingIntent pendingIntent;
    private NotificationManagerCompat notificationManager;
    private Notification mNotification;

    /*public  FindNearbyService mService;
    public  boolean mBound = false;*/

    private CountDownTimer mSearchingTimer; // Timer for closing search service when finished
    private long mTimeLiftInMillis; // remaining time in milliseconds

    private static final String PREFERENCE_SEARCH_END_TIME_KEY = "SearchEnd"; // Key for save EndTime to SharedPreferences
    private static final String PREFERENCE_BATTERY_ALERT_SHOWING_TIMES_KEY = "batteryAlert"; // Key for number of showing battery optimization dialog
    private static final String PREFERENCE_KEY_VISIBLE = "visible" ;

    private final static int SEARCHING_PERIOD = 10*60*1000; //1*60*1000;
    private final static int NOTIFICATION_PENDING_INTENT_REQUEST_CODE = 55; // For visibility notification

    private static final int LIKES_NOTIFICATION_ID = 1;
    private static final int PICK_UPS_NOTIFICATION_ID = 2;
    private static final int MESSAGES_NOTIFICATION_ID = 3;
    private static final int REQUESTS_SENT_NOTIFICATION_ID = 4;
    private static final int REQUESTS_APPROVED_NOTIFICATION_ID = 5;
    private static final int FIND_NOTIFICATION_ID = 6;
    private static final int VISIBILITY_NOTIFICATION_ID = 7; // channel Id for visibility notification, Used to cancel it too

    private SharedPreferences mSharedPreferences; // So save EndTime in SharedPreferences

    private FirebaseUser mFirebaseCurrentUser;
    private String mCurrentUserId;
    private User mCurrentUser;

    //initialize the FirebaseAuth instance
    private FirebaseAuth  mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private static final int REACHED_THE_TOP = 2;
    private static final int SCROLLING_UP = 1;
    private static final int SCROLLING_DOWN = -1;
    private static final int REACHED_THE_BOTTOM = -2;
    private static int mScrollDirection;
    private static int mLastVisibleItem;
    private AudioManager mAudioManager; // To check if mic is muted or not
   /* // To listen to mic mute changes
    private IntentFilter mMuteChangedIntentFilter;
    private MicMuteChangedReceiver mMicMuteChangedReceiver;
    private int checkMicMuteWindow = 0; // to listen for mic mute on api < 28 P*/

    //Fragments tags
    private  static final String MUTED_MIC_ALERT_FRAGMENT_TAG = "MutedMicFragment"; // Tag for confirm block and delete alert fragment


    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     */
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"fragment state = onCreate");

        setHasOptionsMenu(true); // To add search menu item programmatically

        fragmentManager = getChildFragmentManager();

        /*// To listen to mic mute changes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mMuteChangedIntentFilter = new IntentFilter(AudioManager.ACTION_MICROPHONE_MUTE_CHANGED);
            mMicMuteChangedReceiver = new MicMuteChangedReceiver(){
                @Override
                public void onReceive(Context context, Intent intent) {
                    super.onReceive(context, intent);
                    if (AudioManager.ACTION_MICROPHONE_MUTE_CHANGED.equals(intent.getAction())) {
                        Log.d(TAG, "AudioManager microphone mute changed");
                        // To check if mic is muted or not
                        AudioManager mAudioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
                        if(mAudioManager != null && mAudioManager.isMicrophoneMute()){
                            // Stop alarm and radar view
                            mRadarView.stop();
                            mRadarLayout.setVisibility(View.GONE);
                            CancelTimer();
                            *//*if (isMyServiceRunning(FindNearbyService.class)) {
                                showMutedMicDialog();
                            }*//*
                        }
                    }
                }
            };
        }*/

        /*{
            @Override
            public void onReceive(Context context, Intent intent) {
                super.onReceive(context, intent);
                if (AudioManager.ACTION_MICROPHONE_MUTE_CHANGED.equals(intent.getAction())) {
                    Log.d(TAG, "AudioManager microphone mute changed");
                    // To check if mic is muted or not
                    AudioManager mAudioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
                    //if(mAudioManager != null && mAudioManager.isMicrophoneMute()){
                    Toast.makeText(context, R.string.mic_muted_error ,Toast.LENGTH_LONG).show();
                    // Stop alarm and radar view
                }
            }
        };*/

        // prepare the Adapter
        mUserArrayList  = new ArrayList<>();
        mUsersAdapter = new UsersAdapter();

        // Create alarm manager and Intent to be used when user set the visibility alarm
        alarmManager  =  (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        alarmIntent  = new Intent(activity, SoundIdAlarm.class);

        // Initiate viewModel for this fragment instance
        viewModel = new ViewModelProvider(this).get(UsersViewModel.class);

        //Get current logged in user
        mFirebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mCurrentUserId = mFirebaseCurrentUser != null ? mFirebaseCurrentUser.getUid() : null;
        // Current user id is used to know if it's the first log in or not
        // if first login, we will initiate observing the page list, if not, we will just update the user id to invalidate
        viewModel.setCurrentUserId(mFirebaseCurrentUser != null ? mFirebaseCurrentUser.getUid() : null);

        // Only get results when user is already loge in, will be ignored if he is logged out
        if (viewModel.getCurrentUserId() != null) {
            // Get current user. User to send sound id extra to alarm receiver
            viewModel.getUserOnce(viewModel.getCurrentUserId(), new FirebaseUserCallback() {
                @Override
                public void onCallback(User user) {
                    if (user != null){
                        Log.d(TAG , "onAuthStateChanged: getUserOnce Callback: getSoundId= "+user.getSoundId()+ " userName= "+ user.getName() + " userId= "+ user.getKey());
                        mCurrentUser = user;
                    }
                }
            });
        }

        mAuth = FirebaseAuth.getInstance();
        //initialize the AuthStateListener method
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                // User is signed in
                if (user != null) {
                    // User is signed in
                    // If user is logged in, show recycler
                    // because it might be not showing due to previous log out
                    Log.d(TAG, "onAuthStateChanged: user is signed in. set recycler to visible");
                    mUsersRecycler.setVisibility(View.VISIBLE);

                    // Only update current userId if it's changed. if not, we will check if it's the last logged out user
                    // if last logged out, we will updateCurrentUserId to invalidate and get new data
                    if(!TextUtils.equals(viewModel.getCurrentUserId(), user.getUid())){
                        if(viewModel.getCurrentUserId() == null){
                            // if currentUserId is null, it's the first time to open the app
                            // and the user wasn't logged in. initiateObserveSearch();
                            initiateObserveSearch(user.getUid());
                            Log.d(TAG, "onAuthStateChanged: first time to log in. user wasn't logged in. initiateObserveSearch. oldCurrentUserId = " + viewModel.getCurrentUserId()+ " new id= "+user.getUid());
                        }else{
                            // currentUserId exists. it's the second time to log in. user was logged in before. updatePagedList to invalidate and get the new user's list
                            viewModel.updateCurrentUserId(user.getUid());
                            Log.d(TAG, "onAuthStateChanged: second time to log in. user was logged in. updatePagedList. oldCurrentUserId = " + viewModel.getCurrentUserId()+ " new id= "+user.getUid());
                        }

                        // Get current user whenever user id is changed. To send the new sound id extra to alarm receiver
                        viewModel.getUserOnce(user.getUid(), new FirebaseUserCallback() {
                            @Override
                            public void onCallback(User user) {
                                if (user != null){
                                    Log.d(TAG , "onAuthStateChanged: getUserOnce Callback: getSoundId= "+user.getSoundId()+ " userName= "+ user.getName() + " userId= "+ user.getKey());
                                    mCurrentUser = user;
                                }
                            }
                        });

                    }else{
                        //The same user. if it's the last know user, updateCurrentUserId to invalidate
                        Log.d(TAG , "onAuthStateChanged: it's the same user. if it's the last know user, updateCurrentUserId to invalidate");
                        if(TextUtils.equals(viewModel.getLastLogOutUserId(), user.getUid())){
                            viewModel.updateCurrentUserId(user.getUid());

                            // Get current user whenever user id is changed. To send the new sound id extra to alarm receiver
                            viewModel.getUserOnce(user.getUid(), new FirebaseUserCallback() {
                                @Override
                                public void onCallback(User user) {
                                    if (user != null){
                                        Log.d(TAG , "onAuthStateChanged: getUserOnce Callback: getSoundId= "+user.getSoundId()+ " userName= "+ user.getName() + " userId= "+ user.getKey());
                                        mCurrentUser = user;
                                    }
                                }
                            });

                        }
                    }// End of checking if it's the same user or not

                    // set current user id, will be used when comparing new logged in id with the old one
                    viewModel.setCurrentUserId(user.getUid());
                    Log.d(TAG, "onAuthStateChanged: userId is changed. oldCurrentUserId = " + viewModel.getCurrentUserId()+ " new id= "+user.getUid());

                } else {
                    // User is signed out
                    // If user is logged out, hide recycler
                    // because we don't want to show it before displaying login activity
                    mUsersRecycler.setVisibility(View.INVISIBLE);
                    Log.d(TAG, "onAuthStateChanged: signed_out. hide recycler");
                    // Stop find nearby service when log out
                    //startStopSearchService();
                    //toggleSearchingUI();
                    stopMyService(FindNearbyService.class);
                    CancelTimer();

                    // Stop visibility alarm when log out
                    //startStopAlarm(); // start the alarm if it's null, stop it if already started
                    stopAlarm();

                    // clear mUser object in case user will log in with another account
                    if(mCurrentUser != null ){
                        mCurrentUser = null;
                    }

                    // Remove all MainViewModel Listeners
                    /*if(viewModel.getItemPagedList(null).hasObservers()){
                        Log.d(TAG, "onAuthStateChanged: signed_out. Remove all MainViewModel Listeners");
                        viewModel.clearViewModel();
                        viewModel.getItemPagedList(null).removeObservers(MainFragment.this);
                    }*/
                    //viewModel.setCurrentUserId(null);
                    // used to know if the same user logged out then logged in again with the same account
                    // if LastLogOutUserId is not set we can't know if he is the previous user or not, hence his list will be empty because it's not different user
                    viewModel.setLastLogOutUserId(viewModel.getCurrentUserId());
                    mUsersAdapter.submitList(null); // to delete previous list and start fresh

                }
            }
        };
    }

    private void initiateObserveSearch(String userId) {
        // It's best to observe on onActivityCreated so that we dona't have to update ViewModel manually.
        // This is because LiveData will not call the observer since it had already delivered the last result to that observer.
        // But recycler adapter is updated any way despite that LiveData delivers updates only when data changes, and only to active observers.
        // Use getViewLifecycleOwner() instead of this, to get only one observer for this view
        viewModel.getItemPagedList(userId).observe(getViewLifecycleOwner(), new Observer<PagedList<User>>() {
            @Override
            public void onChanged(@Nullable final PagedList<User> items) {
                Log.d(TAG, "mama onChanged");
                if (items != null && items.size()>0) {
                    Log.d(TAG, "submitList size = "+ items.size());
                    mUsersAdapter.submitList(items);
                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "fragment state = onCreateView");
        // Inflate the layout for this fragment
        /*if(activity != null){
            activity.setTitle(R.string.main_frag_title);
        }*/
        View fragView = inflater.inflate(R.layout.fragment_main, container, false);

        // Radar view
        mRadarView =  fragView.findViewById(R.id.radar);
        mTimerText =  fragView.findViewById(R.id.timerText);
        mRadarLayout =  fragView.findViewById(R.id.radar_layout);

        // Initiate the RecyclerView
        mUsersRecycler = fragView.findViewById(R.id.users_recycler);
        mUsersRecycler.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(mActivityContext);
        mUsersRecycler.setLayoutManager(mLinearLayoutManager);

        //viewModel.usersList.observe(this, mUsersAdapter::submitList);

        //observe when a change happen to usersList live data
        mUsersRecycler.setAdapter(mUsersAdapter);

        mUsersRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d(TAG, "onScrollStateChanged newState= "+newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d(TAG, "onScrolled dx= "+dx +" dy= "+dy);

                //int lastCompletelyVisibleItem = mLinearLayoutManager.findLastCompletelyVisibleItemPosition(); // the position of last displayed item
                //int firstCompletelyVisibleItem = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition(); // the position of first displayed item
                int lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition(); // the position of last displayed item
                int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition(); // the position of first displayed item
                int totalItemCount = mUsersAdapter.getItemCount(); // total items count from the adapter
                int visibleItemCount = mUsersRecycler.getChildCount(); // items are shown on screen right now

                Log.d(TAG, "visibleItemCount = "+visibleItemCount +" totalItemCount= "+totalItemCount+" lastVisibleItem "+lastVisibleItem +" firstVisibleItem "+firstVisibleItem);

                //if(lastCompletelyVisibleItem >= (totalItemCount-1)){
                /*if(lastVisibleItem >= (totalItemCount-1)){
                    // The position of last displayed item = total items, witch means we are at the bottom
                    mScrollDirection = REACHED_THE_BOTTOM;
                    Log.i(TAG, "List reached the bottom");
                    // Set scrolling direction and and last visible item which is needed to know
                    // the initial key position weather it's above or below
                    mChatsViewModel.setScrollDirection(mScrollDirection, lastVisibleItem);

                }*/
                if(firstVisibleItem <= 4){
                    // The position of last displayed item is less than visibleItemCount, witch means we are at the top
                    mScrollDirection = REACHED_THE_TOP;
                    Log.i(TAG, "List reached the top");
                    // Set scrolling direction and and last visible item which is needed to know
                    // the initial key position weather it's above or below
                    viewModel.setScrollDirection(mScrollDirection, firstVisibleItem);
                }else{
                    if(dy < 0 ){
                        // dy is negative number,  scrolling up
                        Log.i(TAG, "List scrolling up");
                        mScrollDirection = SCROLLING_UP;
                        // Set scrolling direction and and last visible item which is needed to know
                        // the initial key position weather it's above or below
                        viewModel.setScrollDirection(mScrollDirection, firstVisibleItem);
                    }else{
                        // dy is positive number,  scrolling down
                        Log.i(TAG, "List scrolling down");
                        mScrollDirection = SCROLLING_DOWN;
                        // Set scrolling direction and and last visible item which is needed to know
                        // the initial key position weather it's above or below
                        viewModel.setScrollDirection(mScrollDirection, lastVisibleItem);
                    }
                }
            }
        });

        /*Object timestamp = System.currentTimeMillis();
        // just a test to compare two objects
        User user1 = new User();
        user1.setAvatar("Name");
        user1.setName("Name");
        user1.setBiography("Name");
        user1.setRelationship("Name");
        user1.setInterestedIn("Name");
        user1.setGender("Name");
        user1.setBirthDate(30L);
        user1.setHoroscope("Name");
        user1.setCreated(timestamp);

        User user2 = new User();
        user2.setAvatar("Name");
        user2.setName("Name");
        user2.setBiography("Name");
        user2.setRelationship("Name");
        user2.setInterestedIn("Name");
        user2.setGender("Name");
        user2.setBirthDate(30L);
        user2.setHoroscope("Name");
        user2.setCreated(timestamp);

        if(user1.equals(user2)){
            Log.d(TAG, "users are the same");
        }else{
            Log.d(TAG, "users are the different");
        }*/

        return fragView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "fragment state = onAttach");
        mActivityContext = context;
        if (context instanceof Activity){// check if fragmentContext is an activity
            activity =(Activity) context;
        }
        // get the default SharedPreferences. We will use it to save EndTime for search countdown
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        notificationManager = NotificationManagerCompat.from(context); // To notify the manager to create or cancel notification
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "fragment state = onActivityCreated");
        if((getActivity())!= null){
            ActionBar actionbar = ((MainActivity)getActivity()).getSupportActionBar();
            if (actionbar != null) {
                actionbar.setTitle(R.string.main_frag_title);
                actionbar.setDisplayHomeAsUpEnabled(false);
                actionbar.setHomeButtonEnabled(false);
                actionbar.setDisplayShowCustomEnabled(false);
            }

            // No need to put countdown on the toolbar
            /*LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View actionBarView = inflater.inflate(R.layout.find_toolbar, null);
            if (actionbar != null) {
                actionbar.setCustomView(actionBarView);
            }


            //mTimer = new Timer();
            // custom action bar items to add receiver's avatar and name //
            mTimerText =  actionBarView.findViewById(R.id.last_seen);*/
        }

        if(viewModel.getCurrentUserId() != null){
            initiateObserveSearch(viewModel.getCurrentUserId());
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "fragment state = onStart");
        // Add firebase AuthStateListener
        mAuth.addAuthStateListener(mAuthListener);

        // Update searching UI (radar & timer) on onStart
        toggleSearchingUI();

        /*// Register mic mute change Receiver
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mActivityContext.registerReceiver(mMicMuteChangedReceiver,mMuteChangedIntentFilter);
        }*/
        /*MicMuteChangedReceiver mMicMuteChangedReceiver = new MicMuteChangedReceiver();
        mMicMuteChangedReceiver.registerReceiver(this);*/

        // pass reference to interface from onCreate()
        // Register to receive messages.
        // We are registering an observer (mServiceReceiver) to receive Intents
        // with actions named "custom-event-name".
        LocalBroadcastManager.getInstance(mActivityContext).registerReceiver(mServiceReceiver,
                new IntentFilter("com.basbes.dating.serviceStopped"));
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "fragment state = onStop");
        // Remove firebase AuthStateListener
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        //activity.unbindService(connection);
        //mBound = false;
        // Stop searching UI (radar & timer) on onStart
        CancelTimer();
        mRadarView.stop();
        mRadarLayout.setVisibility(View.GONE);

        // Create a map for all messages need to be updated
        Map<String, Object> updateMap = new HashMap<>();

        // Update all revealed messages on fragment's stop
        if(mUsersAdapter != null){
            // Get revealed list from the adapter
            List<User> brokenAvatarsList = mUsersAdapter.getBrokenAvatarsList();

            for (int i = 0; i < brokenAvatarsList.size(); i++) {
                Log.d(TAG, "brokenAvatarsList url= "+brokenAvatarsList.get(i).getAvatar() + " key= "+brokenAvatarsList.get(i).getKey());
                updateMap.put(brokenAvatarsList.get(i).getKey()+"/avatar", brokenAvatarsList.get(i).getAvatar());
            }
        }

        if(updateMap.size() > 0 && mCurrentUserId != null){
            Log.d(TAG, "brokenAvatarsList url = updateMap.size= "+updateMap.size() +" mCurrentUserId="+mCurrentUserId);
            // update senderAvatar to the new uri
            DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference mUserRef = mDatabaseRef.child("search").child(mCurrentUserId); // Should be search instead of users
            //DatabaseReference mUserRef = mDatabaseRef.child("search").child(mCurrentUserId);
            mUserRef.updateChildren(updateMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // onSuccess clear the list to start all over
                    Log.d(TAG, "brokenAvatarsList url = onSuccess ");
                    mUsersAdapter.clearBrokenAvatarsList();
                }
            });

        }

        /*// unregister Mic Mute Changed Receiver
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mActivityContext.unregisterReceiver(mMicMuteChangedReceiver);
        }*/

        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(mActivityContext).unregisterReceiver(mServiceReceiver);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.menu_main, menu);
        MenuItem prayerItem = menu.add(Menu.NONE, 1, 1, R.string.menu_search);
        prayerItem.setIcon( R.drawable.ic_search_black_24dp );
        prayerItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );

        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == 1) {
            Log.d(TAG, "MenuItem search is clicked");
            if (!isPermissionGranted()) {
                requestPermission();
            } else {
                startStopSearchService();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //A method to check if the service is running or not
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        if(manager != null){
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    // If record audio is granted, stat the findNearby service immediately, if not, ask for permission first
    private boolean isPermissionGranted() {
        Log.d(TAG, "is permission Granted= "+(ContextCompat.checkSelfPermission(mActivityContext, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED));
        return ContextCompat.checkSelfPermission(mActivityContext, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        //return true;
    }

    private void requestPermission() {
        // Permission is not granted
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO)) {
            Log.i(TAG, "requestPermission: permission should show Rationale");
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            showPermissionRationaleDialog();
        } else {
            // No explanation needed; request the permission
            Log.i(TAG, "requestPermission: No explanation needed; request the permission");
            //ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, RESULT_REQUEST_RECORD_AUDIO);
            // Use this requestPermissions(new String[] to receive the result on fragment instead of the activity
            requestPermissions(new String[] {Manifest.permission.RECORD_AUDIO}, RESULT_REQUEST_RECORD_AUDIO);
        }
    }

    //Show a dialog to select whether to edit or un-reveal
    private void showPermissionRationaleDialog() {
        PermissionAlertFragment PermissionRationaleDialog = PermissionAlertFragment.newInstance(mActivityContext, this);
        PermissionRationaleDialog.show(fragmentManager, PERMISSION_RATIONALE_FRAGMENT);
        Log.i(TAG, "showPermissionRationaleDialog: permission AlertFragment show clicked ");
    }

    // Start or stop search service UI onCreateView when fragment starts
    private void toggleSearchingUI() {
        if(isMyServiceRunning(FindNearbyService.class)){
            mRadarView.start();
            mRadarLayout.setVisibility(View.VISIBLE);
            StartTimer();
        }else{
            mRadarView.stop();
            mRadarLayout.setVisibility(View.GONE);
            CancelTimer();
        }
    }

    // Start search service and update UI
    private void startStopSearchService() {
        if (isMyServiceRunning(FindNearbyService.class)) {
            Log.d(TAG, "startStopSearchService: is Service running true= "+isMyServiceRunning(FindNearbyService.class));
            stopMyService(FindNearbyService.class);
            CancelTimer();

            // unregister Mic Mute Changed Receiver
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                mActivityContext.unregisterReceiver(mMicMuteChangedReceiver);
            }*/
        } else {
            // Starting from Api 33 we must grant post notification permission at run time to be able to send notifications
            CheckPermissions.checkNotificationPermission(getContext());

            Log.d(TAG, "startStopSearchService: is Service running false= "+isMyServiceRunning(FindNearbyService.class));
            if(!isMicMuted()){
                Log.d(TAG , "Microphone is not Muted. lets start search service");
                startMyService(FindNearbyService.class);
                StartTimer();

                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    mActivityContext.registerReceiver(mMicMuteChangedReceiver,mMuteChangedIntentFilter);
                }*/
            }else{
                Log.d(TAG , "Microphone is Muted. show dialog to enable user to choose");
                showMutedMicDialog();
            }
        }
    }


    private void startMyService(Class<?> serviceClass) {
        Log.d(TAG, "startMyService ");
        serviceIntent = new Intent(activity, FindNearbyService.class);
        //activity.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
        //serviceIntent.putExtra("userID",mSendText.getText().toString());
        activity.startService(serviceIntent);
        mRadarView.start(); // Start Radar spinning
        mRadarLayout.setVisibility(View.VISIBLE);

        // Save EndTime on SharedPreferences
        long now = System.currentTimeMillis();
        mSharedPreferences.edit().putLong(PREFERENCE_SEARCH_END_TIME_KEY, SEARCHING_PERIOD + now).apply();
    }


    private void stopMyService(Class<?> serviceClass) {
        Log.d(TAG, "stopMyService");
        /*activity.unbindService(connection);
        mBound = false;*/
        serviceIntent = new Intent(activity, FindNearbyService.class);
        activity.stopService(serviceIntent);
        mRadarView.stop(); // Stop Radar spinning
        mRadarLayout.setVisibility(View.GONE);
    }


    private void stopAlarm() {
        Log.d(TAG , "stopAlarm()");
        alarmManager =  (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        alarmIntent = new Intent(activity, SoundIdAlarm.class);
        pendingIntent = PendingIntent.getBroadcast(activity, REQUEST_CODE_ALARM, alarmIntent, pendingIntentUpdateCurrentFlag());
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
        cancelNotification();

        // update sharedPreferences so settings fragment know about it if alarm stops due to logout
        mSharedPreferences.edit().putBoolean(PREFERENCE_KEY_VISIBLE, false).apply();
    }


    // Cancel Ongoing notification when user click visibility again
    private void cancelNotification() {
        notificationManager.cancel(VISIBILITY_NOTIFICATION_ID);
    }

    // Get Request Permissions Result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == RESULT_REQUEST_RECORD_AUDIO) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the task you need to do.
                Log.i(TAG, "onRequestPermissionsResult permission was granted");
                startStopSearchService();
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Log.i(TAG, "onRequestPermissionsResult permission denied");
            }
        }
    }

    /** Defines callbacks for service binding, passed to bindService() */
    /*private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            FindNearbyService.LocalBinder binder = (FindNearbyService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            Log.i(TAG, "onServiceConnected");
            if (mBound) {
                // Call a method from the LocalService.
                // However, if this call were something that might hang, then this request should
                // occur in a separate thread to avoid slowing down the activity performance.
                mTimeLiftInMillis = mService.getTimeLift();
                StartTimer();
                UpdateTimerText(mTimeLiftInMillis);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            Log.i(TAG, "onServiceDisconnected");
        }
    };*/

    private void StartTimer() {

        // check if it's a negative number, return if so
        if(mTimeLiftInMillis < 0){
            return;
        }
        // Cancel any previous timers
        if (mSearchingTimer != null) {
            CancelTimer();
        }

        mTimerText.setVisibility(View.VISIBLE); // make TimerText visible

        long now = System.currentTimeMillis();
        mTimeLiftInMillis = mSharedPreferences.getLong(PREFERENCE_SEARCH_END_TIME_KEY, SEARCHING_PERIOD) - now;
        mSearchingTimer = new CountDownTimer( mTimeLiftInMillis,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //Log.d(TAG, "onTick.  millisUntilFinished= "+ millisUntilFinished);
                mTimeLiftInMillis = millisUntilFinished;
                UpdateTimerText(mTimeLiftInMillis);// Update timer text on every tick

                /*// Only user this method if api is less than 28
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                    checkMicMuteWindow ++; // increase the check window to check every 10 seconds
                    if(checkMicMuteWindow >= 10){
                        // To check if mic is muted or not
                        Log.d(TAG, "10 seconds passed, lets check if mic is muted or not. checkMicMuteWindow= "+checkMicMuteWindow);
                        if(mAudioManager != null && mAudioManager.isMicrophoneMute()) {
                            // Stop alarm and radar view
                            mRadarView.stop();
                            mRadarLayout.setVisibility(View.GONE);
                            CancelTimer();
                            *//*if (isMyServiceRunning(FindNearbyService.class)) {
                                showMutedMicDialog();
                            }*//*
                        }

                        checkMicMuteWindow = 0; // reset the counter to start checking again when it reaches 10 seconds
                    }
                }*/
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "mRemainingTimer onFinish.");
                // End service and UI when timer finishes
                CancelTimer();
                stopMyService(FindNearbyService.class);
            }
        }.start();
    }
    // Cancel active countdown timer
    private void CancelTimer() {
        if(mSearchingTimer != null){
            // cancel timer
            mSearchingTimer.cancel();
            mTimerText.setVisibility(View.GONE);
            Log.d(TAG, "mSearchingTimer canceled");
        }
    }

    private void UpdateTimerText(long timeLiftInMillis) {

        //long days = TimeUnit.MILLISECONDS.toDays(mTimeLiftInMillis);
        //long hours = TimeUnit.MILLISECONDS.toHours(timeLiftInMillis) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(timeLiftInMillis));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeLiftInMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeLiftInMillis));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeLiftInMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeLiftInMillis));
        //long milliseconds = diff - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(diff));

        // formatted timer text
        String formattedTimeLeft = String.format(Locale.getDefault(),"%02d:%02d", minutes,seconds );
        //Log.d(TAG, "UpdateActiveTimeText: hms= "+ formattedTimeLeft);
        //mRemainingTimeText.setText(formattedTimeLeft);
        mTimerText.setText(formattedTimeLeft);
    }

    private boolean isMicMuted() {
        // to check if microphone is muted before starts search service
        mAudioManager = (AudioManager) mActivityContext.getSystemService(AUDIO_SERVICE);
        if(mAudioManager != null){
            Log.d(TAG, "AudioManager isMicrophoneMute =" + mAudioManager.isMicrophoneMute());
            return mAudioManager.isMicrophoneMute();
        }else{
            return false;
        }
    }

    //Show a dialog to allow user to enable his microphone
    private void showMutedMicDialog() {
        MutedMicAlertFragment mutedMicAlertFragment = MutedMicAlertFragment.newInstance(mActivityContext);
        if (getChildFragmentManager() != null) {
            fragmentManager = getChildFragmentManager();
            mutedMicAlertFragment.show(fragmentManager, MUTED_MIC_ALERT_FRAGMENT_TAG);
            Log.i(TAG, "mutedMicAlertFragment show clicked ");
        }
    }

    @Override
    public void onClick(View view, int position, boolean isLongClick) {
        Log.d(TAG, "onClick view= " + view + " position= " + position);
        /*if (position == 2) { // Enable microphone is clicked
            // to check if microphone is muted before starts search service
            if (mAudioManager != null && mAudioManager.isMicrophoneMute()) {
                Log.d(TAG, "AudioManager is Microphone Muted=" + mAudioManager.isMicrophoneMute());
                mAudioManager.setMicrophoneMute(false);
                Log.d(TAG , "Microphone is not Muted. lets start search service");
                startMyService(FindNearbyService.class);
                StartTimer();
            }
        }*/
        if (position == 1) { // Grant microphone permision
            Log.d(TAG, "item clicked position= " + position + " View= "+view);
            //ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, RESULT_REQUEST_RECORD_AUDIO);
            // Use this requestPermissions(new String[] to receive the result on fragment instead of the activity
            requestPermissions(new String[] {Manifest.permission.RECORD_AUDIO}, RESULT_REQUEST_RECORD_AUDIO);
        }
    }

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            if (intent != null && "com.basbes.dating.serviceStopped".equals(intent.getAction())){
                Log.d(TAG, "Got message: serviceStopped");
                if(mAudioManager != null && mAudioManager.isMicrophoneMute()){
                    // Stop alarm and radar view
                    mRadarView.stop();
                    mRadarLayout.setVisibility(View.GONE);
                    CancelTimer();
                    /*if (isMyServiceRunning(FindNearbyService.class)) {
                       showMutedMicDialog();
                    }*/
                }
            }
        }
    };
}

