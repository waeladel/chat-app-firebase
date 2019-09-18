package com.trackaty.chat.DataSources;

import android.util.Log;

import com.trackaty.chat.models.Chat;

import androidx.annotation.NonNull;
import androidx.paging.ItemKeyedDataSource;

public class ChatsDataSource extends ItemKeyedDataSource<Long, Chat> {

    private final static String TAG = ChatsDataSource.class.getSimpleName();
    private String mChatKey;
    private ChatsRepository chatsRepository;

    // get chatKey on the constructor
    public ChatsDataSource(String chatKey){
        //chatsRepository = new ChatsRepository(chatKey);
        this.mChatKey = chatKey;
        Log.d(TAG, "mama ChatsDataSource initiated ");
       /* usersRepository.getUsersChangeSubject().observeOn(Schedulers.io()).subscribeOn(Schedulers.computation()).subscribe();{
            invalidate();
            Log.d(TAG, "mama invalidate ");
        }*/

    }
    // Pass scrolling direction and last/first visible item to the repository
    public void setScrollDirection(int scrollDirection, int lastVisibleItem){
        chatsRepository.setScrollDirection(scrollDirection, lastVisibleItem);
    }

    // a callback to invalidate the data whenever a change happen
    @Override
    public void addInvalidatedCallback(@NonNull InvalidatedCallback onInvalidatedCallback) {
        //super.addInvalidatedCallback(onInvalidatedCallback);
        Log.d(TAG, "mama Callback ChatsDataSource addInvalidatedCallback ");
        // pass firebase Callback to ChatsRepository
        chatsRepository = new ChatsRepository(mChatKey, onInvalidatedCallback);
        //chatsRepository.ChatsChanged(onInvalidatedCallback);
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
    public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull LoadInitialCallback<Chat> callback) {
        /*List<User> items = usersRepository.getMessages(params.requestedInitialKey, params.requestedLoadSize);
        callback.onResult(items);*/
        Log.d(TAG, "mama loadInitial params key" +params.requestedInitialKey+" LoadSize " + params.requestedLoadSize);
        chatsRepository.getChats(params.requestedInitialKey, params.requestedLoadSize, callback);
        //usersRepository.getMessages( 0L, params.requestedLoadSize, callback);

    }

    // load next page
    @Override
    public void loadAfter(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Chat> callback) {
        /*List<User> items = usersRepository.getMessages(params.key, params.requestedLoadSize);
        callback.onResult(items);*/
        chatsRepository.setLoadBeforeCallback(params.key , callback);
        Log.d(TAG, "mama loadAfter params key " + params.key+" LoadSize " + params.requestedLoadSize);
        // using getBefore instead of getAfter because the order is reversed
        //chatsRepository.getBefore(params.key -1, params.requestedLoadSize, callback);
        chatsRepository.getChatsBefore(params.key , params.requestedLoadSize, callback);
    }

    // load previous page
    @Override
    public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Chat> callback) {
        /*List<User> items = fetchItemsBefore(params.key, params.requestedLoadSize);
        callback.onResult(items);*/
        chatsRepository.setLoadAfterCallback(params.key , callback);
        Log.d(TAG, "mama loadBefore params " + params.key+" LoadSize " + params.requestedLoadSize);
        // using getAfter instead of getBefore because the order is reversed
        //chatsRepository.getAfter(params.key +1, params.requestedLoadSize, callback);
        chatsRepository.getChatsAfter(params.key , params.requestedLoadSize, callback);
    }

    @NonNull
    @Override
    public Long getKey(@NonNull Chat chat) {
        return  chat.getLastSentLong();
    }
}
