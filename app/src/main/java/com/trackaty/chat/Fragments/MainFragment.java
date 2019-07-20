package com.trackaty.chat.Fragments;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.king.view.radarview.RadarView;
import com.trackaty.chat.Adapters.UsersAdapter;
import com.trackaty.chat.R;
import com.trackaty.chat.ViewModels.UsersViewModel;
import com.trackaty.chat.activities.MainActivity;
import com.trackaty.chat.models.User;
import com.trackaty.chat.services.FindNearbyService;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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

    private FragmentManager fragmentManager;
    private  static final String PERMISSION_RATIONALE_FRAGMENT = "permissionFragment";
    private  static final int RESULT_REQUEST_RECORD_AUDIO = 21; // for record audio permission
    private Intent serviceIntent;

    private Context mActivityContext;
    private Activity activity;

    private UsersViewModel viewModel;
    private RadarView mRadarView;
    private TextView mTimerText;

    private FloatingActionButton mVisibilityButton; // button to make your self visible to others

    /*public  FindNearbyService mService;
    public  boolean mBound = false;*/

    private CountDownTimer mSearchingTimer; // Timer for closing search service when finished
    private long mTimeLiftInMillis; // remaining time in milliseconds
    private static final String SEARCH_END_TIME_KEY = "Search end time"; // Key for save EndTime to SharedPreferences
    private final static int SEARCHING_PERIOD = 10*60*1000; //1*60*1000;



    private SharedPreferences mSharedPreferences; // So save EndTime in SharedPreferences

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
        Log.d(TAG,"onCreate");

        setHasOptionsMenu(true); // To add search menu item programmatically

        fragmentManager = getFragmentManager();

        // prepare the Adapter
        mUserArrayList  = new ArrayList<>();
        mUsersAdapter = new UsersAdapter();

        // Initiate viewModel for this fragment instance
        viewModel = ViewModelProviders.of(this).get(UsersViewModel.class);

        // It's best to observe on onActivityCreated so that we dona't have to update ViewModel manually.
        // This is because LiveData will not call the observer since it had already delivered the last result to that observer.
        // But recycler adapter is updated any way despite that LiveData delivers updates only when data changes, and only to active observers.
        // Use getViewLifecycleOwner() instead of this, to get only one observer for this view
        viewModel.getItemPagedList().observe(this, new Observer<PagedList<User>>() {
            @Override
            public void onChanged(@Nullable final PagedList<User> items) {
                System.out.println("mama onChanged");
                if (items != null ){
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
                                while(items.size()==0) {
                                    //Keep looping as long as items size is 0
                                    Thread.sleep(20);
                                    Log.d(TAG, "sleep 1000. size= "+items.size()+" sleepCounter="+sleepCounter++);
                                    if(sleepCounter == 1000){
                                        break;
                                    }
                                    //handler.post(this);
                                }
                                //Now items size is greater than 0, let's submit the List
                                Log.d(TAG, "after  sleep finished. size= "+items.size());
                                if(items.size() == 0 && sleepCounter == 1000){
                                    // If we submit List after loop is finish with 0 results
                                    // we may erase another results submitted via newer thread
                                    Log.d(TAG, "Loop finished with 0 items. Don't submitList");
                                }else{
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
        Log.d(TAG, "onCreateView");
        // Inflate the layout for this fragment
        /*if(activity != null){
            activity.setTitle(R.string.main_frag_title);
        }*/
        View fragView = inflater.inflate(R.layout.fragment_main, container, false);

        // Radar view
        mRadarView = (RadarView) fragView.findViewById(R.id.radar);
        mVisibilityButton = (FloatingActionButton) fragView.findViewById(R.id.visibility_fab);
        mTimerText = (TextView) fragView.findViewById(R.id.timerText);

        // Initiate the RecyclerView
        mUsersRecycler = (RecyclerView) fragView.findViewById(R.id.users_recycler);
        mUsersRecycler.setHasFixedSize(true);
        mUsersRecycler.setLayoutManager(new LinearLayoutManager(mActivityContext));

        //viewModel.usersList.observe(this, mUsersAdapter::submitList);

        //observe when a change happen to usersList live data
        mUsersRecycler.setAdapter(mUsersAdapter);

       /* // just a test to compare two objects
        User user1 = new User();
        //user1.setAvatar("wello");
        user1.setName("wello");
        user1.setBiography("wello");
        user1.setRelationship("wello");
        user1.setInterestedIn("wello");
        user1.setGender("wello");
        user1.setBirthDate(30L);
        user1.setHoroscope("wello");

        User user2 = new User();
        user2.setAvatar("wello");
        user2.setName("wello");
        user2.setBiography("wello");
        user2.setRelationship("wello");
        user2.setInterestedIn("wello");
        user2.setGender("wello");
        user2.setBirthDate(30L);
        user2.setHoroscope("wello");

        if(user1.equals(user2)){
            Log.d(TAG, "users are the same");
        }else{
            Log.d(TAG, "users are the deffrent");
        }*/

       // A button to make current user visible to others
        mVisibilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "mVisibilityButton is clicked");
            }
        });

        return fragView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivityContext = context;
        if (context instanceof Activity){// check if fragmentContext is an activity
            activity =(Activity) context;
        }
        // get the default SharedPreferences. We will use it to save EndTime for search countdown
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(((MainActivity)getActivity())!= null){
            ActionBar actionbar = ((MainActivity)getActivity()).getSupportActionBar();
            actionbar.setTitle(R.string.main_frag_title);
            actionbar.setDisplayHomeAsUpEnabled(false);
            actionbar.setHomeButtonEnabled(false);
            actionbar.setDisplayShowCustomEnabled(false);
        }
        //animalViewModel.getAnimals()?.observe(this, Observer(animalAdapter::submitList))
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
        Log.d(TAG, "onStart");
        // Update searching UI (radar & timer) on onStart
        toggleSearchingUI();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        //activity.unbindService(connection);
        //mBound = false;
        // Stop searching UI (radar & timer) on onStart
        CancelTimer();
        mRadarView.stop();
        mRadarView.setVisibility(View.GONE);
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
        switch (id) {
            case 1:
                Log.d(TAG, "MenuItem search is clicked");
                if(!isPermissionGranted()){
                    requestPermission();
                }else{
                    startStopSearchService();
                }
                break;

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
        PermissionAlertFragment PermissionRationaleDialog = PermissionAlertFragment.newInstance();
        PermissionRationaleDialog.show(fragmentManager, PERMISSION_RATIONALE_FRAGMENT);
        Log.i(TAG, "showPermissionRationaleDialog: permission AlertFragment show clicked ");
    }

    // Start or stop search service UI onCreateView when fragment starts
    private void toggleSearchingUI() {
        if(isMyServiceRunning(FindNearbyService.class)){
            mRadarView.start();
            mRadarView.setVisibility(View.VISIBLE);
            StartTimer();
        }else{
            mRadarView.stop();
            mRadarView.setVisibility(View.GONE);
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
        mRadarView.setVisibility(View.VISIBLE);

        // Save EndTime on SharedPreferences
        long now = System.currentTimeMillis();
        mSharedPreferences.edit().putLong(SEARCH_END_TIME_KEY, SEARCHING_PERIOD + now).apply();
    }


    private void stopMyService(Class<?> serviceClass) {
        Log.d(TAG, "stopMyService");
        /*activity.unbindService(connection);
        mBound = false;*/
        serviceIntent = new Intent(activity, FindNearbyService.class);
        activity.stopService(serviceIntent);
        mRadarView.stop(); // Stop Radar spinning
        mRadarView.setVisibility(View.GONE);
    }


    // Get Request Permissions Result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case RESULT_REQUEST_RECORD_AUDIO: {
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
                return;

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
        mTimeLiftInMillis = mSharedPreferences.getLong(SEARCH_END_TIME_KEY, SEARCHING_PERIOD) - now;
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
