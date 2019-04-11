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
import com.trackaty.chat.Adapters.ChatsAdapter;
import com.trackaty.chat.R;
import com.trackaty.chat.ViewModels.ChatsViewModel;
import com.trackaty.chat.ViewModels.MainActivityViewModel;
import com.trackaty.chat.activities.MainActivity;
import com.trackaty.chat.models.Chat;

import java.util.ArrayList;

public class ChatsFragment extends Fragment {

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
    //private int itemsSize;
    private  Handler handler;


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
        mChatsRecycler.setLayoutManager(new LinearLayoutManager(mActivityContext));

        //viewModel.usersList.observe(this, mUsersAdapter::submitList);

        //observe when a change happen to usersList live data
        mChatsRecycler.setAdapter(mChatsAdapter);

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

            /*mMainViewModel.getCurrentUserId().observe(this, new Observer<String>() {
                @Override
                public void onChanged(final String userId) {
                    Log.d(TAG, "onChanged user userId= " + userId);
                    mCurrentUserId = userId;
                }
             });*/

                    //mChatsViewModel = ViewModelProviders.of(this).get(ChatsViewModel.class);

                    mChatsViewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
                        @NonNull
                        @Override
                        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                            return (T)new ChatsViewModel (mCurrentUserId);
                        }
                    }).get(ChatsViewModel.class);


                    mChatsViewModel.itemPagedList.observe(this, new Observer<PagedList<Chat>>() {
                        @Override
                        public void onChanged(@Nullable final PagedList<Chat> items) {

                            if (items != null ){
                                // your code here
                                Log.d(TAG, "mama messages submitList size" +  items.size());
                                mChatsAdapter.submitList(items);

                                //Log.d(TAG, "mama onChanged "+ items.size());
                                /*handler = new Handler();

                                final Runnable r = new Runnable() {
                                    public void run() {
                                        //await().until(itemsIsAdded(items.size()));
                                        Log.d(TAG, "mama messages submitList size" +  items.size());
                                        //mChatsAdapter.submitList(items);}
                                        handler.postDelayed(this, 1000);
                                };

                                handler.postDelayed(r, 1000);*/
                                /*handler = new Handler();
                                Thread thread = new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            while(items.size() == 0) {
                                                sleep(1000);
                                                Log.d(TAG, "mama messages submitList size" +  items.size());
                                                handler.post(this);
                                            }
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };

                                thread.start();*/

                                Log.d(TAG, "mama while ends submitList size" +  items.size());

                                //await().atMost(5, SECONDS);
                                //itemsSize = items.size();
                                //await().until(itemsIsAdded(items.size()));
                                //Log.d(TAG, "mama messages submitList size" +  items.size());
                                //mChatsAdapter.submitList(items);

                                //delay submitList till items size is not 0
                                /*new java.util.Timer().schedule(
                                        new java.util.TimerTask() {
                                            @Override
                                            public void run() {
                                                // your code here
                                                Log.d(TAG, "mama messages submitList size" +  items.size());
                                                mChatsAdapter.submitList(items);
                                            }
                                        },
                                        2000
                                );*/

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
