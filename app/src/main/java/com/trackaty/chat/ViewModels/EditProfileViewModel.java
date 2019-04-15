package com.trackaty.chat.ViewModels;

import android.util.Log;

import com.trackaty.chat.DataSources.UserRepository;
import com.trackaty.chat.Interface.FirebaseUserCallback;
import com.trackaty.chat.models.User;

import androidx.lifecycle.ViewModel;

public class EditProfileViewModel extends ViewModel {

    private final static String TAG = EditProfileViewModel.class.getSimpleName();


    private UserRepository mUserRepository;
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public EditProfileViewModel() {

        // pass chatKey to the constructor of MessagesDataFactory
        Log.d(TAG, "EditProfileViewModel init");
        mUserRepository = new UserRepository();

    }

     /*public LiveData<User> sendReq(String UserId){
        return userRepository.getUser(UserId);
    }*/


    /*public LiveData<User> getUser(String userId){
        return mUserRepository.getCurrentUser(userId);
    }*/

    public void getUserOnce(String userId, FirebaseUserCallback callback) {
        if(user == null){
            mUserRepository.getUserOnce(userId, callback);
        }
    }



    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "mama EditProfileViewModel onCleared:");
        // Remove all Listeners from relationRepository
        mUserRepository.removeListeners();
    }
}
