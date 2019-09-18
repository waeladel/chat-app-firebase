package com.trackaty.chat.dataSources;

import androidx.paging.DataSource;

import com.trackaty.chat.models.DatabaseNotification;

public class NotificationsDataFactory extends DataSource.Factory<Long, DatabaseNotification>{

    private String mUserKey;
    private int scrollDirection;
    private int lastVisibleItem;
    private NotificationsDataSource mDataSource;

    // receive userId on the constructor to get users chats from database
    public NotificationsDataFactory(String userId) {
        mUserKey = userId;
        mDataSource = new NotificationsDataSource(userId);
    }

    /*public void setCallback(FirebaseChatsCallback firebaseCallback) {
        this.firebaseCallback = firebaseCallback;
        //this.firebaseCallback = firebaseCallback;
    }*/

    /*public void setUserKey(String userKey) {
        this.mUserKey = userKey;
    }*/

    // Set scroll direction and last visible item which is used to get initial key's position
    public void setScrollDirection(int scrollDirection, int lastVisibleItem) {
        /*MessagesListRepository.setScrollDirection(scrollDirection);
        this.scrollDirection = scrollDirection;
        this.lastVisibleItem = lastVisibleItem;*/
        // Pass scrolling direction and last/first visible item to data source
        mDataSource.setScrollDirection(scrollDirection, lastVisibleItem);
    }

    public void removeListeners() {
        mDataSource.removeListeners();
    }


    @Override
    public DataSource<Long, DatabaseNotification> create() {
        // pass firebase Callback to ChatsDataSource
        //return new ChatsDataSource(mUserKey);
        return mDataSource;
    }

}