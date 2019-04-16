package com.trackaty.chat.DataSources;

import com.trackaty.chat.models.User;

import androidx.paging.DataSource;

public class UsersDataFactory extends DataSource.Factory<String, User>{
    @Override
    public DataSource<String, User> create() {
        return new UsersDataSource();
    }
}