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
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trackaty.chat.Adapters.UsersAdapter;
import com.trackaty.chat.R;
import com.trackaty.chat.ViewModels.UsersViewModel;
import com.trackaty.chat.activities.MainActivity;
import com.trackaty.chat.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    private final static String TAG = MainFragment.class.getSimpleName();


    private RecyclerView mUsersRecycler;
    private ArrayList<User> mUserArrayList;
    private UsersAdapter mUsersAdapter;
    private  LinearLayoutManager mLinearLayoutManager;

    private Context mActivityContext;
    private Activity activity;

    private UsersViewModel viewModel;

    private FirebaseUser mFirebaseCurrentUser;
    private String mCurrentUserId;
    private User mCurrentUser;

    //initialize the FirebaseAuth instance
    private FirebaseAuth  mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private static final int REACHED_THE_TOP = 2;
    private static final int SCROLLING_UP = 1;
    private static final int SCROLLING_DOWN = -1;
    private static final int REACHED_THE_BOTTOM = -2;
    private static int mScrollDirection;
    private static int mLastVisibleItem;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     */
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");

        //Get current logged in user
        mFirebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mCurrentUserId = mFirebaseCurrentUser != null ? mFirebaseCurrentUser.getUid() : null;

        // prepare the Adapter
        mUserArrayList  = new ArrayList<>();
        mUsersAdapter = new UsersAdapter();

        // Initiate viewModel for this fragment instance
        viewModel = ViewModelProviders.of(this).get(UsersViewModel.class);

        // It's best to observe on onActivityCreated so that we dona't have to update ViewModel manually.
        // This is because LiveData will not call the observer since it had already delivered the last result to that observer.
        // But recycler adapter is updated any way despite that LiveData delivers updates only when data changes, and only to active observers.
        // Use getViewLifecycleOwner() instead of this, to get only one observer for this view
        viewModel.getItemPagedList().observe(this, new Observer<PagedList<User>>() {
            @Override
            public void onChanged(@Nullable final PagedList<User> items) {
                System.out.println("mama onChanged");
                if (items != null ){
                    // Create new Thread to loop until items.size() is greater than 0

                    //delay submitList till items size is not 0
                   /* new java.util.Timer().schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    // your code here
                                    Log.d(TAG, "mama submitList size" +  items.size());
                                    mUsersAdapter.submitList(items);

                                }
                            },
                            1000
                    );*/
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
                                    mUsersAdapter.submitList(items);
                                }

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();
        //initialize the AuthStateListener method
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    // If user is logged in, show recycler
                    // because it might be not showing due to previous log out
                    Log.d(TAG, "onAuthStateChanged: signed in. set recycler to visible");
                    mUsersRecycler.setVisibility(View.VISIBLE);
                } else {
                    // User is signed out
                    // If user is logged out, hide recycler
                    // because we don't want to show it before displaying login activity
                    mUsersRecycler.setVisibility(View.INVISIBLE);
                    Log.d(TAG, "onAuthStateChanged: signed_out. hide recycler");
                }
            }
        };

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView");
        /*if(activity != null){
            activity.setTitle(R.string.main_frag_title);
        }*/
        View fragView = inflater.inflate(R.layout.fragment_main, container, false);

        // Initiate the RecyclerView
        mUsersRecycler = (RecyclerView) fragView.findViewById(R.id.users_recycler);
        mUsersRecycler.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(mActivityContext);
        mUsersRecycler.setLayoutManager(mLinearLayoutManager);

        //viewModel.usersList.observe(this, mUsersAdapter::submitList);

        //observe when a change happen to usersList live data
        mUsersRecycler.setAdapter(mUsersAdapter);

        mUsersRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                int totalItemCount = mUsersAdapter.getItemCount(); // total items count from the adapter
                int visibleItemCount = mUsersRecycler.getChildCount(); // items are shown on screen right now

                Log.d(TAG, "visibleItemCount = "+visibleItemCount +" totalItemCount= "+totalItemCount+" lastVisibleItem "+lastVisibleItem +" firstVisibleItem "+firstVisibleItem);

                //if(lastCompletelyVisibleItem >= (totalItemCount-1)){
                /*if(lastVisibleItem >= (totalItemCount-1)){
                    // The position of last displayed item = total items, witch means we are at the bottom
                    mScrollDirection = REACHED_THE_BOTTOM;
                    Log.i(TAG, "List reached the bottom");
                    // Set scrolling direction and and last visible item which is needed to know
                    // the initial key position weather it's above or below
                    mChatsViewModel.setScrollDirection(mScrollDirection, lastVisibleItem);

                }*/
                if(firstVisibleItem <= 4){
                    // The position of last displayed item is less than visibleItemCount, witch means we are at the top
                    mScrollDirection = REACHED_THE_TOP;
                    Log.i(TAG, "List reached the top");
                    // Set scrolling direction and and last visible item which is needed to know
                    // the initial key position weather it's above or below
                    viewModel.setScrollDirection(mScrollDirection, firstVisibleItem);
                }else{
                    if(dy < 0 ){
                        // dy is negative number,  scrolling up
                        Log.i(TAG, "List scrolling up");
                        mScrollDirection = SCROLLING_UP;
                        // Set scrolling direction and and last visible item which is needed to know
                        // the initial key position weather it's above or below
                        viewModel.setScrollDirection(mScrollDirection, firstVisibleItem);
                    }else{
                        // dy is positive number,  scrolling down
                        Log.i(TAG, "List scrolling down");
                        mScrollDirection = SCROLLING_DOWN;
                        // Set scrolling direction and and last visible item which is needed to know
                        // the initial key position weather it's above or below
                        viewModel.setScrollDirection(mScrollDirection, lastVisibleItem);
                    }
                }
            }
        });

        /*Object timestamp = System.currentTimeMillis();
        // just a test to compare two objects
        User user1 = new User();
        user1.setAvatar("Name");
        user1.setName("Name");
        user1.setBiography("Name");
        user1.setRelationship("Name");
        user1.setInterestedIn("Name");
        user1.setGender("Name");
        user1.setBirthDate(30L);
        user1.setHoroscope("Name");
        user1.setCreated(timestamp);

        User user2 = new User();
        user2.setAvatar("Name");
        user2.setName("Name");
        user2.setBiography("Name");
        user2.setRelationship("Name");
        user2.setInterestedIn("Name");
        user2.setGender("Name");
        user2.setBirthDate(30L);
        user2.setHoroscope("Name");
        user2.setCreated(timestamp);

        if(user1.equals(user2)){
            Log.d(TAG, "users are the same");
        }else{
            Log.d(TAG, "users are the different");
        }*/

        return fragView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");
        mActivityContext = context;
        if (context instanceof Activity){// check if fragmentContext is an activity
            activity =(Activity) context;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        if((getActivity())!= null){
            ActionBar actionbar = ((MainActivity)getActivity()).getSupportActionBar();
            if(actionbar != null){
                actionbar.setTitle(R.string.main_frag_title);
                actionbar.setDisplayHomeAsUpEnabled(false);
                actionbar.setHomeButtonEnabled(false);
                actionbar.setDisplayShowCustomEnabled(false);
            }
        }
        //animalViewModel.getAnimals()?.observe(this, Observer(animalAdapter::submitList))
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        // Add firebase AuthStateListener
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        // Remove firebase AuthStateListener
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

        // Create a map for all messages need to be updated
        Map<String, Object> updateMap = new HashMap<>();

        // Update all revealed messages on fragment's stop
        if(mUsersAdapter != null){
            // Get revealed list from the adapter
            List<User> brokenAvatarsList = mUsersAdapter.getBrokenAvatarsList();

            for (int i = 0; i < brokenAvatarsList.size(); i++) {
                Log.d(TAG, "brokenAvatarsList url= "+brokenAvatarsList.get(i).getAvatar() + " key= "+brokenAvatarsList.get(i).getKey());
                updateMap.put(brokenAvatarsList.get(i).getKey()+"/avatar", brokenAvatarsList.get(i).getAvatar());
            }
        }

        if(updateMap.size() > 0 && mCurrentUserId != null){
            Log.d(TAG, "brokenAvatarsList url = updateMap.size= "+updateMap.size() +" mCurrentUserId="+mCurrentUserId  );
            // update senderAvatar to the new uri
            DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference mUserRef = mDatabaseRef.child("users"); // Should be search instead of users
            //DatabaseReference mUserRef = mDatabaseRef.child("search").child(mCurrentUserId);
            mUserRef.updateChildren(updateMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // onSuccess clear the list to start all over
                    Log.d(TAG, "brokenAvatarsList url = onSuccess ");
                    mUsersAdapter.clearBrokenAvatarsList();
                }
            });

        }

    }
}
