package com.trackaty.chat.DataSources;

import androidx.paging.DataSource;

import com.trackaty.chat.models.Message;

public class MessagesDataFactory extends DataSource.Factory<String, Message>{

    private String mChatKey;
    private MessagesDataSource mDataSource;

    // receive chatKey on the constructor
    public MessagesDataFactory(String chatKey) {
        this.mChatKey = chatKey;
        mDataSource = new MessagesDataSource(mChatKey);
    }

    // To only update message's seen when user is opening the message's tap
    public void setSeeing (boolean seeing) {
        mDataSource.setSeeing(seeing);
    }

    // When last database message is not loaded, Invalidate messagesDataSource to scroll down
    public void invalidateData() {
        mDataSource.invalidateData();
        //messagesDataSource.loadInitial();
    }

    public void removeListeners() {
        mDataSource.removeListeners();
    }

    @Override
    public DataSource<String, Message> create() {
        return mDataSource;
    }



}