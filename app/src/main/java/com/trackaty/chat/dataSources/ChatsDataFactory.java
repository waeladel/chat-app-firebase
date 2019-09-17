package com.trackaty.chat.dataSources;

import com.trackaty.chat.models.Chat;

import androidx.paging.DataSource;

public class ChatsDataFactory extends DataSource.Factory<Long, Chat>{

    private String mUserKey;
    private int scrollDirection;
    private int lastVisibleItem;
    private ChatsDataSource chatsDataSource;

    // receive userId on the constructor to get users chats from database
    public ChatsDataFactory(String userId) {
        mUserKey = userId;
        chatsDataSource = new ChatsDataSource(userId);
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
        chatsDataSource.setScrollDirection(scrollDirection, lastVisibleItem);
    }


    @Override
    public DataSource<Long, Chat> create() {
        // pass firebase Callback to ChatsDataSource
        //return new ChatsDataSource(mUserKey);
        return chatsDataSource;
    }

}