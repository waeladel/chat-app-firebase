package com.trackaty.chat.DataSources;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.trackaty.chat.models.FirebaseListeners;
import com.trackaty.chat.models.Message;
import com.trackaty.chat.models.User;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;

public class MessagesListRepository {

    private final static String TAG = MessagesListRepository.class.getSimpleName();
    private static MessagesListRepository messagesListRepository = null;

    // [START declare_database_ref]
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mMessagesRef;
    private DatabaseReference mUsersRef;

    private static volatile Boolean isInitialFirstLoaded;// = true;
    private static volatile Boolean isAfterFirstLoaded;// = true;
    private static volatile Boolean isBeforeFirstLoaded;// = true;

    private String initialKey;
    private String afterKey;
    private String beforeKey;

    private ValueEventListener MessagesChangesListener;
    private static List<FirebaseListeners> mListenersList;// = new ArrayList<>();
    private MutableLiveData<User> mUser;

    private DataSource.InvalidatedCallback invalidatedCallback;
    private ItemKeyedDataSource.LoadInitialCallback loadInitialCallback;
    private ItemKeyedDataSource.LoadCallback loadAfterCallback;
    private ItemKeyedDataSource.LoadCallback loadBeforeCallback;

    /*private ValueEventListener initialChatsListener;
    private ValueEventListener afterMessagesListener;
    private ValueEventListener beforeMessagesListener;*/

    private ValueEventListener afterMessagesListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // [START_EXCLUDE]
            Log.d(TAG, "start onDataChange isAfterFirstLoaded = "+ isAfterFirstLoaded);
            if (!isAfterFirstLoaded){
                // Remove post value event listener
                removeListeners();
                Log.d(TAG, "mama getMessagesAfter Invalidated removeEventListener");
                //isAfterFirstLoaded =  true;
                Log.d(TAG, "getMessagesAfter onInvalidated(). isAfterFirstLoaded = "+ isAfterFirstLoaded);
                invalidatedCallback.onInvalidated();
                //UsersDataSource.InvalidatedCallback.class.getMethod("loadInitial", "LoadInitialParams");
                //UsersDataSource.invalidate();
                return;
            }

            if (dataSnapshot.exists()) {
                List<Message> messagesList = new ArrayList<>();
                // loop throw users value
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    if(!getLoadAfterKey().equals(snapshot.getKey())) { // if snapshot key = startAt key? don't add it again
                        Message message = snapshot.getValue(Message.class);
                        if (message != null) {
                            message.setKey(snapshot.getKey());
                        }
                        messagesList.add(message);
                        //Log.d(TAG, "mama getMessage = "+ message.getMessage()+" getSnapshotKey= " +  snapshot.getKey());
                    }
                }

                if(messagesList.size() != 0){
                    //callback.onResult(messagesList);
                    getLoadAfterCallback().onResult(messagesList);
                    Log.d(TAG, "mama getMessagesAfter  List.size= " +  messagesList.size()+ " lastkey= "+messagesList.get(messagesList.size()-1).getKey());
                }
            } else {
                // no data
                Log.w(TAG, "mama getMessagesAfter no users exist");
            }
            getListeners();
            isAfterFirstLoaded =  false;
            Log.d(TAG, "end isAfterFirstLoaded = "+ isAfterFirstLoaded);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "mama getMessagesAfter loadPost:onCancelled", databaseError.toException());
        }
    };

    private ValueEventListener beforeMessagesListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // [START_EXCLUDE]
            Log.d(TAG, "start onDataChange isBeforeFirstLoaded = "+ isBeforeFirstLoaded);
            if (!isBeforeFirstLoaded){
                // Remove post value event listener
                removeListeners();
                Log.d(TAG, "mama getMessagesBefore Invalidated removeEventListener");
                //isBeforeFirstLoaded =  true;
                Log.d(TAG, "getMessagesBefore onInvalidated(). isBeforeFirstLoaded = "+ isBeforeFirstLoaded);
                invalidatedCallback.onInvalidated();
                //UsersDataSource.InvalidatedCallback.class.getMethod("loadInitial", "LoadInitialParams");
                //UsersDataSource.invalidate();
                return;
            }

            if (dataSnapshot.exists()) {
                List<Message> messagesList = new ArrayList<>();
                // loop throw users value
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    if(!getLoadBeforeKey().equals(snapshot.getKey())) { // if snapshot key = startAt key? don't add it again
                        Message message = snapshot.getValue(Message.class);
                        if (message != null) {
                            message.setKey(snapshot.getKey());
                        }
                        messagesList.add(message);
                        //Log.d(TAG, "mama getMessage = "+ message.getMessage()+" getSnapshotKey= " +  snapshot.getKey());
                    }
                }

                if(messagesList.size() != 0){
                    //callback.onResult(messagesList);
                    getLoadBeforeCallback().onResult(messagesList);
                    Log.d(TAG, "mama getMessagesBefore  List.size= " +  messagesList.size()+ " lastkey= "+messagesList.get(messagesList.size()-1).getKey());
                }
            } else {
                // no data
                Log.w(TAG, "mama getMessagesBefore no users exist");
            }
            getListeners();
            isBeforeFirstLoaded =  false;
            Log.d(TAG, "end isBeforeFirstLoaded = "+ isBeforeFirstLoaded);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "mama getMessagesBefore:onCancelled", databaseError.toException());
        }
    };

    //private Query getMessagesQuery;

    public MessagesListRepository(String chatKey, @NonNull DataSource.InvalidatedCallback onInvalidatedCallback){
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        // use received chatKey to create a database ref
        mMessagesRef = mDatabaseRef.child("messages").child(chatKey);
        mUsersRef = mDatabaseRef.child("users");
        // call back to invalidate data
        this.invalidatedCallback = onInvalidatedCallback;

        isInitialFirstLoaded =  true;
        isAfterFirstLoaded = true;
        isBeforeFirstLoaded = true;

        Log.d(TAG, "mama MessagesListRepository init. isInitialFirstLoaded= " + isInitialFirstLoaded+ " after= "+isAfterFirstLoaded + " before= "+isBeforeFirstLoaded);

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

    /*public static MessagesListRepository getInstance() {
        if(messagesListRepository == null) {
            //throw new AssertionError("You have to call init first");
            Log.w(TAG, "You have to call init first");
            return null;
        }else{
            return messagesListRepository;
        }
    }


    public synchronized static MessagesListRepository init(String chatKey, @NonNull DataSource.InvalidatedCallback onInvalidatedCallback) {
        if (messagesListRepository != null){

            // in my opinion this is optional, but for the purists it ensures
            // that you only ever get the same instance when you call getInstance
            //throw new AssertionError("You already initialized me");
            Log.w(TAG, "You already initialized me");
            return messagesListRepository;
        }else{
            messagesListRepository = new MessagesListRepository(chatKey, onInvalidatedCallback);
            return messagesListRepository;
        }
    }*/

    // get initial data
    public void getMessages(String initialKey, final int size,
                            @NonNull final ItemKeyedDataSource.LoadInitialCallback<Message> callback) {

        Log.i(TAG, "getMessages initiated. initialKey= " +  initialKey);
        this.initialKey = initialKey;
        Query messagesQuery;
        isInitialFirstLoaded = true;

        ValueEventListener  initialMessagesListener = new ValueEventListener() {
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
                    List<Message> messagesList = new ArrayList<>();
                    // loop throw users value
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        Message message = snapshot.getValue(Message.class);
                        if (message != null) {
                            message.setKey(snapshot.getKey());
                        }
                        messagesList.add(message);
                        //Log.d(TAG, "mama getMessage = "+ message.getMessage()+" getSnapshotKey= " +  snapshot.getKey());
                    }

                    if(messagesList.size() != 0){
                        callback.onResult(messagesList);
                        Log.d(TAG, "mama getMessages  List.size= " +  messagesList.size()+ " lastkey= "+messagesList.get(messagesList.size()-1).getKey());
                    }
                } else {
                    // no data
                    Log.w(TAG, "mama getMessages no users exist");
                }
                getListeners();
                isInitialFirstLoaded =  false;
                Log.d(TAG, "end isInitialFirstLoaded = "+ isInitialFirstLoaded);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "mama loadPost:onCancelled", databaseError.toException());
            }
        };

        if (initialKey == null) {// if it's loaded for the first time. Key is null
            Log.d(TAG, "mama getMessages initialKey= " + initialKey);
            messagesQuery = mMessagesRef.orderByKey()//limitToLast to start from the last (page size) items
                    .limitToLast(size);

        } else {// not the first load. Key is the last seen key
            Log.d(TAG, "mama getMessages initialKey= " + initialKey);
            messagesQuery = mMessagesRef.orderByKey()
                    // Don't start at initialKey because we need to always display the latest message
                    //.startAt(initialKey)
                    .limitToLast(size);
        }

        messagesQuery.addValueEventListener(initialMessagesListener);
        mListenersList.add(new FirebaseListeners(messagesQuery, initialMessagesListener));
    }

    // to get next data
    public void getMessagesAfter(final String key, final int size,
                         @NonNull final ItemKeyedDataSource.LoadCallback<Message> callback){

        Log.i(TAG, "wael getMessagesAfter initiated. AfterKey= " +  key);
        isAfterFirstLoaded = true;
        //this.afterKey = key;
        Query afterMessagesQuery;

        Log.d(TAG, "mama getMessagesAfter. AfterKey= " + key);
        afterMessagesQuery = mMessagesRef.orderByKey()
                            .startAt(key)
                            .limitToFirst(size);

        afterMessagesQuery.addValueEventListener(afterMessagesListener);
        mListenersList.add(new FirebaseListeners(afterMessagesQuery, afterMessagesListener));
        //mUsersRef.addValueEventListener(usersListener);
    }

    // to get previous data
    public void getMessagesBefore(final String key, final int size,
                              @NonNull final ItemKeyedDataSource.LoadCallback<Message> callback){

        Log.i(TAG, "wael getMessagesBefore initiated. BeforeKey= " +  key);

        isBeforeFirstLoaded = true;
        //this.beforeKey = key;
        Query beforeMessagesQuery;

        beforeMessagesQuery = mMessagesRef.orderByKey()
                                .endAt(key)
                                .limitToLast(size);

        beforeMessagesQuery.addValueEventListener(beforeMessagesListener);
        mListenersList.add(new FirebaseListeners(beforeMessagesQuery, beforeMessagesListener));
        //mUsersRef.addValueEventListener(usersListener);
    }

    /*public PublishSubject getUsersChangeSubject() {
        return userAdapterInvalidation;
    }*/

    // to invalidate the data whenever a change happen
   /* public void MessagesChanged(final DataSource.InvalidatedCallback InvalidatedCallback) {

        final Query query = mMessagesRef.orderByKey();

        MessagesChangesListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!isInitialFirstLoaded){
                    isInitialFirstLoaded = true;
                    Log.d(TAG, "mama entireUsersList Invalidated:");
                    // Remove post value event listener
                    if (MessagesChangesListener != null) {
                        query.removeEventListener(MessagesChangesListener);
                        Log.d(TAG, "mama usersChanged Invalidated removeEventListener");
                    }
                    ((ItemKeyedDataSource.InvalidatedCallback)InvalidatedCallback).onInvalidated();
                    //UsersDataSource.InvalidatedCallback.class.getMethod("loadInitial", "LoadInitialParams");
                    //UsersDataSource.invalidate();
                }

                isInitialFirstLoaded =  false;
                if(entireUsersList.size() > 0){
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
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        query.addValueEventListener(MessagesChangesListener);
        //mUsersRef.addValueEventListener(eventListener);
        mListenersList.add(new FirebaseListeners(query, MessagesChangesListener));

        for (int i = 0; i < mListenersList.size(); i++) {
            Log.d(TAG, "MessagesListRepository Listeners ref= "+ mListenersList.get(i).getReference()+ " Listener= "+ mListenersList.get(i).getListener());
            Log.d(TAG, "MessagesListRepository Listeners Query= "+ mListenersList.get(i).getQuery()+ " Listener= "+ mListenersList.get(i).getListener());
            Log.d(TAG, "MessagesListRepository Listeners Query or Ref= "+ mListenersList.get(i).getQueryOrRef()+ " Listener= "+ mListenersList.get(i).getListener());
        }
    }

    public Single<List<User>> getAnimals(int count){
        return RxFirebaseDatabase.data(mUsersRef.orderByKey().limitToFirst(count)).ma

                .map {
            for ArrayValue
            User.getArrayValue(User.class);
        }
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
