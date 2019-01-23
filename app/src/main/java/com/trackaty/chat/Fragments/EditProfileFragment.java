package com.trackaty.chat.Fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.aakira.expandablelayout.ExpandableLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trackaty.chat.Adapters.EditProfileAdapter;
import com.trackaty.chat.Interface.ItemClickListener;
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
public class EditProfileFragment extends Fragment implements ItemClickListener {

    private final static String TAG = EditProfileFragment.class.getSimpleName();

    public  final static int SECTION_IMAGE = 1;
    public  final static int SECTION_EDIT_TEXT = 2;
    public  final static int SECTION_TEXT = 3;
    public  final static int SECTION_SPINNER = 4;
    public  final static int SECTION_ABOUT = 5;
    public  final static int SECTION_WORK = 6;
    public  final static int SECTION_HABITS = 7;

    public  final static String SECTION_ABOUT_HEADLINE = "about";
    public  final static String SECTION_WORK_HEADLINE  = "work_and_education";
    public  final static String SECTION_HABITS_HEADLINE  = "habits";

    private static String PROFILE_LIST_STATE = "list_state";
    private static String ABOUT_LIST_STATE = "about_list_state";
    private static String WORK_LIST_STATE = "work_list_state";
    private static String HABITS_LIST_STATE = "habits_list_state";


    private User currentUser;

    // [START declare_database_ref]
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUserRef;

    private RecyclerView mEditProfileRecycler;
    private ArrayList<Profile> mProfileDataArrayList = new ArrayList<>();
    private ArrayList<Profile> mAboutArrayList = new ArrayList<>();
    private ArrayList<Profile> mWorkArrayList = new ArrayList<>();
    private ArrayList<Profile> mHabitsArrayList = new ArrayList<>();


    private EditProfileAdapter mEditProfileAdapter;

    private Parcelable savedRecyclerLayoutState;
    private static final String BUNDLE_RECYCLER_LAYOUT = "recycler_layout";

    /*private RecyclerView mAboutProfileRecycler;
    private AboutAdapter mAboutProfileAdapter;*/

    private Context getActivityContext;

    private static Boolean mIsAboutAdded ;
    private static Boolean mIsWorkAdded ;
    private static Boolean mIsHabitsAdded ;

    private ImageView expandButton;
    ExpandableLayout expandableLayout;



    public EditProfileFragment() {
        // Required empty public constructor
    }

    // This method will only be called once when the retained
    // Fragment is first created.
    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);
    }*/


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragView = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // set to false to add expandable section header once every onCreateView
        mIsAboutAdded = false;
        mIsWorkAdded = false;
        mIsHabitsAdded = false;

        // Initiate the RecyclerView
        mEditProfileRecycler = (RecyclerView) fragView.findViewById(R.id.edit_recycler);
        mEditProfileRecycler.setHasFixedSize(true);

        if (savedInstanceState != null) {
            Boolean isSavedInstance = savedInstanceState.getBoolean("isSavedInstance");
            Log.d(TAG, "isSavedInstance ="+isSavedInstance);

            // Do something with value if needed
            mProfileDataArrayList = savedInstanceState.getParcelableArrayList(PROFILE_LIST_STATE);
            mAboutArrayList = savedInstanceState.getParcelableArrayList(ABOUT_LIST_STATE);
            mWorkArrayList = savedInstanceState.getParcelableArrayList(WORK_LIST_STATE);
            mHabitsArrayList = savedInstanceState.getParcelableArrayList(HABITS_LIST_STATE);

            savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            restorePreviousState(); // Restore data found in the Bundle
        }else{
            if(getArguments()!= null){
                currentUser = EditProfileFragmentArgs.fromBundle(getArguments()).getCurrentUser();//logged in user
                Log.d(TAG,  "name= " + currentUser.getName() + "pickups=" + currentUser.getPickupCounter());
                showCurrentUser(currentUser); // No saved data, get data from remote
            }
        }


        // prepare the profile Adapter
        /*Profile profilewe = new Profile("mama", "no", 11);
        mAboutArrayList.add(profilewe);
        mAboutArrayList.add(profilewe);
        mAboutArrayList.add(profilewe);
        mAboutArrayList.add(profilewe);*/



        ///////prepare the about Adapter///////
        /*mAboutProfileArrayList  = new ArrayList<>();
        Profile profile = new Profile("mama", "no", 2);
        mAboutProfileArrayList.add(profile);
        mAboutProfileArrayList.add(profile);
        mAboutProfileArrayList.add(profile);
        mAboutProfileArrayList.add(profile);

        mAboutProfileAdapter = new AboutAdapter(getActivityContext,mAboutProfileArrayList);

        // Initiate the about RecyclerView
        mAboutProfileRecycler = (RecyclerView) fragView.findViewById(R.id.expandable_recycler);
        mAboutProfileRecycler.setHasFixedSize(true);

        mAboutProfileRecycler.setLayoutManager(new LinearLayoutManager(getActivityContext));
        mAboutProfileRecycler.setAdapter(mAboutProfileAdapter);

        expandButton= (ImageView) fragView.findViewById(R.id.expand_button);
        expandableLayout = (ExpandableLayout) fragView.findViewById(R.id.expandableLayout);

        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "expandToggle id clicked= ");
                expandableLayout.toggle();

            }
        });*/

        return fragView;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getActivityContext = context;
    }

    // Fires when a configuration change occurs and fragment needs to save state
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean ("isSavedInstance", true);
        outState.putParcelableArrayList(PROFILE_LIST_STATE, mProfileDataArrayList);
        outState.putParcelableArrayList(ABOUT_LIST_STATE, mAboutArrayList);
        outState.putParcelableArrayList(WORK_LIST_STATE, mWorkArrayList);
        outState.putParcelableArrayList(HABITS_LIST_STATE, mHabitsArrayList);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, mEditProfileRecycler.getLayoutManager().onSaveInstanceState());
    }

    private void restorePreviousState() {
        mEditProfileAdapter = new EditProfileAdapter(getActivityContext
                , mProfileDataArrayList
                , mAboutArrayList
                , mWorkArrayList
                , mHabitsArrayList
                , this);

        Log.d(TAG, "mWorkArrayList college 0="+mWorkArrayList.get(0).getValue());
        Log.d(TAG, "mWorkArrayList college 1="+mWorkArrayList.get(1).getValue());
        Log.d(TAG, "mWorkArrayList college 2="+mWorkArrayList.get(2).getValue());

        mEditProfileRecycler.setLayoutManager(new LinearLayoutManager(getActivityContext));
        mEditProfileRecycler.setAdapter(mEditProfileAdapter);
        restoreLayoutManagerPosition();
        mEditProfileAdapter.notifyDataSetChanged();
    }

    private void restoreLayoutManagerPosition() {
        if (savedRecyclerLayoutState != null) {
            mEditProfileRecycler.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    private void showCurrentUser(User currentUser) {

        // [START initialize_database_ref]

                    if (null!= currentUser) {
                        Field[] fields = currentUser.getClass().getDeclaredFields();
                        for (Field f : fields) {
                            Log.d(TAG, "fields=" + f.getName());
                            //Log.d(TAG, "fields="+f.get);
                            getDynamicMethod(f.getName(), currentUser);
                        }
                        Log.d(TAG, "mProfileDataArrayList sorted get size" + mProfileDataArrayList.size());

                        // sort ArrayList into sections then notify the adapter
                        Collections.sort(mProfileDataArrayList, new Sortbysection());

                        for (int i = 0; i < mProfileDataArrayList.size(); i++) {
                            Log.i(TAG, "mProfileDataArrayList sorted" + mProfileDataArrayList.get(i).getKey());
                        }

                        // [END single_value_read]
                        mEditProfileAdapter = new EditProfileAdapter(getActivityContext
                                , mProfileDataArrayList
                                , mAboutArrayList
                                , mWorkArrayList
                                , mHabitsArrayList
                                , this);

                        mEditProfileRecycler.setLayoutManager(new LinearLayoutManager(getActivityContext));
                        mEditProfileRecycler.setAdapter(mEditProfileAdapter);

                        //mEditProfileAdapter.notifyDataSetChanged();
                        //mAboutProfileAdapter.notifyDataSetChanged();
                            /*String userName = dataSnapshot.child("name").getValue().toString();
                            String currentUser = dataSnapshot.getKey();*/
                        Log.d(TAG, "user exist: Name=" + currentUser.getName());
                    }

                }


    private void getDynamicMethod(String fieldName, User currentUser) {

        Method[] methods = currentUser.getClass().getMethods();

        for (Method method : methods) {
            if ((method.getName().startsWith("get")) && (method.getName().length() == (fieldName.length() + 3))) {
                if (method.getName().toLowerCase().endsWith(fieldName.toLowerCase())) {
                    // Method found, run it
                    try {
                        // check if is not null or empty
                        if (method.invoke(currentUser) != null){
                            Log.d(TAG, "Method=" + method.invoke(currentUser));
                            Log.d(TAG, "Method Type=" + method.getGenericReturnType());

                            if((fieldName.equals("avatar") || fieldName.equals("coverImage"))){
                                Profile profileData = new Profile(fieldName, method.invoke(currentUser).toString(),SECTION_IMAGE);
                                mProfileDataArrayList.add(profileData);

                            }else if((fieldName.equals("name")
                                    || fieldName.equals("biography"))){
                            Profile profileData = new Profile(fieldName, method.invoke(currentUser).toString(), SECTION_EDIT_TEXT);
                            mProfileDataArrayList.add(profileData);

                            }else if(fieldName.equals("age")){
                                Profile profileData = new Profile(fieldName, method.invoke(currentUser).toString(), SECTION_TEXT);
                                mProfileDataArrayList.add(profileData);

                            }else if((fieldName.equals("relationship")
                                    || fieldName.equals("gender")
                                    || fieldName.equals("horoscope")
                                    || fieldName.equals("interestedIn"))){
                            Profile profileData = new Profile(fieldName, method.invoke(currentUser).toString(),SECTION_SPINNER);
                            mProfileDataArrayList.add(profileData);

                            } else if(fieldName.equals("work")
                                    || fieldName.equals("college")
                                    || fieldName.equals("school")){
                                //add data for work section
                                Profile profileData = new Profile(fieldName, method.invoke(currentUser).toString(),SECTION_WORK);
                                //mProfileDataArrayList.add(profileData);
                                mWorkArrayList.add(profileData);
                                if(!mIsWorkAdded){
                                    Log.d(TAG, "mIsAboutAdded=" + mIsWorkAdded);
                                    Profile aboutSectionData = new Profile(SECTION_WORK_HEADLINE, method.invoke(currentUser).toString(),SECTION_WORK);
                                    mProfileDataArrayList.add(aboutSectionData);
                                }
                                mIsWorkAdded = true; // to add only one expandable section header

                            }else if(fieldName.equals("nationality")
                                    || fieldName.equals("hometown")
                                    || fieldName.equals("lives")
                                    || fieldName.equals("politics")
                                    || fieldName.equals("religion")){
                                //add data for about section
                                Profile profileData = new Profile(fieldName, method.invoke(currentUser).toString(),SECTION_ABOUT);
                                //mProfileDataArrayList.add(profileData);
                                mAboutArrayList.add(profileData);
                                if(!mIsAboutAdded){
                                    Log.d(TAG, "mIsAboutAdded=" + mIsAboutAdded);
                                    Profile aboutSectionData = new Profile(SECTION_ABOUT_HEADLINE, method.invoke(currentUser).toString(),SECTION_ABOUT);
                                    mProfileDataArrayList.add(aboutSectionData);
                                }
                                mIsAboutAdded = true; // to add only one expandable section header

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
                                Profile profileData = new Profile(fieldName, method.invoke(currentUser).toString(),SECTION_HABITS);
                                //mProfileDataArrayList.add(profileData);
                                mHabitsArrayList.add(profileData);
                                if(!mIsHabitsAdded){
                                    Log.d(TAG, "mIsAboutAdded=" + mIsHabitsAdded);
                                    Profile habitsSectionData = new Profile(SECTION_HABITS_HEADLINE, method.invoke(currentUser).toString(),SECTION_HABITS);
                                    mProfileDataArrayList.add(habitsSectionData);
                                }
                                mIsHabitsAdded = true; // to add only one expandable section header
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

    // use interface to detect item click on the recycler adapter
    @Override
    public void onClick(View view, int position, boolean isLongClick) {
        Log.d(TAG, "item clicked fragment= " + position);
    }
}
