package com.trackaty.chat.ViewModels;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trackaty.chat.DataSources.ChatsDataFactory;
import com.trackaty.chat.models.Chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

public class ChatsViewModel extends ViewModel {

    private final static String TAG = ChatsViewModel.class.getSimpleName();

    private ChatsDataFactory chatsDataFactory;
    private PagedList.Config config;
    public final LiveData<PagedList<Chat>> itemPagedList;

    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUserChatsRef;

    public ChatsViewModel(String UserId) {

        // pass chatKey to the constructor of MessagesDataFactory
        chatsDataFactory = new ChatsDataFactory(UserId);

        Log.d(TAG, "Chat ChatsViewModel init");

        //Enabling Offline Capabilities//
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mUserChatsRef = mDatabaseRef.child("userChats").child(UserId);
        mUserChatsRef.keepSynced(true);

        config = (new PagedList.Config.Builder())
                .setPageSize(20)//10
                .setInitialLoadSizeHint(20)//30
                //.setPrefetchDistance(10)//10
                .setEnablePlaceholders(false)
                .build();

        itemPagedList = new LivePagedListBuilder<>(chatsDataFactory, config).build();

    }

    @Override
    protected void onCleared() {
        Log.d(TAG, "mama ChatsViewModel onCleared:");
        //Todo : remove all listeners
        super.onCleared();
    }
}
