package com.trackaty.chat.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.trackaty.chat.Interface.ItemClickListener;
import com.trackaty.chat.R;
import com.trackaty.chat.Utils.SortSocial;
import com.trackaty.chat.ViewModels.ProfileViewModel;
import com.trackaty.chat.activities.MainActivity;
import com.trackaty.chat.models.Relation;
import com.trackaty.chat.models.Social;
import com.trackaty.chat.models.SocialObj;
import com.trackaty.chat.models.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.trackaty.chat.Utils.StringUtils.getFirstWord;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements ItemClickListener {

    private final static String TAG = ProfileFragment.class.getSimpleName();

    private static final String AVATAR_ORIGINAL_NAME = "original_avatar.jpg";
    private static final String COVER_ORIGINAL_NAME = "original_cover.jpg";

    // requests and relations status
    private static final String RELATION_STATUS_SENDER = "sender";
    private static final String RELATION_STATUS_RECEIVER = "receiver";
    private static final String RELATION_STATUS_STALKER = "stalker";
    private static final String RELATION_STATUS_FOLLOWED = "followed";
    private static final String RELATION_STATUS_NOT_FRIEND = "notFriend";

    private String mRelationStatus;


    // used for sorting the private contacts array, also we must differentiate between request and approve
    private  static final int SECTION_SOCIAL_REQUEST = 100;
    private  static final int SECTION_SOCIAL_APPROVE = 200;

    //Fragments tags
    private  static final String REQUEST_FRAGMENT = "RequestFragment";
    private  static final String EDIT_UNREVEAL_FRAGMENT = "EditFragment";
    private  static final String CONFIRMATION_ALERT_FRAGMENT = "EditFragment";

    private String mCurrentUserId, mUserId;
    private User mUser;
    private Button mSeeMoreButton;
    private FloatingActionButton mLovedByButton, mMessageButton, mRevealButton, mBlockEditButton;

    private FragmentManager fragmentManager;// = getFragmentManager();
    private RevealFragment requestFragment;

    // [START declare_database_ref]
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUserRef;
    private ImageView mCover, mAvatar , mInterestedIcon , mmRelationshipIcon;
    private TextView mUserName, mUserBio , mLovedByHint, mMessageHint, mRevealHint, mBlockEditHint,
            mLovedByValue, mPickUpValue, mRelationship, mInterested;

    private Context mActivityContext;
    private Activity activity;
    private ArrayList<Social> mPrivateContactsList;
    private ArrayList<Social> mRelationsList;
    private ArrayList<Social> mOriginalRelationsList;

    // Map to hold user selections results
    private Map<String, Boolean> mRelationsMap ;
    // MAp to hold the original requests va;ues in case user canel and wen need to start all over again
    private Map<String, Boolean> mOriginalRelationsMap ;

    private Relation mRelations;

    // Create contacts Hash list, to hold user selections when sending a request
    Map<String, Boolean> contactsMap = new HashMap<>();

    private ProfileViewModel mProfileViewModel;


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
        mRevealButton = (FloatingActionButton) fragView.findViewById(R.id.reveal_button);
        mLovedByHint = (TextView) fragView.findViewById(R.id.love_text);
        mMessageHint = (TextView) fragView.findViewById(R.id.message_button_text);
        mRevealHint = (TextView) fragView.findViewById(R.id.reveal_button_text);
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

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        // instantiate a new user relations to use it for reveal requests
        mRelations = new Relation();

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

                mRevealButton.setEnabled(false);
                mRevealButton.setClickable(false);
                mRevealButton.setBackgroundTintList(ColorStateList.valueOf
                        (getResources().getColor(R.color.disabled_button)));
                //getResources().getColor(R.color.colorPrimary));
                mLovedByHint.setEnabled(false);
                mMessageHint.setEnabled(false);
                mRevealHint.setEnabled(false);
            } else {
                // it's another user
                mBlockEditButton.setImageResource(R.drawable.ic_block_24dp);
                mBlockEditHint.setText(R.string.block_button);
                //mUserRef = mDatabaseRef.child("users").child(mUserId);
                //showUser(mUserId);
                // update the reveal request
                mRelationStatus = RELATION_STATUS_NOT_FRIEND;
                mProfileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
                // get relations with selected user if any
                mProfileViewModel.getRelation(mCurrentUserId, mUserId).observe(this, new Observer<Relation>() {
                    @Override
                    public void onChanged(Relation relation) {
                        Log.i(TAG, "onChanged mProfileViewModel getRelation");
                        if (relation != null){
                            // Relation exist
                            switch (relation.getStatus()){
                                case RELATION_STATUS_SENDER:
                                    // If this selected user sent me the request
                                    //Approve request
                                    mRelationStatus = RELATION_STATUS_SENDER;
                                    mRevealHint.setText(R.string.request_button_approve_hint);
                                    mRelationsMap = relation.getContacts();
                                    mOriginalRelationsMap = new HashMap<>(mRelationsMap);
                                    break;
                                case RELATION_STATUS_RECEIVER:
                                    // If this selected received a request from me
                                    //Cancel request
                                    mRelationStatus = RELATION_STATUS_RECEIVER;
                                    mRevealHint.setText(R.string.request_button_cancel_hint);
                                    mRevealHint.setTextColor(getResources().getColor(R.color.colorAccent));
                                    break;
                                case RELATION_STATUS_STALKER:
                                    // If this selected i (currentUser) approved his request
                                    //Edit/ Un-reveal
                                    mRelationStatus = RELATION_STATUS_STALKER;
                                    mRevealHint.setText(R.string.request_button_unreveal_hint);
                                    mRevealHint.setTextColor(getResources().getColor(R.color.colorAccent));
                                    mRelationsMap = relation.getContacts();
                                    mOriginalRelationsMap = new HashMap<>(mRelationsMap);
                                    break;
                                case RELATION_STATUS_FOLLOWED:
                                    // If this selected user approved my (currentUser) request
                                    //Un reveal
                                    mRelationStatus = RELATION_STATUS_FOLLOWED;
                                    mRevealHint.setText(R.string.request_button_unreveal_hint);
                                    mRevealHint.setTextColor(getResources().getColor(R.color.colorAccent));
                                    mRelationsMap = relation.getContacts();
                                    mOriginalRelationsMap = new HashMap<>(mRelationsMap);
                                    break;
                            }
                            Log.d(TAG, "onChanged relation Status= " + relation.getStatus() + " size= " + relation.getContacts().size());

                        }else{
                            // Relation doesn't exist, use default user settings
                            Log.i(TAG, "onChanged relation Status= Relation dosn't exist");
                            mRelationStatus = RELATION_STATUS_NOT_FRIEND;

                            mPrivateContactsList = getPrivateContacts();
                            if(mPrivateContactsList.size()> 0){
                                mRevealHint.setText(R.string.request_button_hint);
                            }else{
                                // disable RevealButton because there are no private contacts
                                mRevealHint.setText(R.string.request_button_hint);
                                mRevealButton.setEnabled(false);
                                mRevealButton.setClickable(false);
                                mRevealButton.setBackgroundTintList(ColorStateList.valueOf
                                        (getResources().getColor(R.color.disabled_button)));
                                mRevealHint.setEnabled(false);
                            }

                        }

                    }
                });
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

            mMessageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != mCurrentUserId && mCurrentUserId.equals(mUserId) && mUser != null) { // it's logged in user profile
                        Log.i(TAG, "don't send message to current logged in user ");
                    } else {
                        Log.d(TAG, "send message to user");
                        NavDirections MessageDirection = ProfileFragmentDirections.actionProfileFragmentToMessagesFragment(mCurrentUserId,null, mUserId,false);
                        //NavController navController = Navigation.findNavController(this, R.id.host_fragment);
                        //check if we are on Main Fragment not on complete Profile already
                        Navigation.findNavController(view).navigate(MessageDirection);
                    }

                }
            });

            fragmentManager = getFragmentManager();

            mRevealButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != mCurrentUserId && mCurrentUserId.equals(mUserId) && mUser != null) { // it's logged in user profile
                        Log.i(TAG, "don't send message to current logged in user ");
                    } else {
                        switch (mRelationStatus){
                            case RELATION_STATUS_SENDER:
                                // If this selected user sent me the request
                                //Approve request
                                // Show Approve dialog
                                contactsMap.clear(); // clear all previous selected check boxes
                                Log.d(TAG, "RevealButton clicked mProfileViewModel Approve request");
                                mRelationsList = getRelationsList(mRelationsMap);
                                for (int i = 0; i < mRelationsList.size(); i++) {
                                    Log.i(TAG, "mRelationsList = " + mRelationsList.get(i).getKey()+ " = "+ mRelationsList.get(i).getValue().getPublic());
                                }

                                showDialog(mRelationsList);
                                break;
                            case RELATION_STATUS_RECEIVER:
                                // If this selected received a request from me
                                //Cancel request
                                mProfileViewModel.cancelRequest(mCurrentUserId, mUserId);
                                Log.d(TAG, "RevealButton clicked mProfileViewModel cancelRequest");
                                break;
                            case RELATION_STATUS_STALKER:
                                // If this selected i (currentUser) approved his request
                                //Edit/ Un-reveal
                                // select to edit contacts or un-reveal all contacts
                                showEditUnrevealDialog();
                                break;
                            case RELATION_STATUS_FOLLOWED:
                                // If this selected user approved my (currentUser) request
                                //Un reveal
                                // select to un-reveal all contacts, you don't have permission to edit contacts
                                showConfirmationDialog();
                                break;
                                default:
                                    // Show request dialog
                                    contactsMap.clear(); // clear all previous selected check boxs
                                    Log.d(TAG, "send reveal request");
                                    mPrivateContactsList = getPrivateContacts();

                                    for (int i = 0; i < mPrivateContactsList.size(); i++) {
                                        Log.i(TAG, "mPrivateContactsList sorted " + mPrivateContactsList.get(i).getKey());
                                    }

                                    if(mPrivateContactsList.size()> 0){
                                        showDialog(mPrivateContactsList);
                                    }

                                    break;
                        }

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

    //Show request/approve dialog
    private void showDialog(ArrayList<Social> contactsList) {
        requestFragment = RevealFragment.newInstance(contactsList,this);
        if (getFragmentManager() != null) {
            fragmentManager = getFragmentManager();
            requestFragment.show(fragmentManager, REQUEST_FRAGMENT);
            Log.i(TAG, "revealRequest show clicked ");
        }
    }

    //Show confirmation dialog before deleting relations
    private void showConfirmationDialog() {
        ConfirmationAlertFragment confirmationFragment = ConfirmationAlertFragment.newInstance(this);
        if (getFragmentManager() != null) {
            fragmentManager = getFragmentManager();
            confirmationFragment.show(fragmentManager, CONFIRMATION_ALERT_FRAGMENT);
            Log.i(TAG, "confirmationFragment show clicked");
        }
    }

    //Show a dialog to select whether to edit or un-reveal
    private void showEditUnrevealDialog() {
        UnrevealAlertFragment editUnrevealFragment = UnrevealAlertFragment.newInstance(this);
        if (getFragmentManager() != null) {
            fragmentManager = getFragmentManager();
            editUnrevealFragment.show(fragmentManager, EDIT_UNREVEAL_FRAGMENT);
            Log.i(TAG, "edit/UnrevealFragment show clicked ");
        }
    }

    //Get all private contacts from userRef database
    private ArrayList<Social> getPrivateContacts() {

        ArrayList<Social> privateContactsList =  new ArrayList<>();
        if (mUser != null) {
            if(null != mUser.getPhone() && !mUser.getPhone().getPublic()){
                privateContactsList.add(new Social("phone", mUser.getPhone(), SECTION_SOCIAL_REQUEST +1 , SECTION_SOCIAL_REQUEST));
            }
            if(null != mUser.getFacebook() && !mUser.getFacebook().getPublic()){
                privateContactsList.add(new Social("facebook", mUser.getFacebook(), SECTION_SOCIAL_REQUEST +2 , SECTION_SOCIAL_REQUEST));
            }
            if(null != mUser.getInstagram() && !mUser.getInstagram().getPublic()){
                privateContactsList.add(new Social("instagram",mUser.getInstagram(), SECTION_SOCIAL_REQUEST +3 , SECTION_SOCIAL_REQUEST));
            }
            if(null != mUser.getTwitter() && !mUser.getTwitter().getPublic()){
                privateContactsList.add(new Social("twitter", mUser.getTwitter(), SECTION_SOCIAL_REQUEST +4 , SECTION_SOCIAL_REQUEST));
            }
            if(null != mUser.getSnapchat() && !mUser.getSnapchat().getPublic()){
                privateContactsList.add(new Social("snapchat", mUser.getSnapchat(), SECTION_SOCIAL_REQUEST +5 , SECTION_SOCIAL_REQUEST));
            }
            if(null != mUser.getTumblr() && !mUser.getTumblr().getPublic()){
                privateContactsList.add(new Social("tumblr",mUser.getTumblr(), SECTION_SOCIAL_REQUEST +6 , SECTION_SOCIAL_REQUEST));
            }
            if(null != mUser.getPubg() && !mUser.getPubg().getPublic()){
                privateContactsList.add(new Social("pubg", mUser.getPubg(), SECTION_SOCIAL_REQUEST +7 , SECTION_SOCIAL_REQUEST));
            }
            if(null != mUser.getVk() && !mUser.getVk().getPublic()){
                privateContactsList.add(new Social("vk", mUser.getVk(), SECTION_SOCIAL_REQUEST +8 , SECTION_SOCIAL_REQUEST));
            }
            if(null != mUser.getAskfm() && !mUser.getAskfm().getPublic()){
                privateContactsList.add(new Social("askfm", mUser.getAskfm(), SECTION_SOCIAL_REQUEST +9 , SECTION_SOCIAL_REQUEST));
            }
            if(null != mUser.getCuriouscat() && !mUser.getCuriouscat().getPublic()){
                privateContactsList.add(new Social("curiouscat", mUser.getCuriouscat(), SECTION_SOCIAL_REQUEST +10 , SECTION_SOCIAL_REQUEST));
            }
            if(null != mUser.getSaraha() && !mUser.getSaraha().getPublic()){
                privateContactsList.add(new Social("saraha", mUser.getSaraha(), SECTION_SOCIAL_REQUEST +11 , SECTION_SOCIAL_REQUEST));
            }
            if(null != mUser.getPinterest() && !mUser.getPinterest().getPublic()){
                privateContactsList.add(new Social("pinterest", mUser.getPinterest(), SECTION_SOCIAL_REQUEST +12 , SECTION_SOCIAL_REQUEST));
            }
            if(null != mUser.getSoundcloud() && !mUser.getSoundcloud().getPublic()){
                privateContactsList.add(new Social("soundcloud", mUser.getSoundcloud(), SECTION_SOCIAL_REQUEST +13 , SECTION_SOCIAL_REQUEST));
            }
            if(null != mUser.getSpotify() && !mUser.getSpotify().getPublic()){
                privateContactsList.add(new Social("spotify", mUser.getSpotify(), SECTION_SOCIAL_REQUEST +14 , SECTION_SOCIAL_REQUEST));
            }
            if(null != mUser.getAnghami() &&  !mUser.getAnghami().getPublic()){
                privateContactsList.add(new Social("anghami", mUser.getAnghami(), SECTION_SOCIAL_REQUEST +15 , SECTION_SOCIAL_REQUEST));
            }
            if(null != mUser.getTwitch() && !mUser.getTwitch().getPublic()){
                privateContactsList.add(new Social("twitch", mUser.getTwitch(), SECTION_SOCIAL_REQUEST +16 , SECTION_SOCIAL_REQUEST));
            }
            if(null != mUser.getYoutube() && !mUser.getYoutube().getPublic()){
                privateContactsList.add(new Social("youtube", mUser.getYoutube(), SECTION_SOCIAL_REQUEST +17 , SECTION_SOCIAL_REQUEST));
            }
            if(null != mUser.getLinkedIn() && !mUser.getLinkedIn().getPublic()){
                privateContactsList.add(new Social("linkedIn", mUser.getLinkedIn(), SECTION_SOCIAL_REQUEST +18 , SECTION_SOCIAL_REQUEST));
            }
            if(null != mUser.getWikipedia() && !mUser.getWikipedia().getPublic()){
                privateContactsList.add(new Social("wikipedia", mUser.getWikipedia(), SECTION_SOCIAL_REQUEST +19 , SECTION_SOCIAL_REQUEST));
            }
            if(null != mUser.getWebsite() && !mUser.getWebsite().getPublic()){
                privateContactsList.add(new Social("website", mUser.getWebsite(), SECTION_SOCIAL_REQUEST +20 , SECTION_SOCIAL_REQUEST));
            }

            // sort ArrayList
            Collections.sort(privateContactsList, new SortSocial());

        }
        return privateContactsList;
    }

    // Get all requested contacts from relationRef database
    private ArrayList<Social> getRelationsList(Map<String, Boolean> relationsMap) {
        ArrayList<Social> relationsList =  new ArrayList<>();

        if (null != relationsMap && !relationsMap.isEmpty()) {

            for (Object o : relationsMap.entrySet()) {
                Map.Entry pair = (Map.Entry) o;
                Log.d(TAG, "relationsMap = " + pair.getKey() + " = " + pair.getValue());

                if(pair.getKey().equals("phone")){
                    SocialObj socialObj = new SocialObj(String.valueOf(pair.getKey()), (Boolean) pair.getValue());
                    relationsList.add(new Social("phone", socialObj, SECTION_SOCIAL_APPROVE +1 , SECTION_SOCIAL_APPROVE));
                }
                if(pair.getKey().equals("facebook")){
                    SocialObj socialObj = new SocialObj(String.valueOf(pair.getKey()), (Boolean) pair.getValue());
                    relationsList.add(new Social("facebook", socialObj, SECTION_SOCIAL_APPROVE +2 , SECTION_SOCIAL_APPROVE));
                }
                if(pair.getKey().equals("instagram")){
                    SocialObj socialObj = new SocialObj(String.valueOf(pair.getKey()), (Boolean) pair.getValue());
                    relationsList.add(new Social("instagram",socialObj, SECTION_SOCIAL_APPROVE +3 , SECTION_SOCIAL_APPROVE));
                }
                if(pair.getKey().equals("twitter")){
                    SocialObj socialObj = new SocialObj(String.valueOf(pair.getKey()), (Boolean) pair.getValue());
                    relationsList.add(new Social("twitter", socialObj, SECTION_SOCIAL_APPROVE +4 , SECTION_SOCIAL_APPROVE));
                }
                if(pair.getKey().equals("snapchat")){
                    SocialObj socialObj = new SocialObj(String.valueOf(pair.getKey()), (Boolean) pair.getValue());
                    relationsList.add(new Social("snapchat", socialObj, SECTION_SOCIAL_APPROVE +5 , SECTION_SOCIAL_APPROVE));
                }
                if(pair.getKey().equals("tumblr")){
                    SocialObj socialObj = new SocialObj(String.valueOf(pair.getKey()), (Boolean) pair.getValue());
                    relationsList.add(new Social("tumblr", socialObj, SECTION_SOCIAL_APPROVE +6 , SECTION_SOCIAL_APPROVE));
                }
                if(pair.getKey().equals("pubg")){
                    SocialObj socialObj = new SocialObj(String.valueOf(pair.getKey()), (Boolean) pair.getValue());
                    relationsList.add(new Social("pubg", socialObj, SECTION_SOCIAL_APPROVE +7 , SECTION_SOCIAL_APPROVE));
                }
                if(pair.getKey().equals("vk")){
                    SocialObj socialObj = new SocialObj(String.valueOf(pair.getKey()), (Boolean) pair.getValue());
                    relationsList.add(new Social("vk", socialObj, SECTION_SOCIAL_APPROVE +8 , SECTION_SOCIAL_APPROVE));
                }
                if(pair.getKey().equals("askfm")){
                    SocialObj socialObj = new SocialObj(String.valueOf(pair.getKey()), (Boolean) pair.getValue());
                    relationsList.add(new Social("askfm", socialObj, SECTION_SOCIAL_APPROVE +9 , SECTION_SOCIAL_APPROVE));
                }
                if(pair.getKey().equals("curiouscat")){
                    SocialObj socialObj = new SocialObj(String.valueOf(pair.getKey()), (Boolean) pair.getValue());
                    relationsList.add(new Social("curiouscat", socialObj, SECTION_SOCIAL_APPROVE +10 , SECTION_SOCIAL_APPROVE));
                }
                if(pair.getKey().equals("saraha")){
                    SocialObj socialObj = new SocialObj(String.valueOf(pair.getKey()), (Boolean) pair.getValue());
                    relationsList.add(new Social("saraha", socialObj, SECTION_SOCIAL_APPROVE +11 , SECTION_SOCIAL_APPROVE));
                }
                if(pair.getKey().equals("pinterest")){
                    SocialObj socialObj = new SocialObj(String.valueOf(pair.getKey()), (Boolean) pair.getValue());
                    relationsList.add(new Social("pinterest", socialObj, SECTION_SOCIAL_APPROVE +12 , SECTION_SOCIAL_APPROVE));
                }
                if(pair.getKey().equals("soundcloud")){
                    SocialObj socialObj = new SocialObj(String.valueOf(pair.getKey()), (Boolean) pair.getValue());
                    relationsList.add(new Social("soundcloud",socialObj, SECTION_SOCIAL_APPROVE +13 , SECTION_SOCIAL_APPROVE));
                }
                if(pair.getKey().equals("spotify")){
                    SocialObj socialObj = new SocialObj(String.valueOf(pair.getKey()), (Boolean) pair.getValue());
                    relationsList.add(new Social("spotify", socialObj, SECTION_SOCIAL_APPROVE +14 , SECTION_SOCIAL_APPROVE));
                }
                if(pair.getKey().equals("anghami")){
                    SocialObj socialObj = new SocialObj(String.valueOf(pair.getKey()), (Boolean) pair.getValue());
                    relationsList.add(new Social("anghami", socialObj, SECTION_SOCIAL_APPROVE +15 , SECTION_SOCIAL_APPROVE));
                }
                if(pair.getKey().equals("twitch")){
                    SocialObj socialObj = new SocialObj(String.valueOf(pair.getKey()), (Boolean) pair.getValue());
                    relationsList.add(new Social("twitch", socialObj, SECTION_SOCIAL_APPROVE +16 , SECTION_SOCIAL_APPROVE));
                }
                if(pair.getKey().equals("youtube")){
                    SocialObj socialObj = new SocialObj(String.valueOf(pair.getKey()), (Boolean) pair.getValue());
                    relationsList.add(new Social("youtube", socialObj, SECTION_SOCIAL_APPROVE +17 , SECTION_SOCIAL_APPROVE));
                }
                if(pair.getKey().equals("linkedIn")){
                    SocialObj socialObj = new SocialObj(String.valueOf(pair.getKey()), (Boolean) pair.getValue());
                    relationsList.add(new Social("linkedIn", socialObj, SECTION_SOCIAL_APPROVE +18 , SECTION_SOCIAL_APPROVE));
                }
                if(pair.getKey().equals("wikipedia")){
                    SocialObj socialObj = new SocialObj(String.valueOf(pair.getKey()), (Boolean) pair.getValue());
                    relationsList.add(new Social("wikipedia", socialObj, SECTION_SOCIAL_APPROVE +19 , SECTION_SOCIAL_APPROVE));
                }
                if(pair.getKey().equals("website")){
                    SocialObj socialObj = new SocialObj(String.valueOf(pair.getKey()), (Boolean) pair.getValue());
                    relationsList.add(new Social("website", socialObj, SECTION_SOCIAL_APPROVE +20 , SECTION_SOCIAL_APPROVE));
                }
            }
        }
        // sort ArrayList
        Collections.sort(relationsList, new SortSocial());

        return relationsList;
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

    @Override
    public void onClick(View view, int position, boolean isLongClick) {
        Log.i(TAG, "onClick view= " + view + " position= " + position);

        if(null != mOriginalRelationsMap){
            for (Object o : mOriginalRelationsMap.entrySet()) {
                Map.Entry pair = (Map.Entry) o;
                Log.d(TAG, "mOriginalRelationsMap = " + pair.getKey() + " = " + pair.getValue());
            }
        }

        if (view instanceof CheckBox) {
            CheckBox checkBox = (CheckBox) view;
            Log.i(TAG, "onClick checkBox= ");
            // do what you want with CheckBox

            if (checkBox.isChecked()) {
                switch (mRelationStatus) {
                    case RELATION_STATUS_SENDER:
                        //Approving request
                        Log.d(TAG, "Approving request onClick checkBox isChecked contacts= " + mRelationsList.get(position).getKey());
                        mRelationsMap.put(mRelationsList.get(position).getKey(), true);
                        break;
                    case RELATION_STATUS_RECEIVER:
                        //Canceling request
                        Log.d(TAG, "checkBox clicked mProfileViewModel cancelRequest");
                        break;
                    case RELATION_STATUS_STALKER:
                        //edit or Un-revealing
                        Log.d(TAG, "checkBox clicked mProfileViewModel edit or Un-reveal");
                        mRelationsMap.put(mRelationsList.get(position).getKey(), true);
                        break;
                    case RELATION_STATUS_FOLLOWED:
                        //Un-revealing, you can't edit cause you don't have permission
                        Log.d(TAG, "checkBox clicked mProfileViewModel Un-reveal");
                        break;
                    default:
                        // Showing request dialog
                        Log.d(TAG, "Request onClick checkBox isChecked contacts= " + mPrivateContactsList.get(position).getKey());
                        contactsMap.put(mPrivateContactsList.get(position).getKey(), false);
                        break;
                }
                /*Field[] fields = mRelations.getClass().getDeclaredFields();
                for (Field f : fields) {

                    //Log.d(TAG, "fields="+f.get);
                    if(f.getName().equals(mPrivateContactsList.get(position).getKey())){
                        Log.d(TAG, "fields=" + f.getName());
                    }
                    //getDynamicMethod(f.getName(), currentUser);
                }*/
                /*try {
                    Field f = mRelations.getClass().getDeclaredField(mPrivateContactsList.get(position).getKey());
                    Log.d(TAG, "field name=" + f.getName());
                    String fieldName = f.getName();
                    contactsMap.put(fieldName, false);
                    String capFieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

                    try {
                        Method method = mRelations.getClass().getMethod("set"+ capFieldName);
                        try {
                            method.invoke( true);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                    //setDynamicMethod(f.getName(), mRelations, true);

                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }*/
            } else {
                switch (mRelationStatus) {
                    case RELATION_STATUS_SENDER:
                        //Approving request
                        Log.d(TAG, "Denying request onClick checkBox not Checked contacts= " + mRelationsList.get(position).getKey());
                        mRelationsMap.put(mRelationsList.get(position).getKey(), false);
                    case RELATION_STATUS_RECEIVER:
                        //Canceling request
                        Log.d(TAG, "RevealButton clicked mProfileViewModel cancelRequest");
                        break;
                    case RELATION_STATUS_STALKER:
                        //Un revealing
                        Log.d(TAG, "RevealButton clicked mProfileViewModel Un reveal");
                        mRelationsMap.put(mRelationsList.get(position).getKey(), false);
                        break;
                    case RELATION_STATUS_FOLLOWED:
                        //Un-revealing, you can't edit cause you don't have permission
                        Log.d(TAG, "checkBox clicked mProfileViewModel Un-reveal");
                        break;
                    default:
                        // Showing request dialog
                        Log.d(TAG, "Request onClick checkBox not Checked");
                        contactsMap.remove(mPrivateContactsList.get(position).getKey());
                        break;
                }
            }

            if(null != contactsMap){
                for (Object o : contactsMap.entrySet()) {
                    Map.Entry pair = (Map.Entry) o;
                    Log.d(TAG, "contactsMap = " + pair.getKey() + " = " + pair.getValue());
                }
            }

            if(null != mRelationsMap){
                for (Object o : mRelationsMap.entrySet()) {
                    Map.Entry pair = (Map.Entry) o;
                    Log.d(TAG, "mRelationsMap = " + pair.getKey() + " = " + pair.getValue());
                }
            }

            if(null != mOriginalRelationsMap){
                for (Object o : mOriginalRelationsMap.entrySet()) {
                    Map.Entry pair = (Map.Entry) o;
                    Log.d(TAG, "mOriginalRelationsMap = " + pair.getKey() + " = " + pair.getValue());
                }
            }

        } else if (view instanceof Button) {
            //Button button = (Button) view;
            Log.i(TAG, "onClick Button= ");
            switch (position) {
                case 0:
                    // cancel clicked
                    Log.i(TAG, "onClick cancel Button");
                    //requestFragment.dismiss();
                    // get relations form database again to start all over
                    if(mRelationsMap != null){
                        mRelationsMap.clear();
                        mRelationsMap = new HashMap<>(mOriginalRelationsMap);
                    }
                    //mRelationsMap = mOriginalRelationsMap;
                    //mRelationsList = getRelationsList(mOriginalRelationsMap);
                    requestFragment.dismiss();
                    break;
                case 1:
                    // send clicked
                    Log.i(TAG, "onClick send Button");
                    requestFragment.dismiss();
                    sendRequest();
                    break;
            }
        } else {
            switch (position) {
                case 3:
                    // edit clicked
                    Log.i(TAG, "edit clicked");
                    Log.d(TAG, "RevealButton clicked mProfileViewModel Un reveal");
                    //contactsMap.clear(); // clear all previous selected check boxes
                    //mOriginalRelationsMap.clear(); // clear all previous selected check boxes
                    mRelationsList = getRelationsList(mRelationsMap);
                    Log.d(TAG, "RevealButton clicked RelationsList size= "+mRelationsList.size());

                    for (int i = 0; i < mRelationsList.size(); i++) {
                        Log.i(TAG, "mRelationsList = " + mRelationsList.get(i).getKey()+ " = "+ mRelationsList.get(i).getValue().getPublic());
                    }
                    showDialog(mRelationsList);
                    break;
                case 4:
                    // un-reveal clicked
                    Log.i(TAG, "un-reveal clicked");
                    //Delete the entire relation
                    mProfileViewModel.cancelRequest(mCurrentUserId, mUserId);
                    break;
                case 5:
                    // un-reveal but show confirmation dialog first
                    Log.i(TAG, "un-reveal clicked and show confirmation dialog");
                    //Show confirmation dialog
                    showConfirmationDialog();
                    break;
            }
        }
    }

    private void sendRequest() {

        for (Object o : contactsMap.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            Log.d(TAG, "contactsMap = " + pair.getKey() + " = " + pair.getValue());
        }

        Map<String, Object> senderValues;
        Map<String, Object> receiverValues;
        Map<String, Object> stalkerValues;
        Map<String, Object> followedValues;
        Relation sender;
        Relation receiver;
        Relation stalker;
        Relation followed;
        Map<String, Object> childUpdates = new HashMap<>();
        switch (mRelationStatus){
            case RELATION_STATUS_SENDER:
                //Approving request
                stalker = new Relation(RELATION_STATUS_STALKER , mRelationsMap);
                stalkerValues = stalker.toMap();

                followed = new Relation(RELATION_STATUS_FOLLOWED, mRelationsMap);
                followedValues = followed.toMap();

                childUpdates.put("/relations/" + mCurrentUserId + "/" + mUserId, stalkerValues);
                childUpdates.put("/relations/" + mUserId + "/" + mCurrentUserId, followedValues);
                break;
            case RELATION_STATUS_RECEIVER:
                //Canceling request
                Log.d(TAG, "RevealButton clicked mProfileViewModel cancelRequest");
                break;
            case RELATION_STATUS_STALKER:
                //Edit / Un-revealing
                Log.d(TAG, "RevealButton clicked mProfileViewModel Un reveal");
                stalker = new Relation(RELATION_STATUS_STALKER , mRelationsMap);
                stalkerValues = stalker.toMap();

                followed = new Relation(RELATION_STATUS_FOLLOWED, mRelationsMap);
                followedValues = followed.toMap();

                childUpdates.put("/relations/" + mCurrentUserId + "/" + mUserId, stalkerValues);
                childUpdates.put("/relations/" + mUserId + "/" + mCurrentUserId, followedValues);
                break;
            case RELATION_STATUS_FOLLOWED:
                //Un-revealing
                Log.d(TAG, "RevealButton clicked mProfileViewModel edit/Un reveal");

                stalker = new Relation(RELATION_STATUS_STALKER , mRelationsMap);
                stalkerValues = stalker.toMap();

                followed = new Relation(RELATION_STATUS_FOLLOWED, mRelationsMap);
                followedValues = followed.toMap();

                childUpdates.put("/relations/" + mCurrentUserId + "/" + mUserId, followedValues);
                childUpdates.put("/relations/" + mUserId + "/" + mCurrentUserId, stalkerValues);
                break;
            default:
                // Showing request dialog
                sender = new Relation(RELATION_STATUS_SENDER , contactsMap);
                senderValues = sender.toMap();

                receiver = new Relation(RELATION_STATUS_RECEIVER, contactsMap);
                receiverValues = receiver.toMap();

                childUpdates.put("/relations/" + mCurrentUserId + "/" + mUserId, receiverValues);
                //childUpdates.put("/relations/" + mCurrentUserId + "/" + mUserId + "/status/", "receiver");
                childUpdates.put("/relations/" + mUserId + "/" + mCurrentUserId, senderValues);
                //childUpdates.put("/relations/" + mUserId + "/" + mCurrentUserId + "/status/", "sender");

                break;
        }

        mDatabaseRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Write was successful!
                Log.i(TAG, "send request onSuccess");
                // ...
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Toast.makeText(getActivity(), R.string.send_request_error,
                                Toast.LENGTH_LONG).show();
                        // ...
                    }
                });

    }

    /*private void setDynamicMethod(String fieldName, Relation relations, Boolean value) {
        Method[] methods = relations.getClass().getMethods();

        for (Method method : methods) {
            if ((method.getName().startsWith("set")) && (method.getName().length() == (fieldName.length() + 3))) {
                if (method.getName().toLowerCase().endsWith(fieldName.toLowerCase())) {
                    // Method found, run it
                    Log.i(TAG, "setDynamicMethod method.getName()= "+method.getName());

                    if(value){
                        // set it to true
                        try {
                            method.invoke(relations,true);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }

                    }else{
                        // set it to false
                    try {
                        method.invoke(relations,false);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    }
                }
        }
    }*/
}
