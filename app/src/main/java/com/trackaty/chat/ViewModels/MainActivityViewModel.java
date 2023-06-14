package com.trackaty.chat.ViewModels;

import android.util.Log;

import com.trackaty.chat.DataSources.MainActivityRepository;
import com.trackaty.chat.DataSources.UserRepository;
import com.trackaty.chat.models.User;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {

    private final static String TAG = "MainActivityViewModel";
    private MainActivityRepository mainActivityRepository;
    private  MutableLiveData<User> currentUser;
    private MutableLiveData<String> currentUserId;
    private MutableLiveData<Long> chatCount, notificationCount;

    public MainActivityViewModel() {

        // pass userId to the constructor of MessagesDataFactory
        mainActivityRepository = new MainActivityRepository();

        //currentUserId = new MutableLiveData<>();
        //chatCount = new MutableLiveData<>();
        //notificationCount = new MutableLiveData<>();

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
            if(chatCount == null){
                chatCount = new MutableLiveData<>();
            }
            chatCount = mainActivityRepository.getChatsCount(userId);
            Log.d(TAG, "updateCurrentUserId chatCount= "+ chatCount.getValue());

            // update notification count of the new user
            if(notificationCount == null){
                notificationCount = new MutableLiveData<>();
            }
            notificationCount = mainActivityRepository.getNotificationsCount(userId);

        }
        currentUserId.setValue(userId);
        //getCurrentUser(); // to fitch user with the new id
    }

    public MutableLiveData<User> getCurrentUser() {
        Log.d(TAG, "getUser"+ currentUserId);
        currentUser = mainActivityRepository.getCurrentUser(currentUserId.getValue());
        return currentUser;
    }

    // Get counts for unread chats
    public MutableLiveData<Long> getChatsCount(String userId) {
        Log.d(TAG, "getChatsCount: current userId "+ userId);
        if(chatCount == null){
            Log.d(TAG, "chatCount is null, get chatCount from database");
            chatCount = new MutableLiveData<>();
            chatCount = mainActivityRepository.getChatsCount(userId);
        }
        Log.d(TAG, "getChatsCount chatCount Count= "+ chatCount.getValue());
        return chatCount;
    }

    // Get counts for unread chats
    public MutableLiveData<Long> getNotificationsCount(String userId) {
        Log.d(TAG, "getNotificationsCount: current userId= "+ userId);
        if(notificationCount == null){
            Log.d(TAG, "notificationCount is null, get notificationCount from database");
            notificationCount = new MutableLiveData<>();
            notificationCount = mainActivityRepository.getNotificationsCount(userId);
        }
        Log.d(TAG, "getNotificationsCount notification Count= "+ notificationCount.getValue());
        return notificationCount;
    }

    @Override
    protected void onCleared() {
        Log.d(TAG, "mama MainActivityViewModel onCleared:");
        mainActivityRepository.removeListeners();
        super.onCleared();
    }


}
