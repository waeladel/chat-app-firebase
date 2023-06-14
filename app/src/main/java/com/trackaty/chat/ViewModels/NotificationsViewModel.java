package com.trackaty.chat.ViewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trackaty.chat.DataSources.NotificationsDataFactory;
import com.trackaty.chat.models.DatabaseNotification;

public class NotificationsViewModel extends ViewModel {

    private final static String TAG = "NotificationsViewModel";

    private NotificationsDataFactory mDataFactory;
    private PagedList.Config config;
    private  LiveData<PagedList<DatabaseNotification>> itemPagedList;
    //public MutableLiveData<PagedList<Chat>> callbackPagedList;

    private DatabaseReference mDatabaseRef;
    private DatabaseReference mNotificationsRef;

    public NotificationsViewModel(String currentUserId) {

        //callbackPagedList = new MutableLiveData<>();
        // pass UserId and  firebaseCallback to the constructor of ChatsDataFactory

        mDataFactory = new NotificationsDataFactory(currentUserId);

        Log.d(TAG, "NotificationsViewModel init");

        //Enabling Offline Capabilities//
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        // keepSync UserChatsRef to work offline
        mNotificationsRef = mDatabaseRef.child("notifications").child("alerts").child(currentUserId);
        mNotificationsRef.keepSynced(true);

        config = (new PagedList.Config.Builder())
                .setPageSize(10)//10
                .setInitialLoadSizeHint(10)//30
                //.setPrefetchDistance(10)//10
                .setEnablePlaceholders(false)
                .build();


    }

    /*public void setUserId(String UserId){
        //mDataFactory.setUserKey(UserId);
        mDataFactory = new ChatsDataFactory(UserId);
        mNotificationsRef = mDatabaseRef.child("userChats").child(UserId);
        mNotificationsRef.keepSynced(true);
    }*/


    public LiveData<PagedList<DatabaseNotification>> getItemPagedList(){
        if(itemPagedList == null){
            Log.d(TAG, "itemPagedList is null, get chats from database");
            itemPagedList = new LivePagedListBuilder<>(mDataFactory, config).build();
        }
        return itemPagedList ;
    }

    // Set scroll direction and last visible item which is used to get initial key's position
    public void setScrollDirection(int scrollDirection, int lastVisibleItem) {
        //MessagesListRepository.setScrollDirection(scrollDirection);
        mDataFactory.setScrollDirection(scrollDirection, lastVisibleItem);
    }

    // To only update notification's seen when user is opening the notification's tap
    public void setSeeing(boolean isSeeing) {
        Log.d(TAG, "NotificationsViewModel set seeing= "+isSeeing);
        // Remove all listeners when fragment is paused so it doesn't update seen filed to true for notifications that user is not watching in fact
        mDataFactory.setSeeing(isSeeing);
    }


    @Override
    protected void onCleared() {
        Log.d(TAG, "NotificationsViewModel onCleared:");
        //NotificationsRepository.removeListeners();
        // Remove all listeners on viewModel cleared
        mDataFactory.removeListeners();
        super.onCleared();
    }

}
