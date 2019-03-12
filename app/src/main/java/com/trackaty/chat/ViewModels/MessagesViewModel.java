package com.trackaty.chat.ViewModels;

import android.util.Log;

import com.trackaty.chat.DataSources.MessagesDataFactory;
import com.trackaty.chat.DataSources.UsersDataFactory;
import com.trackaty.chat.models.Message;
import com.trackaty.chat.models.User;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

public class MessagesViewModel extends ViewModel {

    private final static String TAG = MessagesViewModel.class.getSimpleName();
    private MessagesDataFactory messagesDataFactory;
    private PagedList.Config config;
    public final LiveData<PagedList<Message>> itemPagedList;
    LiveData<ItemKeyedDataSource<String, Message>> liveDataSource;

    public MessagesViewModel(String chatKey) {

        // pass chatKey to the constructor of MessagesDataFactory
        messagesDataFactory = new MessagesDataFactory(chatKey);
        //liveDataSource = messagesDataFactory.getItemLiveDataSource();
        Log.d(TAG, "Message MessagesViewModel init");

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

    @Override
    protected void onCleared() {
        Log.d(TAG, "mama MessagesViewModel onCleared:");
        super.onCleared();
    }

    /*public LiveData<PagedList<Message>> messagesLiveData(String mChatId) {
        return new LivePagedListBuilder<>(messagesDataFactory, config).build();
    }*/
}
