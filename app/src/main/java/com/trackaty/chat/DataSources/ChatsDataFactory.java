package com.trackaty.chat.DataSources;

import com.trackaty.chat.Interface.FirebaseChatsCallback;
import com.trackaty.chat.models.Chat;
import com.trackaty.chat.models.Message;

import androidx.paging.DataSource;

public class ChatsDataFactory extends DataSource.Factory<Long, Chat>{

    private String mUserKey;
    private FirebaseChatsCallback firebaseCallback;

    // receive chatKey on the constructor
    public ChatsDataFactory(String userKey, FirebaseChatsCallback firebaseCallback) {
        this.mUserKey = userKey;
        this.firebaseCallback = firebaseCallback;
    }

    /*public void setCallback(FirebaseChatsCallback firebaseCallback) {
        this.firebaseCallback = firebaseCallback;
        //this.firebaseCallback = firebaseCallback;
    }*/


    @Override
    public DataSource<Long, Chat> create() {
        // pass firebase Callback to ChatsDataSource
        return new ChatsDataSource(mUserKey, firebaseCallback);
    }

}