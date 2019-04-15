package com.trackaty.chat.DataSources;

import com.trackaty.chat.models.Chat;

import androidx.paging.DataSource;

public class ChatsDataFactory extends DataSource.Factory<Long, Chat>{

    private String mUserKey;

    // receive chatKey on the constructor
    public ChatsDataFactory(String userKey) {
        this.mUserKey = userKey;
    }

    /*public void setCallback(FirebaseChatsCallback firebaseCallback) {
        this.firebaseCallback = firebaseCallback;
        //this.firebaseCallback = firebaseCallback;
    }*/


    @Override
    public DataSource<Long, Chat> create() {
        // pass firebase Callback to ChatsDataSource
        return new ChatsDataSource(mUserKey);
    }

}