package com.trackaty.chat.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.trackaty.chat.Fragments.MainFragmentDirections;
import com.trackaty.chat.R;
import com.trackaty.chat.ViewModels.MainActivityViewModel;
import com.trackaty.chat.models.User;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private final static String TAG = MainActivity.class.getSimpleName();
    private TextView mTextMessage;
    private static final int RC_SIGN_IN = 123;

    NavController navController ;
    BottomNavigationView bottomNavigation;

    public String currentUserId;
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


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    goToMain();
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    goToChats();
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.d(TAG, "MainActivity onCreate");

        navController = Navigation.findNavController(this, R.id.host_fragment);

        // update CurrentUserId for all observer fragments
        mMainViewModel = ViewModelProviders.of(MainActivity.this).get(MainActivityViewModel.class);

        /*navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {

            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                Log.d(TAG, "destination Label= "+ destination.getLabel() );
            }
        });*/

        mDestinationListener = (new NavController.OnDestinationChangedListener() {

            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                Log.d(TAG, "destination Label= "+ destination.getLabel());
                Log.d(TAG, "destination id= "+ destination.getId());


                if("fragment_main".equals(destination.getLabel())){
                   bottomNavigation.setVisibility(View.VISIBLE);
                }else if(("chats_fragment".equals(destination.getLabel()))){
                    bottomNavigation.setVisibility(View.VISIBLE);
                }else{
                    bottomNavigation.setVisibility(View.GONE);
                }
            }
        });

        // [START initialize_database_ref]
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        mTextMessage = (TextView) findViewById(R.id.last_message);

        bottomNavigation = (BottomNavigationView) findViewById(R.id.navigation) ;
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        mAuth = FirebaseAuth.getInstance();
        isFirstloaded = true; // first time to open the app

        //mMainViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        //initialize the AuthStateListener method
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in

                    currentUserId = user.getUid();
                    currentUserName = user.getDisplayName();
                    currentUserEmail = user.getEmail();
                    currentUserPhoto = user.getPhotoUrl();
                    currentUserVerified = user.isEmailVerified();

                    Log.d(TAG, "onAuthStateChanged:signed_in: user userId " + currentUserId);
                    Log.d(TAG, "onAuthStateChanged:signed_in_getDisplayName:" + user.getDisplayName());
                    Log.d(TAG, "onAuthStateChanged:signed_in_getEmail():" + user.getEmail());
                    Log.d(TAG, "onAuthStateChanged:signed_in_getPhotoUrl():" + user.getPhotoUrl());
                    Log.d(TAG, "onAuthStateChanged:signed_in_emailVerified?:" + user.isEmailVerified());

                    isUserExist(currentUserId); // if not start complete profile

                    // update CurrentUserId for all observer fragments
                    mMainViewModel.updateCurrentUserId(currentUserId);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    // clear mUser object in case user will log in with another account
                    if(mUser != null ){
                        mUser = null;
                    }
                    goToMain();
                    // set selected bottomNavigation to main icon
                    bottomNavigation.setSelectedItemId(R.id.navigation_home);
                    initiateLogin(); // start login activity
                }
            }
        };

    }//End of onCreate


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
            case R.id.action_profile:
                Log.d(TAG, "MenuItem = 0");
                goToProfile(currentUserId, mUserId, mUser);
                break;
            case R.id.action_menu_invite:
                Log.d(TAG, "MenuItem = 1  INVITE clicked ");
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
            case android.R.id.home:
                onBackPressed();
                break;
        }

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/
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
            lastOnlineRef.setValue(ServerValue.TIMESTAMP);
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
        if(null != onlineListener){
            // Remove onlineListener
            connectedRef.removeEventListener(onlineListener);
            Log.d(TAG, "Remove onlineListener");
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        //showMessage(getString(R.string.google_play_services_error));
        Toast.makeText(MainActivity.this, getString(R.string.google_play_services_error),
                Toast.LENGTH_LONG).show();
        // Sending failed or it was canceled
    }

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
                        //.setTheme(R.style.Background_FirebaseUI)      // Set theme
                        .setTosAndPrivacyPolicyUrls("https://sites.google.com/view/pray-4-mo/home","https://sites.google.com/view/pray-4-mo/home")
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
                    Toast.makeText(MainActivity.this, getString(R.string.sign_in_cancelled),
                            Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Sign in has been cancelled:" + response);
                    if(!isFirstloaded){
                        finish();
                    }
                    return;
                }

                if (ErrorCodes.NO_NETWORK == response.getError().getErrorCode()) {
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

        // database references for online
        myConnectionsRef = mDatabaseRef.child("users").child(currentUserId).child("connections");
        lastOnlineRef  = mDatabaseRef.child("users").child(currentUserId).child("lastOnline");

        // database reference that holds information about user presence
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
                    mUser = dataSnapshot.getValue(User.class);
                    mUserId = dataSnapshot.getKey();
                    /*String userName = dataSnapshot.child("name").getValue().toString();
                    String currentUserId = dataSnapshot.getKey();*/
                    if (mUser != null) {
                        Log.d(TAG, "user exist: Name=" + mUser.getName());
                    }
                    // To store all connections from all devices, Add this device to my connections list
                    if(connection == null){
                        connection = myConnectionsRef.push();
                    }
                    // add lineListener for online statues
                    connectedRef.addValueEventListener(onlineListener);
                } else {
                    // User is null, error out
                    Log.w(TAG, "User is null, no such user");
                    //completeProfile(currentUserName, currentUserEmail);
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
    }

    private void completeProfile( String UserName, String UserEmail) {

        NavDirections directions = MainFragmentDirections.actionMainToCompleteProfile(UserName,UserEmail);

        //NavController navController = Navigation.findNavController(this, R.id.host_fragment);

        //check if we are on Main Fragment not on complete Profile already
        if (R.id.mainFragment == navController.getCurrentDestination().getId()) {
            Navigation.findNavController(this, R.id.host_fragment)
                    .navigate(directions);
        }

    }

    private void goToProfile(String currentUserId,String userId,  User user) {
        if(currentUserId != null && !TextUtils.isEmpty(currentUserId) && user != null){
            Log.i(TAG, "UserId not null");

            NavDirections directions = MainFragmentDirections.actionMainToProfile(userId, user);
            //NavController navController = Navigation.findNavController(this, R.id.host_fragment);

            //check if we are on Main Fragment not on complete Profile already
            if (R.id.mainFragment == navController.getCurrentDestination().getId()) {
                navController.navigate(directions);

                /*Navigation.findNavController(this, R.id.host_fragment)
                        .navigate(directions);*/
            }

        }
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
        navController.navigate(R.id.chatsFragment);
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
        navController.navigate(R.id.mainFragment);
        // change selected bottomNavigation to main

                /*Navigation.findNavController(this, R.id.host_fragment)
                        .navigate(directions);*/

    }

}
