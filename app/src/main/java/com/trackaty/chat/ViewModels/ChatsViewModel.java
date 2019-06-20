package com.trackaty.chat.ViewModels;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trackaty.chat.DataSources.ChatsDataFactory;
import com.trackaty.chat.DataSources.ChatsRepository;
import com.trackaty.chat.models.Chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

public class ChatsViewModel extends ViewModel {

    private final static String TAG = ChatsViewModel.class.getSimpleName();

    private ChatsDataFactory chatsDataFactory;
    private PagedList.Config config;
    private  LiveData<PagedList<Chat>> itemPagedList;
    //public MutableLiveData<PagedList<Chat>> callbackPagedList;

    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUserChatsRef;


    public ChatsViewModel(String currentUserId) {

        //callbackPagedList = new MutableLiveData<>();
        // pass UserId and  firebaseCallback to the constructor of ChatsDataFactory

        chatsDataFactory = new ChatsDataFactory(currentUserId);

        Log.d(TAG, "Chat ChatsViewModel init");

        //Enabling Offline Capabilities//
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        // keepSync UserChatsRef to work offline
        mUserChatsRef = mDatabaseRef.child("userChats").child(currentUserId);
        mUserChatsRef.keepSynced(true);

        config = (new PagedList.Config.Builder())
                .setPageSize(10)//10
                .setInitialLoadSizeHint(10)//30
                //.setPrefetchDistance(10)//10
                .setEnablePlaceholders(false)
                .build();



    }

    /*public void setUserId(String UserId){
        //chatsDataFactory.setUserKey(UserId);
        chatsDataFactory = new ChatsDataFactory(UserId);
        mUserChatsRef = mDatabaseRef.child("userChats").child(UserId);
        mUserChatsRef.keepSynced(true);
    }*/


    public LiveData<PagedList<Chat>> getItemPagedList(){
        if(itemPagedList == null){
            Log.d(TAG, "itemPagedList is null, get chats from database");
            itemPagedList = new LivePagedListBuilder<>(chatsDataFactory, config).build();
        }
        return itemPagedList ;
    }

    // Set scroll direction and last visible item which is used to get initialkey's position
    public void setScrollDirection(int scrollDirection, int lastVisibleItem) {
        //MessagesListRepository.setScrollDirection(scrollDirection);
        chatsDataFactory.setScrollDirection(scrollDirection, lastVisibleItem);
    }


    @Override
    protected void onCleared() {
        Log.d(TAG, "mama ChatsViewModel onCleared:");
        ChatsRepository.removeListeners();
        super.onCleared();
    }

}
