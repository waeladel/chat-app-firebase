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
    private MutableLiveData<Long> chatCount;

    public MainActivityViewModel() {

        // pass userId to the constructor of MessagesDataFactory
        userRepository = new UserRepository();
        currentUserId = new MutableLiveData<>();
        chatCount = new MutableLiveData<>();
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
        Log.d(TAG, "updateCurrentUserId initiated: userId= "+ userId);
        if(currentUserId == null){
            currentUserId = new MutableLiveData<>();
        }

        Log.d(TAG, "updateCurrentUserId currentUserId= "+ currentUserId.getValue());
        // if currentUser id is changed, update chat count
        if(!userId.equals(currentUserId.getValue())){
            // Get the chat counts of the new user
            chatCount = userRepository.getChatsCount(userId);
            Log.d(TAG, "updateCurrentUserId chatCount= "+ chatCount.getValue());
        }
        currentUserId.setValue(userId);
        //getCurrentUser(); // to fitch user with the new id
    }

    public MutableLiveData<User> getCurrentUser() {
        Log.d(TAG, "getUser"+ currentUserId);
        currentUser = userRepository.getCurrentUser(currentUserId.getValue());
        return currentUser;
    }

    // Get counts for unread chats
    public MutableLiveData<Long> getChatsCount(String userId) {
        Log.d(TAG, "getChatsCount"+ userId);
        chatCount = userRepository.getChatsCount(userId);
        Log.d(TAG, "getChatsCount chatCount= "+ chatCount.getValue());
        return chatCount;
    }

    public void clearViewModel() {
        Log.d(TAG, "removeListeners");
        onCleared();
    }

    @Override
    protected void onCleared() {
        Log.d(TAG, "mama MainActivityViewModel onCleared:");
        userRepository.removeListeners();
        super.onCleared();
    }


}
