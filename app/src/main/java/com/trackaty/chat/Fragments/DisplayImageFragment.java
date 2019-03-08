package com.trackaty.chat.Fragments;


import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.trackaty.chat.R;
import com.trackaty.chat.activities.MainActivity;
import com.yanzhenjie.album.widget.photoview.PhotoViewAttacher;


/**
 * A simple {@link Fragment} subclass.
 */
public class DisplayImageFragment extends Fragment {

    private final static String TAG = DisplayImageFragment.class.getSimpleName();
    private String  mUserId, mImageName;
    private StorageReference mStorageRef,mImagesRef, mUserRef ;
    private Context activityContext;
    private Activity activity;
    private ImageView  photoView , loadingIcon;
    PhotoViewAttacher mAttacher;

    public DisplayImageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View fragView = inflater.inflate(R.layout.fragment_display_image, container, false);
        photoView  = (ImageView ) fragView.findViewById(R.id.zoomed_image);
        loadingIcon = (ImageView ) fragView.findViewById(R.id.loading_animation);
        if(getArguments() != null) {
            mUserId = DisplayImageFragmentArgs.fromBundle(getArguments()).getUserId(); // any user
            mImageName = DisplayImageFragmentArgs.fromBundle(getArguments()).getImageName();
            Log.d(TAG, "mCurrentUserId= " + mUserId + "mImageName"+ mImageName);
        }

        // [START create_storage_reference]
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mStorageRef.child("images/"+mUserId +"/"+ mImageName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Picasso.get()
                        .load(uri)
                        .placeholder(R.drawable.ic_picture_gallery_white)
                        .error(R.drawable.ic_broken_image)
                        .into(photoView, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                //do Photo Attacher when picture is loaded successfully
                                loadingIcon.setVisibility(View.GONE);
                                if(mAttacher!=null){
                                    mAttacher.update();
                                }else{
                                    mAttacher = new PhotoViewAttacher(photoView);
                                }
                            }
                            @Override
                            public void onError(Exception e) {
                                loadingIcon.setVisibility(View.GONE);
                                Toast.makeText(activity, R.string.download_image_error,
                                        Toast.LENGTH_LONG).show();
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                loadingIcon.setVisibility(View.GONE);
                Toast.makeText(activity, R.string.download_image_error,
                        Toast.LENGTH_LONG).show();
            }
        });


        return fragView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityContext = context;
        if (context instanceof Activity){// check if context is an activity
            activity =(Activity) context;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(((MainActivity)getActivity())!= null){
            ActionBar actionbar = ((MainActivity)getActivity()).getSupportActionBar();
            actionbar.setTitle(R.string.display_image_title);
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeButtonEnabled(true);
            actionbar.setDisplayShowCustomEnabled(false);
        }

    }

}


