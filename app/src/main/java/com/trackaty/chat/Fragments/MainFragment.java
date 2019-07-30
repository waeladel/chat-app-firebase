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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.trackaty.chat.Adapters.UsersAdapter;
import com.trackaty.chat.R;
import com.trackaty.chat.ViewModels.UsersViewModel;
import com.trackaty.chat.activities.MainActivity;
import com.trackaty.chat.models.User;

import java.util.ArrayList;

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

    private Context mActivityContext;
    private Activity activity;

    private UsersViewModel viewModel;

    private FirebaseUser mFirebaseCurrentUser;
    private String mCurrentUserId;
    private User mCurrentUser;

    //initialize the FirebaseAuth instance
    private FirebaseAuth  mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

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
        mUsersRecycler.setLayoutManager(new LinearLayoutManager(mActivityContext));

        //viewModel.usersList.observe(this, mUsersAdapter::submitList);

        //observe when a change happen to usersList live data
        mUsersRecycler.setAdapter(mUsersAdapter);

       /* // just a test to compare two objects
        User user1 = new User();
        //user1.setAvatar("wello");
        user1.setName("wello");
        user1.setBiography("wello");
        user1.setRelationship("wello");
        user1.setInterestedIn("wello");
        user1.setGender("wello");
        user1.setBirthDate(30L);
        user1.setHoroscope("wello");

        User user2 = new User();
        user2.setAvatar("wello");
        user2.setName("wello");
        user2.setBiography("wello");
        user2.setRelationship("wello");
        user2.setInterestedIn("wello");
        user2.setGender("wello");
        user2.setBirthDate(30L);
        user2.setHoroscope("wello");

        if(user1.equals(user2)){
            Log.d(TAG, "users are the same");
        }else{
            Log.d(TAG, "users are the deffrent");
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
        if(((MainActivity)getActivity())!= null){
            ActionBar actionbar = ((MainActivity)getActivity()).getSupportActionBar();
            actionbar.setTitle(R.string.main_frag_title);
            actionbar.setDisplayHomeAsUpEnabled(false);
            actionbar.setHomeButtonEnabled(false);
            actionbar.setDisplayShowCustomEnabled(false);
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
    }
}
