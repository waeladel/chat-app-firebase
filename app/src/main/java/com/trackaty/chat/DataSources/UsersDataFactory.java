package com.trackaty.chat.DataSources;

import android.util.Log;

import androidx.paging.DataSource;

import com.trackaty.chat.models.User;


public class UsersDataFactory extends DataSource.Factory<Long, User>{

    private final static String TAG = UsersDataFactory.class.getSimpleName();
    private UsersDataSource usersDataSource;

    public UsersDataFactory() {
        Log.d(TAG, "UsersDataFactory init");
        usersDataSource = new UsersDataSource();
    }

    // Set scroll direction and last visible item which is used to get initialkey's position
    public void setScrollDirection(int scrollDirection, int lastVisibleItem) {
        /*MessagesListRepository.setScrollDirection(scrollDirection);
        this.scrollDirection = scrollDirection;
        this.lastVisibleItem = lastVisibleItem;*/
        // Pass scrolling direction and last/first visible item to data source
        usersDataSource.setScrollDirection(scrollDirection, lastVisibleItem);
    }

    // update user id when it's changed, to update the userRef and to invalidate data and fetch new one
    public void updateCurrentUserId(String userId) {
        usersDataSource.updateCurrentUserId(userId);

    }

    @Override
    public DataSource<Long, User> create() {
        return usersDataSource;
    }

}