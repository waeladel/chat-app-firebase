package com.trackaty.chat.Fragments;

import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ServerValue;
import com.trackaty.chat.Adapters.ChatsAdapter;
import com.trackaty.chat.R;
import com.trackaty.chat.ViewModels.ChatsViewModel;
import com.trackaty.chat.ViewModels.MainActivityViewModel;
import com.trackaty.chat.activities.MainActivity;
import com.trackaty.chat.models.Chat;
import com.trackaty.chat.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class ChatsFragment extends Fragment{

    private final static String TAG = ChatsFragment.class.getSimpleName();

    private ChatsViewModel mChatsViewModel;
    private MainActivityViewModel mMainViewModel;

    private RecyclerView mChatsRecycler;
    private ArrayList<Chat> mChatsArrayList;
    private ChatsAdapter mChatsAdapter;

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

    public static ChatsFragment newInstance() {
        return new ChatsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View fragView = inflater.inflate(R.layout.chats_fragment, container, false);

        // prepare the Adapter
        mChatsArrayList = new ArrayList<>();
        mChatsAdapter = new ChatsAdapter();

        // Initiate the RecyclerView
        mChatsRecycler = (RecyclerView) fragView.findViewById(R.id.chats_recycler);
        mChatsRecycler.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(mActivityContext);
        mChatsRecycler.setLayoutManager(mLinearLayoutManager);

        //viewModel.usersList.observe(this, mUsersAdapter::submitList);

        //observe when a change happen to usersList live data
        mChatsRecycler.setAdapter(mChatsAdapter);

        mChatsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                int totalItemCount = mChatsAdapter.getItemCount(); // total items count from the adapter
                int visibleItemCount = mChatsRecycler.getChildCount(); // items are shown on screen right now

                Log.d(TAG, "visibleItemCount = "+visibleItemCount +" totalItemCount= "+totalItemCount+" lastVisibleItem "+lastVisibleItem +" firstVisibleItem "+firstVisibleItem);

                //if(lastCompletelyVisibleItem >= (totalItemCount-1)){
                /*if(lastVisibleItem >= (totalItemCount-1)){
                    // The position of last displayed item = total items, witch means we are at the bottom
                    mScrollDirection = REACHED_THE_BOTTOM;
                    Log.i(TAG, "List reached the bottom");
                    // Set scrolling direction and and last visible item which is needed to know
                    // the initial key position weather it's above or below
                    mChatsViewModel.setScrollDirection(mScrollDirection, lastVisibleItem);

                }*/if(firstVisibleItem <= 4){
                    // The position of last displayed item is less than visibleItemCount, witch means we are at the top
                    mScrollDirection = REACHED_THE_TOP;
                    Log.i(TAG, "List reached the top");
                    // Set scrolling direction and and last visible item which is needed to know
                    // the initial key position weather it's above or below
                    mChatsViewModel.setScrollDirection(mScrollDirection, firstVisibleItem);
                }else{
                    if(dy < 0 ){
                        // dy is negative number,  scrolling up
                        Log.i(TAG, "List scrolling up");
                        mScrollDirection = SCROLLING_UP;
                        // Set scrolling direction and and last visible item which is needed to know
                        // the initial key position weather it's above or below
                        mChatsViewModel.setScrollDirection(mScrollDirection, firstVisibleItem);
                    }else{
                        // dy is positive number,  scrolling down
                        Log.i(TAG, "List scrolling down");
                        mScrollDirection = SCROLLING_DOWN;
                        // Set scrolling direction and and last visible item which is needed to know
                        // the initial key position weather it's above or below
                        mChatsViewModel.setScrollDirection(mScrollDirection, lastVisibleItem);
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
    public void onDestroy() {
        Log.d(TAG, "chats onDestroy");
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(((MainActivity)getActivity())!= null) {
            ActionBar actionbar = ((MainActivity) getActivity()).getSupportActionBar();
            actionbar.setTitle(R.string.chats_frag_title);
            // disable back button because we are using bottom nav
            actionbar.setDisplayHomeAsUpEnabled(false);
            actionbar.setHomeButtonEnabled(false);
            actionbar.setDisplayShowCustomEnabled(false);
            /*actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeButtonEnabled(true);
            actionbar.setDisplayShowCustomEnabled(false);*/

            mFirebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
            if(mFirebaseCurrentUser != null){
                mCurrentUserId = mFirebaseCurrentUser.getUid();
            }

           /* // Create members Hash list, it's better to loop throw  selected members
            User mCurrentUser1 = new User();
            mCurrentUser1.setKey("Hcs4JY1zMJgF1cZsTY9R4xI670R2");
            mCurrentUser1.setName("Wello");
            mCurrentUser1.setAvatar("https://firebasestorage.googleapis.com/v0/b/chat-94dea.appspot.com/o/images%2FHcs4JY1zMJgF1cZsTY9R4xI670R2%2Favatar.jpg?alt=media&token=582f0928-366f-4967-bc11-ea660971f2d7");


            User mChatUser1 = new User();
            mChatUser1.setKey("Hcs4JY1zMJgF1cZsTY9R4xI670R2");
            mChatUser1.setName("Wello");
            mChatUser1.setAvatar("https://firebasestorage.googleapis.com/v0/b/chat-94dea.appspot.com/o/images%2FHcs4JY1zMJgF1cZsTY9R4xI670R2%2Favatar.jpg?alt=media&token=582f0928-366f-4967-bc11-ea660971f2d7");


            Map<String, User> members1 = new HashMap<>();
            members1.put(mCurrentUser1.getKey(), mCurrentUser1);
            members1.put(mChatUser1.getKey(), mChatUser1);


            // Create members Hash list, it's better to loop throw  selected members
            User mCurrentUser2 = new User();
            mCurrentUser2.setKey("Hcs4JY1zMJgF1cZsTY9R4xI670R2");
            mCurrentUser2.setName("Wello");
            mCurrentUser2.setAvatar("https://firebasestorage.googleapis.com/v0/b/chat-94dea.appspot.com/o/images%2FHcs4JY1zMJgF1cZsTY9R4xI670R2%2Favatar.jpg?alt=media&token=582f0928-366f-4967-bc11-ea660971f2d7");


            User mChatUser2 = new User();
            mChatUser2.setKey("Hcs4JY1zMJgF1cZsTY9R4xI670R2");
            mChatUser2.setName("Well");
            mChatUser2.setAvatar("https://firebasestorage.googleapis.com/v0/b/chat-94dea.appspot.com/o/images%2FHcs4JY1zMJgF1cZsTY9R4xI670R2%2Favatar.jpg?alt=media&token=582f0928-366f-4967-bc11-ea660971f2d7");


            Map<String, User> members2 = new HashMap<>();
            members2.put(mCurrentUser2.getKey(), mCurrentUser2);
            members2.put(mChatUser2.getKey(), mChatUser2);

            // Test chat equale method
            String lastMessage = "wello last message";
            String senderId = mCurrentUser1.getKey();
            Chat chat1 = new Chat();
            chat1.setLastMessage("dddd");
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
                                Log.d(TAG, "chats are deffrent");
                            }
                        }
                    },
                    3000
            );*/


            /*mMainViewModel.getCurrentUserId().observe(this, new Observer<String>() {
                @Override
                public void onChanged(final String userId) {
                    Log.d(TAG, "onChanged user userId= " + userId);
                    mCurrentUserId = userId;
                }
             });*/

                    //mChatsViewModel = ViewModelProviders.of(this).get(ChatsViewModel.class);
                    mChatsViewModel = ViewModelProviders.of(this,  new ViewModelProvider.Factory() {
                        @NonNull
                        @Override
                        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                            return (T)new ChatsViewModel (mCurrentUserId);
                        }
                    }).get(ChatsViewModel.class);


            // mChatsViewModel.setUserId(mCurrentUserId);
                    //Pass firebase callback to the viewModel constructor so that we can use it ar the repository
                    /*mChatsViewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
                        @NonNull
                        @Override
                        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                            return (T)new ChatsViewModel (mCurrentUserId);
                        }
                    }).get(ChatsViewModel.class);*/


                    mChatsViewModel.itemPagedList.observe(this, new Observer<PagedList<Chat>>() {
                        @Override
                        public void onChanged(@Nullable final PagedList<Chat> items) {

                            if (items != null ){
                                // your code here
                                Log.d(TAG, "chats onChanged submitList size" +  items.size());
                                // Create new Thread to loop until items.size() is greater than 0
                                new Thread(new Runnable() {
                                    int sleepCounter = 0;
                                    @Override
                                    public void run() {
                                        try {
                                            while(items.size()==0) {
                                                //Keep looping as long as items size is 0
                                                Thread.sleep(20);
                                                Log.d(TAG, "sleep 1000. size= "+items.size()+" sleepCounter="+sleepCounter++);
                                                if(sleepCounter == 1000){
                                                    break;
                                                }
                                                //handler.post(this);
                                            }
                                            //Now items size is greater than 0, let's submit the List
                                            Log.d(TAG, "after  sleep finished. size= "+items.size());
                                            if(items.size() == 0 && sleepCounter == 1000){
                                                // If we submit List after loop is finish with 0 results
                                                // we may erase another results submitted via newer thread
                                                Log.d(TAG, "Loop finished with 0 items. Don't submitList");
                                            }else{
                                                Log.d(TAG, "submitList");
                                                mChatsAdapter.submitList(items);
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
                                            mChatsAdapter.submitList(items);
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
