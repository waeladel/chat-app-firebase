package com.trackaty.chat.DataSources;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trackaty.chat.models.FirebaseListeners;
import com.trackaty.chat.models.User;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;

public class UserRepository {

    private final static String TAG = UserRepository.class.getSimpleName();

    // [START declare_database_ref]
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUsersRef;
    private Boolean isFirstLoaded = true;

    private MutableLiveData<User> mCurrentUser;
    private MutableLiveData<User> mUser;

    // HashMap to keep track of Firebase Listeners
    //private HashMap< DatabaseReference , ValueEventListener> mListenersMap;
    private List<FirebaseListeners> mListenersList;// = new ArrayList<>();

    // a listener for mCurrentUser changes
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
        //usersList = new ArrayList<>();
        //entireUsersList = new ArrayList<>();
        isFirstLoaded = true;
        mCurrentUser = new MutableLiveData<>();
        mUser = new MutableLiveData<>();
        //mListenersMap =  new HashMap<>();
        /*if(mListenersList == null && mListenersList.size() == 0){
            mListenersList = new ArrayList<>();
        }*/
        if(mListenersList != null){
            Log.d(TAG, "mama UsersRepository init. isFirstLoaded= " + mListenersList.size());
        }
        mListenersList = new ArrayList<>();

    }

    public MutableLiveData<User> getCurrentUser(String userId){

        DatabaseReference currentUserRef = mUsersRef.child(userId);
        //final MutableLiveData<User> mCurrentUser = new MutableLiveData<>();
        Log.d(TAG, "getUser initiated: " + userId);

        Log.d(TAG, "getUser Listeners size= "+ mListenersList.size());
        if(mListenersList.size()== 0){
            // Need to add a new Listener
            Log.d(TAG, "getUser adding new Listener= "+ currentUserListener);
            //mListenersMap.put(postSnapshot.getRef(), currentUserListener);
            currentUserRef.addValueEventListener(currentUserListener);
            mListenersList.add(new FirebaseListeners(currentUserRef, currentUserListener));
        }else{
            Log.d(TAG, "getUser Listeners size is not 0= "+ mListenersList.size());
            //there is an old Listener, need to check if it's on this ref
            for (int i = 0; i < mListenersList.size(); i++) {
                //Log.d(TAG, "getUser Listeners ref= "+ mListenersList.get(i).getQueryOrRef()+ " Listener= "+ mListenersList.get(i).getListener());
                if(!mListenersList.get(i).getQueryOrRef().equals(currentUserRef)
                        && (mListenersList.get(i).getListener().equals(currentUserListener))){
                    // This ref doesn't has a listener. Need to add a new Listener
                    Log.d(TAG, "getUser adding new Listener= "+ currentUserListener);
                    currentUserRef.addValueEventListener(currentUserListener);
                    mListenersList.add(new FirebaseListeners(currentUserRef, currentUserListener));
                }else{
                    //there is old Listener on the ref
                    Log.d(TAG, "getUser Listeners= there is old Listener on the ref= "+mListenersList.get(i).getQueryOrRef()+ " Listener= " + mListenersList.get(i).getListener());
                }
            }
        }
        for (int i = 0; i < mListenersList.size(); i++) {
            Log.d(TAG, "getUser loop throw Listeners ref= "+ mListenersList.get(i).getQueryOrRef()+ " Listener= "+ mListenersList.get(i).getListener());
        }
        return mCurrentUser;
    }




    public void removeListeners(){
        for (int i = 0; i < mListenersList.size(); i++) {
            Log.d(TAG, "remove Listeners ref= "+ mListenersList.get(i).getReference()+ " Listener= "+ mListenersList.get(i).getListener());
            Log.d(TAG, "remove Listeners Query= "+ mListenersList.get(i).getQuery()+ " Listener= "+ mListenersList.get(i).getListener());
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

