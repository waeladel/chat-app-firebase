package com.trackaty.chat.DataSources;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.trackaty.chat.models.Chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;

public class ChatsRepository {

    private final static String TAG = ChatsRepository.class.getSimpleName();

    // [START declare_database_ref]
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mChatsRef;
    private Boolean isFirstLoaded = true;
    public ValueEventListener ChatsChangesListener;

    public ChatsRepository(String userKey){
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        // use received chatKey to create a database ref
        mChatsRef = mDatabaseRef.child("userChats").child(userKey);
        isFirstLoaded = true;
        Log.d(TAG, "mama ChatsRepository init. isFirstLoaded= " + isFirstLoaded);
    }

    // get initial data
    public void getChats(Long initialKey, final int size,
                            @NonNull final ItemKeyedDataSource.LoadInitialCallback<Chat> callback){

        if(initialKey == null){// if it's loaded for the first time. Key is null
            Log.d(TAG, "mama getChats initialKey= " +  initialKey);
            mChatsRef.orderByChild("lastSent")//limitToLast to start from the last (page size) items
                    .limitToFirst(size).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // [START_EXCLUDE]
                    if (dataSnapshot.exists()) {
                        // loop throw users value
                        List<Chat> chatsList = new ArrayList<>();
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                            Chat chat = snapshot.getValue(Chat.class);
                            if (chat != null) {
                                chat.setKey(snapshot.getKey());

                                // loop to get all chat members array
                                for (int i = 0; i < chat.getMembers().size(); i++) {
                                    //Log.d(TAG, "mama getChats "+snapshot.getKey()+" getMember= "+ chat.getMembers().get(i));
                                }

                                // loop to get all chat members HashMap
                                Iterator iterator = chat.getMemberHash().entrySet().iterator();
                                while (iterator.hasNext()) {
                                    Map.Entry pair = (Map.Entry)iterator.next();
                                    Log.d(TAG, "mama getChats getMember= "+pair.getKey() + " = " + pair.getValue());
                                    iterator.remove(); // avoids a ConcurrentModificationException
                                }

                            }
                            chatsList.add(chat);
                            Log.d(TAG, "mama getChats = "+ chat.getLastMessage()+" getSnapshotKey= " +  snapshot.getKey());
                        }

                        if(chatsList.size() == 0){
                            return;
                        }

                        Log.d(TAG, "mama getChats usersList.size= " +  chatsList.size()+ " lastkey= "+chatsList.get(chatsList.size()-1).getKey());

                        //initial load
                        /*((ItemKeyedDataSource.LoadInitialCallback)callback)
                                .onResult(usersList, 0, 14);*/
                        Collections.reverse(chatsList);
                        callback.onResult(chatsList);

                    /*else{
                        //next pages load
                        //callback.onResult(usersList);
                        //Log.d(TAG, "mama load next" +  usersList.get(0).getName());
                    }*/
                    } else {
                        Log.w(TAG, "mama getChats no users exist");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "mama loadPost:onCancelled", databaseError.toException());
                }
            });
        }else{// not the first load. Key is the last seen key
            Log.d(TAG, "mama getChats initialKey= " +  initialKey);
            mChatsRef.orderByChild("lastSent")
                    .startAt(initialKey)
                    .limitToFirst(size) //limitToLast to start from the last (page size) items
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // [START_EXCLUDE]
                    if (dataSnapshot.exists()) {
                        // loop throw users value
                        List<Chat> chatsList = new ArrayList<>();
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                            Chat chat = snapshot.getValue(Chat.class);
                            if (chat != null) {
                                chat.setKey(snapshot.getKey());
                            }
                            chatsList.add(chat);
                            Log.d(TAG, "mama getChats = "+ chat.getLastMessage()+" getSnapshotKey= " +  snapshot.getKey());
                        }

                        if(chatsList.size() == 0){
                            return;
                        }

                        Log.d(TAG, "mama getChats usersList.size= " +  chatsList.size()+ " lastkey= "+chatsList.get(chatsList.size()-1).getKey());


                        //initial load
                        /*((ItemKeyedDataSource.LoadInitialCallback)callback)
                                .onResult(usersList, 0, 14);*/
                        Collections.reverse(chatsList);
                        callback.onResult(chatsList);

                    /*else{
                        //next pages load
                        //callback.onResult(usersList);
                        //Log.d(TAG, "mama load next" +  usersList.get(0).getName());
                    }*/
                    } else {
                        Log.w(TAG, "mama getChats no users exist");
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
    public void getChatsAfter(final Long key, final int size,
                         @NonNull final ItemKeyedDataSource.LoadCallback<Chat> callback){
        /*if(key == entireUsersList.get(entireUsersList.size()-1).getCreatedLong()){
            Log.d(TAG, "mama getUsersAfter init. afterKey= " +  key+ "entireUsersList= "+entireUsersList.get(entireUsersList.size()-1).getCreatedLong());
            return;
        }*/
        Log.d(TAG, "mama getChatsAfter. AfterKey= " + key);
        mChatsRef.orderByChild("lastSent")
                .startAt(key)
                .limitToFirst(size).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // [START_EXCLUDE]
                if (dataSnapshot.exists()) {
                    // loop throw users value
                    List<Chat> chatsList = new ArrayList<>();
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        Chat chat = snapshot.getValue(Chat.class);
                        if (chat != null) {
                            chat.setKey(snapshot.getKey());
                        }
                        chatsList.add(chat);
                        Log.d(TAG, "mama getChatsAfter = "+ chat.getLastMessage()+" getSnapshotKey= " +  snapshot.getKey());

                    }

                    if(chatsList.size() == 0){
                        return;
                    }

                    Log.d(TAG, "mama getChatsAfter usersList.size= " +  chatsList.size()+ " lastkey= "+chatsList.get(chatsList.size()-1).getKey());

                    //initial After
                    Collections.reverse(chatsList);
                    callback.onResult(chatsList);
                        /*((ItemKeyedDataSource.LoadCallback)callback)
                                .onResult(usersList);*/

                } else {
                    Log.w(TAG, "mama getChatsAfter no users exist");
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
    public void getChatsBefore(final Long key, final int size,
                              @NonNull final ItemKeyedDataSource.LoadCallback<Chat> callback){
        Log.d(TAG, "mama getChatsBefore. BeforeKey= " +  key);

        /*if(key == entireUsersList.get(0).getCreatedLong()){
            return;
        }*/
        mChatsRef.orderByChild("lastSent")
                .endAt(key)
                .limitToLast(size).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // [START_EXCLUDE]
                if (dataSnapshot.exists()) {
                    // loop throw users value
                    List<Chat> chatsList = new ArrayList<>();
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        Chat chat = snapshot.getValue(Chat.class);
                        if (chat != null) {
                            chat.setKey(snapshot.getKey());
                        }
                        chatsList.add(chat);
                        Log.d(TAG, "mama getChatsBefore = "+ chat.getLastMessage()+" getSnapshotKey= " +  snapshot.getKey());

                    }

                    if(chatsList.size() == 0){
                        return;
                    }

                    Log.d(TAG, "mama getChatsBefore usersList.size= " +  chatsList.size()+ " lastkey= "+chatsList.get(chatsList.size()-1).getKey());


                     //initial before
                    Collections.reverse(chatsList);
                     callback.onResult(chatsList);
                        /*((ItemKeyedDataSource.LoadCallback)callback)
                                .onResult(usersList);*/

                    //initial load
                       /* ((ItemKeyedDataSource.LoadCallback)callback)
                                .onResult(usersList, 0, usersList.size());*/
                } else {
                    Log.w(TAG, "mama getChatsBefore no users exist");
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
    public void ChatsChanged(final DataSource.InvalidatedCallback InvalidatedCallback) {

        final Query query = mChatsRef.orderByChild("lastSent");

        ChatsChangesListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!isFirstLoaded){
                    isFirstLoaded = true;
                    Log.d(TAG, "mama entireUsersList Invalidated:");
                    // Remove post value event listener
                    if (ChatsChangesListener != null) {
                        query.removeEventListener(ChatsChangesListener);
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
        query.addValueEventListener(ChatsChangesListener);
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
