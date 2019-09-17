package com.trackaty.chat.ViewModels;

import android.util.Log;

import com.trackaty.chat.dataSources.RelationRepository;
import com.trackaty.chat.dataSources.UserRepository;
import com.trackaty.chat.models.Relation;
import com.trackaty.chat.models.User;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class MoreProfileViewModel extends ViewModel {

    private final static String TAG = MoreProfileViewModel.class.getSimpleName();


    private RelationRepository relationRepository;
    private UserRepository mUserRepository;
    private LiveData<Relation> mRelation;
    private LiveData<User> mUser;


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
        if(mRelation == null){
            Log.d(TAG, "mRelation is null, get relation from database");
            mRelation = relationRepository.getRelation(currentUserId, userId);
        }
        return mRelation;

    }


    public LiveData<User> getUser(String userId){
        if(mUser == null){
            Log.d(TAG, "mUser is null, get relation from database");
            mUser =  mUserRepository.getCurrentUser(userId);
        }
        return mUser;
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
