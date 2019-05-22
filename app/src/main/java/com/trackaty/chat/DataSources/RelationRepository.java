package com.trackaty.chat.DataSources;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trackaty.chat.R;
import com.trackaty.chat.models.FirebaseListeners;
import com.trackaty.chat.models.Notification;
import com.trackaty.chat.models.Relation;
import com.trackaty.chat.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import static com.trackaty.chat.App.getContext;

public class RelationRepository {

    private final static String TAG = RelationRepository.class.getSimpleName();

    // [START declare_database_ref]
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mRelationRef;
    private DatabaseReference mLikesRef;
    private DatabaseReference mUserChatsRef;
    private DatabaseReference mNotificationsRef;

    private MutableLiveData<Relation> mRelation;
    private MutableLiveData<Long> mlikesResult;
    private MutableLiveData<Long> mPickUpCount;
    private MutableLiveData<Boolean> isLiked;



    // HashMap to keep track of Firebase Listeners
    //private HashMap< DatabaseReference , ValueEventListener> mListenersMap;
    private List<FirebaseListeners> mListenersList;// = new ArrayList<>();

    // a listener for mRelation changes
    private ValueEventListener mRelationListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI
            if (dataSnapshot.exists()) {
                // Get user value
                Log.d(TAG, "getRelation dataSnapshot key: "
                        + dataSnapshot.getKey()+" Listener = "+ mRelationListener);
                //mRelation = dataSnapshot.getValue(User.class);
                mRelation.postValue(dataSnapshot.getValue(Relation.class));
            } else {
                // User is null, error out
                mRelation.postValue(null);
                Log.w(TAG, "Relation  is null, no relation exist");
            }
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());

        }
    };

    // a listener for mRelation changes
    private ValueEventListener mLikesCounterListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI
            if (dataSnapshot.exists()) {
                // Get user value
                Log.d(TAG, "getLikesCount dataSnapshot key: "
                        + dataSnapshot.getKey()+" Listener = "+ mLikesCounterListener+ " count= "+dataSnapshot.getChildrenCount());
                //mRelation = dataSnapshot.getValue(User.class);
                mlikesResult.postValue(dataSnapshot.getChildrenCount());
            } else {
                // User is null, error out
                mlikesResult.postValue(0L);
                Log.w(TAG, "getLikesCount  is null, no data exist");
            }
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "getLikesCount:onCancelled", databaseError.toException());

        }
    };


    // a listener for Pick up counter
    private ValueEventListener mPickUpCounterListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI
            if (dataSnapshot.exists()) {
                // Get user value
                Log.d(TAG, "mPickUpCount dataSnapshot key: "
                        + dataSnapshot.getKey()+" Listener = "+ mPickUpCounterListener+ " count= "+dataSnapshot.getChildrenCount());
                //mRelation = dataSnapshot.getValue(User.class);
                mPickUpCount.postValue(dataSnapshot.getChildrenCount());
            } else {
                // User is null, error out
                mPickUpCount.postValue(0L);
                Log.w(TAG, "mPickUpCount  is null, no data exist");
            }
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "mPickUpCount :onCancelled", databaseError.toException());

        }
    };

    // a listener for mRelation changes
    private ValueEventListener mIsLikedListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI
            if (dataSnapshot.exists()) {
                // Get user value
                Log.d(TAG, "isLiked dataSnapshot key: "
                        + dataSnapshot.getKey()+" Listener = "+ mLikesCounterListener+ " count= "+dataSnapshot.getChildrenCount());
                //mRelation = dataSnapshot.getValue(User.class);
                isLiked.postValue(true);
            } else {
                // User is null, error out
                isLiked.postValue(false);
                Log.w(TAG, "isLiked is null, no data exist");
            }
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "isLiked :onCancelled", databaseError.toException());

        }
    };


    public RelationRepository(){
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mRelationRef = mDatabaseRef.child("relations");
        mLikesRef = mDatabaseRef.child("likes");
        mUserChatsRef = mDatabaseRef.child("userChats");
        mNotificationsRef = mDatabaseRef.child("notifications");
        //usersList = new ArrayList<>();
        //entireUsersList = new ArrayList<>();
        mRelation = new MutableLiveData<>();
        mlikesResult  = new MutableLiveData<>();
        mPickUpCount = new MutableLiveData<>();
        isLiked = new MutableLiveData<>();
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
                removeListeners();
                //mListenersList = new ArrayList<>();
            }
        }
    }

    // Get relation if any between current user and selected user
    public MutableLiveData<Relation> getRelation(String currentUserId , String userId){

        DatabaseReference currentUserRelationRef = mRelationRef.child(currentUserId).child(userId);
        //final MutableLiveData<User> mRelation = new MutableLiveData<>();
        Log.d(TAG, "getCurrentUserRelation initiated: currentUserId= " + currentUserId+ " userId= "+userId );


        Log.d(TAG, "getCurrentUserRelation Listeners size= "+ mListenersList.size());
        if(mListenersList.size()== 0){
            // Need to add a new Listener
            Log.d(TAG, "getCurrentUserRelation adding new Listener= "+ mRelationListener);
            //mListenersMap.put(postSnapshot.getRef(), mRelationListener);
            currentUserRelationRef.addValueEventListener(mRelationListener);
            mListenersList.add(new FirebaseListeners(currentUserRelationRef, mRelationListener));
        }else{
            Log.d(TAG, "getCurrentUserRelation Listeners size is not 0= "+ mListenersList.size());
            //there is an old Listener, need to check if it's on this ref
            for (int i = 0; i < mListenersList.size(); i++) {
                //Log.d(TAG, "getUser Listeners ref= "+ mListenersList.get(i).getQueryOrRef()+ " Listener= "+ mListenersList.get(i).getListener());
                if(!mListenersList.get(i).getQueryOrRef().equals(currentUserRelationRef)
                        && (mListenersList.get(i).getListener().equals(mRelationListener))){
                    // This ref doesn't has a listener. Need to add a new Listener
                    Log.d(TAG, "getCurrentUserRelation adding new Listener= "+ mRelationListener);
                    currentUserRelationRef.addValueEventListener(mRelationListener);
                    mListenersList.add(new FirebaseListeners(currentUserRelationRef, mRelationListener));
                }else if(mListenersList.get(i).getQueryOrRef().equals(currentUserRelationRef)
                        && (mListenersList.get(i).getListener().equals(mRelationListener))){
                    //there is old Listener on the ref
                    Log.d(TAG, "getCurrentUserRelation Listeners= there is old Listener on the ref= "+mListenersList.get(i).getQueryOrRef()+ " Listener= " + mListenersList.get(i).getListener());
                }else{
                    //CounterListener is never used
                    Log.d(TAG, "Listener is never created");
                    currentUserRelationRef.addValueEventListener(mRelationListener);
                    mListenersList.add(new FirebaseListeners(currentUserRelationRef, mRelationListener));
                }
            }
        }
        for (int i = 0; i < mListenersList.size(); i++) {
            Log.d(TAG, "getCurrentUserRelation loop throw Listeners ref= "+ mListenersList.get(i).getQueryOrRef()+ " Listener= "+ mListenersList.get(i).getListener());
        }
        return mRelation;
    }


    // Delete the request
    public void cancelRequest(String currentUserId, String userId) {

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/relations/" + currentUserId + "/" + userId, null);
        childUpdates.put("/relations/" + userId + "/" + currentUserId, null);

        mDatabaseRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Write was successful!
                Log.i(TAG, "send request onSuccess");
                // ...
            }
        });

    }

    // Get relation if any between current user and selected user
    public MutableLiveData<Long> getLoveCount(String userId){

        DatabaseReference userLikesRef = mLikesRef.child(userId);
        //final MutableLiveData<User> mRelation = new MutableLiveData<>();
        Log.d(TAG, "getLoveCount initiated: userId= "+userId );
        Log.d(TAG, "getLoveCount userLikesRef= "+userLikesRef +" mLikesListener= "+mLikesCounterListener);

        Log.d(TAG, "getLoveCount Listeners size= "+ mListenersList.size());
        if(mListenersList.size()== 0){
            // Need to add a new Listener
            Log.d(TAG, "getLoveCount adding new Listener= "+ mLikesCounterListener);
            //mListenersMap.put(postSnapshot.getRef(), mLikesCounterListener);
            userLikesRef.addValueEventListener(mLikesCounterListener);
            mListenersList.add(new FirebaseListeners(userLikesRef, mLikesCounterListener));
        }else{
            Log.d(TAG, "postSnapshot Listeners size is not 0= "+ mListenersList.size());
            //there is an old Listener, need to check if it's on this ref
            for (int i = 0; i < mListenersList.size(); i++) {
                //Log.d(TAG, "getUser Listeners ref= "+ mListenersList.get(i).getQueryOrRef()+ " Listener= "+ mListenersList.get(i).getListener());
                if(mListenersList.get(i).getListener().equals(mLikesCounterListener) &&
                        !mListenersList.get(i).getQueryOrRef().equals(userLikesRef)){
                    // We used this listener before, but on another Ref
                    Log.d(TAG, "We used this listener before, is it on the same ref?");
                        Log.d(TAG, "getLoveCount adding new Listener= "+ mLikesCounterListener);
                        userLikesRef.addValueEventListener(mLikesCounterListener);
                        mListenersList.add(new FirebaseListeners(userLikesRef, mLikesCounterListener));
                    }else if((mListenersList.get(i).getListener().equals(mLikesCounterListener) &&
                        mListenersList.get(i).getQueryOrRef().equals(userLikesRef))){
                        //there is old Listener on the ref
                        Log.d(TAG, "getLoveCount Listeners= there is old Listener on the ref= "+mListenersList.get(i).getQueryOrRef()+ " Listener= " + mListenersList.get(i).getListener());
                    }else{
                    //CounterListener is never used
                    Log.d(TAG, "Listener is never created");
                    userLikesRef.addValueEventListener(mLikesCounterListener);
                    mListenersList.add(new FirebaseListeners(userLikesRef, mLikesCounterListener));
                }
            }
        }
        for (int i = 0; i < mListenersList.size(); i++) {
            Log.d(TAG, "mama getLoveCount loop throw Listeners ref= "+ mListenersList.get(i).getQueryOrRef()+ " Listener= "+ mListenersList.get(i).getListener());
        }
        return mlikesResult;
    }

    // Get relation if any between current user and selected user
    public MutableLiveData<Boolean> getLoveStatues(String currentUserId, String userId){

        DatabaseReference userLikesRef = mLikesRef.child(userId).child(currentUserId);
        //final MutableLiveData<User> mRelation = new MutableLiveData<>();
        Log.d(TAG, "getLoveStatus initiated: userId= "+userId );
        Log.d(TAG, "getLoveStatus Listeners size= "+ mListenersList.size());
        if(mListenersList.size()== 0){
            // Need to add a new Listener
            Log.d(TAG, "getLoveStatus adding new Listener= "+ mIsLikedListener);
            //mListenersMap.put(postSnapshot.getRef(), mIsLikedListener);
            userLikesRef.addValueEventListener(mIsLikedListener);
            mListenersList.add(new FirebaseListeners(userLikesRef, mIsLikedListener));
        }else{
            Log.d(TAG, "getLoveStatus Listeners size is not 0= "+ mListenersList.size());
            //there is an old Listener, need to check if it's on this ref
            for (int i = 0; i < mListenersList.size(); i++) {
                //Log.d(TAG, "getUser Listeners ref= "+ mListenersList.get(i).getQueryOrRef()+ " Listener= "+ mListenersList.get(i).getListener());
                if(mListenersList.get(i).getListener().equals(mIsLikedListener) &&
                        !mListenersList.get(i).getQueryOrRef().equals(userLikesRef)){
                    // We used this listener before, but on another Ref
                    Log.d(TAG, "We used this listener before, is it on the same ref?");
                    Log.d(TAG, "getLoveStatus adding new Listener= "+ mIsLikedListener);
                    userLikesRef.addValueEventListener(mIsLikedListener);
                    mListenersList.add(new FirebaseListeners(userLikesRef, mIsLikedListener));
                }else if((mListenersList.get(i).getListener().equals(mIsLikedListener) &&
                        mListenersList.get(i).getQueryOrRef().equals(userLikesRef))){
                    //there is old Listener on the ref
                    Log.d(TAG, "getLoveStatus Listeners= there is old Listener on the ref= "+mListenersList.get(i).getQueryOrRef()+ " Listener= " + mListenersList.get(i).getListener());
                }else{
                    //CounterListener is never used
                    Log.d(TAG, "Listener is never created");
                    userLikesRef.addValueEventListener(mIsLikedListener);
                    mListenersList.add(new FirebaseListeners(userLikesRef, mIsLikedListener));
                }
            }
        }
        for (int i = 0; i < mListenersList.size(); i++) {
            Log.d(TAG, "mama getLoveCount loop throw Listeners ref= "+ mListenersList.get(i).getQueryOrRef()+ " Listener= "+ mListenersList.get(i).getListener());
        }
        return isLiked;
    }

    // Get pick-up count if any between current user and selected user
    public MutableLiveData<Long> getPickUpCount(String userId){

        DatabaseReference userChatsRef = mUserChatsRef.child(userId);
        //final MutableLiveData<User> mRelation = new MutableLiveData<>();
        Log.d(TAG, "getPickUpCount initiated: userId= "+userId );
        Log.d(TAG, "getPickUpCount userChatsRef= "+userChatsRef +" mLikesListener= "+mPickUpCounterListener);

        Log.d(TAG, "getLoveCount Listeners size= "+ mListenersList.size());
        if(mListenersList.size()== 0){
            // Need to add a new Listener
            Log.d(TAG, "getPickUpCount adding new Listener= "+ mPickUpCounterListener);
            //mListenersMap.put(postSnapshot.getRef(), mPickUpCounterListener);
            userChatsRef.addValueEventListener(mPickUpCounterListener);
            mListenersList.add(new FirebaseListeners(userChatsRef, mPickUpCounterListener));
        }else{
            Log.d(TAG, "postSnapshot Listeners size is not 0= "+ mListenersList.size());
            //there is an old Listener, need to check if it's on this ref
            for (int i = 0; i < mListenersList.size(); i++) {
                //Log.d(TAG, "getUser Listeners ref= "+ mListenersList.get(i).getQueryOrRef()+ " Listener= "+ mListenersList.get(i).getListener());
                if(mListenersList.get(i).getListener().equals(mPickUpCounterListener) &&
                        !mListenersList.get(i).getQueryOrRef().equals(userChatsRef)){
                    // We used this listener before, but on another Ref
                    Log.d(TAG, "We used this listener before, is it on the same ref?");
                    Log.d(TAG, "getPickUpCount adding new Listener= "+ mPickUpCounterListener);
                    userChatsRef.addValueEventListener(mPickUpCounterListener);
                    mListenersList.add(new FirebaseListeners(userChatsRef, mPickUpCounterListener));
                }else if((mListenersList.get(i).getListener().equals(mPickUpCounterListener) &&
                        mListenersList.get(i).getQueryOrRef().equals(userChatsRef))){
                    //there is old Listener on the ref
                    Log.d(TAG, "getLoveCount Listeners= there is old Listener on the ref= "+mListenersList.get(i).getQueryOrRef()+ " Listener= " + mListenersList.get(i).getListener());
                }else{
                    //CounterListener is never used
                    Log.d(TAG, "Listener is never created");
                    userChatsRef.addValueEventListener(mPickUpCounterListener);
                    mListenersList.add(new FirebaseListeners(userChatsRef, mPickUpCounterListener));
                }
            }
        }
        for (int i = 0; i < mListenersList.size(); i++) {
            Log.d(TAG, "mama getLoveCount loop throw Listeners ref= "+ mListenersList.get(i).getQueryOrRef()+ " Listener= "+ mListenersList.get(i).getListener());
        }
        return mPickUpCount;
    }

    // update love and favourites Favorite
    public void sendLove(String currentUserId, String name , String avatar, String userId) {

        /*User user = new User();
        user.setName("wal");
        // Create chat map
        Map<String, Object> userValues = user.toMap();*/

        Map<String, Object> childUpdates = new HashMap<>();
        //favorites is to display current user favorites
        childUpdates.put("/favorites/" + currentUserId + "/" + userId, true);
        //likes is to display who send likes to this particular user
        childUpdates.put("/likes/" + userId + "/" + currentUserId, true);

        // Update notifications
        String notificationKey = mNotificationsRef.child(userId).push().getKey();
        Notification notification = new Notification(getContext().getString(R.string.notification_like_title), getContext().getString(R.string.notification_like_message, name), "like", currentUserId, name, avatar);
        Map<String, Object> notificationValues = notification.toMap();
        childUpdates.put("/notifications/" + userId + "/" +notificationKey, notificationValues);

        mDatabaseRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Write was successful!
                Log.i(TAG, "send love onSuccess");
                // ...
            }
        });

    }

    // update love and favourites Favorite
    public void cancelLove(String currentUserId, String userId) {

        /*User user = new User();
        user.setName("wal");
        // Create chat map
        Map<String, Object> userValues = user.toMap();*/

        Map<String, Object> childUpdates = new HashMap<>();
        //favorites is to display current user favorites
        childUpdates.put("/favorites/" + currentUserId + "/" + userId, null);
        //likes is to display who send likes to this particular user
        childUpdates.put("/likes/" + userId + "/" + currentUserId, null);

        // Cancel notification
        //childUpdates.put("/notifications/" + userId, null);

        mDatabaseRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Write was successful!
                Log.i(TAG, "unlove onSuccess");
                // ...
            }
        });
    }

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

