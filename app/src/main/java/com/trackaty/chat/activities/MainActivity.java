package com.trackaty.chat.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.trackaty.chat.Fragments.MainFragmentDirections;
import com.trackaty.chat.R;
import com.trackaty.chat.ViewModels.MainActivityViewModel;
import com.trackaty.chat.models.User;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 123;

    private NavController navController ;
    private BottomNavigationView bottomNavigation;
    private BadgeDrawable chatsBadge, notificationsBadge;

    //public String currentUserId;
    public String currentUserName;
    public String currentUserEmail;
    public Uri currentUserPhoto;
    public Boolean currentUserVerified;
    private User mUser;
    private String mUserId;

    private boolean isFirstloaded; // boolean to check if back button is clicked on startActivityForResult
    //initialize the FirebaseAuth instance
    private FirebaseAuth  mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // currentUserId needs to be null at first load, to initiate observers for notifications counter and chats counter
    /*FirebaseUser FirebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    String currentUserId = FirebaseCurrentUser != null ? FirebaseCurrentUser.getUid() : null;*/
    private String currentUserId;

    private NavController.OnDestinationChangedListener mDestinationListener ;

    // [START declare_database_ref]
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUserRef;
    //private FirebaseDatabase database ;

    private DatabaseReference myConnectionsRef;
    private DatabaseReference connection;

    // Stores the timestamp of my last disconnect (the last time I was seen online)
    private DatabaseReference lastOnlineRef;

    private DatabaseReference connectedRef;//  = database.getReference(".info/connected");
    //private DatabaseReference connection;

    private MainActivityViewModel mMainViewModel;// ViewMode for getting the latest current user id
    private Intent intent;
    private  static final int RESULT_REQUEST_RECORD_AUDIO = 21; // for record audio permission
    private  static final String PERMISSION_RATIONALE_FRAGMENT = "permissionFragment";
    private Intent serviceIntent;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    goToMain();
                    return true;
                case R.id.navigation_chats:
                    goToChats();
                    return true;
                case R.id.navigation_notifications:
                    goToNotifications();
                    return true;
            }

            return false;
        }


    };

    // A listener for user's online statues
    private ValueEventListener onlineListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            Log.i(TAG, "onDataChange");
            if(snapshot.exists()){
                Log.i(TAG, "snapshot.exists()");
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) { // if user is connected
                    Log.i(TAG, "connected");
                    // Add this device to my connections list
                    // this value could contain info about the device or a timestamp too
                    //connection.setValue(Boolean.TRUE);
                    lastOnlineRef.setValue(0);

                    // When this device disconnects, remove it
                    //connection.onDisconnect().removeValue();

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
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.d(TAG, "MainActivity onCreate");

        navController = Navigation.findNavController(this, R.id.host_fragment);

        // update CurrentUserId for all observer fragments
        mMainViewModel = ViewModelProviders.of(MainActivity.this).get(MainActivityViewModel.class);

        // update CurrentUserId for all observer fragments
        //mMainViewModel.updateCurrentUserId(currentUserId);
        /*navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {

            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                Log.d(TAG, "destination Label= "+ destination.getLabel() );
            }
        });*/

        mDestinationListener = (new NavController.OnDestinationChangedListener() {

            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                Log.d(TAG, "destination Label= "+ destination.getLabel()+ " currentUserId="+ currentUserId);
                Log.d(TAG, "destination id= "+ destination.getId());

                if(TextUtils.equals("fragment_main", destination.getLabel())){
                   bottomNavigation.setVisibility(View.VISIBLE);
                    bottomNavigation.setSelectedItemId(R.id.navigation_home);
                }else if(TextUtils.equals("chats_fragment", destination.getLabel())){
                    bottomNavigation.setVisibility(View.VISIBLE);
                    bottomNavigation.setSelectedItemId(R.id.navigation_chats);
                }else if(TextUtils.equals("fragment_notification", destination.getLabel())) {
                    bottomNavigation.setVisibility(View.VISIBLE);
                    bottomNavigation.setSelectedItemId(R.id.navigation_notifications);
                }else{
                    bottomNavigation.setVisibility(View.GONE);
                }
            }
        });

        // [START initialize_database_ref]
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        bottomNavigation =  findViewById(R.id.navigation) ;
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //notificationsBadge  = bottomNavigation.getBadge(R.id.navigation_notifications);

        /*Menu menu = bottomNavigation.getMenu();
        MenuItem mItem =  menu.findItem(R.id.navigation_chats);
        mItem.getIcon()
        mChatsBadge = (NotificationBadge) findViewById(R.id.badge);*/
/*
        BottomNavigationMenuView mbottomNavigationMenuView =
                (BottomNavigationMenuView) bottomNavigation.getChildAt(0);

        View view = mbottomNavigationMenuView.getChildAt(1);

        BottomNavigationItemView itemView = (BottomNavigationItemView) view;*/

        /*View chat_badge = LayoutInflater.from(this)
                .inflate(R.layout.chat_alerts_layout,
                        mbottomNavigationMenuView, false);
        itemView.addView(chat_badge);

        Menu menu = bottomNavigation.getMenu();
        MenuItem mItem =  menu.findItem(R.id.navigation_chats);
        */

        mAuth = FirebaseAuth.getInstance();
        isFirstloaded = true; // first time to open the app

        //mMainViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        //initialize the AuthStateListener method
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                // User is signed in
                if (user != null) {
                    // If user is logged in, display bottomNavigation and ActionBar
                    // because it might be not showing due to previous log out
                    Log.d(TAG, "onAuthStateChanged: user is signed in. set bottomNavigation and ActionBar to visible");
                    if(navController != null && null != navController.getCurrentDestination()){
                        if(R.id.mainFragment == navController.getCurrentDestination().getId()){
                            bottomNavigation.setVisibility(View.VISIBLE);
                            if(getSupportActionBar() != null){
                                getSupportActionBar().show();
                            }
                        }
                    }

                    // Only update current userId if it's changed
                    if(!TextUtils.equals(currentUserId, user.getUid())){
                        // update CurrentUserId for all observer fragments
                        Log.d(TAG, "onAuthStateChanged: : user is changed. old user = " + currentUserId+ " new= "+user.getUid());
                        if(currentUserId == null){
                            // if currentUserId is null, it's the first time to open the app
                            // and the user is not logged in. initiateObserveChatCount();
                            initiateObserveChatCount(user.getUid());
                            initiateObserveNotificationCount(user.getUid());
                            Log.d(TAG, "onAuthStateChanged: first time to log in. user wasn't logged in. initiateObserveChatCount. oldCurrentUserId = " + currentUserId+ " new id= "+user.getUid());
                        }else{
                            // It's not the first time to open the app
                            // and the user is logged in. just updateCurrentUserId();
                            mMainViewModel.updateCurrentUserId(user.getUid());
                            Log.d(TAG, "onAuthStateChanged: second time to log in. user was logged in. updateCurrentUserId. oldCurrentUserId = " + currentUserId+ " new id= "+user.getUid());
                        }
                    }// End of checking if it's the same user or not

                    // set current user id, will be used when comparing new logged in id with the old one
                    Log.d(TAG, "onAuthStateChanged: oldCurrentUserId = " + currentUserId+ " new id= "+user.getUid());
                    currentUserId = user.getUid();

                    currentUserName = user.getDisplayName();
                    currentUserEmail = user.getEmail();
                    currentUserPhoto = user.getPhotoUrl();
                    currentUserVerified = user.isEmailVerified();

                  /*  Log.d(TAG, "onAuthStateChanged:signed_in: user userId " + currentUserId);
                    Log.d(TAG, "onAuthStateChanged:signed_in_getDisplayName:" + user.getDisplayName());
                    Log.d(TAG, "onAuthStateChanged:signed_in_getEmail():" + user.getEmail());
                    Log.d(TAG, "onAuthStateChanged:signed_in_getPhotoUrl():" + user.getPhotoUrl());
                    Log.d(TAG, "onAuthStateChanged:signed_in_emailVerified?:" + user.isEmailVerified());
*/
                    isUserExist(currentUserId); // if not start complete profile

                } else { // End of checking if it's the same user or not
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    // If user is logged out, hide bottomNavigation and ActionBar
                    // because we don't want to show it before displaying login activity
                    if(navController != null && null != navController.getCurrentDestination()){
                        if(R.id.mainFragment == navController.getCurrentDestination().getId()){
                            bottomNavigation.setVisibility(View.GONE);
                            if(getSupportActionBar() != null){
                                getSupportActionBar().hide();
                            }
                        }
                    }

                    // clear mUser object in case user will log in with another account
                    if(mUser != null ){
                        mUser = null;
                    }
                    goToMain();
                    // set selected bottomNavigation to main icon
                    //bottomNavigation.setSelectedItemId(R.id.navigation_home);
                    initiateLogin(); // start login activity

                    // Don't Remove MainViewModel Listeners, Listeners are needed if the new user was the last logged in user
                    // if Listeners are removed and new user is the same, we will not have Listeners for his counters
                    //mMainViewModel.clearViewModel();
                }
            }
        };

        //navController = Navigation.findNavController(this, R.id.host_fragment);
        Log.d(TAG, "onCreate handleDeepLink. notification intent = "+intent);
        navController.handleDeepLink(intent);

        /*if(currentUserId != null){
            initiateObserveChatCount(currentUserId);
            initiateObserveNotificationCount(currentUserId);
        }*/

    }//End of onCreate

    // start observation for Notifications count
    private void initiateObserveNotificationCount(String userKey) {
        // Get counts for unread chats. first use currentUserId then update it whenever it changed using AuthStateListener
        if(userKey != null){ // in case user is logged out, don't get notification count
            // initiate notifications count observer
            mMainViewModel.getNotificationsCount(userKey).observe(this, new Observer<Long>() {
                @Override
                public void onChanged(Long count) {
                    Log.d(TAG, "getNotificationsCount onChanged notifications count = "+ count + " currentUserId= "+userKey);
                    // Display chats count if > 0
                    if(count != null && count != 0){
                        notificationsBadge = bottomNavigation.getOrCreateBadge(R.id.navigation_notifications); //showBadge() show badge over chats menu item
                        notificationsBadge.setMaxCharacterCount(3); // Max number is 99
                        //chatsBadge.setBackgroundColor(R.drawable.badge_background_shadow);
                        notificationsBadge.setBackgroundColor(getResources().getColor(R.color.color_primary));
                        notificationsBadge.setBadgeTextColor(getResources().getColor(R.color.color_on_primary));
                        notificationsBadge.setNumber(count.intValue());
                        // To show badge again if it was invisible due to being 0
                        notificationsBadge.setVisible(true);
                        // Display cut icon when notifications' count is more than 0
                        bottomNavigation.getMenu().getItem(2).setIcon(R.drawable.ic_notifications_outline_cut);
                    }else{
                        // Hide chat badge. check first if it's null or not
                        if(notificationsBadge != null){
                            notificationsBadge.setNumber(0);
                            notificationsBadge.setVisible(false);
                        }

                        // Display normal icon because there is no notifications
                        bottomNavigation.getMenu().getItem(2).setIcon(R.drawable.ic_notifications_outline);
                    }
                }
            });
        }
    }

    // start observation for chats count
    private void initiateObserveChatCount(String userKey) {
        // Get counts for unread chats. first use currentUserId then update it whenever it changed using AuthStateListener
        if(userKey != null){ // in case user is logged out, don't get chat count
            // initiate chats count observer
            mMainViewModel.getChatsCount(userKey).observe(this, new Observer<Long>() {
                @Override
                public void onChanged(Long count) {
                    Log.d(TAG, "getChatsCount onChanged chats count = "+ count + " currentUserId= "+userKey);
                    // Display chats count if > 0
                    if(count != null && count != 0){
                        chatsBadge = bottomNavigation.getOrCreateBadge(R.id.navigation_chats); // showBadge() show badge over chats menu item
                        chatsBadge.setMaxCharacterCount(3); // Max number is 99
                        //chatsBadge.setBackgroundColor(R.drawable.badge_background_shadow);
                        chatsBadge.setBackgroundColor(getResources().getColor(R.color.color_primary));
                        chatsBadge.setBadgeTextColor(getResources().getColor(R.color.color_on_primary));
                        chatsBadge.setNumber(count.intValue());
                        // To show badge again if it was invisible due to being 0
                        chatsBadge.setVisible(true);

                        // Display cut icon when chats count is more than 0
                        bottomNavigation.getMenu().getItem(1).setIcon(R.drawable.ic_chat_outline_cut);
                    }else{
                        // Hide chat badge. check first if it's null or not
                        if(chatsBadge != null){
                            chatsBadge.setNumber(0);
                            chatsBadge.setVisible(false);
                        }
                        // Display normal icon because there is no chats
                        bottomNavigation.getMenu().getItem(1).setIcon(R.drawable.ic_chat_outline);
                    }
                }
            });
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.intent = intent;
        Log.d(TAG, "onNewIntent. notification intent = "+intent);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Log.d(TAG, "onNewIntent. notification intent = "+intent);
        }

        navController.handleDeepLink(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            /*case 1:
                Log.d(TAG, "MenuItem search is clicked");
                if(!isPermissionGranted()){
                    requestPermission();
                }else{
                    startStopSearchService();
                }

                break;*/
            case R.id.action_profile:
                Log.d(TAG, "MenuItem = 0");
                goToProfile();
                break;
            /*case R.id.action_menu_invite:
                Log.d(TAG, "MenuItem = 1  INVITE clicked ");
                break;*/
            case R.id.action_settings:
                Log.d(TAG, "MenuItem = 2  settings clicked");
                goToSettings();
                break;
            case R.id.action_log_out:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG, "MenuItem = 3");
                            }
                        });
                /*FirebaseAuth.getInstance().signOut(); // logout firebase user
                LoginManager.getInstance().logOut();// logout from facebook too
                Twitter.logOut(); // logout from twitter too
                // Google sign out
               Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                //updateUI(null);
                                Log.d(TAG, "Google sign out succeeded");
                            }
                        });*/
                // finish all activities and fragments to start fresh
                //finishAffinity();
                //finish();
                break;
            /*case R.id.action_menu_home:
                Log.d(TAG, "MenuItem = 4  home clicked ");
                goToMain();
                break;*/
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStart() {
        super.onStart();
        if(isFirstloaded){ // only add the Listener when loaded for the first time
            mAuth.addAuthStateListener(mAuthListener);
            Log.d(TAG, "onStart mAuthListener added");
        }
        Log.d(TAG, "MainActivity onStart");
        Log.d(TAG, "mAuthListener="+ mAuthListener);

        //add Listener for destination changes
        navController.addOnDestinationChangedListener(mDestinationListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "MainActivity onStop");

        // Set time for last time online when activity stops
        if(null != lastOnlineRef){
            // Only update last online time when it's not null
            // if lastOnlineRef is null it means the user is new and not recorded on database yet
            lastOnlineRef.setValue(ServerValue.TIMESTAMP);
            Log.d(TAG, "lastOnlineRef is not null");
        }

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

        if (mDestinationListener != null) {
            //remove the Listener for destination changes
            navController.removeOnDestinationChangedListener(mDestinationListener);
        }

    }

    @Override
    public void onPause () {
        super.onPause ();
        Log.d(TAG, "MainActivity onPause");
    }

    @Override
    public void onDestroy () {
        super.onDestroy ();
        Log.d(TAG, "MainActivity onDestroy");
        if(null != onlineListener && null != connectedRef){
            // Remove onlineListener
            connectedRef.removeEventListener(onlineListener);
            Log.d(TAG, "Remove connectedRef onlineListener");
        }
    }

    /*@Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        //showMessage(getString(R.string.google_play_services_error));
        Toast.makeText(MainActivity.this, getString(R.string.google_play_services_error),
                Toast.LENGTH_LONG).show();
        // Sending failed or it was canceled
    }*/

    private void initiateLogin() {

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()
                //new AuthUI.IdpConfig.GoogleBuilder().build(),
                //new AuthUI.IdpConfig.TwitterBuilder().build(),
                //new AuthUI.IdpConfig.FacebookBuilder().build()

        );
        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.mipmap.ic_launcher)      // Set logo drawable
                        .setAlwaysShowSignInMethodScreen(true)
                        .setTheme(R.style.Background_FirebaseUI)      // Set theme
                        .setTosAndPrivacyPolicyUrls("https://sites.google.com/view/basbes/terms-of-service","https://sites.google.com/view/basbes/privacy-policy")
                        .build(),
                RC_SIGN_IN);

        isFirstloaded = false;

    }

    //Activity result after user selects a provider he wants to use
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            Log.d(TAG, "requestCode ok:" + requestCode);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                Log.d(TAG, "Sign in successfully:" + response);
                isFirstloaded = true; // to add the Listener because it won't be added Automatically on onStart
                mAuth.addAuthStateListener(mAuthListener); //
                //finish();
            } else {
                // Sign in failed, check response for error code
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    //Toast.makeText(MainActivity.this, getString(R.string.sign_in_cancelled), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Sign in has been cancelled. response is null");
                    if(!isFirstloaded){
                        finish();
                    }
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Log.d(TAG, "No internet connection:" + response);

                    Toast.makeText(MainActivity.this, getString(R.string.no_internet_connection),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                Log.d(TAG, "Unknown error occurred:" + response);

                Toast.makeText(MainActivity.this, getString(R.string.unknown_error),
                        Toast.LENGTH_LONG).show();

                Log.e(TAG, "Sign-in error: ", response.getError());
            }
        }
    }

    private void isUserExist(String currentUserId) {

        // Read from the database just once
        Log.d(TAG, "currentUserId Value is: " + currentUserId);
        mUserRef = mDatabaseRef.child("users").child(currentUserId);

// [START single_value_read]
        //ValueEventListener postListener = new ValueEventListener() {
        //mUserRef.addValueEventListener(postListener);
        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // [START_EXCLUDE]
                if (dataSnapshot.exists()) {
                    // Get user value
                    mUser = dataSnapshot.getValue(User.class);
                    mUserId = dataSnapshot.getKey();
                    /*String userName = dataSnapshot.child("name").getValue().toString();
                    String currentUserId = dataSnapshot.getKey();*/

                    // If name or avatar is empty go to goToCompleteProfile
                    if (null == mUser.getName() || null == mUser.getAvatar()) {
                        Log.d(TAG, "user exist: Name=" + mUser.getName());
                        goToCompleteProfile();
                        //return;
                    }

                    // Don't update tokens or lastOnline unless user exist, that's why references are moved here
                    // database references for online
                    myConnectionsRef = mDatabaseRef.child("users").child(currentUserId).child("connections");
                    lastOnlineRef  = mDatabaseRef.child("users").child(currentUserId).child("lastOnline");

                    // database reference that holds information about user presence
                    connectedRef  = FirebaseDatabase.getInstance().getReference(".info/connected");

                    // To store all connections from all devices, Add this device to my connections list
                    if(connection == null){
                        connection = myConnectionsRef.push();
                    }
                    // add lineListener for online statues
                    connectedRef.addValueEventListener(onlineListener);

                    // Set user's notification tokens
                    FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                @Override
                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                    if (!task.isSuccessful()) {
                                        Log.w(TAG, "getInstanceId failed", task.getException());
                                        return;
                                    }

                                    if(null != task.getResult()){
                                        // Get new Instance ID token
                                        String token = task.getResult().getToken();
                                        //mTokensRef.child(mUserId).child(token).setValue(true);
                                        mUserRef.child("tokens").child(token).setValue(true);
                                    }
                                }
                            });
                    // End of set user's notification tokens
                } else {
                    // User is null, error out
                    Log.w(TAG, "User is null, no such user");
                    //goToCompleteProfile(currentUserName, currentUserEmail);
                    // Make all presence references null, In case user logout from existing account
                    // then creates a new account that is not exist on the database yet.
                    myConnectionsRef = null;
                    lastOnlineRef  = null;
                    connectedRef  = null;
                    goToCompleteProfile();
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
    }

    private void goToCompleteProfile() {

        NavDirections direction = MainFragmentDirections.actionMainToCompleteProfile();
        //NavController navController = Navigation.findNavController(this, R.id.host_fragment);

        //check if we are on Main Fragment not on complete Profile already
        if (null != navController.getCurrentDestination() && R.id.mainFragment == navController.getCurrentDestination().getId()) {
            //navController.navigate(R.id.completeProfileFragment);
            //Navigation.findNavController(this, R.id.host_fragment).navigate(direction);
            // Must use direction to get the benefits of pop stack
            navController.navigate(direction);
        }

    }

    private void goToProfile() {

        if (null != navController.getCurrentDestination() && R.id.profileFragment != navController.getCurrentDestination().getId()) {
            navController.navigate(R.id.profileFragment);
        }

        /*if(user != null){
            Log.i(TAG, "UserId not null");

            NavDirections directions = MainFragmentDirections.actionMainToProfile(userId, user);
            //NavController navController = Navigation.findNavController(this, R.id.host_fragment);

            //check if we are on Main Fragment not on complete Profile already
            if (R.id.mainFragment == navController.getCurrentDestination().getId()) {
                navController.navigate(directions);

                *//*Navigation.findNavController(this, R.id.host_fragment)
                        .navigate(directions);*//*
            }

        }*/
    }

    // Go to Chats fragment
    private void goToChats() {

       /* NavDirections directions = MainFragmentDirections.actionMainFragtToChatsFrag();
        //NavController navController = Navigation.findNavController(this, R.id.host_fragment);

        //check if we are not on chat fragment already
        if (R.id.chatsFragment != navController.getCurrentDestination().getId()) {
            navController.navigate(directions);
        }*/

        //Navigation.findNavController(this, R.id.host_fragment).navigate(R.id.chatsFragment);
        if (null != navController.getCurrentDestination() && R.id.chatsFragment != navController.getCurrentDestination().getId()) {
            navController.navigate(R.id.chatsFragment);
        }
                /*Navigation.findNavController(this, R.id.host_fragment)
                        .navigate(directions);*/

    }

    // Go to Chats fragment
    private void goToMain() {

       /* NavDirections directions = MainFragmentDirections.actionMainFragtToChatsFrag();
        //NavController navController = Navigation.findNavController(this, R.id.host_fragment);

        //check if we are not on chat fragment already
        if (R.id.chatsFragment != navController.getCurrentDestination().getId()) {
            navController.navigate(directions);
        }*/

        //Navigation.findNavController(this, R.id.host_fragment).navigate(R.id.mainFragment);
        if (null != navController.getCurrentDestination() && R.id.mainFragment != navController.getCurrentDestination().getId()) {
            navController.navigate(R.id.mainFragment);
        }

        // change selected bottomNavigation to main

                /*Navigation.findNavController(this, R.id.host_fragment)
                        .navigate(directions);*/

    }

    // Go to Settings fragment
    private void goToSettings() {

        //Navigation.findNavController(this, R.id.host_fragment).navigate(R.id.mainFragment);
        if (null != navController.getCurrentDestination() && R.id.settingsFragment != navController.getCurrentDestination().getId()) {
            navController.navigate(R.id.settingsFragment);
        }

    }

    // Go to notifications fragment
    private void goToNotifications() {

        //Navigation.findNavController(this, R.id.host_fragment).navigate(R.id.mainFragment);
        if (null != navController.getCurrentDestination() && R.id.notificationsFragment != navController.getCurrentDestination().getId()) {
            navController.navigate(R.id.notificationsFragment);
        }
    }

}
