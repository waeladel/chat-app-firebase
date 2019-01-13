package com.trackaty.chat.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.trackaty.chat.R;
import com.trackaty.chat.models.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private final static String TAG = ProfileFragment.class.getSimpleName();

    public String userId;
    private Button mSeeMoreButton;

    // [START declare_database_ref]
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUserRef;
    private ImageView mCover, mAvatar , mInterestedIcon , mmRelationshipIcon;
    private TextView mUserName, mUserBio , mlovedBy
            , mPickUpNum, mRelationship, mInterested;


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
        mCover = (ImageView) fragView.findViewById(R.id.coverImage);
        mAvatar = (ImageView) fragView.findViewById(R.id.profile_image);
        mUserName = (TextView) fragView.findViewById(R.id.user_name_text);
        mUserBio = (TextView) fragView.findViewById(R.id.user_bio_text);
        mlovedBy = (TextView) fragView.findViewById(R.id.user_loved_by_value);
        mPickUpNum = (TextView) fragView.findViewById(R.id.pick_up_value);
        mRelationship = (TextView) fragView.findViewById(R.id.user_relationship_value);
        mInterested = (TextView) fragView.findViewById(R.id.user_interested_value);
        mInterestedIcon = (ImageView) fragView.findViewById(R.id.interested_in_icon);
        mmRelationshipIcon = (ImageView) fragView.findViewById(R.id.relationship_icon);


        if(getArguments() != null){
            userId = ProfileFragmentArgs.fromBundle(getArguments()).getUserId();
            Log.d(TAG, "userId= " + userId);

            // [START initialize_database_ref]
            mDatabaseRef = FirebaseDatabase.getInstance().getReference();
            mUserRef = mDatabaseRef.child("users").child(userId);

            mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // [START_EXCLUDE]
                    if (dataSnapshot.exists()) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null){

                            if(user.getCoverImage() != null){
                                Picasso.get()
                                        .load(user.getCoverImage())
                                        .placeholder(R.drawable.ic_picture_gallery_white )
                                        .error(R.drawable.ic_broken_image)
                                        .into(mCover);
                            }

                            if(user.getAvatar() != null){
                                Picasso.get()
                                        .load(user.getAvatar())
                                        .placeholder(R.drawable.ic_user_account_grey_white )
                                        .error(R.drawable.ic_broken_image)
                                        .into(mAvatar);
                            }

                            if(user.getName() != null){
                                mUserName.setText(user.getName());
                                mSeeMoreButton.setText(getString(R.string.see_more_about_button, user.getName()));
                            }else{
                                mSeeMoreButton.setText(R.string.see_more_button);
                            }

                            if(user.getBiography() != null){
                                mUserBio.setText(user.getBiography());
                            }

                            mlovedBy.setText(getString(R.string.user_loved_by, user.getLoveCounter()));
                            mPickUpNum.setText(getString(R.string.user_pickedup_by, user.getPickupCounter()));

                            //mRelationship.setText(getString(R.string.user_relationship_value, user.getRelationship()));

                            if(user.getRelationship() != null){
                                switch (user.getRelationship()) {
                                    case "single":
                                        mRelationship.setText(R.string.single);
                                        mmRelationshipIcon.setImageResource(R.drawable.ic_favorite_border_black_24dp);
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

                            if(user.getInterestedIn() != null){
                                switch (user.getInterestedIn()) {
                                    case "women":
                                        mInterested.setText(R.string.women);
                                        mInterestedIcon.setImageResource(R.drawable.ic_business_woman);
                                        break;
                                    case "men":
                                        mInterested.setText(R.string.men);
                                        mInterestedIcon.setImageResource(R.drawable.ic_business_man);
                                        break;
                                    case "Both":
                                        mInterested.setText(R.string.both_men_women);
                                        mInterestedIcon.setImageResource(R.drawable.ic_men_and_women_toilet);
                                        break;
                                    default:
                                        mInterested.setText(R.string.not_specified);
                                        mInterestedIcon.setVisibility(View.INVISIBLE);
                                        break;
                                }

                            }

                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    // [START_EXCLUDE]
                    //setEditingEnabled(true);
                    // [END_EXCLUDE]
                }
            });
            // [END single_value_read]
        }

        mSeeMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "SeeMoreButton id clicked= ");
                NavDirections direction = ProfileFragmentDirections.actionProfileToMoreProfile(userId);
                NavController navController = Navigation.findNavController(v);
                navController.navigate(direction);

            }
        });

        return fragView;
    }


}
