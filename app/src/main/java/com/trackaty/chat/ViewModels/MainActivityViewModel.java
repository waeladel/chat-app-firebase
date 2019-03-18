package com.trackaty.chat.ViewModels;

import android.util.Log;

import com.trackaty.chat.DataSources.UserRepository;
import com.trackaty.chat.models.User;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {

    private final static String TAG = MainActivityViewModel.class.getSimpleName();
    private UserRepository userRepository;
    public  MutableLiveData<User> currentUser;
    private MutableLiveData<String> currentUserId;

    public MainActivityViewModel() {

        // pass userId to the constructor of MessagesDataFactory
        userRepository = new UserRepository();
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
        currentUser = userRepository.getCurrentUser(currentUserId.getValue());
        return currentUser;
    }

    @Override
    protected void onCleared() {
        Log.d(TAG, "mama MainActivityViewModel onCleared:");
        userRepository.removeListeners();
        super.onCleared();
    }


}
