package com.trackaty.chat.ViewModels;

import android.text.format.DateUtils;
import android.util.Log;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trackaty.chat.DataSources.MessagesDataFactory;
import com.trackaty.chat.DataSources.MessagesListRepository;
import com.trackaty.chat.DataSources.MessagesRepository;
import com.trackaty.chat.models.Message;
import com.trackaty.chat.models.User;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

public class MessagesViewModel extends ViewModel {

    private final static String TAG = MessagesViewModel.class.getSimpleName();
    private MessagesDataFactory messagesDataFactory;
    private PagedList.Config config;
    public LiveData<PagedList<Message>> itemPagedList;
    public  MutableLiveData<PagedList<Message>> items;
    LiveData<ItemKeyedDataSource<String, Message>> liveDataSource;
    public  LiveData<User> chatUser;
    private MessagesRepository messagesRepository;

    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUserMessagesRef;
    private Timer mTimer;
    private  MutableLiveData<CharSequence> agoTime;


    public MessagesViewModel(String chatKey) {

        // pass chatKey to the constructor of MessagesDataFactory
        messagesDataFactory = new MessagesDataFactory(chatKey);
        messagesRepository = new MessagesRepository();
        agoTime = new MutableLiveData<>();
        //liveDataSource = messagesDataFactory.getItemLiveDataSource();
        Log.d(TAG, "Message MessagesViewModel init");

        //Enabling Offline Capabilities//
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mUserMessagesRef = mDatabaseRef.child("messages").child(chatKey);
        mUserMessagesRef.keepSynced(true);

        config = (new PagedList.Config.Builder())
                .setPageSize(20)//10
                .setInitialLoadSizeHint(20)//30
                //.setPrefetchDistance(10)//10
                .setEnablePlaceholders(false)
                .build();

        itemPagedList = new LivePagedListBuilder<>(messagesDataFactory, config).build();
        /*itemPagedList = (new LivePagedListBuilder(messagesDataFactory,config))
                .build();*/

    }

    public LiveData<User> getChatUser(String userId) {
        Log.d(TAG, "getUser"+ userId);
        chatUser = messagesRepository.getUser(userId);
        return chatUser;
    }



    public LiveData<PagedList<Message>> getMessagesList() {
        Log.d(TAG, "getMessagesList initiated");
        return itemPagedList;
    }



    @Override
    protected void onCleared() {
        Log.d(TAG, "mama MessagesViewModel onCleared:");

        // Remove all Listeners from messagesRepository
        messagesRepository.removeListeners();

        // Remove all Listeners from MessagesListRepository
        MessagesListRepository.removeListeners();
        super.onCleared();
    }

}
