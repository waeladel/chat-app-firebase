package com.trackaty.chat.ViewModels;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.trackaty.chat.DataSources.MessagesDataFactory;
import com.trackaty.chat.DataSources.MessagesListRepository;
import com.trackaty.chat.DataSources.MessagesRepository;
import com.trackaty.chat.models.Message;
import com.trackaty.chat.models.User;

import java.util.ArrayList;
import java.util.List;

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
    private MessagesListRepository messagesListRepository;
    private MutableLiveData<Boolean> isInvalid;


    private Query getMessagesQuery;
    // [START declare_database_ref]
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mMessagesRef;
    private DatabaseReference mUsersRef;

    List<Message> initialMessagesList = new ArrayList<>();
    private Boolean isFirstLoaded = true;



    public MessagesViewModel(String chatKey) {

        // pass chatKey to the constructor of MessagesDataFactory
        messagesDataFactory = new MessagesDataFactory(chatKey);
        messagesRepository = new MessagesRepository();
        //liveDataSource = messagesDataFactory.getItemLiveDataSource();
        Log.d(TAG, "Message MessagesViewModel init");

        config = (new PagedList.Config.Builder())
                .setPageSize(5)//10
                .setInitialLoadSizeHint(5)//30
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
        messagesRepository.removeListeners();

        MessagesListRepository.removeListeners();

        super.onCleared();
    }

}
