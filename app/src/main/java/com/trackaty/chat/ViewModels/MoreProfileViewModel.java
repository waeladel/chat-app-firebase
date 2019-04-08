package com.trackaty.chat.ViewModels;

import android.util.Log;

import com.trackaty.chat.DataSources.RelationRepository;
import com.trackaty.chat.DataSources.UserRepository;
import com.trackaty.chat.models.Relation;
import com.trackaty.chat.models.User;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class MoreProfileViewModel extends ViewModel {

    private final static String TAG = MoreProfileViewModel.class.getSimpleName();


    private RelationRepository relationRepository;
    private UserRepository mUserRepository;

    public MoreProfileViewModel() {

        // pass chatKey to the constructor of MessagesDataFactory
        Log.d(TAG, "MoreProfileViewModel init");
        relationRepository = new RelationRepository();
        mUserRepository = new UserRepository();

    }

     /*public LiveData<User> sendReq(String UserId){
        return userRepository.getUser(UserId);
    }*/

    public LiveData<Relation> getRelation(String currentUserId , String userId){
        return relationRepository.getRelation(currentUserId, userId);
    }


    public LiveData<User> getUser(String userId){
        return mUserRepository.getCurrentUser(userId);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "mama MoreProfileViewModel onCleared:");
        // Remove all Listeners from relationRepository
        relationRepository.removeListeners();
        mUserRepository.removeListeners();
    }
}
