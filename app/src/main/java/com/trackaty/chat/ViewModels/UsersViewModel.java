package com.trackaty.chat.ViewModels;

import android.content.ClipData;
import android.database.Observable;
import android.util.Log;

import com.trackaty.chat.DataSources.UsersDataFactory;
import com.trackaty.chat.DataSources.UsersRepository;
import com.trackaty.chat.models.User;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

public class UsersViewModel extends ViewModel {

    private final static String TAG = UsersViewModel.class.getSimpleName();

    private UsersDataFactory usersDataSourceFactory;
    private PagedList.Config config;
    private LiveData<PagedList<User>> liveDataSource;
    public final LiveData<PagedList<User>> usersList;
    //LiveData<ItemKeyedDataSource<Long, User>> liveDataSource;

    public UsersViewModel() {
        usersDataSourceFactory = new UsersDataFactory();
        Log.d(TAG, "mama UsersViewModel init");
        //liveDataSource = usersDataSourceFactory.getUserLiveDataSource();

        config = (new PagedList.Config.Builder())
                .setPageSize(30)//10
                .setInitialLoadSizeHint(30)//30
                //.setPrefetchDistance(10)//10
                .setEnablePlaceholders(false)
                .build();

        usersList = new LivePagedListBuilder<>(usersDataSourceFactory, config).build();
    }

    /*public Observable<PagedList> getPagedListObservable(){
        return new RxPagedListBuilder(usersDataSourceFactory, config).buildObservable();
    }*/

    /*public LiveData<PagedList<User>> getPagedListObservable(){
        Log.d(TAG, "mama UsersViewModel getPagedListObservable");
        return new LivePagedListBuilder<>(usersDataSourceFactory, config).build();
    }*/

    @Override
    protected void onCleared() {
        Log.d(TAG, "mama UsersViewModel onCleared:");
        super.onCleared();
    }
}