package com.trackaty.chat.DataSources;

import com.trackaty.chat.models.Chat;
import com.trackaty.chat.models.Message;

import androidx.paging.DataSource;

public class ChatsDataFactory extends DataSource.Factory<Long, Chat>{

    private String mUserKey;

    // receive chatKey on the constructor
    public ChatsDataFactory(String userKey) {
        this.mUserKey = userKey;
    }

    @Override
    public DataSource<Long, Chat> create() {
        return new ChatsDataSource(mUserKey);
    }

}