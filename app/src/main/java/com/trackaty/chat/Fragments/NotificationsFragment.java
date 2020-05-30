package com.trackaty.chat.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trackaty.chat.Adapters.NotificationsAdapter;
import com.trackaty.chat.R;
import com.trackaty.chat.ViewModels.NotificationsViewModel;
import com.trackaty.chat.activities.MainActivity;
import com.trackaty.chat.models.Chat;
import com.trackaty.chat.models.DatabaseNotification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationsFragment extends Fragment{

    private final static String TAG = NotificationsFragment.class.getSimpleName();

    private NotificationsViewModel mViewModel;

    private RecyclerView mRecycler;
    private ArrayList<Chat> mArrayList;
    private NotificationsAdapter mAdapter;

    private FirebaseUser mFirebaseCurrentUser;
    private String mCurrentUserId;

    private Context mActivityContext;
    private Activity activity;
    private  LinearLayoutManager mLinearLayoutManager;

    private static final int REACHED_THE_TOP = 2;
    private static final int SCROLLING_UP = 1;
    private static final int SCROLLING_DOWN = -1;
    private static final int REACHED_THE_BOTTOM = -2;
    private static int mScrollDirection;
    private static int mLastVisibleItem;

    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Notifications onCreate");

        mFirebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mFirebaseCurrentUser != null){
            mCurrentUserId = mFirebaseCurrentUser.getUid();
        }

        /*Long currentTime = System.currentTimeMillis();
        DatabaseNotification notification1 = new DatabaseNotification("Like", "oveoHBBXX5TcjkPvjCejLPzfhrx2", "User oii", "http://sdfege");
        notification1.setSeen(true);
        notification1.setClicked(true);
        notification1.setSent(34546L);

        DatabaseNotification notification2 = new DatabaseNotification("Like", "oveoHBBXX5TcjkPvjCejLPzfhrx2", "User oii", "http://sdfege");
        notification2.setSeen(true);
        notification2.setClicked(true);
        notification2.setSent(34546L);

        if(notification1.equals(notification2)){
            Log.d(TAG, "Notifications equals: are the same");
        }else{
            Log.d(TAG, "Notifications equals: are deffreint");
        }*/

        // prepare the Adapter in onCreate to use only one Adapter
        mArrayList = new ArrayList<>();
        mAdapter = new NotificationsAdapter(this);
        //mAdapter = new NotificationsAdapter(mActivityContext);

        // Create ViewModel in onCreate to use only one ViewModel and observer
        // So we don't recreate the observer when user comeback to active ViewModel
        //mViewModel = ViewModelProviders.of(this).get(NotificationsViewModel.class);
        mViewModel = new ViewModelProvider(this,  new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T)new NotificationsViewModel (mCurrentUserId);
            }
        }).get(NotificationsViewModel.class);

        // mViewModel.setUserId(mCurrentUserId);
        //Pass firebase callback to the viewModel constructor so that we can use it ar the repository
                    /*mViewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
                        @NonNull
                        @Override
                        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                            return (T)new ChatsViewModel (mCurrentUserId);
                        }
                    }).get(ChatsViewModel.class);*/


        // It's best to observe on onActivityCreated so that we dona't have to update ViewModel manually.
        // This is because LiveData will not call the observer since it had already delivered the last result to that observer.
        // But recycler adapter is updated any way despite that LiveData delivers updates only when data changes, and only to active observers.
        // Use getViewLifecycleOwner() instead of this, to get only one observer for this view
        mViewModel.getItemPagedList().observe(this, new Observer<PagedList<DatabaseNotification>>() {
            @Override
            public void onChanged(@Nullable final PagedList<DatabaseNotification> items) {

                if (items != null ){
                    // your code here
                    Log.d(TAG, "Notifications onChanged submitList size" +  items.size());
                    // Create new Thread to loop until items.size() is greater than 0
                    new Thread(new Runnable() {
                        int sleepCounter = 0;
                        @Override
                        public void run() {
                            try {
                                while(items.size()==0) {
                                    //Keep looping as long as items size is 0
                                    Thread.sleep(20);
                                    Log.d(TAG, "Notifications onChanged. sleep 1000. size= "+items.size()+" sleepCounter="+sleepCounter++);
                                    if(sleepCounter == 1000){
                                        break;
                                    }
                                    //handler.post(this);
                                }
                                //Now items size is greater than 0, let's submit the List
                                Log.d(TAG, "Notifications onChanged. after  sleep finished. size= "+items.size());
                                if(items.size() == 0 && sleepCounter == 1000){
                                    // If we submit List after loop is finish with 0 results
                                    // we may erase another results submitted via newer thread
                                    Log.d(TAG, "Notifications onChanged. Loop finished with 0 items. Don't submitList");
                                }else{
                                    Log.d(TAG, "Notifications onChanged. submitList= "+items.size());
                                    mAdapter.submitList(items);
                                }

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();
                                /*Thread thread = new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            while(items.size()==0) {
                                                //Keep looping as long as items size is 0
                                                sleep(10);
                                                Log.d(TAG, "sleep 1000. size= "+items.size());
                                                //handler.post(this);
                                            }
                                            //Now items size is greater than 0, let's submit the List
                                            Log.d(TAG, "after  sleep finished. size= "+items.size());
                                            mAdapter.submitList(items);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                thread.start();*/
                }
            }
        });

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View fragView = inflater.inflate(R.layout.fragment_notification, container, false);

        Log.d(TAG, "chats onCreateView");


        // Initiate the RecyclerView
        mRecycler = fragView.findViewById(R.id.notifications_recycler);
        mRecycler.setHasFixedSize(true);

        mLinearLayoutManager = new LinearLayoutManager(mActivityContext);
        mRecycler.setLayoutManager(mLinearLayoutManager);
        mRecycler.setAdapter(mAdapter);


        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d(TAG, "onScrollStateChanged newState= "+newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d(TAG, "onScrolled dx= "+dx +" dy= "+dy);

                //int lastCompletelyVisibleItem = mLinearLayoutManager.findLastCompletelyVisibleItemPosition(); // the position of last displayed item
                //int firstCompletelyVisibleItem = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition(); // the position of first displayed item
                int lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition(); // the position of last displayed item
                int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition(); // the position of first displayed item
                int totalItemCount = mAdapter.getItemCount(); // total items count from the adapter
                int visibleItemCount = mRecycler.getChildCount(); // items are shown on screen right now

                Log.d(TAG, "visibleItemCount = "+visibleItemCount +" totalItemCount= "+totalItemCount+" lastVisibleItem "+lastVisibleItem +" firstVisibleItem "+firstVisibleItem);

                //if(lastCompletelyVisibleItem >= (totalItemCount-1)){
                /*if(lastVisibleItem >= (totalItemCount-1)){
                    // The position of last displayed item = total items, witch means we are at the bottom
                    mScrollDirection = REACHED_THE_BOTTOM;
                    Log.i(TAG, "List reached the bottom");
                    // Set scrolling direction and and last visible item which is needed to know
                    // the initial key position weather it's above or below
                    mViewModel.setScrollDirection(mScrollDirection, lastVisibleItem);

                }*/if(firstVisibleItem <= 4){
                    // The position of last displayed item is less than visibleItemCount, witch means we are at the top
                    mScrollDirection = REACHED_THE_TOP;
                    Log.i(TAG, "List reached the top");
                    // Set scrolling direction and and last visible item which is needed to know
                    // the initial key position weather it's above or below
                    mViewModel.setScrollDirection(mScrollDirection, firstVisibleItem);
                }else{
                    if(dy < 0 ){
                        // dy is negative number,  scrolling up
                        Log.i(TAG, "List scrolling up");
                        mScrollDirection = SCROLLING_UP;
                        // Set scrolling direction and and last visible item which is needed to know
                        // the initial key position weather it's above or below
                        mViewModel.setScrollDirection(mScrollDirection, firstVisibleItem);
                    }else{
                        // dy is positive number,  scrolling down
                        Log.i(TAG, "List scrolling down");
                        mScrollDirection = SCROLLING_DOWN;
                        // Set scrolling direction and and last visible item which is needed to know
                        // the initial key position weather it's above or below
                        mViewModel.setScrollDirection(mScrollDirection, lastVisibleItem);
                    }
                }
            }
        });
        return fragView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivityContext = context;

        if (context instanceof Activity){// check if fragmentContext is an activity
            activity =(Activity) context;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        // Create a map for all messages need to be updated
        Map<String, Object> updateMap = new HashMap<>();

        // Update all revealed messages on fragment's stop
        if(mAdapter != null){
            // Get revealed list from the adapter
            List<DatabaseNotification> brokenAvatarsList = mAdapter.getBrokenAvatarsList();

            for (int i = 0; i < brokenAvatarsList.size(); i++) {
                Log.d(TAG, "brokenAvatarsList url= "+brokenAvatarsList.get(i).getSenderAvatar() + " key= "+brokenAvatarsList.get(i).getKey());
                updateMap.put(brokenAvatarsList.get(i).getKey()+"/senderAvatar", brokenAvatarsList.get(i).getSenderAvatar());
            }
        }

        if(updateMap.size() > 0 && mCurrentUserId != null){
            Log.d(TAG, "brokenAvatarsList url = updateMap.size= "+updateMap.size() +" mCurrentUserId="+mCurrentUserId  );
            // update senderAvatar to the new uri
            DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference mNotificationsRef = mDatabaseRef.child("notifications").child("alerts").child(mCurrentUserId);
            //mNotificationsRef.child(key).child("senderAvatar").setValue(String.valueOf(uri));
            mNotificationsRef.updateChildren(updateMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // onSuccess clear the list to start all over
                    Log.d(TAG, "brokenAvatarsList url = onSuccess ");
                    mAdapter.clearBrokenAvatarsList();
                }
            });

        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "chats onDestroy");
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "chats onActivityCreated");

        if((getActivity())!= null) {
            ActionBar actionbar = ((MainActivity) getActivity()).getSupportActionBar();
            if (actionbar != null) {
                actionbar.setTitle(R.string.notifications_frag_title);
                // disable back button because we are using bottom nav
                actionbar.setDisplayHomeAsUpEnabled(false);
                actionbar.setHomeButtonEnabled(false);
                actionbar.setDisplayShowCustomEnabled(false);
                /*actionbar.setDisplayHomeAsUpEnabled(true);
                actionbar.setHomeButtonEnabled(true);
                actionbar.setDisplayShowCustomEnabled(false);*/
            }

            /*// Create members Hash list, it's better to loop throw  selected members
            ChatMember mCurrentUser1 = new ChatMember();
            mCurrentUser1.setKey("Hcs4JY1zMJgF1cZsTY9R4xI670R2");
            mCurrentUser1.setName("Name");
            //mCurrentUser1.setAvatar("https://firebasestorage.googleapis.com/v0/b/chat-94dea.appspot.com/o/images%2FHcs4JY1zMJgF1cZsTY9R4xI670R2%2Favatar.jpg?alt=media&token=582f0928-366f-4967-bc11-ea660971f2d7");
            mCurrentUser1.setSaw(true);


           *//* ChatMember mChatUser1 = new ChatMember();
            mChatUser1.setKey("oveoHBBXX5TcjkPvjCejLPzfhrx2");
            mChatUser1.setName("User oiiiiiiiii");
            //mChatUser1.setAvatar("https://firebasestorage.googleapis.com/v0/b/chat-94dea.appspot.com/o/images%2FHcs4JY1zMJgF1cZsTY9R4xI670R2%2Favatar.jpg?alt=media&token=582f0928-366f-4967-bc11-ea660971f2d7");
            mChatUser1.setSaw(true);*//*


            Map<String, ChatMember> members1 = new HashMap<>();
            members1.put(mCurrentUser1.getKey(), mCurrentUser1);
            //members1.put(mChatUser1.getKey(), mChatUser1);


            // Create members Hash list, it's better to loop throw  selected members
            ChatMember mCurrentUser2 = new ChatMember();
            mCurrentUser2.setKey("Hcs4JY1zMJgF1cZsTY9R4xI670R2");
            mCurrentUser2.setName("Name");
            //mCurrentUser2.setAvatar("https://firebasestorage.googleapis.com/v0/b/chat-94dea.appspot.com/o/images%2FHcs4JY1zMJgF1cZsTY9R4xI670R2%2Favatar.jpg?alt=media&token=582f0928-366f-4967-bc11-ea660971f2d7");
            mCurrentUser2.setSaw(true);


            *//*ChatMember mChatUser2 = new ChatMember();
            mChatUser2.setKey("oveoHBBXX5TcjkPvjCejLPzfhrx2");
            mChatUser2.setName("User oiiiiiiiii");
            //mChatUser2.setAvatar("https://firebasestorage.googleapis.com/v0/b/chat-94dea.appspot.com/o/images%2FHcs4JY1zMJgF1cZsTY9R4xI670R2%2Favatar.jpg?alt=media&token=582f0928-366f-4967-bc11-ea660971f2d7");
            mChatUser2.setSaw(true);*//*


            Map<String, ChatMember> members2 = new HashMap<>();
            members2.put(mCurrentUser2.getKey(), mCurrentUser2);
            //members2.put(mChatUser2.getKey(), mChatUser2);

            // Test chat equale method
            String lastMessage = "last message";
            String senderId = mCurrentUser1.getKey();
            Chat chat1 = new Chat();
            chat1.setLastMessage("last message");
            chat1.setSender(senderId);
            chat1.setMembers(members1);
            Long currentTime = System.currentTimeMillis();
            chat1.setLastSent(currentTime);

            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            // your code here
                            Chat chat2 = new Chat("dddd", senderId, members2);
                            chat2.setLastSent(currentTime);
                            if(chat1.equals(chat2)){
                                Log.d(TAG, "chats are the same" );
                            }else{
                                Log.d(TAG, "chats are different");
                            }
                        }
                    },
                    3000
            );
*/

            /*mMainViewModel.getCurrentUserId().observe(this, new Observer<String>() {
                @Override
                public void onChanged(final String userId) {
                    Log.d(TAG, "onChanged user userId= " + userId);
                    mCurrentUserId = userId;
                }
             });*/

        }

        /*// update the CurrentUserId whenever it changes due to log out
        mMainViewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);

        // update the CurrentUser whenever it changes
        mMainViewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                Log.d(TAG, "onChanged user userId name= "+user.getName());
            }
        });*/

    }
}

