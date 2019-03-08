package com.trackaty.chat.Fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        /*if(activity != null){
            activity.setTitle(R.string.main_frag_title);
        }*/
        View fragView = inflater.inflate(R.layout.fragment_main, container, false);

        // prepare the Adapter
        mUserArrayList  = new ArrayList<>();
        mUsersAdapter = new UsersAdapter();

        // Initiate the RecyclerView
        mUsersRecycler = (RecyclerView) fragView.findViewById(R.id.users_recycler);
        mUsersRecycler.setHasFixedSize(true);
        mUsersRecycler.setLayoutManager(new LinearLayoutManager(mActivityContext));

        //viewModel.usersList.observe(this, mUsersAdapter::submitList);

        //observe when a change happen to usersList live data
        mUsersRecycler.setAdapter(mUsersAdapter);

        // just a test to compare two objects
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
        }

        return fragView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivityContext = context;
        if (context instanceof Activity){// check if context is an activity
            activity =(Activity) context;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(((MainActivity)getActivity())!= null){
            ActionBar actionbar = ((MainActivity)getActivity()).getSupportActionBar();
            actionbar.setTitle(R.string.main_frag_title);
            actionbar.setDisplayHomeAsUpEnabled(false);
            actionbar.setHomeButtonEnabled(false);
            actionbar.setDisplayShowCustomEnabled(false);
        }

        // Initiate viewModel for this fragment instance
        viewModel = ViewModelProviders.of(this).get(UsersViewModel.class);
        //viewModel.getPagedListObservable().observe(this, new Observer<PagedList<User>>() {
        viewModel.usersList.observe(this, new Observer<PagedList<User>>() {
            @Override
            public void onChanged(@Nullable final PagedList<User> items) {
                System.out.println("mama onChanged");
                if (items != null ){
                /*Log.d(TAG, "mama submitList size" +  items.size());
                mUsersAdapter.submitList(items);*/
                    //delay submitList till items size is not 0
                    new java.util.Timer().schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    // your code here
                                    Log.d(TAG, "mama submitList size" +  items.size());
                                    mUsersAdapter.submitList(items);

                                }
                            },
                            5000
                    );

                }
            }
        });

        //animalViewModel.getAnimals()?.observe(this, Observer(animalAdapter::submitList))
    }

}
