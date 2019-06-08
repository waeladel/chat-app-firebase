package com.trackaty.chat.DataSources;

import com.trackaty.chat.models.Message;
import com.trackaty.chat.models.User;

import androidx.paging.DataSource;

public class MessagesDataFactory extends DataSource.Factory<String, Message>{

    public static String mChatKey;
    private MessagesDataSource messagesDataSource;

    // receive chatKey on the constructor
    public MessagesDataFactory(String chatKey) {
        this.mChatKey = chatKey;
        messagesDataSource = new MessagesDataSource(mChatKey);
    }

    // When last database message is not loaded, Invalidate messagesDataSource to scroll down
    public void invalidateData() {
        messagesDataSource.invalidateData();
        //messagesDataSource.loadInitial();
    }

    @Override
    public DataSource<String, Message> create() {
        return messagesDataSource;
    }



}