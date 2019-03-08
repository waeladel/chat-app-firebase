package com.trackaty.chat.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;
import com.trackaty.chat.R;
import com.trackaty.chat.activities.MainActivity;
import com.trackaty.chat.models.User;

import static com.trackaty.chat.Utils.StringUtils.getFirstWord;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private final static String TAG = ProfileFragment.class.getSimpleName();

    private static final String AVATAR_ORIGINAL_NAME = "original_avatar.jpg";
    private static final String COVER_ORIGINAL_NAME = "original_cover.jpg";

    private String mCurrentUserId, mUserId;
    private User mUser;
    private Button mSeeMoreButton;
    private FloatingActionButton mLovedByButton, mMessageButton, mBlockEditButton;

    // [START declare_database_ref]
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUserRef;
    private ImageView mCover, mAvatar , mInterestedIcon , mmRelationshipIcon;
    private TextView mUserName, mUserBio , mLovedByHint, mMessageHint, mBlockEditHint,
            mLovedByValue, mPickUpValue, mRelationship, mInterested;

    private Context mActivityContext;
    private Activity activity;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragView = inflater.inflate(R.layout.fragment_profile, container, false);

        mSeeMoreButton = (Button) fragView.findViewById(R.id.see_more_button);
        mBlockEditButton = (FloatingActionButton) fragView.findViewById(R.id.block_edit_button);
        mLovedByButton = (FloatingActionButton) fragView.findViewById(R.id.love_button);
        mMessageButton = (FloatingActionButton) fragView.findViewById(R.id.message_button);
        mLovedByHint = (TextView) fragView.findViewById(R.id.love_text);
        mMessageHint = (TextView) fragView.findViewById(R.id.message_text);
        mCover = (ImageView) fragView.findViewById(R.id.coverImage);
        mAvatar = (ImageView) fragView.findViewById(R.id.user_image);
        mUserName = (TextView) fragView.findViewById(R.id.user_name_text);
        mUserBio = (TextView) fragView.findViewById(R.id.user_bio_text);
        mBlockEditHint = (TextView) fragView.findViewById(R.id.block_edit_text);
        mLovedByValue = (TextView) fragView.findViewById(R.id.user_loved_by_value);
        mPickUpValue = (TextView) fragView.findViewById(R.id.pick_up_value);
        mRelationship = (TextView) fragView.findViewById(R.id.user_relationship_value);
        mInterested = (TextView) fragView.findViewById(R.id.user_interested_value);
        mInterestedIcon = (ImageView) fragView.findViewById(R.id.interested_in_icon);
        mmRelationshipIcon = (ImageView) fragView.findViewById(R.id.relationship_icon);

        if(getArguments() != null) {
            mCurrentUserId = ProfileFragmentArgs.fromBundle(getArguments()).getCurrentUserId();//logged in user
            mUserId = ProfileFragmentArgs.fromBundle(getArguments()).getUserId(); // any user
            mUser = ProfileFragmentArgs.fromBundle(getArguments()).getUser();// any user
            Log.d(TAG, "mCurrentUserId= " + mCurrentUserId + "mUserId= " + mUserId + "name= " + mUser.getName() + "pickups=" + mUser.getPickupCounter());

            // toggle mBlockEditButton
            if (null != mCurrentUserId && mCurrentUserId.equals(mUserId)) { // it's logged in user profile
                Log.d(TAG, "it's logged in user profile= " + mUserId);
                mBlockEditButton.setImageResource(R.drawable.ic_user_edit_profile);
                mBlockEditHint.setText(R.string.edit_profile_button);
                mLovedByButton.setEnabled(false);
                mLovedByButton.setClickable(false);
                mLovedByButton.setBackgroundTintList(ColorStateList.valueOf
                        (getResources().getColor(R.color.disabled_button)));
                mMessageButton.setEnabled(false);
                mMessageButton.setClickable(false);
                mMessageButton.setBackgroundTintList(ColorStateList.valueOf
                        (getResources().getColor(R.color.disabled_button)));
                //getResources().getColor(R.color.colorPrimary));
                mLovedByHint.setEnabled(false);
                mMessageHint.setEnabled(false);
            } else {
                // it's another user
                mBlockEditButton.setImageResource(R.drawable.ic_block_24dp);
                mBlockEditHint.setText(R.string.block_button);
                //mUserRef = mDatabaseRef.child("users").child(mUserId);
                //showUser(mUserId);
            }
            // display user data
            showCurrentUser();

            mBlockEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != mCurrentUserId && mCurrentUserId.equals(mUserId) && mUser != null) { // it's logged in user profile
                        Log.i(TAG, "going to edit profile fragment= ");
                        NavDirections direction = ProfileFragmentDirections.actionProfileToEditProfile(mUser,mCurrentUserId);
                        NavController navController = Navigation.findNavController(view);
                        navController.navigate(direction);
                    } else {
                        Log.d(TAG, "blockUser clicked");
                        //blockUser();
                    }

                }
            });

            mCover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  if (mUserId != null) {
                        Log.i(TAG, "going to Cover image view");
                        NavDirections direction = ProfileFragmentDirections.actionProfileToDisplayImage(mUserId, COVER_ORIGINAL_NAME);
                        NavController navController = Navigation.findNavController(view);
                        navController.navigate(direction);
                    }
                }
            });

            mAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   if (mUserId != null) {
                        Log.i(TAG, "going to Avatar image view");
                        NavDirections direction = ProfileFragmentDirections.actionProfileToDisplayImage(mUserId, AVATAR_ORIGINAL_NAME);
                        NavController navController = Navigation.findNavController(view);
                        navController.navigate(direction);
                    }
                }
            });

        }

        mSeeMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "SeeMoreButton id clicked= ");
                if (null != mCurrentUserId && mUserId  != null && mUser != null) {
                    NavDirections direction = ProfileFragmentDirections.actionProfileToMoreProfile(mCurrentUserId, mUserId, mUser);
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(direction);
                }


            }
        });

        return fragView;
    }

    private void showCurrentUser() {
        // [display parcelable data]
        if (mUser != null) {

            // Get user social dynamic methods

            // Get user values
            if (null != mUser.getCoverImage()) {
                Picasso.get()
                        .load(mUser.getCoverImage())
                        .placeholder(R.drawable.ic_picture_gallery_white)
                        .error(R.drawable.ic_broken_image)
                        .into(mCover);
            }

            if (null != mUser.getAvatar()) {
                Picasso.get()
                        .load(mUser.getAvatar())
                        .placeholder(R.drawable.ic_user_account_grey_white)
                        .error(R.drawable.ic_broken_image)
                        .into(mAvatar);
            }

            if (null != mUser.getName()) {
                mUserName.setText(mUser.getName());
                // to display only the first name
                mSeeMoreButton.setText(getString(R.string.see_more_about_button, getFirstWord(mUser.getName()))); // only first name
            } else {
                mSeeMoreButton.setText(R.string.see_more_button);
            }

            if (null != mUser.getBiography()) {
                mUserBio.setText(mUser.getBiography());
            }

            mLovedByValue.setText(getString(R.string.user_loved_by, mUser.getLoveCounter()));
            mPickUpValue.setText(getString(R.string.user_pickedup_by, mUser.getPickupCounter()));

            //mRelationship.setText(getString(R.string.user_relationship_value, user.getRelationship()));

            if (null != mUser.getRelationship()) {
                switch (mUser.getRelationship()) {
                    case "single":
                        mRelationship.setText(R.string.single);
                        mmRelationshipIcon.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        break;
                    case "searching":
                        mRelationship.setText(R.string.searching);
                        mmRelationshipIcon.setImageResource(R.drawable.ic_search_black_24dp);
                        break;
                    case "committed":
                        mRelationship.setText(R.string.committed);
                        mmRelationshipIcon.setImageResource(R.drawable.ic_two_hearts);
                        break;
                    case "engaged":
                        mRelationship.setText(R.string.engaged);
                        mmRelationshipIcon.setImageResource(R.drawable.ic_hearts_rings);
                        break;
                    case "married":
                        mRelationship.setText(R.string.married);
                        mmRelationshipIcon.setImageResource(R.drawable.ic_hearts_rings);
                        break;
                    case "civil union":
                        mRelationship.setText(R.string.civil_union);
                        mmRelationshipIcon.setImageResource(R.drawable.ic_hearts_rings);
                        break;
                    case "domestic partnership":
                        mRelationship.setText(R.string.domestic_partnership);
                        mmRelationshipIcon.setImageResource(R.drawable.ic_two_hearts);
                        break;
                    case "open relationship":
                        mRelationship.setText(R.string.open_relationship);
                        mmRelationshipIcon.setImageResource(R.drawable.ic_two_hearts);
                        break;
                    case "open marriage":
                        mRelationship.setText(R.string.open_marriage);
                        mmRelationshipIcon.setImageResource(R.drawable.ic_hearts_rings);
                        break;
                    case "separated":
                        mRelationship.setText(R.string.separated);
                        mmRelationshipIcon.setImageResource(R.drawable.ic_broken_heart);
                        break;
                    case "divorced":
                        mRelationship.setText(R.string.divorced);
                        mmRelationshipIcon.setImageResource(R.drawable.ic_broken_heart);
                        break;
                    case "widowed":
                        mRelationship.setText(R.string.widowed);
                        mmRelationshipIcon.setImageResource(R.drawable.ic_broken_heart);
                        break;
                    default:
                        mRelationship.setText(R.string.not_specified);
                        mmRelationshipIcon.setVisibility(View.INVISIBLE);
                        break;
                }

            }

            if (null != mUser.getInterestedIn()) {
                switch (mUser.getInterestedIn()) {
                    case "men":
                        mInterested.setText(R.string.men);
                        mInterestedIcon.setImageResource(R.drawable.ic_business_man);
                        break;
                    case "women":
                        mInterested.setText(R.string.women);
                        mInterestedIcon.setImageResource(R.drawable.ic_business_woman);
                        break;
                    case "both":
                        mInterested.setText(R.string.both_men_women);
                        mInterestedIcon.setImageResource(R.drawable.ic_wc_men_and_women_24dp);
                        break;
                    default:
                        mInterested.setText(R.string.not_specified);
                        mInterestedIcon.setVisibility(View.INVISIBLE);
                        break;
                }

            }

            // End of display parcelable data]
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(((MainActivity)getActivity())!= null){
            ActionBar actionbar = ((MainActivity)getActivity()).getSupportActionBar();
            actionbar.setTitle(R.string.profile_frag_title);
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeButtonEnabled(true);
            actionbar.setDisplayShowCustomEnabled(false);
        }
    }
}
