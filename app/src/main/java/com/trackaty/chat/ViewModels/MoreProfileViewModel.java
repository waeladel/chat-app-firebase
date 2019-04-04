package com.trackaty.chat.ViewModels;

import android.util.Log;

import com.trackaty.chat.DataSources.RelationRepository;
import com.trackaty.chat.models.Relation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class MoreProfileViewModel extends ViewModel {

    private final static String TAG = MoreProfileViewModel.class.getSimpleName();


    private RelationRepository relationRepository;

    public MoreProfileViewModel() {

        // pass chatKey to the constructor of MessagesDataFactory
        Log.d(TAG, "MoreProfileViewModel init");
        relationRepository = new RelationRepository();

    }

    /*public LiveData<User> sendReq(String UserId){

        return userRepository.getUser(UserId);
    }*/

    public LiveData<Relation> getRelation(String currentUserId , String userId){
        return relationRepository.getRelation(currentUserId, userId);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "mama ProfileViewModel onCleared:");
        // Remove all Listeners from relationRepository
        relationRepository.removeListeners();
    }
}
