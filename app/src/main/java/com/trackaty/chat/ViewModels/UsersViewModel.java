package com.trackaty.chat.ViewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.google.firebase.database.DatabaseReference;
import com.trackaty.chat.DataSources.UsersDataFactory;
import com.trackaty.chat.DataSources.UsersRepository;
import com.trackaty.chat.models.User;

public class UsersViewModel extends ViewModel {

    private final static String TAG = UsersViewModel.class.getSimpleName();

    private UsersDataFactory usersDataFactory;
    private PagedList.Config config;
    private LiveData<PagedList<User>> liveDataSource;
    private  LiveData<PagedList<User>> usersList;
    //LiveData<ItemKeyedDataSource<Long, User>> liveDataSource;

    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUsersRef;

    public UsersViewModel() {
        usersDataFactory = new UsersDataFactory();
        Log.d(TAG, "mama UsersViewModel init");
        //liveDataSource = usersDataSourceFactory.getUserLiveDataSource();

        //Enabling Offline Capabilities//
        //Offline Capabilities should be for search results not all users//

        /*mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mUsersRef = mDatabaseRef.child("users");
        mUsersRef.keepSynced(true);*/

        config = (new PagedList.Config.Builder())
                .setPageSize(10)//10
                .setInitialLoadSizeHint(10)//30
                //.setPrefetchDistance(10)//10
                .setEnablePlaceholders(false)
                .build();

        //usersList = new LivePagedListBuilder<>(usersDataSourceFactory, config).build();
    }

    public LiveData<PagedList<User>> getItemPagedList(){
        if(usersList == null){
            Log.d(TAG, "itemPagedList is null, get items from database");
            usersList = new LivePagedListBuilder<>(usersDataFactory, config).build();
        }
        return usersList ;
    }

    /*public Observable<PagedList> getPagedListObservable(){
        return new RxPagedListBuilder(usersDataSourceFactory, config).buildObservable();
    }*/

    /*public LiveData<PagedList<User>> getPagedListObservable(){
        Log.d(TAG, "mama UsersViewModel getPagedListObservable");
        return new LivePagedListBuilder<>(usersDataSourceFactory, config).build();
    }*/

    // Set scroll direction and last visible item which is used to get initial key's position
    public void setScrollDirection(int scrollDirection, int lastVisibleItem) {
        //MessagesListRepository.setScrollDirection(scrollDirection);
        usersDataFactory.setScrollDirection(scrollDirection, lastVisibleItem);
    }

    @Override
    protected void onCleared() {
        Log.d(TAG, "mama UsersViewModel onCleared:");
        UsersRepository.removeListeners();
        super.onCleared();
    }
}