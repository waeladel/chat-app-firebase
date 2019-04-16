package com.trackaty.chat.DataSources;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.trackaty.chat.models.FirebaseListeners;
import com.trackaty.chat.models.Message;
import com.trackaty.chat.models.User;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;

public class MessagesRepository {

    private final static String TAG = MessagesRepository.class.getSimpleName();

    // [START declare_database_ref]
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUsersRef;
    private Boolean isFirstLoaded = true;
    private List<FirebaseListeners> mListenersList;// = new ArrayList<>();
    private MutableLiveData<User> mUser;


// a listener for chat User changes
private ValueEventListener userListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI
            if (dataSnapshot.exists()) {
                // Get user value
                Log.d(TAG, "getUser dataSnapshot key: "
                        + dataSnapshot.getKey()+" Listener = "+userListener);
                //mCurrentUser = dataSnapshot.getValue(User.class);
                mUser.postValue(dataSnapshot.getValue(User.class));
            } else {
                // User is null, error out
                Log.w(TAG, "User is null, no such user");
            }
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());

        }
    };


    public MessagesRepository(){
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mUsersRef = mDatabaseRef.child("users");
        isFirstLoaded = true;
        Log.d(TAG, "mama MessagesRepository init. isFirstLoaded= " + isFirstLoaded);
        mUser = new MutableLiveData<>();
        if(mListenersList == null){
            mListenersList = new ArrayList<>();
            Log.d(TAG, "mListenersList is null. new ArrayList is created= " + mListenersList.size());
        }
    }

    public MutableLiveData<User> getUser(String userId){

        DatabaseReference userRef = mUsersRef.child(userId);
        //final MutableLiveData<User> mCurrentUser = new MutableLiveData<>();
        Log.d(TAG, "getUser initiated: " + userId);
        Log.d(TAG, "getUser Listeners userListener= "+ mListenersList.size());
        if(mListenersList.size()== 0){
            // Need to add a new Listener
            Log.d(TAG, "getUser adding new Listener= "+ userListener);
            //mListenersMap.put(postSnapshot.getRef(), userListener);
            userRef.addValueEventListener(userListener);
            mListenersList.add(new FirebaseListeners(userRef, userListener));
        }else{
            Log.d(TAG, "getUser Listeners size is not 0= "+ mListenersList.size());
            //there is an old Listener, need to check if it's on this ref
            for (int i = 0; i < mListenersList.size(); i++) {
                //Log.d(TAG, "getUser Listeners ref= "+ mListenersList.get(i).getQueryOrRef()+ " Listener= "+ mListenersList.get(i).getListener());
                if(!mListenersList.get(i).getQueryOrRef().equals(userRef)
                        && (mListenersList.get(i).getListener().equals(userListener))){
                    // This ref doesn't has a listener. Need to add a new Listener
                    Log.d(TAG, "getUser adding new Listener= "+ userListener);
                    userRef.addValueEventListener(userListener);
                    mListenersList.add(new FirebaseListeners(userRef, userListener));
                }else{
                    //there is old Listener on the ref
                    Log.d(TAG, "getUser Listeners= there is old Listener on the ref= "+mListenersList.get(i).getReference()+ " Listener= " + mListenersList.get(i).getListener());
                }
            }
        }

        for (int i = 0; i < mListenersList.size(); i++) {
            Log.d(TAG, "getUser loop throw ref= "+ mListenersList.get(i).getQueryOrRef()+ " Listener= "+ mListenersList.get(i).getListener());
        }

        Log.d(TAG, "getUser= "+ mUser);

        return mUser;
    }


    // remove all listeners when the ViewModel is cleared
    public void removeListeners(){
        for (int i = 0; i < mListenersList.size(); i++) {
            //Log.d(TAG, "remove Listeners ref= "+ mListenersList.get(i).getReference()+ " Listener= "+ mListenersList.get(i).getListener());
            //Log.d(TAG, "remove Listeners Query= "+ mListenersList.get(i).getQuery()+ " Listener= "+ mListenersList.get(i).getListener());
            Log.d(TAG, "remove Listeners Query or Ref= "+ mListenersList.get(i).getQueryOrRef()+ " Listener= "+ mListenersList.get(i).getListener());

            if(null != mListenersList.get(i).getListener()){
                mListenersList.get(i).getQueryOrRef().removeEventListener(mListenersList.get(i).getListener());
            }
            /*if(null != mListenersList.get(i).getReference()){
                mListenersList.get(i).getReference().removeEventListener(mListenersList.get(i).getListener());
            }else if(null != mListenersList.get(i).getQuery()){
                mListenersList.get(i).getQuery().removeEventListener(mListenersList.get(i).getListener());
            }*/
        }
        mListenersList.clear();
    }

}
