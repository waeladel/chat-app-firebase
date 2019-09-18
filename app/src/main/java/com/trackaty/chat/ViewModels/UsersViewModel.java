package com.trackaty.chat.ViewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.google.firebase.database.DatabaseReference;
import com.trackaty.chat.DataSources.UserRepository;
import com.trackaty.chat.DataSources.UsersDataFactory;
import com.trackaty.chat.DataSources.UsersRepository;
import com.trackaty.chat.Interface.FirebaseUserCallback;
import com.trackaty.chat.models.User;

public class UsersViewModel extends ViewModel {

    private final static String TAG = UsersViewModel.class.getSimpleName();

    private UsersDataFactory usersDataFactory;
    private PagedList.Config config;
    private LiveData<PagedList<User>> liveDataSource;
    private LiveData<PagedList<User>> usersList;
    private MutableLiveData<LiveData<PagedList<User>>> mutableUsersList;
    //LiveData<ItemKeyedDataSource<Long, User>> liveDataSource;

    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUsersRef;
    private User mUser;
    private UserRepository mUserRepository;
    private String mCurrentUserId ,LastLogOutUserId;

    public UsersViewModel() {
        usersDataFactory = new UsersDataFactory();
        Log.d(TAG, "mama UsersViewModel init");
        mUserRepository = new UserRepository();
        mutableUsersList = new MutableLiveData<>();
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

        usersList = new LivePagedListBuilder<>(usersDataFactory, config).build();
        mutableUsersList.setValue(usersList);
    }

    public LiveData<PagedList<User>> getItemPagedList(String userId){
        /*if(usersList == null){
            Log.d(TAG, "itemPagedList is null, get items from database");
            usersList = new LivePagedListBuilder<>(usersDataFactory, config).build();
        }*/
        // if currentUser id is changed, update PagedList to fetch the data of the new user
        /*if(userId != null && !TextUtils.equals(userId, mCurrentUserId)){
            usersList = new LivePagedListBuilder<>(usersDataFactory, config).build();
            Log.d(TAG, "currentUser id is changed. create new list. userId= "+userId +" old viewModel id= "+mCurrentUserId );
            return usersList;
        }else{
            Log.d(TAG, "same user id. return existing list. userId= "+userId +" old viewModel id= "+mCurrentUserId);
            return usersList ;
        }*/
        return usersList ;

    }

    // update user id when it's changed, to update the userRef and to invalidate data and fetch new one
    public void updateCurrentUserId(String userId) {
        usersDataFactory.updateCurrentUserId(userId);
    }

    public String getCurrentUserId() {
        return mCurrentUserId;
    }

    public void setCurrentUserId(String uid) {
        this.mCurrentUserId = uid;
    }

    public void setLastLogOutUserId(String LastLogOutUserId) {
        this.LastLogOutUserId = LastLogOutUserId;
    }

    // used to know if the same user logged out then logged in again with the same account
    // if LastLogOutUserId is not set we can't know if he is the previous user or not, hence his list will be empty because it's not different user
    public String getLastLogOutUserId() {
        return LastLogOutUserId;
    }

    // get current user to send it's sound id extra to alarm receiver
    public void getUserOnce(String userId, FirebaseUserCallback callback) {
        mUserRepository.getUserOnce(userId, callback);
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

    public void clearViewModel() {
        Log.d(TAG, "removeListeners");
        onCleared();
    }

    @Override
    protected void onCleared() {
        Log.d(TAG, "mama UsersViewModel onCleared:");
        UsersRepository.removeListeners();
        super.onCleared();
    }

}