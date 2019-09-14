package com.trackaty.chat.Fragments;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.king.view.radarview.RadarView;
import com.trackaty.chat.Adapters.UsersAdapter;
import com.trackaty.chat.Interface.FirebaseUserCallback;
import com.trackaty.chat.R;
import com.trackaty.chat.ViewModels.UsersViewModel;
import com.trackaty.chat.activities.MainActivity;
import com.trackaty.chat.models.User;
import com.trackaty.chat.receivers.SoundIdAlarm;
import com.trackaty.chat.services.FindNearbyService;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.content.Context.POWER_SERVICE;
import static com.trackaty.chat.App.VISIBILITY_CHANNEL_ID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

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

    private FloatingActionButton mVisibilityButton; // button to make your self visible to others

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
    //private String mCurrentUserId;
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

        fragmentManager = getFragmentManager();

        // prepare the Adapter
        mUserArrayList  = new ArrayList<>();
        mUsersAdapter = new UsersAdapter();

        // Create alarm manager and Intent to be used when user set the visibility alarm
        alarmManager  =  (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        alarmIntent  = new Intent(activity, SoundIdAlarm.class);

        // Initiate viewModel for this fragment instance
        viewModel = ViewModelProviders.of(this).get(UsersViewModel.class);

        //Get current logged in user
        mFirebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
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
                if (items != null) {
                    // Create new Thread to loop until items.size() is greater than 0

                    //delay submitList till items size is not 0
                   /* new java.util.Timer().schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    // your code here
                                    Log.d(TAG, "mama submitList size" +  items.size());
                                    mUsersAdapter.submitList(items);

                                }
                            },
                            1000
                    );*/
                    // Create new Thread to loop until items.size() is greater than 0
                    new Thread(new Runnable() {
                        int sleepCounter = 0;

                        @Override
                        public void run() {
                            try {
                                while (items.size() == 0) {
                                    //Keep looping as long as items size is 0
                                    Thread.sleep(20);
                                    Log.d(TAG, "sleep 1000. size= " + items.size() + " sleepCounter=" + sleepCounter++);
                                    if (sleepCounter == 1000) {
                                        break;
                                    }
                                    //handler.post(this);
                                }
                                //Now items size is greater than 0, let's submit the List
                                Log.d(TAG, "after  sleep finished. size= " + items.size());
                                if (items.size() == 0 && sleepCounter == 1000) {
                                    // If we submit List after loop is finish with 0 results
                                    // we may erase another results submitted via newer thread
                                    Log.d(TAG, "Loop finished with 0 items. Don't submitList");
                                } else {
                                    Log.d(TAG, "submitList");
                                    mUsersAdapter.submitList(items);
                                }

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();
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
        user1.setAvatar("wello");
        user1.setName("wello");
        user1.setBiography("wello");
        user1.setRelationship("wello");
        user1.setInterestedIn("wello");
        user1.setGender("wello");
        user1.setBirthDate(30L);
        user1.setHoroscope("wello");
        user1.setCreated(timestamp);

        User user2 = new User();
        user2.setAvatar("wello");
        user2.setName("wello");
        user2.setBiography("wello");
        user2.setRelationship("wello");
        user2.setInterestedIn("wello");
        user2.setGender("wello");
        user2.setBirthDate(30L);
        user2.setHoroscope("wello");
        user2.setCreated(timestamp);

        if(user1.equals(user2)){
            Log.d(TAG, "users are the same");
        }else{
            Log.d(TAG, "users are the deffrent");
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
                actionbar.setDisplayShowCustomEnabled(true);
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

    /*@Override
    public void onResume() {
        super.onResume();
        if (mBound) {
            // Call a method from the LocalService.
            // However, if this call were something that might hang, then this request should
            // occur in a separate thread to avoid slowing down the activity performance.
            mTimeLiftInMillis = mService.getTimeLift();
            UpdateTimerText(mTimeLiftInMillis);
        }

    }*/

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "fragment state = onStart");
        // Add firebase AuthStateListener
        mAuth.addAuthStateListener(mAuthListener);

        // Update searching UI (radar & timer) on onStart
        toggleSearchingUI();
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
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, RESULT_REQUEST_RECORD_AUDIO);
        }
    }

    //Show a dialog to select whether to edit or un-reveal
    private void showPermissionRationaleDialog() {
        PermissionAlertFragment PermissionRationaleDialog = PermissionAlertFragment.newInstance(mActivityContext);
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
        } else {
            Log.d(TAG, "startStopSearchService: is Service running false= "+isMyServiceRunning(FindNearbyService.class));
            startMyService(FindNearbyService.class);
            StartTimer();
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

    // Start or stop the alarm when Visibility is clicked
    private void startStopAlarm() {
        if (isAlarmExist()) {
            stopAlarm();
        } else {
            if(!isDozeWhiteList()){ // check if the app is already exempted from battery optimization
                int batteryAlertShowTimes = mSharedPreferences.getInt(PREFERENCE_BATTERY_ALERT_SHOWING_TIMES_KEY, 0);
                if(batteryAlertShowTimes < 3){
                    // show the dialog only if it wasn't shown 3 times before
                    showBatteryDialog();
                    mSharedPreferences.edit().putInt(PREFERENCE_BATTERY_ALERT_SHOWING_TIMES_KEY, batteryAlertShowTimes+1).apply();
                }
            }
            setAlarm();
        }
    }

    private boolean isAlarmExist() {
        PendingIntent checkPendingIntent = PendingIntent.getBroadcast(activity, REQUEST_CODE_ALARM, alarmIntent, PendingIntent.FLAG_NO_CREATE);
        if (checkPendingIntent != null){
            Log.d(TAG , "isAlarmExist: yet it is exist. checkPendingIntent= "+checkPendingIntent);
            return true;
        }else{
            Log.d(TAG , "isAlarmExist: not exist. checkPendingIntent= "+checkPendingIntent);
            return false;
        }
    }

    private void setAlarm() {
        Log.d(TAG , "setAlarm(). mCurrentUserId= "+viewModel.getCurrentUserId() + " soundId="+ mCurrentUser.getSoundId());
        //create a Bundle object
        Bundle extras = new Bundle();
        extras.putString(USER_ID_KEY, viewModel.getCurrentUserId());
        extras.putInt(USER_SOUND_ID_KEY, mCurrentUser.getSoundId());
        //attach the bundle to the Intent object
        alarmIntent.putExtras(extras);

        //alarmIntent.putExtra(USER_SOUND_ID_KEY, mCurrentUser.getSoundId());
        //alarmIntent.putExtra(USER_ID_KEY, mCurrentUserId);
        pendingIntent = PendingIntent.getBroadcast(activity, REQUEST_CODE_ALARM, alarmIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        if (alarmManager != null) {
            Log.d(TAG , "setAlarm: alarmManager= "+alarmManager);
            //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),60000, pendingIntent);
            //alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() , 60000, pendingIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), pendingIntent);
            }else{
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() , pendingIntent);
            }
        }
        setNotification(); // Create Ongoing notification when alarm is set
        /*//alarmManager.setAndAllowWhileIdle();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC, System.currentTimeMillis(), pendingIntent);
        }*/

    }

    private void stopAlarm() {
        Log.d(TAG , "stopAlarm()");
        alarmManager =  (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        alarmIntent = new Intent(activity, SoundIdAlarm.class);
        pendingIntent = PendingIntent.getBroadcast(activity, REQUEST_CODE_ALARM, alarmIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
        cancelNotification();

        // update sharedPreferences so settings fragment know about it if alarm stops due to logout
        mSharedPreferences.edit().putBoolean(PREFERENCE_KEY_VISIBLE, false).apply();
    }

    private boolean isDozeWhiteList() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = activity.getPackageName();
            PowerManager pm = (PowerManager) activity.getSystemService(POWER_SERVICE);
            if (pm != null) {
                // is exempt from power optimization
                // Not exempt from power optimization
                return pm.isIgnoringBatteryOptimizations(packageName);
            }else{
                return false;
            }
        }else{
            // API is below android marshmallow anyway
            return true;
        }
    }

    //Show a dialog to confirm blocking user
    private void showBatteryDialog() {
        BatteryAlertFragment batteryFragment = BatteryAlertFragment.newInstance(mActivityContext);
        if (getFragmentManager() != null) {
            fragmentManager = getFragmentManager();
            batteryFragment.show(fragmentManager, BATTERY_ALERT_FRAGMENT);
            Log.i(TAG, "blockFragment show clicked ");
        }
    }

    // Create Ongoing notification when alarm is set
    private void setNotification() {
        Intent NotificationClickIntent = new Intent(mActivityContext, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mActivityContext, NOTIFICATION_PENDING_INTENT_REQUEST_CODE, NotificationClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mNotification = new NotificationCompat.Builder(mActivityContext, VISIBILITY_CHANNEL_ID)
                .setContentTitle(getString(R.string.notification_visibility_title))
                .setContentText(getString(R.string.notification_visibility_body))
                .setSmallIcon(R.mipmap.ic_notification)
                .setColor(getResources().getColor(R.color.color_primary))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_SOCIAL)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();

        notificationManager.notify(VISIBILITY_NOTIFICATION_ID, mNotification);

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
                Log.d(TAG, "onTick.  millisUntilFinished= "+ millisUntilFinished);
                mTimeLiftInMillis = millisUntilFinished;
                UpdateTimerText(mTimeLiftInMillis);// Update timer text on every tick
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "mRemainingTimer onFinish.");
                // End searvice and UI when timer finishes
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
        Log.d(TAG, "UpdateActiveTimeText: hms= "+ formattedTimeLeft);
        //mRemainingTimeText.setText(formattedTimeLeft);
        mTimerText.setText(formattedTimeLeft);
    }

}
