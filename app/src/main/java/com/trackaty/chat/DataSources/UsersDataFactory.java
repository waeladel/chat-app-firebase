package com.trackaty.chat.DataSources;

import com.trackaty.chat.models.User;

import androidx.paging.DataSource;

public class UsersDataFactory extends DataSource.Factory<Long, User>{
    @Override
    public DataSource<Long, User> create() {
        return new UsersDataSource();
    }
}