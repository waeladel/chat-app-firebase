package com.trackaty.chat.Fragments;


import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.trackaty.chat.Adapters.EditProfileAdapter;
import com.trackaty.chat.Interface.FirebaseUserCallback;
import com.trackaty.chat.Interface.ItemClickListener;
import com.trackaty.chat.R;
import com.trackaty.chat.Utils.FilesHelper;
import com.trackaty.chat.Utils.SortSocial;
import com.trackaty.chat.Utils.SortBySection;
import com.trackaty.chat.ViewModels.EditProfileViewModel;
import com.trackaty.chat.activities.MainActivity;
import com.trackaty.chat.models.Profile;
import com.trackaty.chat.models.Social;
import com.trackaty.chat.models.User;
import com.trackaty.chat.models.Variables;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static android.app.Activity.RESULT_OK;
import static com.trackaty.chat.Utils.MenuHelper.menuIconWithText;


/**
 * A simple {@link Fragment} subclass.
 */
public class CompleteProfileFragment extends Fragment implements ItemClickListener {

    private final static String TAG = CompleteProfileFragment.class.getSimpleName();


    private final int MENU_ITEM_SAVE_ID = 1;

    private  static final int SECTION_AVATAR = 100;
    private  static final int SECTION_COVER = 200;
    private  static final int SECTION_EDIT_TEXT = 300;
    private  static final int SECTION_TEXT = 400;
    private  static final int SECTION_SPINNER = 500;
    private  static final int SECTION_ABOUT = 600;
    private  static final int SECTION_WORK = 700;
    private  static final int SECTION_HABITS = 800;
    private  static final int SECTION_SOCIAL = 900;


    private  final static String SECTION_ABOUT_HEADLINE = "about";
    private  final static String SECTION_WORK_HEADLINE  = "work_and_education";
    private  final static String SECTION_HABITS_HEADLINE  = "habits";
    private  final static String SECTION_SOCIAL_HEADLINE  = "social_and_contacts";

    private static final String PROFILE_LIST_STATE = "list_state";
    private static final String ABOUT_LIST_STATE = "about_list_state";
    private static final String WORK_LIST_STATE = "work_list_state";
    private static final String HABITS_LIST_STATE = "habits_list_state";
    private static final String SOCIAL_LIST_STATE = "social_list_state";
    private static final String VARIABLES_LIST_STATE = "variables_list_state";

    private static final String AVATAR_THUMBNAIL_NAME = "avatar.jpg";
    private static final String COVER_THUMBNAIL_NAME = "cover.jpg";
    private static final String AVATAR_ORIGINAL_NAME = "original_avatar.jpg";
    private static final String COVER_ORIGINAL_NAME = "original_cover.jpg";

    private  final static String IMAGE_HOLDER_POSITION = "position";

    private static final int CROP_IMAGE_AVATAR_REQUEST_CODE = 103;
    private static final int CROP_IMAGE_COVER_REQUEST_CODE = 104;


    //private User currentUser;
    private String currentUserId;
    private FirebaseUser mFirebaseCurrentUser;


    // [START declare_database_ref]
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUserRef;

    private RecyclerView mEditProfileRecycler;
    private ArrayList<Profile> mProfileDataArrayList = new ArrayList<>();
    private ArrayList<Profile> mAboutArrayList = new ArrayList<>();
    private ArrayList<Profile> mWorkArrayList = new ArrayList<>();
    private ArrayList<Profile> mHabitsArrayList = new ArrayList<>();
    private ArrayList<Social> mSocialArrayList = new ArrayList<>();
    private ArrayList<Variables> mVariablesArrayList = new ArrayList<>();


    private EditProfileAdapter mEditProfileAdapter;

    private Parcelable savedRecyclerLayoutState;
    private static final String BUNDLE_RECYCLER_LAYOUT = "recycler_layout";

    private Context mActivityContext;
    private Activity activity;

    private EditProfileViewModel mEditProfileViewModel;


    private static Boolean mIsSocialAdded ;
    private static Boolean mIsAboutAdded ;
    private static Boolean mIsWorkAdded ;
    private static Boolean mIsHabitsAdded ;

    private Uri mAvatarOriginalUri;
    private Uri mCoverOriginalUri;
    private Uri mAvatarUri;
    private Uri mCoverUri;

    private StorageReference mStorageRef;
    private StorageReference mImagesRef;

    private ArrayList<AlbumFile> mMediaFiles;

    private Long birthInMillis;
    private NavController navController ;
    //val data: String = CompleteProfileFragmentArgs.fromBundle(arguments).data // data = "Any data"
    // To write wave file on notifications folder
    private File mOutputFile;
    private FileOutputStream outStream;

    public CompleteProfileFragment() {
        // Required empty public constructor
    }

    // This method will only be called once when the retained
    // Fragment is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        //setRetainInstance(true);
        //show Menu
        setHasOptionsMenu(true);

        // [initialize the adapter on oCreate to create it only once, not every onCreateView when user get back to this fragment]
        mEditProfileAdapter = new EditProfileAdapter(mActivityContext
                , mProfileDataArrayList
                , mAboutArrayList
                , mWorkArrayList
                , mHabitsArrayList
                , mSocialArrayList
                , mVariablesArrayList
                ,this
                , this);

        //Get current logged in user
        mFirebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = mFirebaseCurrentUser!= null ? mFirebaseCurrentUser.getUid() : null;

        // Step 4: Set output file for notification audio
        mOutputFile = FilesHelper.getOutputMediaFile(FilesHelper.MEDIA_TYPE_Audio);

        mEditProfileViewModel = ViewModelProviders.of(this).get(EditProfileViewModel.class);

        /*currentUser = mEditProfileViewModel.getUserOnce(currentUserId);
        showCurrentUser(currentUser);*/

        Log.d(TAG,  "currentUserId " + currentUserId);

        // Get EditProfileViewModel.User from database if it's null
        if(mEditProfileViewModel.getUser() == null){
            mEditProfileViewModel.getUserOnce(currentUserId, new FirebaseUserCallback() {
                @Override
                public void onCallback(User user) {
                    if(user != null){
                        Log.d(TAG,  "FirebaseUserCallback onCallback. name= " + user.getName()+ " key= "+user.getKey());
                        mEditProfileViewModel.setUser(user);
                        //currentUser = mEditProfileViewModel.getUser();
                        showCurrentUser(mEditProfileViewModel.getUser());
                    }else{
                        Log.d(TAG,  "no user exist. user is null");
                        // We can't display fields unless there is a user object,
                        // lets create a fake user to use it's fields to get dynamic methods working
                        User tempUser = new User();
                        tempUser.setKey(currentUserId);
                        mEditProfileViewModel.setUser(tempUser);
                        showCurrentUser(mEditProfileViewModel.getUser());
                    }
                }
            });
        }else{
            Log.d(TAG,  "getUserOnce. user is not null. no need to get user from database "+mEditProfileViewModel.getUser().getName());
            //currentUser = mEditProfileViewModel.getUser();
            showCurrentUser(mEditProfileViewModel.getUser());
            //restoreLayoutManagerPosition();
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragView = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        // set to false to add expandable section header once every onCreateView
        mIsSocialAdded = false;
        mIsAboutAdded = false;
        mIsWorkAdded = false;
        mIsHabitsAdded = false;

        // Initiate the RecyclerView
        mEditProfileRecycler = (RecyclerView) fragView.findViewById(R.id.edit_recycler);
        mEditProfileRecycler.setHasFixedSize(true);

        // [START create_storage_reference]
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mImagesRef = mStorageRef.child("images");

        /*if (savedInstanceState != null) {
            currentUserId = savedInstanceState.getString("currentUserId");
            Log.d(TAG, "isSavedInstance ="+currentUserId);

            // Get savedInstanceState and do something with value if needed
            mProfileDataArrayList = savedInstanceState.getParcelableArrayList(PROFILE_LIST_STATE);
            mAboutArrayList = savedInstanceState.getParcelableArrayList(ABOUT_LIST_STATE);
            mWorkArrayList = savedInstanceState.getParcelableArrayList(WORK_LIST_STATE);
            mHabitsArrayList = savedInstanceState.getParcelableArrayList(HABITS_LIST_STATE);
            mVariablesArrayList = savedInstanceState.getParcelableArrayList(VARIABLES_LIST_STATE);
            mSocialArrayList = savedInstanceState.getParcelableArrayList(SOCIAL_LIST_STATE);

            savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            restorePreviousState(); // Restore data found in the Bundle
        }else{
            //Get current logged in user
            mFirebaseCurrentUser = FirebaseAuth.getInstance().getUser();
            currentUserId = mFirebaseCurrentUser!= null ? mFirebaseCurrentUser.getUid() : null;

            if(getArguments()!= null){
                currentUser = EditProfileFragmentArgs.fromBundle(getArguments()).getUser();//logged in user
                //currentUserId = EditProfileFragmentArgs.fromBundle(getArguments()).getCurrentUserId();//logged in user id
                Log.d(TAG,  "name= " + currentUser.getName() + "pickups=" + currentUser.getPickupCounter());
                showCurrentUser(currentUser); // No saved data, get data from remote
            }
        }*/


        mEditProfileRecycler.setLayoutManager(new LinearLayoutManager(mActivityContext));
        mEditProfileRecycler.setAdapter(mEditProfileAdapter);

       /* .observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if(user != null){
                    currentUser = user;
                    Log.d(TAG,  "mEditProfileViewModel getUser name= " + currentUser.getName() + "pickups=" + currentUser.getPickupCounter());
                    if(mProfileDataArrayList != null && mProfileDataArrayList.size()>0
                            && mAboutArrayList != null && mAboutArrayList.size()>0
                            && mWorkArrayList != null && mWorkArrayList.size()>0
                            && mHabitsArrayList != null && mHabitsArrayList.size()>0
                            && mSocialArrayList != null && mSocialArrayList.size()>0
                            && mVariablesArrayList != null && mVariablesArrayList.size()>0){
                        // Clear old Array data
                        Log.i(TAG,  "Clear old Array data");
                        mProfileDataArrayList.clear();
                        mAboutArrayList.clear();
                        mWorkArrayList.clear();
                        mHabitsArrayList.clear();
                        mSocialArrayList.clear();
                        mVariablesArrayList.clear();
                        showCurrentUser(currentUser);
                    }else{
                        showCurrentUser(currentUser);
                    }

                }
            }
        });*/

        // [START database reference]
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mUserRef = mDatabaseRef.child("users").child(currentUserId);

        navController = NavHostFragment.findNavController(this);

        return fragView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivityContext = context;

        if (context instanceof Activity){// check if fragmentContext is an activity
            activity =(Activity) context;
        }

        /*Album.initialize(AlbumConfig.newBuilder(activity)
                .setAlbumLoader(new MediaLoader())
                .build());*/
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        /*inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu,inflater);*/
        MenuItem saveItem = menu.add(Menu.NONE, 1, 1, menuIconWithText(getResources().getDrawable(R.drawable.ic_save_black_24dp), getResources().getString(R.string.menu_save)));
        //saveItem.setIcon( R.drawable.ic_save_black_24dp );
        saveItem.setShowAsAction( MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        //inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == MENU_ITEM_SAVE_ID) {
            Log.d(TAG, "MenuItem = SAVE");
            profileSave();
            writeToExternal(); // write wav notification sound if it's not already exists
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if((getActivity())!= null){
            ActionBar actionbar = ((MainActivity)getActivity()).getSupportActionBar();
            if (actionbar != null) {
                actionbar.setTitle(R.string.complete_profile_frag_title);
                // disable back button because we need to exit if back is clicked
                actionbar.setDisplayHomeAsUpEnabled(false);
                actionbar.setHomeButtonEnabled(false);
                actionbar.setDisplayShowCustomEnabled(false);
            }
        }
    }

    // Fires when a configuration change occurs and fragment needs to save state
    /*@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString ("currentUserId", currentUserId);
        outState.putParcelableArrayList(PROFILE_LIST_STATE, mProfileDataArrayList);
        outState.putParcelableArrayList(ABOUT_LIST_STATE, mAboutArrayList);
        outState.putParcelableArrayList(WORK_LIST_STATE, mWorkArrayList);
        outState.putParcelableArrayList(HABITS_LIST_STATE, mHabitsArrayList);
        outState.putParcelableArrayList(SOCIAL_LIST_STATE, mSocialArrayList);
        outState.putParcelableArrayList(VARIABLES_LIST_STATE, mVariablesArrayList);

        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, mEditProfileRecycler.getLayoutManager().onSaveInstanceState());
    }*/

    private void restorePreviousState() {
        mEditProfileAdapter = new EditProfileAdapter(mActivityContext
                , mProfileDataArrayList
                , mAboutArrayList
                , mWorkArrayList
                , mHabitsArrayList
                , mSocialArrayList
                , mVariablesArrayList
                ,this
                , this);

        Log.d(TAG, "mWorkArrayList college 0="+mWorkArrayList.get(0).getValue());
        Log.d(TAG, "mWorkArrayList college 1="+mWorkArrayList.get(1).getValue());
        Log.d(TAG, "mWorkArrayList college 2="+mWorkArrayList.get(2).getValue());

        mEditProfileRecycler.setLayoutManager(new LinearLayoutManager(mActivityContext));
        mEditProfileRecycler.setAdapter(mEditProfileAdapter);
        //restoreLayoutManagerPosition();
        mEditProfileAdapter.notifyDataSetChanged();
    }

    /*private void restoreLayoutManagerPosition() {
        if (savedRecyclerLayoutState != null) {
            mEditProfileRecycler.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }*/

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
            Collections.sort(mProfileDataArrayList, new SortBySection());
            Collections.sort(mAboutArrayList, new SortBySection());
            Collections.sort(mWorkArrayList, new SortBySection());
            Collections.sort(mHabitsArrayList, new SortBySection());
            Collections.sort(mSocialArrayList, new SortSocial());


            for (int i = 0; i < mProfileDataArrayList.size(); i++) {
                Log.i(TAG, "mProfileDataArrayList sorted" + mProfileDataArrayList.get(i).getKey());
            }

            // [set settings variable for collapse and progress icon]
            for (int i = 0; i < 5; i++) {
                mVariablesArrayList.add(new Variables(false));
            }

            //mEditProfileAdapter.notifyDataSetChanged();
            //mAboutProfileAdapter.notifyDataSetChanged();
                            /*String userName = dataSnapshot.child("name").getValue().toString();
                            String currentUser = dataSnapshot.getKey();*/
            Log.d(TAG, "user exist: Name=" + currentUser.getName());

            //restoreLayoutManagerPosition();
            mEditProfileAdapter.notifyDataSetChanged();
        }

    }


    private void getDynamicMethod(String fieldName, User currentUser) {

        Method[] methods = currentUser.getClass().getMethods();

        for (Method method : methods) {
            if ((method.getName().startsWith("get")) && (method.getName().length() == (fieldName.length() + 3))) {
                if (method.getName().toLowerCase().endsWith(fieldName.toLowerCase())) {
                    // Method found, run it
                    try {

                        String value;
                        // check if method is null
                        if (method.invoke(currentUser) != null){
                            value = String.valueOf(method.invoke(currentUser));
                            Log.d(TAG, "DynamicMethod value= " +method.getName()+" = "+ value);
                            Log.d(TAG, "DynamicMethod ReturnType= " +method.getReturnType().getSimpleName());
                        }else{
                            value = null;
                        }

                        Log.d(TAG, "Method=" +method.getName()+" = "+ value);
                        Log.d(TAG, "Method Type=" + method.getGenericReturnType());

                        if(fieldName.equals("avatar")){
                            mProfileDataArrayList.add(new Profile(fieldName, value,SECTION_AVATAR, SECTION_AVATAR));
                        }

                        if(fieldName.equals("coverImage")){
                            mProfileDataArrayList.add(new Profile(fieldName, value,SECTION_COVER, SECTION_COVER));
                        }

                        if(fieldName.equals("name")){
                            mProfileDataArrayList.add(new Profile(fieldName, value,SECTION_EDIT_TEXT, SECTION_EDIT_TEXT));
                        }
                        if(fieldName.equals("biography")){
                            mProfileDataArrayList.add(new Profile(fieldName, value,SECTION_EDIT_TEXT+1, SECTION_EDIT_TEXT));
                        }
                        if(fieldName.equals("birthDate")){
                            mProfileDataArrayList.add(new Profile(fieldName, value,SECTION_TEXT, SECTION_TEXT));
                        }

                        if((fieldName.equals("gender")
                                || fieldName.equals("interestedIn")
                                || fieldName.equals("relationship")
                                || fieldName.equals("horoscope"))){

                            if(fieldName.equals("gender")){
                                mProfileDataArrayList.add(new Profile(fieldName, value,SECTION_SPINNER+1 , SECTION_SPINNER));
                            }
                            if(fieldName.equals("interestedIn")){
                                mProfileDataArrayList.add(new Profile(fieldName, value,SECTION_SPINNER+2, SECTION_SPINNER));
                            }
                            if(fieldName.equals("relationship")){
                                mProfileDataArrayList.add(new Profile(fieldName, value,SECTION_SPINNER+3, SECTION_SPINNER));
                            }
                            if(fieldName.equals("horoscope")){
                                mProfileDataArrayList.add(new Profile(fieldName, value,SECTION_SPINNER+4 , SECTION_SPINNER));
                            }
                        }

                        if((fieldName.equals("phone")
                                || fieldName.equals("facebook")
                                || fieldName.equals("instagram")
                                || fieldName.equals("twitter")
                                || fieldName.equals("snapchat")
                                || fieldName.equals("tumblr")
                                || fieldName.equals("pubg")
                                || fieldName.equals("vk")
                                || fieldName.equals("askfm")
                                || fieldName.equals("curiouscat")
                                || fieldName.equals("saraha")
                                || fieldName.equals("pinterest")
                                || fieldName.equals("soundcloud")
                                || fieldName.equals("spotify")
                                || fieldName.equals("anghami")
                                || fieldName.equals("twitch")
                                || fieldName.equals("youtube")
                                || fieldName.equals("linkedIn")
                                || fieldName.equals("wikipedia")
                                || fieldName.equals("website"))){
                            //add data for social recycler
                            /*Map<String, SocialObj> socialMap = new HashMap<>();
                            SocialObj social = new SocialObj();
                            social.setPublic(true);
                            social.setUrl(value);

                            socialMap.put("url",social );
                            SocialObj socialObj = new SocialObj();
                            //socialObj.setPublic(true);
                            //socialObj.setUrl("test test");*/

                            /*Object social;
                            if (method.invoke(currentUser) != null){
                                social = method.invoke(currentUser);
                            }else{
                                social = null;
                            }*/

                            if(fieldName.equals("phone")){
                                mSocialArrayList.add(new Social(fieldName, currentUser.getPhone(), SECTION_SOCIAL+1 ,SECTION_SOCIAL));
                            }
                            if(fieldName.equals("facebook")){
                                mSocialArrayList.add(new Social(fieldName, currentUser.getFacebook(), SECTION_SOCIAL+2 ,SECTION_SOCIAL));
                            }
                            if(fieldName.equals("instagram")){
                                mSocialArrayList.add(new Social(fieldName,currentUser.getInstagram(), SECTION_SOCIAL+3 ,SECTION_SOCIAL));
                            }
                            if(fieldName.equals("twitter")){
                                mSocialArrayList.add(new Social(fieldName, currentUser.getTwitter(), SECTION_SOCIAL+4 ,SECTION_SOCIAL));
                            }
                            if(fieldName.equals("snapchat")){
                                mSocialArrayList.add(new Social(fieldName, currentUser.getSnapchat(), SECTION_SOCIAL+5 ,SECTION_SOCIAL));
                            }
                            if(fieldName.equals("tumblr")){
                                mSocialArrayList.add(new Social(fieldName,currentUser.getTumblr(), SECTION_SOCIAL+6 ,SECTION_SOCIAL));
                            }
                            if(fieldName.equals("pubg")){
                                mSocialArrayList.add(new Social(fieldName, currentUser.getPubg(), SECTION_SOCIAL+7 ,SECTION_SOCIAL));
                            }
                            if(fieldName.equals("vk")){
                                mSocialArrayList.add(new Social(fieldName, currentUser.getVk(), SECTION_SOCIAL+8 ,SECTION_SOCIAL));
                            }
                            if(fieldName.equals("askfm")){
                                mSocialArrayList.add(new Social(fieldName, currentUser.getAskfm(), SECTION_SOCIAL+9 ,SECTION_SOCIAL));
                            }
                            if(fieldName.equals("curiouscat")){
                                mSocialArrayList.add(new Social(fieldName, currentUser.getCuriouscat(), SECTION_SOCIAL+10 ,SECTION_SOCIAL));
                            }
                            if(fieldName.equals("saraha")){
                                mSocialArrayList.add(new Social(fieldName, currentUser.getSaraha(), SECTION_SOCIAL+11 ,SECTION_SOCIAL));
                            }
                            if(fieldName.equals("pinterest")){
                                mSocialArrayList.add(new Social(fieldName, currentUser.getPinterest(), SECTION_SOCIAL+12 ,SECTION_SOCIAL));
                            }
                            if(fieldName.equals("soundcloud")){
                                mSocialArrayList.add(new Social(fieldName, currentUser.getSoundcloud(), SECTION_SOCIAL+13 ,SECTION_SOCIAL));
                            }
                            if(fieldName.equals("spotify")){
                                mSocialArrayList.add(new Social(fieldName, currentUser.getSpotify(), SECTION_SOCIAL+14 ,SECTION_SOCIAL));
                            }
                            if(fieldName.equals("anghami")){
                                mSocialArrayList.add(new Social(fieldName, currentUser.getAnghami(), SECTION_SOCIAL+15 ,SECTION_SOCIAL));
                            }
                            if(fieldName.equals("twitch")){
                                mSocialArrayList.add(new Social(fieldName, currentUser.getTwitch(), SECTION_SOCIAL+16 ,SECTION_SOCIAL));
                            }
                            if(fieldName.equals("youtube")){
                                mSocialArrayList.add(new Social(fieldName, currentUser.getYoutube(), SECTION_SOCIAL+17 ,SECTION_SOCIAL));
                            }
                            if(fieldName.equals("linkedIn")){
                                mSocialArrayList.add(new Social(fieldName, currentUser.getLinkedIn(), SECTION_SOCIAL+18 ,SECTION_SOCIAL));
                            }
                            if(fieldName.equals("wikipedia")){
                                mSocialArrayList.add(new Social(fieldName, currentUser.getWikipedia(), SECTION_SOCIAL+19 ,SECTION_SOCIAL));
                            }
                            if(fieldName.equals("website")){
                                mSocialArrayList.add(new Social(fieldName, currentUser.getWebsite(), SECTION_SOCIAL+20 ,SECTION_SOCIAL));
                            }

                            if(!mIsSocialAdded){
                                Log.d(TAG, "mIsSocialAdded is false");
                                Profile socialData = new Profile(SECTION_SOCIAL_HEADLINE, "",SECTION_SOCIAL, SECTION_SOCIAL);
                                mProfileDataArrayList.add(socialData);
                            }
                            mIsSocialAdded = true; // to add only one expandable section header

                        }

                        else if(fieldName.equals("work")
                                || fieldName.equals("college")
                                || fieldName.equals("school")){

                            if(fieldName.equals("work")){
                                mWorkArrayList.add(new Profile(fieldName, value,SECTION_WORK+1, SECTION_WORK));
                            }
                            if(fieldName.equals("college")){
                                mWorkArrayList.add(new Profile(fieldName, value,SECTION_WORK+2, SECTION_WORK));
                            }
                            if(fieldName.equals("school")){
                                mWorkArrayList.add(new Profile(fieldName, value,SECTION_WORK+3, SECTION_WORK));
                            }

                            if(!mIsWorkAdded){
                                Log.d(TAG, "mIsAboutAdded is false");
                                Profile aboutSectionData = new Profile(SECTION_WORK_HEADLINE, "",SECTION_WORK, SECTION_WORK);
                                mProfileDataArrayList.add(aboutSectionData);
                            }
                            mIsWorkAdded = true; // to add only one expandable section header

                        }

                        if(fieldName.equals("lives")
                                || fieldName.equals("hometown")
                                || fieldName.equals("nationality")
                                || fieldName.equals("politics")
                                || fieldName.equals("religion")){

                            //add data for about section
                            if(fieldName.equals("lives")){
                                mAboutArrayList.add(new Profile(fieldName, value,SECTION_ABOUT+ 1, SECTION_ABOUT));
                            }
                            if(fieldName.equals("hometown")){
                                mAboutArrayList.add(new Profile(fieldName, value,SECTION_ABOUT+ 2, SECTION_ABOUT));
                            }
                            if(fieldName.equals("nationality")){
                                mAboutArrayList.add(new Profile(fieldName, value,SECTION_ABOUT+ 3, SECTION_ABOUT));
                            }
                            if(fieldName.equals("politics")){
                                mAboutArrayList.add(new Profile(fieldName, value,SECTION_ABOUT+4, SECTION_ABOUT));
                            }
                            if(fieldName.equals("religion")){
                                mAboutArrayList.add(new Profile(fieldName, value,SECTION_ABOUT+5, SECTION_ABOUT));
                            }

                            if(!mIsAboutAdded){
                                Log.d(TAG, "mIsAboutAdded is false");
                                Profile aboutSectionData = new Profile(SECTION_ABOUT_HEADLINE, "",SECTION_ABOUT, SECTION_ABOUT);
                                mProfileDataArrayList.add(aboutSectionData);
                            }
                            mIsAboutAdded = true; // to add only one expandable section header

                        }else if(fieldName.equals("smoke")
                                || fieldName.equals("shisha")
                                || fieldName.equals("drugs")
                                || fieldName.equals("drink")
                                || fieldName.equals("gamer")
                                || fieldName.equals("cook")
                                || fieldName.equals("read")
                                || fieldName.equals("athlete")
                                || fieldName.equals("travel")){
                            //add data for habits section
                            if(fieldName.equals("smoke")){
                                mHabitsArrayList.add(new Profile(fieldName, value,SECTION_HABITS+1, SECTION_HABITS));
                            }
                            if(fieldName.equals("shisha")){
                                mHabitsArrayList.add(new Profile(fieldName, value,SECTION_HABITS+2, SECTION_HABITS));
                            }
                            if(fieldName.equals("drugs")){
                                mHabitsArrayList.add(new Profile(fieldName, value,SECTION_HABITS+3, SECTION_HABITS));
                            }
                            if(fieldName.equals("drink")){
                                mHabitsArrayList.add(new Profile(fieldName, value,SECTION_HABITS+4, SECTION_HABITS));
                            }
                            if(fieldName.equals("gamer")){
                                mHabitsArrayList.add(new Profile(fieldName, value,SECTION_HABITS+5, SECTION_HABITS));
                            }
                            if(fieldName.equals("cook")){
                                mHabitsArrayList.add(new Profile(fieldName, value,SECTION_HABITS+6, SECTION_HABITS));
                            }
                            if(fieldName.equals("read")){
                                mHabitsArrayList.add(new Profile(fieldName, value,SECTION_HABITS+7, SECTION_HABITS));
                            }
                            if(fieldName.equals("athlete")){
                                mHabitsArrayList.add(new Profile(fieldName, value,SECTION_HABITS+8, SECTION_HABITS));
                            }
                            if(fieldName.equals("travel")){
                                mHabitsArrayList.add(new Profile(fieldName, value,SECTION_HABITS+9, SECTION_HABITS));
                            }

                            if(!mIsHabitsAdded){
                                Log.d(TAG, "mIsAboutAdded is false");
                                Profile habitsSectionData = new Profile(SECTION_HABITS_HEADLINE, "",SECTION_HABITS, SECTION_HABITS);
                                mProfileDataArrayList.add(habitsSectionData);
                            }
                            mIsHabitsAdded = true; // to add only one expandable section header
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "requestCode ="+ requestCode);
        if (data != null) {
            int position = 0;
            if(null != data.getExtras()){
                position = data.getExtras().getInt(IMAGE_HOLDER_POSITION,0);
            }
            switch (requestCode){
                case CROP_IMAGE_AVATAR_REQUEST_CODE:
                    Log.d(TAG, "AVATAR_CROP_PICTURE requestCode= "+ requestCode);
                    Log.d(TAG, "AVATAR_CROP_PICTURE position= "+ position);
                    CropImage.ActivityResult avatarResult = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        mAvatarOriginalUri = avatarResult.getOriginalUri();
                        mAvatarUri = avatarResult.getUri();
                        compressImage(mAvatarOriginalUri,"original avatar" ,position);
                        uploadImage(mAvatarUri, "avatar", position);
                        Log.d(TAG, "mAvatarOriginalUri = "+ mAvatarOriginalUri);
                        Log.d(TAG, "mAvatarUri = "+ mAvatarUri);

                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Exception error = avatarResult.getError();
                        Toast.makeText(mActivityContext, error.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                    break;
                case CROP_IMAGE_COVER_REQUEST_CODE:
                    Log.d(TAG, "COVER CROP_PICTURE requestCode= "+ requestCode);
                    Log.d(TAG, "COVER CROP_PICTURE position= "+ position);
                    CropImage.ActivityResult coverResult = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        mCoverOriginalUri = coverResult.getOriginalUri();
                        mCoverUri = coverResult.getUri();
                        Log.d(TAG, "mCoverOriginalUri = "+ mCoverOriginalUri);
                        Log.d(TAG, "mCoverUri = "+ mCoverUri);
                        //uploadImage(mCoverUri, "coverImage", position);
                        compressImage(mCoverOriginalUri,"original cover" ,position);
                        uploadImage(mCoverUri, "coverImage", position);
                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Exception error = coverResult.getError();
                        Toast.makeText(mActivityContext, error.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }

    }

    private void compressImage(final Uri imageUri, final String type, final int position) {
        if (null != imageUri && null != imageUri.getPath()) {
            File imageFile = new File(imageUri.getPath());
            Luban.get(getContext())
                    .load(imageFile)                     // pass image to be compressed
                    .putGear(Luban.THIRD_GEAR)      // set compression level, defaults to 3
                    .setCompressListener(new OnCompressListener() { // Set up return

                        @Override
                        public void onStart() {
                            //Called when compression starts, display loading UI here
                            Log.d(TAG, "compress :onStart= ");
                        }
                        @Override
                        public void onSuccess(File file) {
                            //Called when compression finishes successfully, provides compressed image
                            Log.d(TAG, "compress :onSuccess= "+file.getPath());
                            Uri compressImageUri = Uri.fromFile(file);
                            uploadImage(compressImageUri, type, position);
                        }

                        @Override
                        public void onError(Throwable e) {
                            //Called if an error has been encountered while compressing
                            Log.d(TAG, "compress :onError= "+e);
                            uploadImage(imageUri, type, position);

                        }
                    }).launch();    // Start compression
        }
    }

    private void uploadImage(Uri imageUri, final String type, final int position) {
        //Uri fileUri = Uri.fromFile(new File(imageUri.getPath()));
        StorageReference userRef ; //= mStorageRef.child("images/"+currentUserId+"avatar.jpg");

        switch (type){
            case "avatar":
                userRef = mStorageRef.child("images/"+currentUserId +"/"+ AVATAR_THUMBNAIL_NAME);
                break;
            case "coverImage":
                userRef = mStorageRef.child("images/"+currentUserId +"/"+ COVER_THUMBNAIL_NAME );
                break;
            case "original avatar":
                userRef = mStorageRef.child("images/"+currentUserId +"/"+ AVATAR_ORIGINAL_NAME);
                break;
            case "original cover":
                userRef = mStorageRef.child("images/"+currentUserId +"/"+ COVER_ORIGINAL_NAME);
                break;
            default:
                userRef = mStorageRef.child("images/"+currentUserId+AVATAR_THUMBNAIL_NAME);
                break;
        }

        // Create the file metadata
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build();

        // Upload file and metadata to the path 'images/mountains.jpg'
        UploadTask uploadTask = userRef.putFile(imageUri, metadata);

        if(type.equals("avatar") || type.equals("coverImage")){
            // Listen for state changes, errors, and completion of the upload.
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    System.out.println("Upload is " + progress + "% done");
                    mVariablesArrayList.set(position, new Variables(true));
                    mEditProfileAdapter.notifyItemChanged(position);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Handle successful uploads on complete
                    Log.d(TAG, "uploads :onSuccess");
                    //mVariablesArrayList.get(0).setProgressIsVisible(false);
                }
            });// [END of Listen for state changes, errors, and completion of the upload]

            // [get DownloadUrl]
            final StorageReference finalUserRef = userRef;
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        if(null != task.getException()){
                            throw task.getException();
                        }
                    }
                    // Continue with the task to get the download URL
                    return finalUserRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();

                        // set EditProfileViewModel.user values
                        switch (type){
                            case "avatar":
                                mProfileDataArrayList.set(position,new Profile(type, String.valueOf(downloadUri),SECTION_AVATAR, SECTION_AVATAR));
                                mEditProfileViewModel.getUser().setAvatar(String.valueOf(downloadUri));
                                break;
                            case "coverImage":
                                mProfileDataArrayList.set(position,new Profile(type, String.valueOf(downloadUri),SECTION_COVER, SECTION_COVER));
                                mEditProfileViewModel.getUser().setCoverImage(String.valueOf(downloadUri));
                                break;
                        }
                        //mVariablesArrayList.get(position).setValue(false);
                        mVariablesArrayList.set(position, new Variables(false));
                        mEditProfileAdapter.notifyItemChanged(position);

                    } else {
                        // Handle failures
                        Log.d(TAG, "uploads :FailureL");
                        Toast.makeText(activity, R.string.upload_image_error,
                                Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
    }

    private void selectMedia(final boolean isAvatar, final int position) {
        Album.image(this) // Image and video mix options.
                .singleChoice() // Multi-Mode, Single-Mode: singleChoice().
                .requestCode(200) // The request code will be returned in the listener.
                .columnCount(2) // The number of columns in the page list.
                //.selectCount(1)  // Choose up to a few images.
                .camera(true) // Whether the camera appears in the Item.
                .onResult(new Action<ArrayList<AlbumFile>>() {
                    @Override
                    public void onAction(int requestCode, @NonNull ArrayList<AlbumFile> result) {
                        // accept the result.
                        mMediaFiles = result;
                        AlbumFile albumFile = mMediaFiles.get(0);
                        Uri MediaUri = Uri.parse(albumFile.getPath()) ;

                        Log.d(TAG, "MediaType" +albumFile.getMediaType());
                        Log.d(TAG, "MediaUri" +MediaUri);

                        cropImage(MediaUri, isAvatar, position);
                    }
                })
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(int requestCode, @NonNull String result) {
                        // The user canceled the operation.
                    }
                })
                .start();

    }



    private void cropImage(Uri mediaUri, boolean isAvatar, int position) {
        if(isAvatar){
            Intent intent = CropImage.activity(Uri.fromFile(new File(mediaUri.toString())))
                    //.setGuidelines(CropImageView.Guidelines.ON)
                    .setAllowRotation(true)
                    .setAutoZoomEnabled(true)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setActivityTitle(getString(R.string.crop_activity_title))
                    .setCropMenuCropButtonTitle(getString(R.string.upload_button))
                    //.setAspectRatio(1,1)
                    .setFixAspectRatio(true)
                    //.setMaxCropResultSize(600, 600)
                    .setMinCropResultSize(300,300)
                    .setRequestedSize(300,300)//resize
                    .getIntent(mActivityContext);
            //.start(mActivityContext, this);
            //intent.putExtra(IMAGE_HOLDER_POSITION,position);
            Bundle mBundle = new Bundle();
            mBundle.putInt(IMAGE_HOLDER_POSITION,position);
            intent.putExtras(mBundle);

            this.startActivityForResult(intent, CROP_IMAGE_AVATAR_REQUEST_CODE );

        }else{

            Intent intent = CropImage.activity(Uri.fromFile(new File(mediaUri.toString())))
                    //.setGuidelines(CropImageView.Guidelines.ON)
                    .setAllowRotation(true)
                    .setAutoZoomEnabled(true)
                    .setActivityTitle(getString(R.string.crop_activity_title))
                    .setCropMenuCropButtonTitle(getString(R.string.upload_button))
                    .setAspectRatio(2,1)
                    //.setMaxCropResultSize(600, 600)
                    .setMinCropResultSize(300,300)
                    .setRequestedSize(600,300) //resize
                    .getIntent(mActivityContext);
            //.start(mActivityContext, this);
            //intent.putExtra(IMAGE_HOLDER_POSITION,position);
            Bundle mBundle = new Bundle();
            mBundle.putInt(IMAGE_HOLDER_POSITION,position);
            intent.putExtras(mBundle);
            this.startActivityForResult(intent, CROP_IMAGE_COVER_REQUEST_CODE);
        }

        Log.d(TAG, "cropImage starts" +mediaUri);
    }

    private void profileSave() {

        //Log.d(TAG, "currentUser getCreatedLong= "+currentUser.getCreatedLong());
        /* //apparently it's updated automatically
        if( currentUser != null && currentUser.getSent() == null){
            Log.d(TAG, "profileSave getSent= "+currentUser.getSent());
            currentUser.setSent(ServerValue.TIMESTAMP);
        }*/

        /*for (int i = 0; i < mProfileDataArrayList.size(); i++) {
            Log.d(TAG, "ProfileDataArrayList Key= "+mProfileDataArrayList.get(i).getKey()+ " ProfileDataArrayList Value= "+ mSocialArrayList.get(i).getValue());
            switch (mProfileDataArrayList.get(i).getKey()){
                case "avatar":
                    if(null != mProfileDataArrayList.get(i).getValue()){
                        currentUser.setAvatar(mProfileDataArrayList.get(i).getValue());
                    }
                    break;
                case "coverImage":
                    if(null != mProfileDataArrayList.get(i).getValue()){
                        currentUser.setCoverImage(mProfileDataArrayList.get(i).getValue());
                    }
                    break;
                case "name":
                    Log.d(TAG, "save profile set name" +mProfileDataArrayList.get(i).getValue());
                    currentUser.setName(mProfileDataArrayList.get(i).getValue());
                    break;
                case "biography":
                    currentUser.setBiography(mProfileDataArrayList.get(i).getValue());
                    break;
                case "birthDate":
                    if(birthInMillis != null){
                        currentUser.setBirthDate(birthInMillis);
                    }
                    break;
                case "relationship":
                    currentUser.setRelationship(mProfileDataArrayList.get(i).getValue());
                    break;
                case "gender":
                    currentUser.setGender(mProfileDataArrayList.get(i).getValue());
                    break;
                case "horoscope":
                    currentUser.setHoroscope(mProfileDataArrayList.get(i).getValue());
                    break;
                case "interestedIn":
                    currentUser.setInterestedIn(mProfileDataArrayList.get(i).getValue());
                    break;
            }
        }// end of mProfileDataArrayList loop

        for (int i = 0; i < mWorkArrayList.size(); i++) {
            Log.d(TAG, "ProfileDataArrayList getKey= "+mWorkArrayList.get(i).getKey()+ " ProfileDataArrayList Value= "+ mSocialArrayList.get(i).getValue());
            switch (mWorkArrayList.get(i).getKey()){
                case "work":
                    currentUser.setWork(mWorkArrayList.get(i).getValue());
                    break;
                case "college":
                    currentUser.setCollege(mWorkArrayList.get(i).getValue());
                    break;
                case "school":
                    currentUser.setSchool(mWorkArrayList.get(i).getValue());
                    break;
            }
        }// end of mWorkArrayList loop

        for (int i = 0; i < mAboutArrayList.size(); i++) {
            Log.d(TAG, "ProfileDataArrayList getKey= "+mAboutArrayList.get(i).getKey()+ " ProfileDataArrayList Value= "+ mSocialArrayList.get(i).getValue());
            switch (mAboutArrayList.get(i).getKey()){
                case "nationality":
                    currentUser.setNationality(mAboutArrayList.get(i).getValue());
                    break;
                case "hometown":
                    currentUser.setHometown(mAboutArrayList.get(i).getValue());
                    break;
                case "lives":
                    currentUser.setLives(mAboutArrayList.get(i).getValue());
                    break;
                case "politics":
                    currentUser.setPolitics(mAboutArrayList.get(i).getValue());
                    break;
                case "religion":
                    currentUser.setReligion(mAboutArrayList.get(i).getValue());
                    break;
            }
        }// end of mAboutArrayList loop

        for (int i = 0; i < mHabitsArrayList.size(); i++) {
            Log.d(TAG, "ProfileDataArrayList getKey= "+mHabitsArrayList.get(i).getKey()+ " ProfileDataArrayList Value= "+ mSocialArrayList.get(i).getValue());
            switch (mHabitsArrayList.get(i).getKey()){
                case "athlete":
                    if(null!= mHabitsArrayList.get((i)).getValue() && mHabitsArrayList.get((i)).getValue().equals("true")){
                        currentUser.setAthlete(true);
                    }else if(null!= mHabitsArrayList.get((i)).getValue() && mHabitsArrayList.get((i)).getValue().equals("false")){
                        currentUser.setAthlete(false);
                    }else{
                        currentUser.setAthlete(null);
                    }
                    break;
                case "smoke":
                    if(null!= mHabitsArrayList.get((i)).getValue() && mHabitsArrayList.get((i)).getValue().equals("true")){
                        currentUser.setSmoke(true);
                    }else if(null!= mHabitsArrayList.get((i)).getValue() && mHabitsArrayList.get((i)).getValue().equals("false")){
                        currentUser.setSmoke(false);
                    }else{
                        currentUser.setSmoke(null);
                    }
                    break;
                case "travel":
                    if(null!= mHabitsArrayList.get((i)).getValue() && mHabitsArrayList.get((i)).getValue().equals("true")){
                        currentUser.setTravel(true);
                    }else if(null!= mHabitsArrayList.get((i)).getValue() && mHabitsArrayList.get((i)).getValue().equals("false")){
                        currentUser.setTravel(false);
                    }else{
                        currentUser.setTravel(null);
                    }
                    break;
                case "shisha":
                    if(null!= mHabitsArrayList.get((i)).getValue() && mHabitsArrayList.get((i)).getValue().equals("true")){
                        currentUser.setShisha(true);
                    }else if(null!= mHabitsArrayList.get((i)).getValue() && mHabitsArrayList.get((i)).getValue().equals("false")){
                        currentUser.setShisha(false);
                    }else{
                        currentUser.setShisha(null);
                    }
                    break;
                case "cook":
                    if(null!= mHabitsArrayList.get((i)).getValue() && mHabitsArrayList.get((i)).getValue().equals("true")){
                        currentUser.setCook(true);
                    }else if(null!= mHabitsArrayList.get((i)).getValue() && mHabitsArrayList.get((i)).getValue().equals("false")){
                        currentUser.setCook(false);
                    }else{
                        currentUser.setCook(null);
                    }
                    break;
                case "drink":
                    if(null!= mHabitsArrayList.get((i)).getValue() && mHabitsArrayList.get((i)).getValue().equals("true")){
                        currentUser.setDrink(true);
                    }else if(null!= mHabitsArrayList.get((i)).getValue() && mHabitsArrayList.get((i)).getValue().equals("false")){
                        currentUser.setDrink(false);
                    }else{
                        currentUser.setDrink(null);
                    }
                    break;
                case "drugs":
                    if(null!= mHabitsArrayList.get((i)).getValue() && mHabitsArrayList.get((i)).getValue().equals("true")){
                        currentUser.setDrugs(true);
                    }else if(null!= mHabitsArrayList.get((i)).getValue() && mHabitsArrayList.get((i)).getValue().equals("false")){
                        currentUser.setDrugs(false);
                    }else{
                        currentUser.setDrugs(null);
                    }
                case "gamer":
                    if(null!= mHabitsArrayList.get((i)).getValue() && mHabitsArrayList.get((i)).getValue().equals("true")){
                        currentUser.setGamer(true);
                    }else if(null!= mHabitsArrayList.get((i)).getValue() && mHabitsArrayList.get((i)).getValue().equals("false")){
                        currentUser.setGamer(false);
                    }else{
                        currentUser.setGamer(null);
                    }
                    break;
                case "read":
                    if(null!= mHabitsArrayList.get((i)).getValue() && mHabitsArrayList.get((i)).getValue().equals("true")){
                        currentUser.setRead(true);
                    }else if(null!= mHabitsArrayList.get((i)).getValue() && mHabitsArrayList.get((i)).getValue().equals("false")){
                        currentUser.setRead(false);
                    }else{
                        currentUser.setRead(null);
                    }
                    break;
            }
        }// end of mHabitsArrayList loop

        for (int i = 0; i < mSocialArrayList.size(); i++) {
            Log.d(TAG, "ProfileDataArrayList getKey= "+mSocialArrayList.get(i).getKey()+ " ProfileDataArrayList Value= "+ mSocialArrayList.get(i).getValue());

            switch (mSocialArrayList.get(i).getKey()){
                case "phone":
                    currentUser.setPhone(mSocialArrayList.get(i).getValue());
                    break;
                case "facebook":
                    currentUser.setFacebook(mSocialArrayList.get(i).getValue());
                    break;
                case "instagram":
                    currentUser.setInstagram(mSocialArrayList.get(i).getValue());
                    break;
                case "twitter":
                    currentUser.setTwitter(mSocialArrayList.get(i).getValue());
                    break;
                case "snapchat":
                    currentUser.setSnapchat(mSocialArrayList.get(i).getValue());
                    break;
                case "tumblr":
                    currentUser.setTumblr(mSocialArrayList.get(i).getValue());
                    break;
                case "pubg":
                    currentUser.setPubg(mSocialArrayList.get(i).getValue());
                    break;
                case "vk":
                    currentUser.setVk(mSocialArrayList.get(i).getValue());
                    break;
                case "askfm":
                    currentUser.setAskfm(mSocialArrayList.get(i).getValue());
                    break;
                case "curiouscat":
                    currentUser.setCuriouscat(mSocialArrayList.get(i).getValue());
                    break;
                case "saraha":
                    currentUser.setSaraha(mSocialArrayList.get(i).getValue());
                    break;
                case "pinterest":
                    currentUser.setPinterest(mSocialArrayList.get(i).getValue());
                    break;
                case "soundcloud":
                    currentUser.setSoundcloud(mSocialArrayList.get(i).getValue());
                    break;
                case "spotify":
                    currentUser.setSpotify(mSocialArrayList.get(i).getValue());
                    break;
                case "anghami":
                    currentUser.setAnghami(mSocialArrayList.get(i).getValue());
                    break;
                case "twitch":
                    currentUser.setTwitch(mSocialArrayList.get(i).getValue());
                    break;
                case "youtube":
                    currentUser.setYoutube(mSocialArrayList.get(i).getValue());
                    break;
                case "linkedIn":
                    currentUser.setLinkedIn(mSocialArrayList.get(i).getValue());
                    break;
                case "wikipedia":
                    currentUser.setWikipedia(mSocialArrayList.get(i).getValue());
                    break;
                case "website":
                    currentUser.setWebsite(mSocialArrayList.get(i).getValue());
                    break;
            }
        }// end of mSocialArrayList loop*/

        // no need to loop through array lists, get values from mEditProfileViewModel.user
        Log.d(TAG, "getUser token= " +mEditProfileViewModel.getUser().getTokens().size());
        if(TextUtils.isEmpty(mEditProfileViewModel.getUser().getName())){
            Toast.makeText(getActivity(), R.string.empty_profile_name_error,
                    Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(mEditProfileViewModel.getUser().getAvatar())){
            Toast.makeText(getActivity(), R.string.empty_profile_avatar_error,
                    Toast.LENGTH_LONG).show();
            return;
        }

        mEditProfileViewModel.getUser().setCreated(ServerValue.TIMESTAMP);
        mUserRef.setValue(mEditProfileViewModel.getUser()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Write was successful!
                Log.i(TAG, "mUserRef onSuccess");
                // Return to main fragment
                if (null != navController.getCurrentDestination() && R.id.mainFragment != navController.getCurrentDestination().getId()) {
                    /*if(navController != null){
                        //navController.navigate(R.id.mainFragment);
                        // Go up to main fragment
                        navController = Navigation.findNavController(activity, R.id.host_fragment);
                        navController.navigateUp();
                    }*/
                    navController.navigateUp();
                }


            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Toast.makeText(getActivity(), R.string.update_profile_error,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    // If WRITE_EXTERNAL_STORAGE is granted, start writing wav file immediately, if not, ask for permission first
    private boolean isPermissionGranted() {
        Log.d(TAG, "is permission Granted= "+(ContextCompat.checkSelfPermission(mActivityContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED));
        return ContextCompat.checkSelfPermission(mActivityContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        //return true;
    }

    // write notification sound to notification folder
    private void writeToExternal(){

        // return if we don't have permission or we couldn't create output file or the wav file already exists
        if (!isPermissionGranted() || mOutputFile == null || mOutputFile.exists()) {
            Log.i(TAG, "writeToExternal return");
            return;
        }

        Log.i(TAG, "writeToExternal starts");

        InputStream in = getResources().openRawResource(R.raw.basbes);
        try {
            outStream = new FileOutputStream(mOutputFile.getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte[] buff = new byte[1024];
        int read;

        try {
            while ((read = in.read(buff)) > 0) {
                if (outStream != null) {
                    Log.i(TAG, "mediaStorageDir not null. outStream = "+ outStream);
                    outStream.write(buff, 0, read);
                }else{
                    Log.i(TAG, "mediaStorageDir is null");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Tell MediaScanner about the new file. Wait for it to assign a {@link Uri}.
        final String mimeType = mActivityContext.getContentResolver().getType(Uri.fromFile(mOutputFile));
        MediaScannerConnection.scanFile(
                mActivityContext,
                new String[]{mOutputFile.getAbsolutePath()},
                new String[]{mimeType},
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.v(TAG, "file " + path + " was scanned successfully: " + uri);
                    }
                });

    }

    // user interface to detect item click on the recycler adapter
    @Override
    public void onClick(View view, int position, boolean isLongClick) {
        Log.d(TAG, "item clicked fragment= " + position);
        switch (mProfileDataArrayList.get(position).getKey()) {
            case "avatar":
                Log.d(TAG, "avatar item clicked= " + position);
                selectMedia(true, position);
                break;
            case "coverImage":
                Log.d(TAG, "coverImage item clicked= " + position);
                selectMedia(false, position);
                break;
            case "birthDate":
                //mEditProfileAdapter.notifyDataSetChanged();
                DatePickerFragment datePicker;
                if(null != mEditProfileViewModel.getUser().getBirthDate()){
                    datePicker = DatePickerFragment.newInstance(mActivityContext, mEditProfileViewModel.getUser().getBirthDate());
                }else{
                    datePicker = new DatePickerFragment(mActivityContext);
                }
                if (getFragmentManager() != null) {
                    datePicker.setCallBack(ondate); //Set Call back to capture selected date
                    datePicker.show(getFragmentManager(),"date picker");
                    Log.i(TAG, "datePicker show clicked ");
                }
                break;
        }

    }

    //A call back to capture selected date
    private DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, monthOfYear);
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            //String birthDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(c.getTime());
            birthInMillis = c.getTimeInMillis(); // so store birth on the database

            for (int i = 0; i < mProfileDataArrayList.size(); i++) {
                if(mProfileDataArrayList.get(i).getKey().equals("birthDate")){
                    mProfileDataArrayList.set(i, new Profile("birthDate",String.valueOf(birthInMillis),SECTION_TEXT, SECTION_TEXT ));
                    mEditProfileAdapter.notifyItemChanged(i);

                    // set EditProfileViewModel.user values
                    if(birthInMillis != null){
                        mEditProfileViewModel.getUser().setBirthDate(birthInMillis);
                    }
                }
                Log.i(TAG, "mProfileDataArrayList sorted" + mProfileDataArrayList.get(i).getKey());
            }
            /*c.getTimeInMillis();
            DateHelper.getBirthDate(c.getTime());
            c.getTime();*/
        }
    };
}

