package com.trackaty.chat.DataSources;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.trackaty.chat.Interface.FirebaseUserCallback;
import com.trackaty.chat.models.FirebaseListeners;
import com.trackaty.chat.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private final static String TAG = "UserRepository";

    // [START declare_database_ref]
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUsersRef, mChatsRef, mNotificationsRef;
    private Boolean isFirstLoaded = true;

    private MutableLiveData<User> mCurrentUser;
    private MutableLiveData<User> mUser;
    private MutableLiveData<User> mSingleValueUser;
    private MutableLiveData<Long> mChatsCount, mNotificationsCount;

    // HashMap to keep track of Firebase Listeners
    //private HashMap< DatabaseReference , ValueEventListener> mListenersMap;
    // Change mListenersList to static so that it's the same for all instance
    private  List<FirebaseListeners> mListenersList;// = new ArrayList<>();

    // A listener for mCurrentUser changes
    private ValueEventListener currentUserListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI
            if (dataSnapshot.exists()) {
                // Get user value
                Log.d(TAG, "getUser dataSnapshot key: "
                        + dataSnapshot.getKey()+" Listener = "+currentUserListener);
                //mCurrentUser = dataSnapshot.getValue(User.class);
                mCurrentUser.postValue(dataSnapshot.getValue(User.class));
            } else {
                // User is null, error out
                mCurrentUser.postValue(null); // return null to disable buttons when unsaved new user opened his profile
                Log.w(TAG, "User is null, no such user");
            }
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());

        }
    };

    public UserRepository(){
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mUsersRef = mDatabaseRef.child("users");
        mChatsRef = mDatabaseRef.child("userChats");
        mNotificationsRef = mDatabaseRef.child("notifications").child("alerts");
        //usersList = new ArrayList<>();
        //entireUsersList = new ArrayList<>();
        isFirstLoaded = true;
        mCurrentUser = new MutableLiveData<>();
        mUser = new MutableLiveData<>();
        mSingleValueUser = new MutableLiveData<>();
        mChatsCount = new MutableLiveData<>();
        mNotificationsCount = new MutableLiveData<>();
        //mListenersMap =  new HashMap<>();
        /*if(mListenersList == null && mListenersList.size() == 0){
            mListenersList = new ArrayList<>();
        }
        if(mListenersList != null){
            Log.d(TAG, "mama UsersRepository init. isFirstLoaded= " + mListenersList.size());
        }
        mListenersList = new ArrayList<>();*/
        if(mListenersList == null){
            mListenersList = new ArrayList<>();
            Log.d(TAG, "mListenersList is null. new ArrayList is created= " + mListenersList.size());
        }else{
            Log.d(TAG, "mListenersList is not null. Size= " + mListenersList.size());
            if(mListenersList.size() >0){
                Log.d(TAG, "mListenersList is not null and not empty. Size= " + mListenersList.size()+" Remove previous listeners");
                // No need to remove old Listeners, we are gonna reuse them
                removeListeners();
                //mListenersList = new ArrayList<>();
            }
        }

    }

    public MutableLiveData<User> getCurrentUser(String userId){

        DatabaseReference currentUserRef = mUsersRef.child(userId);
        //final MutableLiveData<User> mCurrentUser = new MutableLiveData<>();
        Log.d(TAG, "getUser: initiated: " + userId);

        Log.d(TAG, "getUser: Listeners size= "+ mListenersList.size());
        if(mListenersList.size()== 0){
            // Need to add a new Listener
            Log.d(TAG, "getUser: adding new Listener= "+ mListenersList);
            //mListenersMap.put(postSnapshot.getRef(), mPickUpCounterListener);
            currentUserRef.addValueEventListener(currentUserListener);
            mListenersList.add(new FirebaseListeners(currentUserRef, currentUserListener));
        }else{
            Log.d(TAG, "getUser: postSnapshot Listeners size is not 0= "+ mListenersList.size());
            //there is an old Listener, need to check if it's on this ref
            for (int i = 0; i < mListenersList.size(); i++) {
                //Log.d(TAG, "getChatsCount Listeners ref= "+ mListenersList.get(i).getQueryOrRef()+ " Listener= "+ mListenersList.get(i).getListener());
                if(mListenersList.get(i).getListener().equals(currentUserListener) &&
                        !mListenersList.get(i).getQueryOrRef().equals(currentUserRef)){
                    // We used this listener before, but on another Ref
                    Log.d(TAG, "getUser: We used this listener before, is it on the same ref?");
                    Log.d(TAG, "getUser: adding new Listener= "+ currentUserListener);
                    currentUserRef.addValueEventListener(currentUserListener);
                    mListenersList.add(new FirebaseListeners(currentUserRef, currentUserListener));
                }else if((mListenersList.get(i).getListener().equals(currentUserListener) &&
                        mListenersList.get(i).getQueryOrRef().equals(currentUserRef))){
                    //there is old Listener on the ref
                    Log.d(TAG, "getUser: Listeners= there is old Listener on the ref= "+mListenersList.get(i).getQueryOrRef()+ " Listener= " + mListenersList.get(i).getListener());
                }else{
                    //userListener is never used
                    Log.d(TAG, "getUser: Listener is never created");
                    currentUserRef.addValueEventListener(currentUserListener);
                    mListenersList.add(new FirebaseListeners(currentUserRef, currentUserListener));
                }
            }
        }

        for (int i = 0; i < mListenersList.size(); i++) {
            Log.d(TAG, "getUser: loop throw Listeners ref= "+ mListenersList.get(i).getQueryOrRef()+ " Listener= "+ mListenersList.get(i).getListener());
        }
        return mCurrentUser;
    }

    public void getUserOnce(String userId, final FirebaseUserCallback callback){

        DatabaseReference UserRef = mUsersRef.child(userId);
        //final MutableLiveData<User> mCurrentUser = new MutableLiveData<>();
        Log.d(TAG, "getUser initiated: " + userId);

        UserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get user value
                    Log.d(TAG, "getUserOnce dataSnapshot key: "
                            + dataSnapshot.getKey()+" Listener = "+currentUserListener);
                    //mSingleValueUser = dataSnapshot.getValue(User.class);
                    //mSingleValueUser.postValue(dataSnapshot.getValue(User.class));
                    //int sID = dataSnapshot.child("soundId").getValue(Integer.class);
                    //Log.d(TAG, "getUserOnce User sound id= "+ sID);
                    User user = dataSnapshot.getValue(User.class);
                    if(user != null){
                        user.setKey(dataSnapshot.getKey());
                        Log.d(TAG, "getUserOnce User sound id= "+ user.getSoundId());
                    }
                    callback.onCallback(user);
                } else {
                    // Return a null user to view model to know when user doesn't exist,
                    // So we don't create or update tokens and online presence
                    callback.onCallback(null);
                    Log.w(TAG, "getUserOnce User is null, no such user");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "getUserOnce User onCancelled" +databaseError);
            }
        });
    }



    public void removeListeners(){
        if(null != mListenersList){
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
 }

