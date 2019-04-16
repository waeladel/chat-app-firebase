package com.trackaty.chat.DataSources;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.trackaty.chat.models.FirebaseListeners;
import com.trackaty.chat.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    private String initialKey;
    private String afterKey;
    private String beforeKey;

    private static volatile Boolean isInitialFirstLoaded;// = true;
    private static volatile Boolean isAfterFirstLoaded;// = true;
    private static volatile Boolean isBeforeFirstLoaded;// = true;

    private DataSource.InvalidatedCallback invalidatedCallback;
    private ItemKeyedDataSource.LoadInitialCallback loadInitialCallback;
    private ItemKeyedDataSource.LoadCallback loadAfterCallback;
    private ItemKeyedDataSource.LoadCallback loadBeforeCallback;

    private static List<FirebaseListeners> mListenersList;// = new ArrayList<>();

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

    private ValueEventListener afterListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // [START_EXCLUDE]
            Log.d(TAG, "start onDataChange isAfterFirstLoaded = "+ isAfterFirstLoaded);
            if (!isAfterFirstLoaded){
                // Remove post value event listener
                removeListeners();
                Log.d(TAG, "mama getUsersAfter Invalidated removeEventListener");
                //isAfterFirstLoaded =  true;
                Log.d(TAG, "getUsersAfter onInvalidated(). isAfterFirstLoaded = "+ isAfterFirstLoaded);
                invalidatedCallback.onInvalidated();
                //UsersDataSource.InvalidatedCallback.class.getMethod("loadInitial", "LoadInitialParams");
                //UsersDataSource.invalidate();
                return;
            }

            if (dataSnapshot.exists()) {
                List<User> UsersList = new ArrayList<>();
                // loop throw users value
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Log.d(TAG, "getUsersAfter dataSnapshot loop= " +  snapshot.getKey());
                    if(!getLoadAfterKey().equals(snapshot.getKey())) { // if snapshot key = startAt key? don't add it again
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            user.setKey(snapshot.getKey());
                            Log.d(TAG, "getUsersAfter UsersList loop= " +  user.getKey());
                        }
                        UsersList.add(user);
                        //Log.d(TAG, "mama getMessage = "+ message.getMessage()+" getSnapshotKey= " +  snapshot.getKey());
                    }
                }

                if(UsersList.size() != 0){
                    //callback.onResult(messagesList);
                    getLoadAfterCallback().onResult(UsersList);
                    Log.d(TAG, "mama getUsersAfter  List.size= " +  UsersList.size()+ " lastkey= "+UsersList.get(UsersList.size()-1).getKey());
                }
            } else {
                // no data
                Log.w(TAG, "mama getUsersAfter no users exist");
            }
            getListeners();
            isAfterFirstLoaded =  false;
            Log.d(TAG, "getUsersAfter: end isAfterFirstLoaded = "+ isAfterFirstLoaded);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "mama getUsersAfter loadPost:onCancelled", databaseError.toException());
        }
    };

    private ValueEventListener beforeListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // [START_EXCLUDE]
            Log.d(TAG, "start onDataChange isBeforeFirstLoaded = "+ isBeforeFirstLoaded);
            if (!isBeforeFirstLoaded){
                // Remove post value event listener
                removeListeners();
                Log.d(TAG, "mama getUsersBefore Invalidated removeEventListener");
                //isBeforeFirstLoaded =  true;
                Log.d(TAG, "getUsersBefore onInvalidated(). isBeforeFirstLoaded = "+ isBeforeFirstLoaded);
                invalidatedCallback.onInvalidated();
                //UsersDataSource.InvalidatedCallback.class.getMethod("loadInitial", "LoadInitialParams");
                //UsersDataSource.invalidate();
                return;
            }

            if (dataSnapshot.exists()) {
                List<User> usersList = new ArrayList<>();
                // loop throw users value
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Log.d(TAG, "getUsersBefore dataSnapshot loop= " +  snapshot.getKey());
                    if(!getLoadBeforeKey().equals(snapshot.getKey())) { // if snapshot key = startAt key? don't add it again
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            user.setKey(snapshot.getKey());
                            Log.d(TAG, "getUsersBefore usersList loop= " +  user.getKey());
                        }
                        usersList.add(user);
                        //Log.d(TAG, "mama getUsersBefore = "+ message.getMessage()+" getSnapshotKey= " +  snapshot.getKey());
                    }
                }

                if(usersList.size() != 0){
                    //callback.onResult(messagesList);
                    getLoadBeforeCallback().onResult(usersList);
                    Log.d(TAG, "mama getUsersBefore  List.size= " +  usersList.size()+ " lastkey= "+usersList.get(0).getKey());
                }
            } else {
                // no data
                Log.w(TAG, "mama getUsersBefore no users exist");
            }
            getListeners();
            isBeforeFirstLoaded =  false;
            Log.d(TAG, "getUsersBefore: end isBeforeFirstLoaded = "+ isBeforeFirstLoaded);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "mama getUsersBefore:onCancelled", databaseError.toException());
        }
    };



    public UsersRepository(@NonNull DataSource.InvalidatedCallback onInvalidatedCallback){
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mUsersRef = mDatabaseRef.child("users");
        //usersList = new ArrayList<>();
        //entireUsersList = new ArrayList<>();
        isFirstLoaded = true;
        currentUser = new MutableLiveData<>();

        this.invalidatedCallback = onInvalidatedCallback;

        isInitialFirstLoaded =  true;
        isAfterFirstLoaded = true;
        isBeforeFirstLoaded = true;

        Log.d(TAG, "mama UsersRepository init. isInitialFirstLoaded= " + isInitialFirstLoaded+ " after= "+isAfterFirstLoaded + " before= "+isBeforeFirstLoaded);

        if(mListenersList == null){
            mListenersList = new ArrayList<>();
            Log.d(TAG, "mListenersList is null. new ArrayList is created= " + mListenersList.size());
        }else{
            Log.d(TAG, "mListenersList is not null. Size= " + mListenersList.size());
            if(mListenersList.size() >0){
                Log.d(TAG, "mListenersList is not null and not empty. Size= " + mListenersList.size()+" Remove previous listeners");
                removeListeners();
                //mListenersList = new ArrayList<>();
            }
        }
    }


    // get initial data
    public void getUsers(String initialKey, final int size,
                         @NonNull final ItemKeyedDataSource.LoadInitialCallback<User> callback){

        Log.i(TAG, "getUsers initiated. initialKey= " +  initialKey);
        this.initialKey = initialKey;
        Query usersQuery;
        isInitialFirstLoaded = true;

        ValueEventListener initialUsersListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // [START_EXCLUDE]
                Log.d(TAG, "start onDataChange. isInitialFirstLoaded = "+ isInitialFirstLoaded);

                if (!isInitialFirstLoaded){
                    // Remove post value event listener
                    removeListeners();
                    Log.d(TAG, "mama usersChanged Invalidated removeEventListener");
                    //isInitialFirstLoaded =  true;
                    Log.d(TAG, "onInvalidated(). isInitialFirstLoaded = "+ isInitialFirstLoaded);
                    invalidatedCallback.onInvalidated();
                    //UsersDataSource.InvalidatedCallback.class.getMethod("loadInitial", "LoadInitialParams");
                    //UsersDataSource.invalidate();
                    return;
                }

                if (dataSnapshot.exists()) {
                    List<User> usersList = new ArrayList<>();
                    // loop throw users value
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        Log.d(TAG, "getUsers dataSnapshot loop= " +  snapshot.getKey());
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            user.setKey(snapshot.getKey());
                            Log.d(TAG, "getUsers usersList loop= " +  user.getKey());
                        }
                        usersList.add(user);
                        //Log.d(TAG, "mama getMessage = "+ message.getMessage()+" getSnapshotKey= " +  snapshot.getKey());
                    }

                    if(usersList.size() != 0){
                        callback.onResult(usersList);
                        Log.d(TAG, "mama getUsersLists  List.size= " +  usersList.size()+ " lastkey= "+usersList.get(usersList.size()-1).getKey());
                    }
                } else {
                    // no data
                    Log.w(TAG, "mama getUsers no users exist");
                }
                getListeners();
                isInitialFirstLoaded =  false;
                Log.d(TAG, "getUsers. End isInitialFirstLoaded = "+ isInitialFirstLoaded);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "mama loadPost:onCancelled", databaseError.toException());
            }
        };

        if (initialKey == null) {// if it's loaded for the first time. Key is null
            Log.d(TAG, "mama getUsers initialKey= " + initialKey);
            usersQuery = mUsersRef.orderByKey()//limitToLast to start from the last (page size) items
                    .limitToFirst(size);

        } else {// not the first load. Key is the last seen key
            Log.d(TAG, "mama getUsers initialKey= " + initialKey);

            usersQuery = mUsersRef.orderByKey()
                    .startAt(initialKey)
                    .limitToFirst(size);
        }

        usersQuery.addValueEventListener(initialUsersListener);
        mListenersList.add(new FirebaseListeners(usersQuery, initialUsersListener));

    }

    // to get next data
    public void getUsersAfter(final String key, final int size,
                              @NonNull final ItemKeyedDataSource.LoadCallback<User> callback){

        Log.d(TAG, "mama getUsersAfter. AfterKey= " + key);
        isAfterFirstLoaded = true;
        //this.afterKey = key;
        Query afterQuery;

        afterQuery = mUsersRef.orderByKey()
                .startAt(key)
                .limitToFirst(size);

        afterQuery.addValueEventListener(afterListener);
        mListenersList.add(new FirebaseListeners(afterQuery, afterListener));

    }

    // to get previous data
    public void getUsersBefore(final String key, final int size,
                               @NonNull final ItemKeyedDataSource.LoadCallback<User> callback){
        Log.d(TAG, "mama getUsersBefore. BeforeKey= " +  key);

        isBeforeFirstLoaded = true;
        //this.beforeKey = key;
        Query beforeQuery;

        beforeQuery = mUsersRef.orderByKey()
                .endAt(key)
                .limitToLast(size);

        beforeQuery.addValueEventListener(beforeListener);
        mListenersList.add(new FirebaseListeners(beforeQuery, beforeListener));

    }

    /*public PublishSubject getUsersChangeSubject() {
        return userAdapterInvalidation;
    }*/

    /*public Single<List<User>> getAnimals(int count){
        return RxFirebaseDatabase.data(mUsersRef.orderByKey().limitToFirst(count)).ma

                .map {
            for ArrayValue
            User.getArrayValue(User.class);
        }
    }*/

    /*public MutableLiveData<User> getUser(String userId){

        DatabaseReference currentUserRef = mUsersRef.child(userId);
        //final MutableLiveData<User> currentUser = new MutableLiveData<>();
        Log.d(TAG, "getUser initiated: " + userId);

        //mListenersMap.put(postSnapshot.getRef(), currentUserListener);
        currentUserRef.addValueEventListener(currentUserListener);
        mListenersMap.put(currentUserRef, currentUserListener);

        for (Map.Entry<DatabaseReference, ValueEventListener> entry : mListenersMap.entrySet()) {
            DatabaseReference ref = entry.getKey();
            ValueEventListener listener = entry.getValue();
            Log.d(TAG, "Map getUser. ref= " + ref+ " listener= "+listener);
            //ref.removeEventListener(listener);
        }

        return currentUser;
    }*/

    //removeListeners is static so it can be triggered when ViewModel is onCleared
    public static void removeListeners(){

        for (int i = 0; i < mListenersList.size(); i++) {
            //Log.d(TAG, "removed Listeners ref= "+ mListenersList.get(i).getReference()+ " Listener= "+ mListenersList.get(i).getListener());
            //Log.d(TAG, "removed Listeners Query= "+ mListenersList.get(i).getQuery()+ " Listener= "+ mListenersList.get(i).getListener());
            Log.d(TAG, "removed Listeners Query or Ref= "+ mListenersList.get(i).getQueryOrRef()+ " Listener= "+ mListenersList.get(i).getListener());

            if(null != mListenersList.get(i).getListener()){
                mListenersList.get(i).getQueryOrRef().removeEventListener(mListenersList.get(i).getListener());
            }
            /*if(null != mListenersList.get(i).getReference()){
                mListenersList.get(i).getReference().removeEventListener(mListenersList.get(i).getListener());
            }else if(null != mListenersList.get(i).getQuery()){
                mListenersList.get(i).getQuery().removeEventListener(mListenersList.get(i).getListener());
            }*/
        }
        mListenersList.clear();
    }

    public void getListeners(){

        for (int i = 0; i < mListenersList.size(); i++) {
            //Log.d(TAG, "Listeners ref= "+ mListenersList.get(i).getReference()+ " Listener= "+ mListenersList.get(i).getListener());
            //Log.d(TAG, "Listeners Query= "+ mListenersList.get(i).getQuery()+ " Listener= "+ mListenersList.get(i).getListener());
            Log.d(TAG, "Listeners Query or Ref= "+ mListenersList.get(i).getQueryOrRef()+ " Listener= "+ mListenersList.get(i).getListener());
        }
    }

    public ItemKeyedDataSource.LoadInitialCallback getLoadInitialCallback() {
        return loadInitialCallback;
    }

    public void setLoadInitialCallback(ItemKeyedDataSource.LoadInitialCallback loadInitialCallback) {
        this.loadInitialCallback = loadInitialCallback;
    }

    public ItemKeyedDataSource.LoadCallback getLoadAfterCallback() {
        return loadAfterCallback;
    }

    public void setLoadAfterCallback(String key, ItemKeyedDataSource.LoadCallback loadAfterCallback) {
        this.loadAfterCallback = loadAfterCallback;
        this.afterKey = key;
    }

    public ItemKeyedDataSource.LoadCallback getLoadBeforeCallback() {
        return loadBeforeCallback;
    }

    public void setLoadBeforeCallback(String key, ItemKeyedDataSource.LoadCallback loadBeforeCallback) {
        this.loadBeforeCallback = loadBeforeCallback;
        this.beforeKey = key;
    }

    public String getLoadAfterKey() {
        return afterKey;
    }

    public String getLoadBeforeKey() {
        return beforeKey;
    }

}




