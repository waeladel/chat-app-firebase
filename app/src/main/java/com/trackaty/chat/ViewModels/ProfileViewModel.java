package com.trackaty.chat.ViewModels;

import android.util.Log;

import com.trackaty.chat.DataSources.RelationRepository;
import com.trackaty.chat.models.Relation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;

public class ProfileViewModel extends ViewModel {

    private final static String TAG = ProfileViewModel.class.getSimpleName();


    private RelationRepository relationRepository;

    public ProfileViewModel() {

        // pass chatKey to the constructor of MessagesDataFactory
        Log.d(TAG, "ProfileViewModel init");
        relationRepository = new RelationRepository();

    }

    /*public LiveData<User> sendReq(String UserId){

        return userRepository.getUser(UserId);
    }*/

    public LiveData<Relation> getRelation(String currentUserId , String userId){
        return relationRepository.getRelation(currentUserId, userId);
    }

    public void cancelRequest(String currentUserId, String userId) {
        relationRepository.cancelRequest(currentUserId, userId);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "mama ProfileViewModel onCleared:");
        // Remove all Listeners from relationRepository
        relationRepository.removeListeners();
    }
}
