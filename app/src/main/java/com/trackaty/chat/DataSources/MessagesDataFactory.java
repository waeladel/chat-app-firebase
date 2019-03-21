package com.trackaty.chat.DataSources;

import com.trackaty.chat.models.Message;
import com.trackaty.chat.models.User;

import androidx.paging.DataSource;

public class MessagesDataFactory extends DataSource.Factory<String, Message>{

    public static String mChatKey;

    // receive chatKey on the constructor
    public MessagesDataFactory(String chatKey) {
        this.mChatKey = chatKey;
    }

    @Override
    public DataSource<String, Message> create() {
        return new MessagesDataSource(mChatKey);
    }



}