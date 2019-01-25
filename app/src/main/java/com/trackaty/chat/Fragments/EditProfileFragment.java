package com.trackaty.chat.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.trackaty.chat.Adapters.EditProfileAdapter;
import com.trackaty.chat.Interface.ItemClickListener;
import com.trackaty.chat.R;
import com.trackaty.chat.Utils.Sortbysection;
import com.trackaty.chat.activities.MainActivity;
import com.trackaty.chat.models.Profile;
import com.trackaty.chat.models.User;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;

import static android.app.Activity.RESULT_OK;
import static com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE;
import static com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_EXTRA_RESULT;


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

    public static final int CROP_IMAGE_AVATAR_REQUEST_CODE = 103;
    public static final int CROP_IMAGE_COVER_REQUEST_CODE = 104;


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

    private Context activityContext;
    private Activity activity;


    private static Boolean mIsAboutAdded ;
    private static Boolean mIsWorkAdded ;
    private static Boolean mIsHabitsAdded ;

    private Uri mAvatarOriginalUri;
    private Uri mCoverOriginalUri;
    private Uri mAvatarUri;
    private Uri mCoverUri;
    //StorageReference avatarRef = mStorageRef.child("images")


    private ArrayList<AlbumFile> mMediaFiles;

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

        return fragView;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityContext = context;

        if (context instanceof Activity){// check if context is an activity
            activity =(Activity) context;
        }

        /*Album.initialize(AlbumConfig.newBuilder(activity)
                .setAlbumLoader(new MediaLoader())
                .build());*/
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(((MainActivity)getActivity())!= null){
            ((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.edit_profile_frag_title);
            ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((MainActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);

        }

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
        mEditProfileAdapter = new EditProfileAdapter(activityContext
                , mProfileDataArrayList
                , mAboutArrayList
                , mWorkArrayList
                , mHabitsArrayList
                , this);

        Log.d(TAG, "mWorkArrayList college 0="+mWorkArrayList.get(0).getValue());
        Log.d(TAG, "mWorkArrayList college 1="+mWorkArrayList.get(1).getValue());
        Log.d(TAG, "mWorkArrayList college 2="+mWorkArrayList.get(2).getValue());

        mEditProfileRecycler.setLayoutManager(new LinearLayoutManager(activityContext));
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
                        mEditProfileAdapter = new EditProfileAdapter(activityContext
                                , mProfileDataArrayList
                                , mAboutArrayList
                                , mWorkArrayList
                                , mHabitsArrayList
                                , this);

                        mEditProfileRecycler.setLayoutManager(new LinearLayoutManager(activityContext));
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
        switch (mProfileDataArrayList.get(position).getKey()) {

            case "avatar":
                Log.d(TAG, "avatar item clicked= " + position);
                selectMedia(true);
                break;
            case "coverImage":
                Log.d(TAG, "coverImage item clicked= " + position);
                selectMedia(false);
                break;
            case "age":
                Log.d(TAG, "age item clicked= " + position);
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "requestCode ="+ requestCode);

        switch (requestCode){
            case CROP_IMAGE_AVATAR_REQUEST_CODE:
                Log.d(TAG, "AVATAR_CROP_PICTURE requestCode= "+ requestCode);
                CropImage.ActivityResult avatarResult = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    mAvatarOriginalUri = avatarResult.getOriginalUri();
                    mAvatarUri = avatarResult.getUri();
                    Log.d(TAG, "mAvatarOriginalUri = "+ mAvatarOriginalUri);
                    Log.d(TAG, "mAvatarUri = "+ mAvatarUri);

                    /*sPhotoResultUri = result.getUri();
                    mProfileImageButton.setImageURI(sPhotoResultUri);*/
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = avatarResult.getError();
                    Toast.makeText(activityContext, error.toString(),
                            Toast.LENGTH_LONG).show();
                }
                break;
            case CROP_IMAGE_COVER_REQUEST_CODE:
                Log.d(TAG, "COVER CROP_PICTURE requestCode= "+ requestCode);
                CropImage.ActivityResult coverResult = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    mCoverOriginalUri = coverResult.getOriginalUri();
                    mCoverUri = coverResult.getUri();
                    Log.d(TAG, "mCoverOriginalUri = "+ mCoverOriginalUri);
                    Log.d(TAG, "mCoverUri = "+ mCoverUri);
                    /*sPhotoResultUri = result.getUri();
                    mProfileImageButton.setImageURI(sPhotoResultUri);*/
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = coverResult.getError();
                    Toast.makeText(activityContext, error.toString(),
                            Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    private void selectMedia(final boolean isAvater) {
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

                        cropImage(MediaUri, isAvater);
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



    private void cropImage(Uri mediaUri, boolean isAvater) {
        if(isAvater){
            Intent intent = CropImage.activity(Uri.fromFile(new File(mediaUri.toString())))
                    //.setGuidelines(CropImageView.Guidelines.ON)
                    .setAllowRotation(true)
                    .setAutoZoomEnabled(true)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    //.setAspectRatio(1,1)
                    .setFixAspectRatio(true)
                    //.setMaxCropResultSize(600, 600)
                    .setMinCropResultSize(300,300)
                    .setRequestedSize(300,300)//resize
                    .getIntent(activityContext);
                    //.start(activityContext, this);
            this.startActivityForResult(intent, CROP_IMAGE_AVATAR_REQUEST_CODE );

        }else{

            Intent intent = CropImage.activity(Uri.fromFile(new File(mediaUri.toString())))
                    //.setGuidelines(CropImageView.Guidelines.ON)
                    .setAllowRotation(true)
                    .setAutoZoomEnabled(true)
                    .setAspectRatio(2,1)
                    //.setMaxCropResultSize(600, 600)
                    .setMinCropResultSize(300,300)
                    .setRequestedSize(600,300) //resize
                    .getIntent(activityContext);
                    //.start(activityContext, this);
            this.startActivityForResult(intent, CROP_IMAGE_COVER_REQUEST_CODE);
        }

        Log.d(TAG, "cropImage starts" +mediaUri);


    }

}
