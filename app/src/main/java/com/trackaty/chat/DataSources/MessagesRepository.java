package com.trackaty.chat.DataSources;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.trackaty.chat.models.Message;
import com.trackaty.chat.models.User;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;

public class MessagesRepository {

    private final static String TAG = MessagesRepository.class.getSimpleName();

    // [START declare_database_ref]
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mMessagesRef;
    private Boolean isFirstLoaded = true;
    public ValueEventListener MessagesChangesListener;

    public MessagesRepository(String chatKey){
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        // use received chatKey to create a database ref
        mMessagesRef = mDatabaseRef.child("messages").child(chatKey);
        isFirstLoaded = true;
        Log.d(TAG, "mama MessagesRepository init. isFirstLoaded= " + isFirstLoaded);
    }

    // get initial data
    public void getMessages(String initialKey, final int size,
                            @NonNull final ItemKeyedDataSource.LoadInitialCallback<Message> callback){

        if(initialKey == null){// if it's loaded for the first time. Key is null
            Log.d(TAG, "mama getMessages initialKey= " +  initialKey);
            mMessagesRef.orderByKey()//limitToLast to start from the last (page size) items
                    .limitToLast(size).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // [START_EXCLUDE]
                    if (dataSnapshot.exists()) {
                        // loop throw users value
                        List<Message> messagesList = new ArrayList<>();
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                            Message message = snapshot.getValue(Message.class);
                            if (message != null) {
                                message.setKey(snapshot.getKey());
                            }
                            messagesList.add(message);
                            Log.d(TAG, "mama getMessage = "+ message.getMessage()+" getSnapshotKey= " +  snapshot.getKey());
                        }

                        if(messagesList.size() == 0){
                            return;
                        }

                        Log.d(TAG, "mama getMessages usersList.size= " +  messagesList.size()+ " lastkey= "+messagesList.get(messagesList.size()-1).getKey());

                        //initial load
                        /*((ItemKeyedDataSource.LoadInitialCallback)callback)
                                .onResult(usersList, 0, 14);*/
                        callback.onResult(messagesList);

                    /*else{
                        //next pages load
                        //callback.onResult(usersList);
                        //Log.d(TAG, "mama load next" +  usersList.get(0).getName());
                    }*/
                    } else {
                        Log.w(TAG, "mama getMessages no users exist");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "mama loadPost:onCancelled", databaseError.toException());
                }
            });
        }else{// not the first load. Key is the last seen key
            Log.d(TAG, "mama getMessages initialKey= " +  initialKey);
            mMessagesRef.orderByKey()
                    .startAt(initialKey)
                    .limitToLast(size) //limitToLast to start from the last (page size) items
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // [START_EXCLUDE]
                    if (dataSnapshot.exists()) {
                        // loop throw users value
                        List<Message> messagesList = new ArrayList<>();
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                            Message message = snapshot.getValue(Message.class);
                            if (message != null) {
                                message.setKey(snapshot.getKey());
                            }
                            messagesList.add(message);
                            Log.d(TAG, "mama getMessage = "+ message.getMessage()+" getSnapshotKey= " +  snapshot.getKey());
                        }

                        if(messagesList.size() == 0){
                            return;
                        }

                        Log.d(TAG, "mama getMessages usersList.size= " +  messagesList.size()+ " lastkey= "+messagesList.get(messagesList.size()-1).getKey());


                        //initial load
                        /*((ItemKeyedDataSource.LoadInitialCallback)callback)
                                .onResult(usersList, 0, 14);*/
                        callback.onResult(messagesList);

                    /*else{
                        //next pages load
                        //callback.onResult(usersList);
                        //Log.d(TAG, "mama load next" +  usersList.get(0).getName());
                    }*/
                    } else {
                        Log.w(TAG, "mama getMessages no users exist");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                }
            });
        }

         //mUsersRef.addValueEventListener(usersListener);

    }

    // to get next data
    public void getMessagesAfter(final String key, final int size,
                         @NonNull final ItemKeyedDataSource.LoadCallback<Message> callback){
        /*if(key == entireUsersList.get(entireUsersList.size()-1).getCreatedLong()){
            Log.d(TAG, "mama getUsersAfter init. afterKey= " +  key+ "entireUsersList= "+entireUsersList.get(entireUsersList.size()-1).getCreatedLong());
            return;
        }*/

        Log.d(TAG, "mama getUsersAfter. AfterKey= " + key);
        mMessagesRef.orderByKey()
                .startAt(key)
                .limitToFirst(size).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // [START_EXCLUDE]
                if (dataSnapshot.exists()) {
                    // loop throw users value
                    List<Message> messagesList = new ArrayList<>();
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        if(!key.equals(snapshot.getKey())){ // if snapshot key = startAt key? don't add it again
                            Message message = snapshot.getValue(Message.class);
                            if (message != null) {
                                message.setKey(snapshot.getKey());
                            }
                            messagesList.add(message);
                            Log.d(TAG, "mama getMessage = "+ message.getMessage()+" getSnapshotKey= " +  snapshot.getKey());
                        }
                    }

                    if(messagesList.size() == 0){
                        return;
                    }

                    Log.d(TAG, "mama getMessages usersList.size= " +  messagesList.size()+ " lastkey= "+messagesList.get(messagesList.size()-1).getKey());

                    //initial After
                    callback.onResult(messagesList);
                        /*((ItemKeyedDataSource.LoadCallback)callback)
                                .onResult(usersList);*/

                } else {
                    Log.w(TAG, "mama getUsersAfter no users exist");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
        //mUsersRef.addValueEventListener(usersListener);
    }

    // to get previous data
    public void getMessagesBefore(final String key, final int size,
                              @NonNull final ItemKeyedDataSource.LoadCallback<Message> callback){
        Log.d(TAG, "mama getUsersBefore. BeforeKey= " +  key);

        /*if(key == entireUsersList.get(0).getCreatedLong()){
            return;
        }*/
        mMessagesRef.orderByKey()
                .endAt(key)
                .limitToLast(size).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // [START_EXCLUDE]
                if (dataSnapshot.exists()) {
                    // loop throw users value
                    List<Message> messagesList = new ArrayList<>();
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        if(!key.equals(snapshot.getKey())){ // if snapshot key = startAt key? don't add it again
                            Message message = snapshot.getValue(Message.class);
                            if (message != null) {
                                message.setKey(snapshot.getKey());
                            }
                            messagesList.add(message);
                            Log.d(TAG, "mama getMessage = "+ message.getMessage()+" getSnapshotKey= " +  snapshot.getKey());

                        }

                    }

                    if(messagesList.size() == 0){
                        return;
                    }

                    Log.d(TAG, "mama getMessages usersList.size= " +  messagesList.size()+ " lastkey= "+messagesList.get(messagesList.size()-1).getKey());


                     //initial before
                     callback.onResult(messagesList);
                        /*((ItemKeyedDataSource.LoadCallback)callback)
                                .onResult(usersList);*/

                    //initial load
                       /* ((ItemKeyedDataSource.LoadCallback)callback)
                                .onResult(usersList, 0, usersList.size());*/
                } else {
                    Log.w(TAG, "mama getUsersBefore no users exist");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
        //mUsersRef.addValueEventListener(usersListener);


    }

    /*public PublishSubject getUsersChangeSubject() {
        return userAdapterInvalidation;
    }*/

    // to invalidate the data whenever a change happen
    public void MessagesChanged(final DataSource.InvalidatedCallback InvalidatedCallback) {

        final Query query = mMessagesRef.orderByKey();

        MessagesChangesListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!isFirstLoaded){
                    isFirstLoaded = true;
                    Log.d(TAG, "mama entireUsersList Invalidated:");
                    // Remove post value event listener
                    if (MessagesChangesListener != null) {
                        query.removeEventListener(MessagesChangesListener);
                        Log.d(TAG, "mama usersChanged Invalidated removeEventListener");
                    }
                    ((ItemKeyedDataSource.InvalidatedCallback)InvalidatedCallback).onInvalidated();
                    //UsersDataSource.InvalidatedCallback.class.getMethod("loadInitial", "LoadInitialParams");
                    //UsersDataSource.invalidate();
                }

                isFirstLoaded =  false;
                /*if(entireUsersList.size() > 0){
                    entireUsersList.clear();
                    ((ItemKeyedDataSource.InvalidatedCallback)onInvalidatedCallback).onInvalidated();
                    Log.d(TAG, "mama entireUsersList Invalidated:");
                    return;
                }

                if (dataSnapshot.exists()) {
                    // loop throw users value
                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren()){
                        entireUsersList.add(userSnapshot.getValue(User.class));
                    }

                    Log.d(TAG, "mama entireUsersList size= "+entireUsersList.size()+"dataSnapshot count= "+dataSnapshot.getChildrenCount());

                } else {
                    Log.w(TAG, "mama usersChanged no users exist");
                }*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        query.addValueEventListener(MessagesChangesListener);
        //mUsersRef.addValueEventListener(eventListener);
    }

    /*public Single<List<User>> getAnimals(int count){
        return RxFirebaseDatabase.data(mUsersRef.orderByKey().limitToFirst(count)).ma

                .map {
            for ArrayValue
            User.getArrayValue(User.class);
        }
    }*/

}
