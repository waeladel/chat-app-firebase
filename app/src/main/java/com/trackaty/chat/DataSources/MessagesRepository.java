package com.trackaty.chat.DataSources;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.trackaty.chat.models.Chat;
import com.trackaty.chat.models.FirebaseListeners;
import com.trackaty.chat.models.Message;
import com.trackaty.chat.models.User;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;

public class MessagesRepository {

    private final static String TAG = MessagesRepository.class.getSimpleName();

    // [START declare_database_ref]
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUsersRef , mMessagesRef, mChatRef;
    private Boolean isFirstLoaded = true;
    private static List<FirebaseListeners> mListenersList;// = new ArrayList<>();
    private MutableLiveData<User> mUser;
    private MutableLiveData<String> mSenderId;
    private MutableLiveData<Chat> mChat;



    // a listener for chat User changes
    private ValueEventListener userListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI
            if (dataSnapshot.exists()) {
                // Get user value
                Log.d(TAG, "getUser dataSnapshot key: "
                        + dataSnapshot.getKey()+" Listener = "+userListener);
                //mCurrentUser = dataSnapshot.getValue(User.class);
                mUser.postValue(dataSnapshot.getValue(User.class));
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


    // a listener for chat  changes
    private ValueEventListener chatListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI
            if (dataSnapshot.exists()) {
                // Get user value
                Log.d(TAG, "chat dataSnapshot key: "
                        + dataSnapshot.getKey()+" Listener = "+chatListener);
                //mCurrentUser = dataSnapshot.getValue(User.class);
                mChat.postValue(dataSnapshot.getValue(Chat.class));
            } else {
                // Chat is null, error out
                Log.w(TAG, "chat is null, no such chat");
                //mChat.postValue(null);
            }
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "load chat:onCancelled", databaseError.toException());

        }
    };

    public MessagesRepository(){
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mUsersRef = mDatabaseRef.child("users");
        mMessagesRef = mDatabaseRef.child("messages");
        mChatRef = mDatabaseRef.child("chats");
        isFirstLoaded = true;
        Log.d(TAG, "mama MessagesRepository init. isFirstLoaded= " + isFirstLoaded);
        mUser = new MutableLiveData<>();
        mSenderId = new MutableLiveData<>();
        mChat = new MutableLiveData<>();
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

    public MutableLiveData<User> getUser(String userId){

        DatabaseReference userRef = mUsersRef.child(userId);
        //final MutableLiveData<User> mCurrentUser = new MutableLiveData<>();
        Log.d(TAG, "getUser initiated: " + userId);
        Log.d(TAG, "getUser mListenersList size= "+ mListenersList.size());

        if(mListenersList.size()== 0){
            // Need to add a new Listener
            Log.d(TAG, "getUser adding new Listener= "+ mListenersList);
            //mListenersMap.put(postSnapshot.getRef(), mPickUpCounterListener);
            userRef.addValueEventListener(userListener);
            mListenersList.add(new FirebaseListeners(userRef, userListener));
        }else{
            Log.d(TAG, "postSnapshot Listeners size is not 0= "+ mListenersList.size());
            //there is an old Listener, need to check if it's on this ref
            for (int i = 0; i < mListenersList.size(); i++) {
                //Log.d(TAG, "getUser Listeners ref= "+ mListenersList.get(i).getQueryOrRef()+ " Listener= "+ mListenersList.get(i).getListener());
                if(mListenersList.get(i).getListener().equals(userListener) &&
                        !mListenersList.get(i).getQueryOrRef().equals(userRef)){
                    // We used this listener before, but on another Ref
                    Log.d(TAG, "We used this listener before, is it on the same ref?");
                    Log.d(TAG, "getUser adding new Listener= "+ userListener);
                    userRef.addValueEventListener(userListener);
                    mListenersList.add(new FirebaseListeners(userRef, userListener));
                }else if((mListenersList.get(i).getListener().equals(userListener) &&
                        mListenersList.get(i).getQueryOrRef().equals(userRef))){
                    //there is old Listener on the ref
                    Log.d(TAG, "getUser Listeners= there is old Listener on the ref= "+mListenersList.get(i).getQueryOrRef()+ " Listener= " + mListenersList.get(i).getListener());
                }else{
                    //userListener is never used
                    Log.d(TAG, "Listener is never created");
                    userRef.addValueEventListener(userListener);
                    mListenersList.add(new FirebaseListeners(userRef, userListener));
                }
            }
        }

        for (int i = 0; i < mListenersList.size(); i++) {
            Log.d(TAG, "getUser loop throw ref= " + mListenersList.get(i).getQueryOrRef() + " Listener= " + mListenersList.get(i).getListener());
        }

        Log.d(TAG, "getUser= "+ mUser);

        return mUser;
    }

    /*public MutableLiveData<String> getSenderId (String chatId){

        DatabaseReference MessagesRef = mMessagesRef.child(chatId);
        //final MutableLiveData<User> mCurrentUser = new MutableLiveData<>();
        Log.d(TAG, "getUser initiated: " + chatId);
        Log.d(TAG, "getUser Listeners userListener= "+ mListenersList.size());
        MessagesRef.limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                if (dataSnapshot.exists()) {

                    List<Message> messagesList = new ArrayList<>();
                    // loop throw users value
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        Message message = snapshot.getValue(Message.class);
                        if (message != null) {
                            message.setKey(snapshot.getKey());
                        }
                        messagesList.add(message);
                    }
                    // Get user value
                    Log.d(TAG, "senderId dataSnapshot key: "
                            + messagesList.get(0).getSenderId());
                    //mCurrentUser = dataSnapshot.getValue(User.class);
                    mSenderId.postValue(messagesList.get(0).getSenderId());
                } else {
                    // User is null, error out
                    Log.w(TAG, "senderId is null, no such user");
                    mSenderId.postValue(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });


        return mSenderId;
    }*/

    public MutableLiveData<Chat> getChat(String chatId){

        DatabaseReference chatRef = mChatRef.child(chatId);
        //final MutableLiveData<User> mCurrentUser = new MutableLiveData<>();
        Log.d(TAG, "getChat initiated: " + chatId);
        Log.d(TAG, "getChat mListenersList size= "+ mListenersList.size());
        if(mListenersList.size()== 0){
            // Need to add a new Listener
            Log.d(TAG, "getChat adding new Listener= "+ mListenersList);
            //mListenersMap.put(postSnapshot.getRef(), mPickUpCounterListener);
            chatRef.addValueEventListener(chatListener);
            mListenersList.add(new FirebaseListeners(chatRef, chatListener));
        }else{
            Log.d(TAG, "postSnapshot Listeners size is not 0= "+ mListenersList.size());
            //there is an old Listener, need to check if it's on this ref
            for (int i = 0; i < mListenersList.size(); i++) {
                //Log.d(TAG, "getUser Listeners ref= "+ mListenersList.get(i).getQueryOrRef()+ " Listener= "+ mListenersList.get(i).getListener());
                if(mListenersList.get(i).getListener().equals(chatListener) &&
                        !mListenersList.get(i).getQueryOrRef().equals(chatRef)){
                    // We used this listener before, but on another Ref
                    Log.d(TAG, "We used this listener before, is it on the same ref?");
                    Log.d(TAG, "getChat adding new Listener= "+ chatListener);
                    chatRef.addValueEventListener(chatListener);
                    mListenersList.add(new FirebaseListeners(chatRef, chatListener));
                }else if((mListenersList.get(i).getListener().equals(chatListener) &&
                        mListenersList.get(i).getQueryOrRef().equals(chatRef))){
                    //there is old Listener on the ref
                    Log.d(TAG, "getChat Listeners= there is old Listener on the ref= "+mListenersList.get(i).getQueryOrRef()+ " Listener= " + mListenersList.get(i).getListener());
                }else{
                    //chatListener is never used
                    Log.d(TAG, "Listener is never created");
                    chatRef.addValueEventListener(chatListener);
                    mListenersList.add(new FirebaseListeners(chatRef, chatListener));
                }
            }
        }

        /*for (int i = 0; i < mListenersList.size(); i++) {
            Log.d(TAG, "getChat loop throw ref= "+ mListenersList.get(i).getQueryOrRef()+ " Listener= "+ mListenersList.get(i).getListener());
        }*/

        return mChat;
    }


    // remove all listeners when the ViewModel is cleared
    public void removeListeners(){
        for (int i = 0; i < mListenersList.size(); i++) {
            //Log.d(TAG, "remove Listeners ref= "+ mListenersList.get(i).getReference()+ " Listener= "+ mListenersList.get(i).getListener());
            //Log.d(TAG, "remove Listeners Query= "+ mListenersList.get(i).getQuery()+ " Listener= "+ mListenersList.get(i).getListener());
            Log.d(TAG, "remove Listeners Query or Ref= "+ mListenersList.get(i).getQueryOrRef()+ " Listener= "+ mListenersList.get(i).getListener());

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

}
