package com.trackaty.chat.ViewModels;

import android.util.Log;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trackaty.chat.DataSources.MessagesDataFactory;
import com.trackaty.chat.DataSources.MessagesListRepository;
import com.trackaty.chat.DataSources.MessagesRepository;
import com.trackaty.chat.models.Chat;
import com.trackaty.chat.models.Message;
import com.trackaty.chat.models.User;

import java.util.Timer;

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
    public  LiveData<Chat> chat;
    private MessagesRepository messagesRepository;

    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUserMessagesRef;
    private Timer mTimer;
    private  MutableLiveData<CharSequence> agoTime;


    public MessagesViewModel(String chatKey, String chatUserId) {

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
                .setPageSize(10)//10  20
                .setInitialLoadSizeHint(10)//30  20
                //.setPrefetchDistance(10)//10
                .setEnablePlaceholders(false)
                //.setEnablePlaceholders(true)
                .build();

        itemPagedList = new LivePagedListBuilder<>(messagesDataFactory, config).build();
        /*itemPagedList = (new LivePagedListBuilder(messagesDataFactory,config))
                .build();*/
        chat = messagesRepository.getChat(chatKey);
        chatUser = messagesRepository.getUser(chatUserId);

    }


    public LiveData<User> getChatUser(String userId) {
        Log.d(TAG, "getUser"+ userId);
        return chatUser ;
        //chatUser = messagesRepository.getUser(userId);
        //return messagesRepository.getUser(userId);
    }

    /*public LiveData<String> getSenderId(String chatId) {
        Log.d(TAG, "getSenderId chatId"+ chatId);
        return  messagesRepository.getSenderId(chatId);
    }*/

    public LiveData<Chat> getChat(String chatId) {
        Log.d(TAG, "chatId"+ chatId);
        return chat;
        //return  messagesRepository.getChat(chatId);
    }


    /*public LiveData<PagedList<Message>> getMessagesList() {
        Log.d(TAG, "getMessagesList initiated");
        return itemPagedList;
    }*/

    // To make all messages revealed when "reveal forever" is selected
    public void revealMessages(String chatId) {
        Log.d(TAG, "chatId"+ chatId);
        messagesRepository.revealMessages(chatId);

    }

    // Set scroll direction and last visible item which is used to get initialkey's position
    public void setScrollDirection(int scrollDirection, int lastVisibleItem) {
        //MessagesListRepository.setScrollDirection(scrollDirection);
        MessagesListRepository.setScrollDirection(scrollDirection, lastVisibleItem);
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
