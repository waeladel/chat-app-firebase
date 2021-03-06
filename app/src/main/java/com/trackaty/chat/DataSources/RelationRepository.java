package com.trackaty.chat.DataSources;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trackaty.chat.models.DatabaseNotification;
import com.trackaty.chat.models.FirebaseListeners;
import com.trackaty.chat.models.Relation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.trackaty.chat.Utils.DatabaseKeys.getJoinedKeys;

public class RelationRepository {

    private final static String TAG = RelationRepository.class.getSimpleName();

    // [START declare_database_ref]
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mRelationRef;
    private DatabaseReference mLikesRef, mUserFavoritesRef;
    private DatabaseReference mUserChatsRef;
    private DatabaseReference mNotificationsRef;

    private MutableLiveData<Relation> mRelation;
    private MutableLiveData<Long> mLikesResult;
    private MutableLiveData<Long> mPickUpCount;
    private MutableLiveData<String> likeStatus;

    // Database Like's statues
    private static final String LIKE_TYPE_LOVED = "Loved";
    private static final String LIKE_TYPE_NOT_LOVED = "NotLoved";
    private static final String LIKE_TYPE_ADMIRER= "Admirer";

    // DatabaseNotification's types
    private static final String NOTIFICATION_TYPE_PICK_UP = "Pickup";
    private static final String NOTIFICATION_TYPE_MESSAGE = "Message";
    private static final String NOTIFICATION_TYPE_LIKE = "Like"; // if other user liked me. I did't liked him
    private static final String NOTIFICATION_TYPE_LIKE_BACK = "LikeBack"; // if other user liked me after i liked him
    private static final String NOTIFICATION_TYPE_REQUESTS_SENT = "RevealSent";
    private static final String NOTIFICATION_TYPE_REQUESTS_APPROVED = "RevealApproved";

    // requests and relations status
    private static final String RELATION_STATUS_SENDER = "sender";
    private static final String RELATION_STATUS_RECEIVER = "receiver";
    private static final String RELATION_STATUS_STALKER = "stalker";
    private static final String RELATION_STATUS_FOLLOWED = "followed";
    private static final String RELATION_STATUS_NOT_FRIEND = "notFriend";
    private static final String RELATION_STATUS_BLOCKING = "blocking"; // the selected user is blocking me (current user)
    private static final String RELATION_STATUS_BLOCKED= "blocked"; // the selected user is blocked by me (current user)

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
                mLikesResult.postValue(dataSnapshot.getChildrenCount());
            } else {
                // User is null, error out
                mLikesResult.postValue(0L);
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
                Log.d(TAG, "likeStatus dataSnapshot key: "
                        + dataSnapshot.getKey()+" Listener = "+ mLikesCounterListener+ " count= "+dataSnapshot.getChildrenCount());
                //mRelation = dataSnapshot.getValue(User.class);
                // Data exist, i (current user) loved this user already
                likeStatus.postValue(LIKE_TYPE_LOVED);
                Log.d(TAG, "isLiked is LIKE_TYPE_LOVED");
            } else {
                //No data exist. check if this user liked me or not
                if(mListenersList.size()== 0){
                    // Need to add a new Listener
                    Log.d(TAG, "getLoveStatus adding new Listener= "+ mFavoritesListener);
                    //mListenersMap.put(postSnapshot.getRef(), mFavoritesListener);
                    mUserFavoritesRef.addValueEventListener(mFavoritesListener);
                    mListenersList.add(new FirebaseListeners(mUserFavoritesRef, mFavoritesListener));
                }else{
                    Log.d(TAG, "getLoveStatus Listeners size is not 0= "+ mListenersList.size());
                    //there is an old Listener, need to check if it's on this ref
                    for (int i = 0; i < mListenersList.size(); i++) {
                        //Log.d(TAG, "getUser Listeners ref= "+ mListenersList.get(i).getQueryOrRef()+ " Listener= "+ mListenersList.get(i).getListener());
                        if(mListenersList.get(i).getListener().equals(mFavoritesListener) &&
                                !mListenersList.get(i).getQueryOrRef().equals(mUserFavoritesRef)){
                            // We used this listener before, but on another Ref
                            Log.d(TAG, "We used this listener before, is it on the same ref?");
                            Log.d(TAG, "getLoveStatus adding new Listener= "+ mFavoritesListener);
                            mUserFavoritesRef.addValueEventListener(mFavoritesListener);
                            mListenersList.add(new FirebaseListeners(mUserFavoritesRef, mFavoritesListener));
                        }else if((mListenersList.get(i).getListener().equals(mFavoritesListener) &&
                                mListenersList.get(i).getQueryOrRef().equals(mUserFavoritesRef))){
                            //there is old Listener on the ref
                            Log.d(TAG, "getLoveStatus Listeners= there is old Listener on the ref= "+mListenersList.get(i).getQueryOrRef()+ " Listener= " + mListenersList.get(i).getListener());
                        }else{
                            //CounterListener is never used
                            Log.d(TAG, "Listener is never created");
                            mUserFavoritesRef.addValueEventListener(mFavoritesListener);
                            mListenersList.add(new FirebaseListeners(mUserFavoritesRef, mFavoritesListener));
                        }
                    }
                }
                // I (current user) never liked this user, neither did he
                likeStatus.postValue(LIKE_TYPE_NOT_LOVED);
                Log.w(TAG, "isLiked is null, no data exist");
            }

        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "isLiked :onCancelled", databaseError.toException());

        }
    };

    // A listener for Favorites changes, to know if this user liked me before or not
    // To know if current logged in user is on this user favorite list
    private ValueEventListener mFavoritesListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI
            if (dataSnapshot.exists()) {
                // this user is admirer to current user
                if(TextUtils.equals(likeStatus.getValue(),LIKE_TYPE_NOT_LOVED)){
                    // Only change status to admirer if the status is "Not Loved" (button is love)
                    likeStatus.postValue(LIKE_TYPE_ADMIRER);
                    Log.d(TAG, "isLiked is LIKE_TYPE_ADMIRER");
                }
            }else{
                // This user is not admirer. they never love each other
                if(TextUtils.equals(likeStatus.getValue(),LIKE_TYPE_ADMIRER)){
                    // Only change status to "Not Loved" if the status is Admirer (button is love back)
                    likeStatus.postValue(LIKE_TYPE_NOT_LOVED);
                    Log.d(TAG, "isLiked is LIKE_TYPE_NOT_LOVED");
                }
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
        mLikesResult = new MutableLiveData<>();
        mPickUpCount = new MutableLiveData<>();
        likeStatus = new MutableLiveData<>();
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
        return mLikesResult;
    }

    // Get relation if any between current user and selected user
    public MutableLiveData<String> getLoveStatues(String currentUserId, String userId){

        // This user is liked by current logged in user
        DatabaseReference userLikesRef = mLikesRef.child(userId).child(currentUserId);
        // current logged in user is on this user favorite list
        mUserFavoritesRef = mDatabaseRef.child("favorites").child(userId).child(currentUserId);
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

        Log.d(TAG, "likeStatus= "+ likeStatus.getValue());

        return likeStatus;
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
    //public void sendLove(String currentUserId, String name , String avatar, String userId) {
    public void sendLove(String currentUserId, String name, String avatar, String userId, String notificationType) {
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
        //String notificationKey = mNotificationsRef.child(userId).push().getKey();
        String notificationKey = currentUserId+ NOTIFICATION_TYPE_LIKE;
        //DatabaseNotification notification = new DatabaseNotification(getContext().getString(R.string.notification_like_title), getContext().getString(R.string.notification_like_message, name), "like", currentUserId, name, avatar);
        DatabaseNotification notification = new DatabaseNotification(notificationType, currentUserId, name, avatar);
        //notification.setType(notificationType);
        Map<String, Object> notificationValues = notification.toMap();
        childUpdates.put("/notifications/alerts/" + userId + "/" +notificationKey, notificationValues);

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

    // Block user without deleting conversation
    public void blockUser(String currentUserId, String userId) {
        Map<String, Object> childUpdates = new HashMap<>();
        //Cancel likes i (current user) sent to this (target user). Keep like he sent to me (current user)
        childUpdates.put("/favorites/" + currentUserId + "/" + userId, null);
        //likes is to display who send likes to this particular user
        childUpdates.put("/likes/" + userId + "/" + currentUserId, null);

        // Update relations to blocking (current user) and blocked (target user)
        childUpdates.put("/relations/" + currentUserId + "/" + userId+ "/status", RELATION_STATUS_BLOCKED);
        childUpdates.put("/relations/" + userId + "/" + currentUserId+ "/status", RELATION_STATUS_BLOCKING);

        // Chat ID is not passed from MainFragment, we need to create
        String chatId = getJoinedKeys(currentUserId , userId);
        // update chat active to -1, which means it's blocked chat room
        childUpdates.put("/chats/" + chatId +"/active",-1);

        // Delete chats with this person from chats recycler view
        /*childUpdates.put("/userChats/" + currentUserId + "/" + chatId, null);
        childUpdates.put("/userChats/" + userId + "/" + chatId, null);*/

        // Delete notifications


        mDatabaseRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Write was successful!
                Log.i(TAG, "block onSuccess");
                // ...
            }
        });
    }

    // Block user and delete the conversation (userChat table)
    public void blockDelete(String currentUserId, String userId) {
        Map<String, Object> childUpdates = new HashMap<>();
        //Cancel likes i (current user) sent to this (target user). Keep like he sent to me (current user)
        childUpdates.put("/favorites/" + currentUserId + "/" + userId, null);
        //likes is to display who send likes to this particular user
        childUpdates.put("/likes/" + userId + "/" + currentUserId, null);

        // Update relations to blocking (current user) and blocked (target user)
        childUpdates.put("/relations/" + currentUserId + "/" + userId+ "/status", RELATION_STATUS_BLOCKED);
        childUpdates.put("/relations/" + userId + "/" + currentUserId+ "/status", RELATION_STATUS_BLOCKING);

        // Chat ID is not passed from MainFragment, we need to create
        String chatId = getJoinedKeys(currentUserId , userId);
        // update chat active to -1, which means it's blocked chat room
        childUpdates.put("/chats/" + chatId +"/active",-1);

        // Delete chats with this person from chats recycler view
        childUpdates.put("/userChats/" + currentUserId + "/" + chatId, null);
        childUpdates.put("/userChats/" + userId + "/" + chatId, null);

        // Delete notifications


        mDatabaseRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Write was successful!
                Log.i(TAG, "block onSuccess");
                // ...
            }
        });
    }

    // Delete blocking/blocked relation to start fresh
    public void unblockUser(String currentUserId, String userId) {
        Map<String, Object> childUpdates = new HashMap<>();

        // Update relations to null. To start fresh
        childUpdates.put("/relations/" + currentUserId + "/" + userId, null);
        childUpdates.put("/relations/" + userId + "/" + currentUserId, null);

        // Chat ID is not passed from MainFragment, we need to create
        String chatId = getJoinedKeys(currentUserId , userId);
        // update chat to null, to delete the chat room and start fresh
        childUpdates.put("/chats/" + chatId ,null);

        mDatabaseRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Write was successful!
                Log.i(TAG, "block onSuccess");
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

