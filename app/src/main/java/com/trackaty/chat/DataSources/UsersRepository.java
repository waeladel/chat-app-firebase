package com.trackaty.chat.DataSources;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.trackaty.chat.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;

public class UsersRepository {

    private final static String TAG = UsersRepository.class.getSimpleName();

    // [START declare_database_ref]
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUsersRef;
    //List<User> usersList ;
    //List<User> entireUsersList ;
    private Boolean isFirstLoaded = true;
    public ValueEventListener usersChangesListener;

    //initialize the FirebaseAuth instance
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private LiveData<String> currentUserId;
    //private LiveData<User> currentUser;
    private MutableLiveData<User>currentUser;

    // HashMap to keep track of Firebase Listeners
    //private ValueEventListener currentUserListener;
    private HashMap<DatabaseReference, ValueEventListener> mListenersMap;
    // a listener for currentUser changes
    private ValueEventListener currentUserListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI
            if (dataSnapshot.exists()) {
                // Get user value
                Log.d(TAG, "getCurrentUser dataSnapshot key: "
                        + dataSnapshot.getKey()+" Listener = "+currentUserListener);
                //currentUser = dataSnapshot.getValue(User.class);
                currentUser.postValue(dataSnapshot.getValue(User.class));
            } else {
                // User is null, error out
                Log.w(TAG, "User is null, no such user");
            }
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());

        }
    };


    public UsersRepository(){
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mUsersRef = mDatabaseRef.child("users");
        //usersList = new ArrayList<>();
        //entireUsersList = new ArrayList<>();
        isFirstLoaded = true;
        currentUser = new MutableLiveData<>();
        mListenersMap =  new HashMap<>();
        Log.d(TAG, "mama UsersRepository init. isFirstLoaded= " + isFirstLoaded);

    }


    // get initial data
    public void getUsers(Long initialKey, final int size,
                         @NonNull final ItemKeyedDataSource.LoadInitialCallback<User> callback){
        //DatabaseReference usersRef = mDatabaseRef.child("users");
        if(initialKey == null){// if it's loaded for the first time. Key is null
            Log.d(TAG, "mama getMessages initialKey= " +  initialKey);
            mUsersRef.orderByChild("created")
                    .limitToFirst(size).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // [START_EXCLUDE]
                    if (dataSnapshot.exists()) {
                        // loop throw users value
                        List<User> usersList = new ArrayList<>();
                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()){
                            User user = userSnapshot.getValue(User.class);
                            //Map<String, Object> user = userSnapshot.getValue().

                            if (user != null) {
                                user.setKey(userSnapshot.getKey());
                            }
                            usersList.add(user);
                            Log.d(TAG, "mama getMessages dataSnapshot. getSnapshotKey= " +  userSnapshot.getKey());
                        }

                        if(usersList.size() == 0){
                            return;
                        }

                        Log.d(TAG, "mama getMessages usersList.size= " +  usersList.size()+ " lastkey= "+usersList.get(usersList.size()-1).getCreatedLong());

                        if(callback instanceof ItemKeyedDataSource.LoadInitialCallback){

                            //initial load
                        /*((ItemKeyedDataSource.LoadInitialCallback)callback)
                                .onResult(usersList, 0, 14);*/
                            callback.onResult(usersList);
                        }
                    /*else{
                        //next pages load
                        //callback.onResult(usersList);
                        //Log.d(TAG, "mama load next" +  usersList.get(0).getName());
                    }*/
                    } else {
                        Log.w(TAG, "mama getMessages no users exist");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                }
            });
        }else{// not the first load. Key is the last seen key
            Log.d(TAG, "mama getMessages initialKey= " +  initialKey);
            mUsersRef.orderByChild("created")
                    .startAt(initialKey)
                    .limitToFirst(size).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // [START_EXCLUDE]
                    if (dataSnapshot.exists()) {
                        // loop throw users value
                        List<User> usersList = new ArrayList<>();
                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()){
                            User user = userSnapshot.getValue(User.class);
                            if (user != null) {
                                user.setKey(userSnapshot.getKey());
                            }
                            usersList.add(user);
                            Log.d(TAG, "mama getMessages dataSnapshot. getSnapshotKey= " +  userSnapshot.getKey());
                        }

                        if(usersList.size() == 0){
                            return;
                        }

                        Log.d(TAG, "mama getMessages usersList.size= " +  usersList.size()+ " lastkey= "+usersList.get(usersList.size()-1).getCreatedLong());

                        if(callback instanceof ItemKeyedDataSource.LoadInitialCallback){

                            //initial load
                        /*((ItemKeyedDataSource.LoadInitialCallback)callback)
                                .onResult(usersList, 0, 14);*/
                            callback.onResult(usersList);
                        }
                    /*else{
                        //next pages load
                        //callback.onResult(usersList);
                        //Log.d(TAG, "mama load next" +  usersList.get(0).getName());
                    }*/
                    } else {
                        Log.w(TAG, "mama getMessages no users exist");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                }
            });
        }

         //mUsersRef.addValueEventListener(usersListener);

    }

    // to get next data
    public void getUsersAfter(final Long key, final int size,
                         @NonNull final ItemKeyedDataSource.LoadCallback<User> callback){
        /*if(key == entireUsersList.get(entireUsersList.size()-1).getCreatedLong()){
            Log.d(TAG, "mama getUsersAfter init. afterKey= " +  key+ "entireUsersList= "+entireUsersList.get(entireUsersList.size()-1).getCreatedLong());
            return;
        }*/

        Log.d(TAG, "mama getUsersAfter. AfterKey= " + key);
        mUsersRef.orderByChild("created")
                .startAt(key)
                .limitToFirst(size).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // [START_EXCLUDE]
                if (dataSnapshot.exists()) {
                    // loop throw users value
                    List<User> usersList = new ArrayList<>();
                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren()){
                        User user = userSnapshot.getValue(User.class);
                        if (user != null) {
                            user.setKey(userSnapshot.getKey());
                        }
                        usersList.add(user);
                        Log.d(TAG, "mama getUsersAfter dataSnapshot. getSnapshotKey= " +  userSnapshot.getKey());
                    }

                    if(usersList.size() == 0){
                        return;
                    }

                    Log.d(TAG, "mama getUsersAfter usersList.size= " +  usersList.size()+ "lastkey= "+usersList.get(usersList.size()-1).getCreatedLong());

                    if(callback instanceof ItemKeyedDataSource.LoadCallback){

                        //initial After
                        callback.onResult(usersList);
                        /*((ItemKeyedDataSource.LoadCallback)callback)
                                .onResult(usersList);*/
                    }

                } else {
                    Log.w(TAG, "mama getUsersAfter no users exist");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
        //mUsersRef.addValueEventListener(usersListener);
    }

    // to get previous data
    public void getUsersBefore(final Long key, final int size,
                              @NonNull final ItemKeyedDataSource.LoadCallback<User> callback){
        Log.d(TAG, "mama getUsersBefore. BeforeKey= " +  key);

        /*if(key == entireUsersList.get(0).getCreatedLong()){
            return;
        }*/
        mUsersRef.orderByChild("created")
                .endAt(key)
                .limitToLast(size).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // [START_EXCLUDE]
                if (dataSnapshot.exists()) {
                    // loop throw users value
                    List<User> usersList = new ArrayList<>();
                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren()){
                        User user = userSnapshot.getValue(User.class);
                        if (user != null) {
                            user.setKey(userSnapshot.getKey());
                        }
                        usersList.add(user);
                        Log.d(TAG, "mama getUsersBefore dataSnapshot. getSnapshotKeys= " +  userSnapshot.getKey());
                    }

                    if(usersList.size() == 0){
                        return;
                    }

                    Log.d(TAG, "mama getUsersBefore usersList.size= " +  usersList.size()+ "lastkey= "+usersList.get(usersList.size()-1).getCreatedLong());


                    if(callback instanceof ItemKeyedDataSource.LoadCallback){

                        //initial before
                        callback.onResult(usersList);
                        /*((ItemKeyedDataSource.LoadCallback)callback)
                                .onResult(usersList);*/
                    }
                    //initial load
                       /* ((ItemKeyedDataSource.LoadCallback)callback)
                                .onResult(usersList, 0, usersList.size());*/
                } else {
                    Log.w(TAG, "mama getUsersBefore no users exist");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
        //mUsersRef.addValueEventListener(usersListener);


    }

    /*public PublishSubject getUsersChangeSubject() {
        return userAdapterInvalidation;
    }*/

    // to invalidate the data whenever a change happen
    public void usersChanged(final DataSource.InvalidatedCallback InvalidatedCallback) {

        final Query query = mUsersRef.orderByChild("created");

        usersChangesListener  = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!isFirstLoaded){
                    isFirstLoaded = true;
                    Log.d(TAG, "mama entireUsersList Invalidated:");
                    // Remove post value event listener
                    if (usersChangesListener != null) {
                        query.removeEventListener(usersChangesListener);
                        Log.d(TAG, "mama usersChanged Invalidated removeEventListener");
                    }
                    ((ItemKeyedDataSource.InvalidatedCallback)InvalidatedCallback).onInvalidated();
                    //UsersDataSource.InvalidatedCallback.class.getMethod("loadInitial", "LoadInitialParams");
                    //UsersDataSource.invalidate();
                }

                isFirstLoaded =  false;
                /*if(entireUsersList.size() > 0){
                    entireUsersList.clear();
                    ((ItemKeyedDataSource.InvalidatedCallback)onInvalidatedCallback).onInvalidated();
                    Log.d(TAG, "mama entireUsersList Invalidated:");
                    return;
                }

                if (dataSnapshot.exists()) {
                    // loop throw users value
                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren()){
                        entireUsersList.add(userSnapshot.getValue(User.class));
                    }

                    Log.d(TAG, "mama entireUsersList size= "+entireUsersList.size()+"dataSnapshot count= "+dataSnapshot.getChildrenCount());

                } else {
                    Log.w(TAG, "mama usersChanged no users exist");
                }*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        query.addValueEventListener(usersChangesListener);
        //mUsersRef.addValueEventListener(eventListener);
    }

    /*public Single<List<User>> getAnimals(int count){
        return RxFirebaseDatabase.data(mUsersRef.orderByKey().limitToFirst(count)).ma

                .map {
            for ArrayValue
            User.getArrayValue(User.class);
        }
    }*/

    /*public MutableLiveData<User> getCurrentUser(String userId){

        DatabaseReference currentUserRef = mUsersRef.child(userId);
        //final MutableLiveData<User> currentUser = new MutableLiveData<>();
        Log.d(TAG, "getCurrentUser initiated: " + userId);

        //mListenersMap.put(postSnapshot.getRef(), currentUserListener);
        currentUserRef.addValueEventListener(currentUserListener);
        mListenersMap.put(currentUserRef, currentUserListener);

        for (Map.Entry<DatabaseReference, ValueEventListener> entry : mListenersMap.entrySet()) {
            DatabaseReference ref = entry.getKey();
            ValueEventListener listener = entry.getValue();
            Log.d(TAG, "Map getCurrentUser. ref= " + ref+ " listener= "+listener);
            //ref.removeEventListener(listener);
        }

        return currentUser;
    }*/

 }

