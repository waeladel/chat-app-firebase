package com.trackaty.chat.dataSources;

import androidx.paging.DataSource;

import com.trackaty.chat.models.User;


public class UsersDataFactory extends DataSource.Factory<Long, User>{

    private UsersDataSource usersDataSource;

    public UsersDataFactory() {
        usersDataSource = new UsersDataSource();
    }

    // Set scroll direction and last visible item which is used to get initial key's position
    public void setScrollDirection(int scrollDirection, int lastVisibleItem) {
        /*MessagesListRepository.setScrollDirection(scrollDirection);
        this.scrollDirection = scrollDirection;
        this.lastVisibleItem = lastVisibleItem;*/
        // Pass scrolling direction and last/first visible item to data source
        usersDataSource.setScrollDirection(scrollDirection, lastVisibleItem);
    }

    @Override
    public DataSource<Long, User> create() {
        return usersDataSource;
    }
}