package com.trackaty.chat.ViewModels;

import android.util.Log;

import com.trackaty.chat.DataSources.MessagesDataFactory;
import com.trackaty.chat.DataSources.UsersRepository;
import com.trackaty.chat.models.Message;
import com.trackaty.chat.models.User;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

public class MainActivityViewModel extends ViewModel {

    private final static String TAG = MainActivityViewModel.class.getSimpleName();
    private UsersRepository usersRepository;
    public  MutableLiveData<User> currentUser;
    private MutableLiveData<String> currentUserId;

    public MainActivityViewModel() {

        // pass userId to the constructor of MessagesDataFactory
        usersRepository = new UsersRepository();
        currentUserId = new MutableLiveData<>();
        //liveDataSource = messagesDataFactory.getItemLiveDataSource();
        Log.d(TAG, "MainActivityViewModel init");
    }

    public MutableLiveData<String> getCurrentUserId() {
        Log.d(TAG, "getCurrentUserId initiated");
        if(currentUserId == null){
            currentUserId = new MutableLiveData<>();
        }
        return currentUserId;
    }

    public void updateCurrentUserId(String userId) {
        Log.d(TAG, "updateCurrentUserId initiated:"+ userId);
        if(currentUserId == null){
            currentUserId = new MutableLiveData<>();
        }
        currentUserId.setValue(userId);
        getCurrentUser(); // to fitch user with the new id
    }

    public MutableLiveData<User> getCurrentUser() {
        Log.d(TAG, "getCurrentUser"+ currentUserId);
        currentUser = usersRepository.getCurrentUser(currentUserId.getValue());
        return currentUser;
    }

    @Override
    protected void onCleared() {
        Log.d(TAG, "mama MainActivityViewModel onCleared:");
        super.onCleared();
    }


}
