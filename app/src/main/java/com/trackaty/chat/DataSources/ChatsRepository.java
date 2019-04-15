package com.trackaty.chat.DataSources;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.trackaty.chat.Interface.FirebaseChatsCallback;
import com.trackaty.chat.models.Chat;
import com.trackaty.chat.models.FirebaseListeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;

public class ChatsRepository {

    private final static String TAG = ChatsRepository.class.getSimpleName();

    // [START declare_database_ref]
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mChatsRef;
    private Boolean isFirstLoaded = true;
    //public ValueEventListener ChatsChangesListener;
    public ValueEventListener initialChatsListener;

    private static List<FirebaseListeners> mListenersList;// = new ArrayList<>();
    private MutableLiveData<Chat> mChat;

    private DataSource.InvalidatedCallback invalidatedCallback;
    private ItemKeyedDataSource.LoadInitialCallback loadInitialCallback;
    private ItemKeyedDataSource.LoadCallback loadAfterCallback;
    private ItemKeyedDataSource.LoadCallback loadBeforeCallback;
    private FirebaseChatsCallback firebaseCallback;

    private static volatile Boolean isInitialFirstLoaded;// = true;
    private static volatile Boolean isAfterFirstLoaded;// = true;
    private static volatile Boolean isBeforeFirstLoaded;// = true;
    private static volatile Boolean isInitialKey;

    private Long initialKey;
    private Long afterKey;
    private Long beforeKey;

    // A listener for chat changes
    private ValueEventListener afterListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // [START_EXCLUDE]
            Log.d(TAG, "start onDataChange isAfterFirstLoaded = "+ isAfterFirstLoaded);
            if (!isAfterFirstLoaded){
                // Remove post value event listener
                removeListeners();
                Log.d(TAG, "mama getAfter Invalidated removeEventListener");
                //isAfterFirstLoaded =  true;
                Log.d(TAG, "getAfter onInvalidated(). isAfterFirstLoaded = "+ isAfterFirstLoaded);
                invalidatedCallback.onInvalidated();
                //UsersDataSource.InvalidatedCallback.class.getMethod("loadInitial", "LoadInitialParams");
                //UsersDataSource.invalidate();
                return;
            }

            if (dataSnapshot.exists()) {
                List<Chat> chatsList = new ArrayList<>();
                // loop throw users value
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat != null) {
                        chat.setKey(snapshot.getKey());
                        if(getLoadAfterKey()!= chat.getLastSentLong()) { // if snapshot key = startAt key? don't add it again
                            chatsList.add(chat);
                        }
                    }
                }

                if(chatsList.size() != 0){
                    //callback.onResult(messagesList);
                    Log.d(TAG, "mama getAfter  List.size= " +  chatsList.size()+ " lastkey= "+chatsList.get(chatsList.size()-1).getKey());
                    Collections.reverse(chatsList);
                    getLoadAfterCallback().onResult(chatsList);
                }
            } else {
                // no data
                Log.w(TAG, "mama getAfter no users exist");
            }
            getListeners();
            isAfterFirstLoaded =  false;
            Log.d(TAG, "end isAfterFirstLoaded = "+ isAfterFirstLoaded);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "mama getAfter loadPost:onCancelled", databaseError.toException());
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
                Log.d(TAG, "mama getBefore Invalidated removeEventListener");
                //isBeforeFirstLoaded =  true;
                Log.d(TAG, "getBefore onInvalidated(). isBeforeFirstLoaded = "+ isBeforeFirstLoaded);
                invalidatedCallback.onInvalidated();
                //UsersDataSource.InvalidatedCallback.class.getMethod("loadInitial", "LoadInitialParams");
                //UsersDataSource.invalidate();
                return;
            }

            if (dataSnapshot.exists()) {
                List<Chat> chatsList = new ArrayList<>();
                // loop throw users value
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat != null) {
                        chat.setKey(snapshot.getKey());
                        if(getLoadBeforeKey()!= chat.getLastSentLong()) { // if snapshot key = startAt key? don't add it again
                            chatsList.add(chat);
                        }
                    }
                }

                if(chatsList.size() != 0){
                    //callback.onResult(messagesList);
                    Log.d(TAG, "mama getBefore  List.size= " +  chatsList.size()+ " lastkey= "+chatsList.get(chatsList.size()-1).getKey());
                    Collections.reverse(chatsList);
                    getLoadBeforeCallback().onResult(chatsList);
                }
            } else {
                // no data
                Log.w(TAG, "mama getBefore no users exist");
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


    public ChatsRepository(String userKey, @NonNull DataSource.InvalidatedCallback onInvalidatedCallback, FirebaseChatsCallback firebaseCallback){
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        // use received chatKey to create a database ref
        mChatsRef = mDatabaseRef.child("userChats").child(userKey);
        isFirstLoaded = true;
        Log.d(TAG, "mama mDatabaseRef init. isFirstLoaded= " + isFirstLoaded);
        // call back to invalidate data
        this.invalidatedCallback = onInvalidatedCallback;
        this.firebaseCallback = firebaseCallback;

        isInitialFirstLoaded =  true;
        isAfterFirstLoaded = true;
        isBeforeFirstLoaded = true;

        Log.d(TAG, "mama mDatabaseRef init. isInitialFirstLoaded= " + isInitialFirstLoaded+ " after= "+isAfterFirstLoaded + " before= "+isBeforeFirstLoaded);

        if(mListenersList == null){
            mListenersList = new ArrayList<>();
            Log.d(TAG, "mama mDatabaseRef init. mListenersList size= " + mListenersList.size());
        }
    }

    // get initial data
    public void getChats(Long initialKey, final int size,
                         @NonNull final ItemKeyedDataSource.LoadInitialCallback<Chat> callback){

        this.initialKey = initialKey;
        Query chatsQuery;
        isInitialFirstLoaded = true;

        initialChatsListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // [START_EXCLUDE]
                    Log.d(TAG, "start onDataChange. isInitialFirstLoaded = " + isInitialFirstLoaded);

                    if (!isInitialFirstLoaded) {
                        // Remove post value event listener
                        removeListeners();
                        Log.d(TAG, "mama chatsChanged Invalidated removeEventListener");
                        //isInitialFirstLoaded =  true;
                        Log.d(TAG, "onInvalidated(). isInitialFirstLoaded = " + isInitialFirstLoaded);
                        invalidatedCallback.onInvalidated();
                        //UsersDataSource.InvalidatedCallback.class.getMethod("loadInitial", "LoadInitialParams");
                        //UsersDataSource.invalidate();
                        return;
                    }

                    if (dataSnapshot.exists()) {
                        // loop throw users value
                        List<Chat> chatsList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Chat chat = snapshot.getValue(Chat.class);
                            if (chat != null) {
                                chat.setKey(snapshot.getKey());
                            }

                            chatsList.add(chat);
                            Log.d(TAG, "mama getChats = " + chat.getLastMessage() + " getSnapshotKey= " + snapshot.getKey());
                        }

                        if (chatsList.size() != 0) {
                            Collections.reverse(chatsList);
                            callback.onResult(chatsList);
                            if(firebaseCallback != null){
                                firebaseCallback.onCallback(chatsList);
                            }
                            Log.d(TAG, "mama getMessages  List.size= " + chatsList.size() + " lastkey= " + chatsList.get(chatsList.size() - 1).getKey());
                        }

                    } else {
                        // No data exist
                        Log.w(TAG, "isInitialKey. getChats no chats exist");
                        // It might failed because the initial key is changed and there is no data above it.
                        // Try to get any data regardless of the initial key
                        Log.d(TAG, "isInitialKey. Try to get any data regardless of the initial key "+ isInitialKey);
                        if(isInitialKey){
                            // If no data and we are doing a query with Initial Key, try another query without it
                            isInitialKey = false; // Make isInitialKey boolean false so that we don't loop forever
                            Query chatsQuery = mChatsRef
                                    .orderByChild("lastSent")//limitToLast to start from the last (page size) items
                                    .limitToFirst(size);

                            Log.d(TAG, "isInitialKey. initialChatsListener is added to Query without InitialKey "+ isInitialKey);
                            chatsQuery.addValueEventListener(initialChatsListener);
                            mListenersList.add(new FirebaseListeners(chatsQuery, initialChatsListener));
                        }
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
            Log.d(TAG, "mama getChatss initialKey= " + initialKey);
            isInitialKey = false;
            chatsQuery = mChatsRef
                    .orderByChild("lastSent")//limitToLast to start from the last (page size) items
                    .limitToFirst(size);

        } else {// not the first load. Key is the last seen key
            Log.d(TAG, "mama getChatss initialKey= " + initialKey);
            isInitialKey = true;
            chatsQuery = mChatsRef
                    .orderByChild("lastSent")
                    .startAt(initialKey)
                    .limitToFirst(size);
        }

        chatsQuery.addValueEventListener(initialChatsListener);
        mListenersList.add(new FirebaseListeners(chatsQuery, initialChatsListener));
        //mUsersRef.addValueEventListener(usersListener);

    }

    // to get next data
    public void getChatsAfter(final Long key, final int size,
                         @NonNull final ItemKeyedDataSource.LoadCallback<Chat> callback){
        /*if(key == entireUsersList.get(entireUsersList.size()-1).getCreatedLong()){
            Log.d(TAG, "mama getUsersAfter init. afterKey= " +  key+ "entireUsersList= "+entireUsersList.get(entireUsersList.size()-1).getCreatedLong());
            return;
        }*/
        Log.d(TAG, "mama getAfter. AfterKey= " + key);

        isAfterFirstLoaded = true;
        //this.afterKey = key;
        Query afterQuery;

        afterQuery = mChatsRef
                .orderByChild("lastSent")
                .startAt(key)
                .limitToFirst(size);

        afterQuery.addValueEventListener(afterListener);
        mListenersList.add(new FirebaseListeners(afterQuery, afterListener));
        //mUsersRef.addValueEventListener(usersListener);
    }

    // to get previous data
    public void getChatsBefore(final Long key, final int size,
                              @NonNull final ItemKeyedDataSource.LoadCallback<Chat> callback){
        Log.d(TAG, "mama getChatsBefore. BeforeKey= " +  key);
        /*if(key == entireUsersList.get(0).getCreatedLong()){
            return;
        }*/
        isBeforeFirstLoaded = true;
        //this.beforeKey = key;
        Query beforeQuery;

        beforeQuery = mChatsRef
                .orderByChild("lastSent")
                .endAt(key)
                .limitToLast(size);

        beforeQuery.addValueEventListener(beforeListener);
        mListenersList.add(new FirebaseListeners(beforeQuery, beforeListener));

        //mUsersRef.addValueEventListener(usersListener);
    }

    /*public PublishSubject getUsersChangeSubject() {
        return userAdapterInvalidation;
    }*/

    // to invalidate the data whenever a change happen
    /*public void ChatsChanged(final DataSource.InvalidatedCallback InvalidatedCallback) {

        final Query query = mChatsRef.orderByChild("lastSent");

        ChatsChangesListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!isFirstLoaded){
                    isFirstLoaded = true;
                    Log.d(TAG, "mama entireUsersList Invalidated:");
                    // Remove post value event listener
                    if (ChatsChangesListener != null) {
                        query.removeEventListener(ChatsChangesListener);
                        Log.d(TAG, "mama usersChanged Invalidated removeEventListener");
                    }
                    ((ItemKeyedDataSource.InvalidatedCallback)InvalidatedCallback).onInvalidated();
                    //UsersDataSource.InvalidatedCallback.class.getMethod("loadInitial", "LoadInitialParams");
                    //UsersDataSource.invalidate();
                }

                isFirstLoaded =  false;
                *//*if(entireUsersList.size() > 0){
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
                }*//*
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        query.addValueEventListener(ChatsChangesListener);
        //mUsersRef.addValueEventListener(eventListener);
    }*/

    /*public Single<List<User>> getAnimals(int count){
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

}
