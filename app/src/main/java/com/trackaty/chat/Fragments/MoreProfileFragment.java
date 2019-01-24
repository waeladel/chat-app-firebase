package com.trackaty.chat.Fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trackaty.chat.Adapters.ProfileAdapter;
import com.trackaty.chat.R;
import com.trackaty.chat.Utils.Sortbysection;
import com.trackaty.chat.models.Profile;
import com.trackaty.chat.models.User;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 */
public class MoreProfileFragment extends Fragment {

    private final static String TAG = MoreProfileFragment.class.getSimpleName();
    private String mCurrentUserId, mUserId;
    private User mUser;


    public  final static int SECTION_ABOUT = 1;
    public  final static int SECTION_WORK = 2;
    public  final static int SECTION_HABITS = 3;



    // [START declare_database_ref]
    /*private DatabaseReference mDatabaseRef;
    private DatabaseReference mUserRef;*/

    private RecyclerView mProfileRecycler;
    private ArrayList <Profile> mUserArrayList;
    private ProfileAdapter mProfileAdapter;

    private Context mActivityContext;
    private Activity activity;

    public MoreProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragView = inflater.inflate(R.layout.fragment_more_profile, container, false);

        // prepare the Adapter
        mUserArrayList  = new ArrayList<>();
        /*mUserArrayList.add("mama");
        mUserArrayList.add("baba");
        mUserArrayList.add("lala");
        mUserArrayList.add("tata");
        mUserArrayList.add("kaka");*/

        mProfileAdapter  = new ProfileAdapter(mActivityContext,mUserArrayList);

        // Initiate the RecyclerView
        mProfileRecycler  = (RecyclerView) fragView.findViewById(R.id.profile_recycler);
        mProfileRecycler.setHasFixedSize(true);
        mProfileRecycler.setLayoutManager(new LinearLayoutManager(mActivityContext));
        mProfileRecycler.setAdapter(mProfileAdapter);

        if (getArguments() != null) {
            mCurrentUserId = MoreProfileFragmentArgs.fromBundle(getArguments()).getUserId();
            mUserId = MoreProfileFragmentArgs.fromBundle(getArguments()).getUserId(); // any user
            mUser = ProfileFragmentArgs.fromBundle(getArguments()).getUser();// any user
            Log.d(TAG, "mCurrentUserId= " + mCurrentUserId + "mUserId= " + mUserId + "name= " + mUser.getName() + "pickups=" + mUser.getPickupCounter());

            // [display parcelable data]
            if (null != mCurrentUserId && mCurrentUserId.equals(mUserId)) {
                // it's logged in user profile
                showCurrentUser(mUser);
            } else {
                // it's another user
                //mUserRef = mDatabaseRef.child("users").child(mUserId);
                //showUser(mUserId);
            }
        }

        if(activity != null){
            activity.setTitle(R.string.more_profile_frag_title);
        }

        return fragView;
    }

    private void showCurrentUser(User user) {
        if (user != null) {

            Field[] fields = user.getClass().getFields();
            for (Field f : fields) {
                Log.d(TAG, "fields=" + f.getName());
                //Log.d(TAG, "fields="+f.get);
                getDynamicMethod(f.getName(), user);
            }
            Log.d(TAG, "mUserArrayList sorted get size" + mUserArrayList.size());

            // sort ArrayList into sections then notify the adapter
            Collections.sort(mUserArrayList, new Sortbysection());

            for (int i = 0; i < mUserArrayList.size(); i++) {
                Log.i(TAG, "mUserArrayList sorted" + mUserArrayList.get(i).getKey());
            }

            mProfileAdapter.notifyDataSetChanged();
                            /*String userName = dataSnapshot.child("name").getValue().toString();
                            String mCurrentUserId = dataSnapshot.getKey();*/
            Log.d(TAG, "user exist: Name=" + user.getName());

        }

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivityContext = context;
        if (context instanceof Activity){// check if context is an activity
            activity =(Activity) context;
        }
    }

    private void getDynamicMethod(String fieldName, User user) {

        Method[] methods = user.getClass().getMethods();

        for (Method method : methods) {
            if ((method.getName().startsWith("get")) && (method.getName().length() == (fieldName.length() + 3))) {
                if (method.getName().toLowerCase().endsWith(fieldName.toLowerCase())) {
                    // Method found, run it
                    try {
                        // check if is not null or empty
                        if (method.invoke(user) != null){
                            Log.d(TAG, "Method=" + method.invoke(user));
                            Log.d(TAG, "Method Type=" + method.getGenericReturnType());

                            if(fieldName.equals("work")
                                    || fieldName.equals("college")
                                    || fieldName.equals("school")){
                                //add data for work section
                                Profile profileData = new Profile(fieldName, method.invoke(user).toString(),SECTION_WORK);
                                mUserArrayList.add(profileData);
                            }else if(fieldName.equals("age")
                                    || fieldName.equals("gender")
                                    || fieldName.equals("nationality")
                                    || fieldName.equals("hometown")
                                    || fieldName.equals("horoscope")
                                    || fieldName.equals("lives")
                                    || fieldName.equals("politics")
                                    || fieldName.equals("religion")){
                                //add data for about section
                                Profile profileData = new Profile(fieldName, method.invoke(user).toString(),SECTION_ABOUT);
                                mUserArrayList.add(profileData);
                            }else if(fieldName.equals("athlete")
                                    || fieldName.equals("smoke")
                                    || fieldName.equals("travel")
                                    || fieldName.equals("shisha")
                                    || fieldName.equals("cook")
                                    || fieldName.equals("drink")
                                    || fieldName.equals("drugs")
                                    || fieldName.equals("gamer")
                                    || fieldName.equals("read")){
                                //add data for habits section
                                Profile profileData = new Profile(fieldName, method.invoke(user).toString(),SECTION_HABITS);
                                mUserArrayList.add(profileData);
                            }

                        }


                    } catch (IllegalAccessException e) {
                        Log.e(TAG, "Could not determine method: " + method.getName());

                    } catch (InvocationTargetException e) {

                        Log.e(TAG, "Could not determine method:" + method.getName());
                    }

                }
            }
        }

    }

}
