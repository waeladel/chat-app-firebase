package com.trackaty.chat.dataSources;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.trackaty.chat.models.FirebaseListeners;
import com.trackaty.chat.models.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class UsersRepository {

    private final static String TAG = UsersRepository.class.getSimpleName();

    // [START declare_database_ref]
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUsersRef;
    //List<User> usersList ;
    //List<User> entireUsersList ;
    private Boolean isFirstLoaded = true;
    public ValueEventListener usersChangesListener;

    private Long initialKey;
    private Long afterKey;
    private Long beforeKey;

    private static volatile Boolean isInitialFirstLoaded;// = true;
    private static volatile Boolean isAfterFirstLoaded;// = true;
    private static volatile Boolean isBeforeFirstLoaded;// = true;
    private static volatile Boolean isInitialKey;

    private DataSource.InvalidatedCallback invalidatedCallback;
    private ItemKeyedDataSource.LoadInitialCallback loadInitialCallback;
    private ItemKeyedDataSource.LoadCallback loadAfterCallback;
    private ItemKeyedDataSource.LoadCallback loadBeforeCallback;

    public ValueEventListener initialUsersListener;

    private static final int REACHED_THE_TOP = 2;
    private static final int SCROLLING_UP = 1;
    private static final int SCROLLING_DOWN = -1;
    private static final int REACHED_THE_BOTTOM = -2;
    private static int mScrollDirection;
    private static int mVisibleItem;

    private static List<FirebaseListeners> mListenersList;// = new ArrayList<>();
    private static List<User> totalItemsList;// = new ArrayList<>();

    // Get current user ID
    private FirebaseUser FirebaseCurrentUser;
    private String currentUserId;

    //private MutableLiveData<User>currentUser;

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
                List<User> usersList = new ArrayList<>();
                // loop throw users value
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Log.d(TAG, "getUsersAfter dataSnapshot loop= " +  snapshot.getKey());
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        user.setKey(snapshot.getKey());
                        if(getLoadAfterKey()!= user.getCreatedLong()) { // if snapshot key = startAt key? don't add it again
                            usersList.add(user);
                        }
                    }
                }

                if(usersList.size() != 0){
                    //callback.onResult(messagesList);
                    Log.d(TAG, "mama getAfter  List.size= " +  usersList.size()+ " last key= "+usersList.get(usersList.size()-1).getKey());
                    Collections.reverse(usersList);
                    getLoadAfterCallback().onResult(usersList);

                    // Create a reversed list to add users to the beginning of totalItemsList
                    List<User> reversedList = new ArrayList<>(usersList);
                    Collections.reverse(reversedList);
                    for (int i = 0; i < reversedList.size(); i++) {
                        // Add messages to totalItemsList ArrayList to be used to get the initial key position
                        totalItemsList.add(0, reversedList.get(i));
                    }
                    //totalItemsList.addAll( 0, reversedList);
                    // Get TotalItems logs
                    printTotalItems("After");
                    /*for (int i = 0; i < reversedList.size(); i++) {
                        Log.d(TAG, "After totalItemsList : key= "+ chatsList.get(i).getKey()+ " message= "+ chatsList.get(i).getLastMessage()+ " size= "+chatsList.size());
                    }*/

                }
            } else {
                // no data
                Log.w(TAG, "mama getUsersAfter no users exist");
            }
            printListeners();
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
                    Long created= (Long) snapshot.child("created").getValue();
                    Log.d(TAG, "getBefore dataSnapshot loop= " +  snapshot.getKey()+ " created= "+created);
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        user.setKey(snapshot.getKey());
                        if(getLoadBeforeKey()!= user.getCreatedLong()) { // if snapshot key = startAt key? don't add it again
                            usersList.add(user);
                        }
                    }
                }

                if(usersList.size() != 0){
                    //callback.onResult(messagesList);
                    Log.d(TAG, "mama getBefore  List.size= " +  usersList.size()+ " last key= "+usersList.get(usersList.size()-1).getKey());
                    Collections.reverse(usersList);
                    getLoadBeforeCallback().onResult(usersList);
                    totalItemsList.addAll(usersList); // add items to totalItems ArrayList to be used to get the initial key position

                    /*for (int i = 0; i < chatsList.size(); i++) {
                        Log.d(TAG, "before totalItemsList : key= "+ chatsList.get(i).getKey()+ " message= "+ chatsList.get(i).getLastMessage()+ " size= "+chatsList.size());
                    }*/
                    // Get TotalItems logs
                    printTotalItems("Before");
                }
            } else {
                // no data
                Log.w(TAG, "mama getUsersBefore no users exist");
            }
            printListeners();
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

        /*FirebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = FirebaseCurrentUser != null ? FirebaseCurrentUser.getUid() : null;

        Log.d(TAG, "create new UsersRepository. currentUserId="+ currentUserId);*/

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mUsersRef = mDatabaseRef.child("users");
        //usersList = new ArrayList<>();
        //entireUsersList = new ArrayList<>();
        isFirstLoaded = true;
        //currentUser = new MutableLiveData<>();

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

        if(totalItemsList == null){
            totalItemsList = new ArrayList<>();
            Log.d(TAG, "totalItemsList is null. new ArrayList is created= " + totalItemsList.size());
        }else{
            Log.d(TAG, "totalItemsList is not null. Size= " + totalItemsList.size());
            if(totalItemsList.size() >0){
                Log.d(TAG, "totalItemsList is not null and not empty. Size= " + totalItemsList.size());
                // Clear the list of total items to start all over
                //totalItemsList.clear();
            }
        }
    }

    // Set the scrolling direction and get the last/first visible item
    public void setScrollDirection(int scrollDirection, int visibleItem) {
        mScrollDirection = scrollDirection;
        mVisibleItem = visibleItem;
        Log.d(TAG, "mScrollDirection = " + mScrollDirection+ " VisibleItem= "+ mVisibleItem);

    }

    // get initial data
    public void getUsers(Long initialKey, final int size,
                         @NonNull final ItemKeyedDataSource.LoadInitialCallback<User> callback){

        Log.i(TAG, "getUsers initiated. initialKey= " +  initialKey);
        this.initialKey = initialKey;
        Query usersQuery;
        isInitialFirstLoaded = true;

        initialUsersListener = new ValueEventListener() {
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
                        Log.d(TAG, "fuck getUsers dataSnapshot loop= " +  snapshot.getKey());
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            user.setKey(snapshot.getKey());
                            Log.d(TAG, "getUsers usersList loop= " +  user.getKey());
                        }
                        usersList.add(user);
                        //Log.d(TAG, "mama getMessage = "+ message.getMessage()+" getSnapshotKey= " +  snapshot.getKey());
                    }

                    if(usersList.size() != 0){
                        Collections.reverse(usersList);
                        Log.d(TAG, "fuck getUsers dataSnapshot size= " +  usersList.size());
                        callback.onResult(usersList);

                        // Add chats to totalItemsList ArrayList to be used to get the initial key position
                        totalItemsList.addAll(usersList);
                        printTotalItems("Initial");
                        Log.d(TAG, "mama getUsersLists  List.size= " +  usersList.size()+ " last key= "+usersList.get(usersList.size()-1).getKey());
                    }
                } else {
                    // no data
                    Log.w(TAG, "mama getUsers no users exist");
                    // It might failed because the initial key is changed and there is no data above it.
                    // Try to get any data regardless of the initial key
                    Log.d(TAG, "isInitialKey. Try to get any data regardless of the initial key "+ isInitialKey);
                    if(isInitialKey){
                        // If no data and we are doing a query with Initial Key, try another query without it
                        isInitialKey = false; // Make isInitialKey boolean false so that we don't loop forever
                        Query usersQuery = mUsersRef
                                .orderByChild("created")//limitToLast to start from the last (page size) items
                                .limitToLast(size);

                        Log.d(TAG, "isInitialKey. initialListener is added to Query without InitialKey "+ isInitialKey);
                        usersQuery.addValueEventListener(initialUsersListener);
                        mListenersList.add(new FirebaseListeners(usersQuery, initialUsersListener));
                    }
                }
                printListeners();
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
            isInitialKey = false;
            usersQuery = mUsersRef
                    .orderByChild("created")//limitToLast to start from the last (page size) items
                    .limitToLast(size);

        } else {// not the first load. Key is the last seen key
            Log.d(TAG, "mama getUsers initialKey= " + initialKey);
            isInitialKey = true;

            switch (mScrollDirection){
                // No need to detected reaching to bottom
                /*case REACHED_THE_BOTTOM:
                    Log.d(TAG, "messages query = REACHED_THE_BOTTOM. ScrollDirection= "+mScrollDirection+ " mVisibleItem= "+ mVisibleItem + " totalItemsList size= "+totalItemsList.size());
                    chatsQuery = mUsersRef
                            .orderByChild("created")//limitToLast to start from the last (page size) items
                            .limitToFirst(size);
                    break;*/
                case REACHED_THE_TOP:
                    Log.d(TAG, "users query = REACHED_THE_TOP. ScrollDirection= "+mScrollDirection+ " mVisibleItem= "+ mVisibleItem + " totalItemsList size= "+totalItemsList.size());
                    usersQuery = mUsersRef
                            .orderByChild("created")//limitToLast to start from the last (page size) items
                            .limitToLast(size);
                    break;
                case SCROLLING_UP:
                    /*Log.d(TAG, "messages query = Load data from top to bottom (above InitialKey cause list is reversed). ScrollDirection= "+mScrollDirection+ " InitialKey Position= "+getInitialKeyPosition() +" mVisibleItem= "+mVisibleItem+ " Item Message= "+ totalItemsList.get(mVisibleItem).getLastMessage() +" totalItemsList size= "+totalItemsList.size());
                    chatsQuery = mUsersRef
                            .orderByChild("created")//limitToLast to start from the last (page size) items
                            .endAt(initialKey)
                            .limitToLast(size);*/

                    // list is reversed, smaller Keys are on bottom
                    // InitialKey is in the top, must load data from top to bottom
                    // list is reversed, load data above InitialKey
                    Log.d(TAG, "users query = Load data from top to bottom (above InitialKey cause list is reversed). ScrollDirection= "+mScrollDirection+ " InitialKey Position= "+getInitialKeyPosition() +" first VisibleItem= "+ mVisibleItem + " Item Message= "+ totalItemsList.get(mVisibleItem).getName() +" totalItemsList size= "+totalItemsList.size());
                    usersQuery = mUsersRef
                            .orderByChild("created")
                            .endAt(getItem(mVisibleItem).getCreatedLong())//Using first visible item key instead of initial key
                            .limitToLast(size);
                    break;
                case SCROLLING_DOWN:
                    /*Log.d(TAG, "messages query = Load data from bottom to top (below InitialKey cause list is reversed). ScrollDirection= "+mScrollDirection+ " InitialKey Position= "+getInitialKeyPosition() +" mVisibleItem= "+mVisibleItem+ " Item Message= "+ totalItemsList.get(mVisibleItem).getLastMessage() +" totalItemsList size= "+totalItemsList.size());
                    chatsQuery = mUsersRef
                            .orderByChild("created")//limitToLast to start from the last (page size) items
                            .startAt(initialKey)
                            .limitToFirst(size);*/
                    // InitialKey is in the bottom, must load data from bottom to top
                    // list is reversed, load data below InitialKey
                    Log.d(TAG, "users query = Load data from bottom to top (below InitialKey cause list is reversed). ScrollDirection= "+mScrollDirection+ " InitialKey Position= "+getInitialKeyPosition() +"  last VisibleItem= "+ mVisibleItem + " Item Message= "+ totalItemsList.get(mVisibleItem).getName() +" totalItemsList size= "+totalItemsList.size());
                    usersQuery = mUsersRef
                            .orderByChild("created")
                            .startAt(getItem(mVisibleItem).getCreatedLong())//Using last visible item key instead of initial key
                            .limitToFirst(size);
                    break;
                default:
                   /*// list is reversed, greater Keys are on top
                    if(getInitialKeyPosition() >= mVisibleItem){
                        // InitialKey is in the bottom, must load data from bottom to top
                        // list is reversed, load data below InitialKey
                        Log.d(TAG, "messages query = Load data from bottom to top (below InitialKey cause list is reversed). ScrollDirection= "+mScrollDirection+ " InitialKey Position= "+getInitialKeyPosition() +" mVisibleItem= "+ mVisibleItem + " totalItemsList size= "+totalItemsList.size());
                        chatsQuery = mUsersRef
                                .orderByChild("created")//limitToLast to start from the last (page size) items
                                .startAt(initialKey)
                                .limitToFirst(size);

                    }else{
                        // list is reversed, smaller Keys are on bottom
                        // InitialKey is in the top, must load data from top to bottom
                        // list is reversed, load data above InitialKey
                        Log.d(TAG, "messages query = Load data from top to bottom (above InitialKey cause list is reversed). ScrollDirection= "+mScrollDirection+ " InitialKey Position= "+getInitialKeyPosition() +" mVisibleItem= "+ mVisibleItem + " totalItemsList size= "+totalItemsList.size());
                        chatsQuery = mUsersRef
                                .orderByChild("created")//limitToLast to start from the last (page size) items
                                .endAt(initialKey)
                                .limitToLast(size);
                    }
                    break;*/
                    Log.d(TAG, "users query = default. ScrollDirection= "+mScrollDirection+ " mVisibleItem= "+ mVisibleItem + " totalItemsList size= "+totalItemsList.size());
                    usersQuery = mUsersRef
                            .orderByChild("created")//limitToLast to start from the last (page size) items
                            .limitToLast(size);
            }
        }

        // Clear the list of total items to start all over
        totalItemsList.clear();
        Log.d(TAG, "messages query = totalItemsList is cleared");

        usersQuery.addValueEventListener(initialUsersListener);
        mListenersList.add(new FirebaseListeners(usersQuery, initialUsersListener));

    }

    // to get next data
    public void getUsersAfter(final Long key, final int size,
                              @NonNull final ItemKeyedDataSource.LoadCallback<User> callback){

        Log.d(TAG, "mama getUsersAfter. AfterKey= " + key);
        isAfterFirstLoaded = true;
        //this.afterKey = key;
        Query afterQuery;

        afterQuery = mUsersRef
                .orderByChild("created")
                .startAt(key)
                .limitToFirst(size);

        afterQuery.addValueEventListener(afterListener);
        mListenersList.add(new FirebaseListeners(afterQuery, afterListener));

    }

    // to get previous data
    public void getUsersBefore(final Long key, final int size,
                               @NonNull final ItemKeyedDataSource.LoadCallback<User> callback){
        Log.d(TAG, "mama getUsersBefore. BeforeKey= " +  key);

        isBeforeFirstLoaded = true;
        //this.beforeKey = key;
        Query beforeQuery;

        beforeQuery = mUsersRef
                .orderByChild("created")
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

    public void printListeners(){

        for (int i = 0; i < mListenersList.size(); i++) {
            //Log.d(TAG, "Listeners ref= "+ mListenersList.get(i).getReference()+ " Listener= "+ mListenersList.get(i).getListener());
            //Log.d(TAG, "Listeners Query= "+ mListenersList.get(i).getQuery()+ " Listener= "+ mListenersList.get(i).getListener());
            Log.d(TAG, "Listeners Query or Ref= "+ mListenersList.get(i).getQueryOrRef()+ " Listener= "+ mListenersList.get(i).getListener());
        }
    }

    public void printTotalItems(String type){
        Log.d(TAG, "Getting totalItemsList... Type"+ type );
        for (int i = 0; i < totalItemsList.size(); i++) {
            Log.d(TAG, "totalItemsList : key= "+ totalItemsList.get(i).getKey()+ " name= "+ totalItemsList.get(i).getName()+ " size= "+totalItemsList.size());
        }
    }

    // get the position of initial key
    public int getInitialKeyPosition(){
        Log.d(TAG, "Getting get InitialKeyPosition... getInitialKey()= "+getInitialKey()+ " totalItemsList size= "+totalItemsList.size());
        int Position = 0;
        for (int i = 0; i < totalItemsList.size(); i++) {
            if(totalItemsList.get(i).getCreatedLong() == getInitialKey()){
                Log.d(TAG, "users query InitialKeyPosition: key= "+ getInitialKey()+ " name= "+totalItemsList.get(i).getName() +" Position= " +Position);
                return Position;
            }else{
                Position++;
            }
        }
        return Position;
    }

    // get item and item's key from adapter position
    public User getItem(int position){
        Log.d(TAG, "Getting getItem... getInitialKey()= "+getInitialKey()+ " item name= "+ totalItemsList.get(position).getName());
        return totalItemsList.get(position);
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

    public void setLoadAfterCallback(Long key, ItemKeyedDataSource.LoadCallback loadAfterCallback) {
        this.loadAfterCallback = loadAfterCallback;
        this.afterKey = key;
    }

    public ItemKeyedDataSource.LoadCallback getLoadBeforeCallback() {
        return loadBeforeCallback;
    }

    public void setLoadBeforeCallback(Long key, ItemKeyedDataSource.LoadCallback loadBeforeCallback) {
        this.loadBeforeCallback = loadBeforeCallback;
        this.beforeKey = key;
    }

    public Long getLoadAfterKey() {
        return afterKey;
    }

    public Long getLoadBeforeKey() {
        return beforeKey;
    }

    public Long getInitialKey() {
        return initialKey;
    }

}




