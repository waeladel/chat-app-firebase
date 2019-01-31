package com.trackaty.chat.Fragments;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.trackaty.chat.Adapters.EditProfileAdapter;
import com.trackaty.chat.Interface.ItemClickListener;
import com.trackaty.chat.R;
import com.trackaty.chat.Utils.DateHelper;
import com.trackaty.chat.Utils.Sortbysection;
import com.trackaty.chat.activities.MainActivity;
import com.trackaty.chat.models.Profile;
import com.trackaty.chat.models.User;
import com.trackaty.chat.models.Variables;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment implements ItemClickListener{

    private final static String TAG = EditProfileFragment.class.getSimpleName();

    private  static final int SECTION_IMAGE = 1;
    private  static final int SECTION_EDIT_TEXT = 2;
    private  static final int SECTION_TEXT = 3;
    private  static final int SECTION_SPINNER = 4;
    private  static final int SECTION_ABOUT = 5;
    private  static final int SECTION_WORK = 6;
    private  static final int SECTION_HABITS = 7;
    private  static final int SECTION_SOCIAL = 8;


    public  final static String SECTION_ABOUT_HEADLINE = "about";
    public  final static String SECTION_WORK_HEADLINE  = "work_and_education";
    public  final static String SECTION_HABITS_HEADLINE  = "habits";
    public  final static String SECTION_SOCIAL_HEADLINE  = "social_and_contacts";


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

    public  final static String IMAGE_HOLDER_POSITION = "position";


    public static final int CROP_IMAGE_AVATAR_REQUEST_CODE = 103;
    public static final int CROP_IMAGE_COVER_REQUEST_CODE = 104;


    private User currentUser;
    private String currentUserId;


    // [START declare_database_ref]
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUserRef;

    private RecyclerView mEditProfileRecycler;
    private ArrayList<Profile> mProfileDataArrayList = new ArrayList<>();
    private ArrayList<Profile> mAboutArrayList = new ArrayList<>();
    private ArrayList<Profile> mWorkArrayList = new ArrayList<>();
    private ArrayList<Profile> mHabitsArrayList = new ArrayList<>();
    private ArrayList<Profile> mSocialArrayList = new ArrayList<>();
    private ArrayList<Variables> mVariablesArrayList = new ArrayList<>();



    private EditProfileAdapter mEditProfileAdapter;

    private Parcelable savedRecyclerLayoutState;
    private static final String BUNDLE_RECYCLER_LAYOUT = "recycler_layout";

    private Context activityContext;
    private Activity activity;


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

        if (savedInstanceState != null) {
            Boolean isSavedInstance = savedInstanceState.getBoolean("isSavedInstance");
            Log.d(TAG, "isSavedInstance ="+isSavedInstance);

            // Do something with value if needed
            mProfileDataArrayList = savedInstanceState.getParcelableArrayList(PROFILE_LIST_STATE);
            mAboutArrayList = savedInstanceState.getParcelableArrayList(ABOUT_LIST_STATE);
            mWorkArrayList = savedInstanceState.getParcelableArrayList(WORK_LIST_STATE);
            mHabitsArrayList = savedInstanceState.getParcelableArrayList(HABITS_LIST_STATE);
            mVariablesArrayList = savedInstanceState.getParcelableArrayList(VARIABLES_LIST_STATE);
            mSocialArrayList = savedInstanceState.getParcelableArrayList(SOCIAL_LIST_STATE);

            savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            restorePreviousState(); // Restore data found in the Bundle
        }else{
            if(getArguments()!= null){
                currentUser = EditProfileFragmentArgs.fromBundle(getArguments()).getCurrentUser();//logged in user
                currentUserId = EditProfileFragmentArgs.fromBundle(getArguments()).getCurrentUserId();//logged in user id
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
        outState.putParcelableArrayList(SOCIAL_LIST_STATE, mSocialArrayList);
        outState.putParcelableArrayList(VARIABLES_LIST_STATE, mVariablesArrayList);

        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, mEditProfileRecycler.getLayoutManager().onSaveInstanceState());
    }

    private void restorePreviousState() {
        mEditProfileAdapter = new EditProfileAdapter(activityContext
                , mProfileDataArrayList
                , mAboutArrayList
                , mWorkArrayList
                , mHabitsArrayList
                , mSocialArrayList
                , mVariablesArrayList
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

                        // [set settings variable for collapse and progress icon]
                        for (int i = 0; i < 5; i++) {
                            mVariablesArrayList.add(new Variables(false));
                        }

                        // [initialize the adapter]
                        mEditProfileAdapter = new EditProfileAdapter(activityContext
                                , mProfileDataArrayList
                                , mAboutArrayList
                                , mWorkArrayList
                                , mHabitsArrayList
                                , mSocialArrayList
                                , mVariablesArrayList
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

                        String value;
                        // check if method is null
                        if (method.invoke(currentUser) != null){
                            value = method.invoke(currentUser).toString();
                        }else{
                            value = "";
                        }
                            Log.d(TAG, "Method=" + value);
                            Log.d(TAG, "Method Type=" + method.getGenericReturnType());

                            if((fieldName.equals("avatar") || fieldName.equals("coverImage"))){
                                Profile profileData = new Profile(fieldName, value,SECTION_IMAGE);
                                mProfileDataArrayList.add(profileData);

                            }else if((fieldName.equals("name")
                                    || fieldName.equals("biography"))){
                            Profile profileData = new Profile(fieldName, value, SECTION_EDIT_TEXT);
                            mProfileDataArrayList.add(profileData);

                            }else if(fieldName.equals("age")){
                                Profile profileData = new Profile(fieldName, value, SECTION_TEXT);
                                mProfileDataArrayList.add(profileData);

                            }else if((fieldName.equals("phone")
                                    || fieldName.equals("facebook")
                                    || fieldName.equals("instagram")
                                    || fieldName.equals("twitter")
                                    || fieldName.equals("snapchat")
                                    || fieldName.equals("tumblr")
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
                                Profile profileData = new Profile(fieldName, value,SECTION_SOCIAL);
                                //mProfileDataArrayList.add(profileData);
                                mSocialArrayList.add(profileData);
                                if(!mIsSocialAdded){
                                    Log.d(TAG, "mIsSocialAdded=" + mIsSocialAdded);
                                    Profile socialData = new Profile(SECTION_SOCIAL_HEADLINE, value,SECTION_SOCIAL);
                                    mProfileDataArrayList.add(socialData);
                                }
                                mIsSocialAdded = true; // to add only one expandable section header

                            }else if((fieldName.equals("relationship")
                                    || fieldName.equals("gender")
                                    || fieldName.equals("horoscope")
                                    || fieldName.equals("interestedIn"))){
                            Profile profileData = new Profile(fieldName, value,SECTION_SPINNER);
                            mProfileDataArrayList.add(profileData);

                            } else if(fieldName.equals("work")
                                    || fieldName.equals("college")
                                    || fieldName.equals("school")){
                                //add data for work section
                                Profile profileData = new Profile(fieldName, value,SECTION_WORK);
                                //mProfileDataArrayList.add(profileData);
                                mWorkArrayList.add(profileData);
                                if(!mIsWorkAdded){
                                    Log.d(TAG, "mIsAboutAdded=" + mIsWorkAdded);
                                    Profile aboutSectionData = new Profile(SECTION_WORK_HEADLINE, value,SECTION_WORK);
                                    mProfileDataArrayList.add(aboutSectionData);
                                }
                                mIsWorkAdded = true; // to add only one expandable section header

                            }else if(fieldName.equals("nationality")
                                    || fieldName.equals("hometown")
                                    || fieldName.equals("lives")
                                    || fieldName.equals("politics")
                                    || fieldName.equals("religion")){
                                //add data for about section
                                Profile profileData = new Profile(fieldName, value,SECTION_ABOUT);
                                //mProfileDataArrayList.add(profileData);
                                mAboutArrayList.add(profileData);
                                if(!mIsAboutAdded){
                                    Log.d(TAG, "mIsAboutAdded=" + mIsAboutAdded);
                                    Profile aboutSectionData = new Profile(SECTION_ABOUT_HEADLINE, value,SECTION_ABOUT);
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
                                Profile profileData = new Profile(fieldName, value,SECTION_HABITS);
                                //mProfileDataArrayList.add(profileData);
                                mHabitsArrayList.add(profileData);
                                if(!mIsHabitsAdded){
                                    Log.d(TAG, "mIsAboutAdded=" + mIsHabitsAdded);
                                    Profile habitsSectionData = new Profile(SECTION_HABITS_HEADLINE, value,SECTION_HABITS);
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
            int position = data.getExtras().getInt(IMAGE_HOLDER_POSITION,0);
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
                        Toast.makeText(activityContext, error.toString(),
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
                        Toast.makeText(activityContext, error.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }

    }

    private void compressImage(final Uri imageUri, final String type, final int position) {
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
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return finalUserRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        mProfileDataArrayList.set(position,new Profile(type, downloadUri.toString(),SECTION_IMAGE));
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

    private void selectMedia(final boolean isAvater, final int position) {
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

                        cropImage(MediaUri, isAvater, position);
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



    private void cropImage(Uri mediaUri, boolean isAvater, int position) {
        if(isAvater){
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
                    .getIntent(activityContext);
                    //.start(activityContext, this);
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
                    .getIntent(activityContext);
                    //.start(activityContext, this);
            //intent.putExtra(IMAGE_HOLDER_POSITION,position);
            Bundle mBundle = new Bundle();
            mBundle.putInt(IMAGE_HOLDER_POSITION,position);
            intent.putExtras(mBundle);
            this.startActivityForResult(intent, CROP_IMAGE_COVER_REQUEST_CODE);
        }

        Log.d(TAG, "cropImage starts" +mediaUri);


    }

    // use interface to detect item click on the recycler adapter
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
            case "age":
                //mEditProfileAdapter.notifyDataSetChanged();
                DatePickerFragment datePicker = new DatePickerFragment();
                if (getFragmentManager() != null) {
                    datePicker.setCallBack(ondate); //Set Call back to capture selected date
                    datePicker.show(getFragmentManager(),"date picker");
                    Log.i(TAG, "datePicker show clicked ");
                }
                break;
        }

    }

    //A call back to capture selected date
    OnDateSetListener ondate = new OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, monthOfYear);
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String birthDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(c.getTime());

            for (int i = 0; i < mProfileDataArrayList.size(); i++) {
                if(mProfileDataArrayList.get(i).getKey().equals("age")){
                    mProfileDataArrayList.set(i, new Profile("age",birthDate,SECTION_TEXT ));
                    mEditProfileAdapter.notifyItemChanged(i);
                }
                Log.i(TAG, "mProfileDataArrayList sorted" + mProfileDataArrayList.get(i).getKey());
            }
            /*c.getTimeInMillis();
            DateHelper.getAge(c.getTime());
            c.getTime();*/
        }
    };
}

