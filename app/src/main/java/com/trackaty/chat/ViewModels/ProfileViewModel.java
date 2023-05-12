package com.trackaty.chat.ViewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.trackaty.chat.DataSources.RelationRepository;
import com.trackaty.chat.DataSources.UserRepository;
import com.trackaty.chat.Interface.FirebaseUserCallback;
import com.trackaty.chat.models.Relation;
import com.trackaty.chat.models.User;

public class ProfileViewModel extends ViewModel {

    private final static String TAG = ProfileViewModel.class.getSimpleName();


    private RelationRepository relationRepository;
    private UserRepository mUserRepository;

    private LiveData<Relation> mRelation;
    private LiveData<Long> mLoveCount;
    private LiveData<Long> mPickUpCount;
    private LiveData<String> mLoveStatues;
    private LiveData<User> mUser;


    public ProfileViewModel() {

        // pass chatKey to the constructor of MessagesDataFactory
        Log.d(TAG, "ProfileViewModel init");
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
        }else{
            Log.d(TAG, "mRelation already exist");
        }
        return mRelation;
    }


    public void cancelRequest(String currentUserId, String userId) {
        relationRepository.cancelRequest(currentUserId, userId);
    }

    // update love and favourites Favorite
    //public void sendLove(String currentUserId, String name, String avatar, String userId) {
    public void sendLove(String currentUserId, String name, String avatar, String userId, String notificationType) {
        relationRepository.sendLove(currentUserId ,name, avatar, userId, notificationType);
    }

    /*public void sendLove(String currentUserId, String name, String avatar, String userId) {
        relationRepository.sendLove(currentUserId, name, avatar, userId);
    }*/

    // cancel love and favourites
    public void cancelLove(String currentUserId, String userId) {
        relationRepository.cancelLove(currentUserId, userId);
    }



    // get love counts
    public LiveData<Long> getLoveCount(String userId) {
        if(mLoveCount == null){
            Log.d(TAG, "mLoveCount is null, get relation from database");
            mLoveCount = relationRepository.getLoveCount(userId);
        }
        return mLoveCount;

    }

    // get love counts
    public LiveData<Long> getPickUpCount(String userId) {
        if(mPickUpCount == null){
            Log.d(TAG, "mPickUpCount is null, get relation from database");
            mPickUpCount = relationRepository.getPickUpCount(userId);
        }
        return mPickUpCount;
    }

    // get love counts
    public LiveData<String> getLoveStatues(String currentUserId, String userId) {
        if(mLoveStatues == null){
            Log.d(TAG, "mLoveStatues is null, get relation from database");
            mLoveStatues = relationRepository.getLoveStatues(currentUserId, userId);
        }
        return mLoveStatues;
    }

    public void blockUser(String currentUserId, String userId, String relation, boolean isDeleteChat) {
        relationRepository.blockUser(currentUserId, userId, relation, isDeleteChat);
    }


    public void unblockUser(String currentUserId, String userId, String relationStatus) {
        relationRepository.unblockUser(currentUserId, userId, relationStatus);
    }

    public LiveData<User> getUser(String currentUserId){
        if(mUser == null){
            Log.d(TAG, "mUser is null, get relation from database");
            mUser =  mUserRepository.getCurrentUser(currentUserId);
        }
        return mUser;
    }

    public void getUserOnce(String userId, FirebaseUserCallback callback) {
        mUserRepository.getUserOnce(userId, callback);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "mama ProfileViewModel onCleared:");
        // Remove all Listeners from relationRepository
        relationRepository.removeListeners();
        mUserRepository.removeListeners();
    }
}
