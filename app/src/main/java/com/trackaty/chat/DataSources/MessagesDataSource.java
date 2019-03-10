package com.trackaty.chat.DataSources;

import android.util.Log;

import com.trackaty.chat.models.Message;
import com.trackaty.chat.models.User;

import androidx.annotation.NonNull;
import androidx.paging.ItemKeyedDataSource;

public class MessagesDataSource extends ItemKeyedDataSource<String, Message> {

    private final static String TAG = MessagesDataSource.class.getSimpleName();
    private String mChatKey;
    private MessagesRepository messagesRepository;

    // get chatKey on the constructor
    public MessagesDataSource(String chatKey){
        messagesRepository = new MessagesRepository(chatKey);
        this.mChatKey = chatKey;
       /* usersRepository.getUsersChangeSubject().observeOn(Schedulers.io()).subscribeOn(Schedulers.computation()).subscribe();{
            invalidate();
            Log.d(TAG, "mama invalidate ");
        }*/
    }

    // a callback to invalidate the data whenever a change happen
    @Override
    public void addInvalidatedCallback(@NonNull InvalidatedCallback onInvalidatedCallback) {
        //super.addInvalidatedCallback(onInvalidatedCallback);
        Log.d(TAG, "mama Callback Invalidated ");
        messagesRepository.MessagesChanged(onInvalidatedCallback);
        //invalidate();
    }

    @Override
    public void invalidate() {
        Log.d(TAG, "mama Invalidated ");
        super.invalidate();
    }

    @Override
    public boolean isInvalid() {
        Log.d(TAG, "isInvalid = "+super.isInvalid());
        return super.isInvalid();
    }

    // load the initial data based on page size and key (key in null on the first load)
    @Override
    public void loadInitial(@NonNull LoadInitialParams<String> params, @NonNull LoadInitialCallback<Message> callback) {
        /*List<User> items = usersRepository.getMessages(params.requestedInitialKey, params.requestedLoadSize);
        callback.onResult(items);*/
        Log.d(TAG, "mama loadInitial params key" +params.requestedInitialKey+" LoadSize " + params.requestedLoadSize);
        messagesRepository.getMessages(params.requestedInitialKey, params.requestedLoadSize, callback);
        //usersRepository.getMessages( 0L, params.requestedLoadSize, callback);

    }

    // load next page
    @Override
    public void loadAfter(@NonNull LoadParams<String> params, @NonNull LoadCallback<Message> callback) {
        /*List<User> items = usersRepository.getMessages(params.key, params.requestedLoadSize);
        callback.onResult(items);*/
        Log.d(TAG, "mama loadAfter params key " + params.key+" LoadSize " + params.requestedLoadSize);
        messagesRepository.getMessagesAfter(params.key, params.requestedLoadSize, callback);
    }

    // load previous page
    @Override
    public void loadBefore(@NonNull LoadParams<String> params, @NonNull LoadCallback<Message> callback) {
        /*List<User> items = fetchItemsBefore(params.key, params.requestedLoadSize);
        callback.onResult(items);*/
        Log.d(TAG, "mama loadBefore params " + params.key+" LoadSize " + params.requestedLoadSize);
        messagesRepository.getMessagesBefore(params.key, params.requestedLoadSize, callback);
    }

    @NonNull
    @Override
    public String getKey(@NonNull Message message) {
        return  message.getKey();
    }
}