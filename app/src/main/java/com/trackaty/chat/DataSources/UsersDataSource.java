package com.trackaty.chat.DataSources;

import android.util.Log;

import com.trackaty.chat.models.User;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.paging.ItemKeyedDataSource;
import rx.schedulers.Schedulers;

public class UsersDataSource extends ItemKeyedDataSource<Long, User> {

    private final static String TAG = UsersDataSource.class.getSimpleName();

    private UsersRepository usersRepository;

    public UsersDataSource(){
        usersRepository = new UsersRepository();
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
        usersRepository.usersChanged(onInvalidatedCallback);
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
    public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull LoadInitialCallback<User> callback) {
        /*List<User> items = usersRepository.getUsers(params.requestedInitialKey, params.requestedLoadSize);
        callback.onResult(items);*/
        Log.d(TAG, "mama loadInitial params key" +params.requestedInitialKey+" " + params.requestedLoadSize);
        usersRepository.getUsers( params.requestedInitialKey, params.requestedLoadSize, callback);
        //usersRepository.getUsers( 0L, params.requestedLoadSize, callback);

    }

    // load next page
    @Override
    public void loadAfter(@NonNull LoadParams<Long> params, @NonNull LoadCallback<User> callback) {
        /*List<User> items = usersRepository.getUsers(params.key, params.requestedLoadSize);
        callback.onResult(items);*/
        Log.d(TAG, "mama loadAfter params key " + (params.key+1));
        usersRepository.getUsersAfter(params.key +1, params.requestedLoadSize, callback);
    }

    // load previous page
    @Override
    public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<User> callback) {
        /*List<User> items = fetchItemsBefore(params.key, params.requestedLoadSize);
        callback.onResult(items);*/
        Log.d(TAG, "mama loadBefore params " + (params.key-1));
        usersRepository.getUsersBefore(params.key -1, params.requestedLoadSize, callback);
    }

    @NonNull
    @Override
    public Long getKey(@NonNull User user) {
        return  user.getCreatedLong();
    }
}
