package com.trackaty.chat.Adapters;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.trackaty.chat.Fragments.MainFragmentDirections;
import com.trackaty.chat.Interface.ItemClickListener;
import com.trackaty.chat.R;
import com.trackaty.chat.Utils.DateHelper;
import com.trackaty.chat.models.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UsersAdapter extends PagedListAdapter<User, UsersAdapter.ConcertViewHolder> {

    private final static String TAG = UsersAdapter.class.getSimpleName();
    private FirebaseUser currentUser;
    private String currentUserId;
    //private StateListDrawable placeholderList;
    private Drawable mPlaceholderDrawable;
    // A not static array list for all notifications sender avatar to update the broken avatars when fragment stops
    private List<User> brokenAvatarsList;// = new ArrayList<>();

    private static final String AVATAR_ORIGINAL_NAME = "original_avatar.jpg";
    private static final String COVER_ORIGINAL_NAME = "original_cover.jpg";
    private static final String AVATAR_THUMBNAIL_NAME = "avatar.jpg";
    private static final String COVER_THUMBNAIL_NAME = "cover.jpg";

    public UsersAdapter() {
        super(DIFF_CALLBACK);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            currentUserId = currentUser.getUid();
        }

        // Only create the static list if it's null
        if(brokenAvatarsList == null){
            brokenAvatarsList = new ArrayList<>();
            Log.d(TAG, "brokenAvatarsList is null. new ArrayList is created= " + brokenAvatarsList.size());
        }/*else{
            Log.d(TAG, "brokenAvatarsList is not null. size=  "+ brokenAvatarsList.size());
            if(brokenAvatarsList.size() >0){
                // Clear the list to start all over
                brokenAvatarsList.clear();
                Log.d(TAG, "brokenAvatarsList is cleared. size=  "+ brokenAvatarsList.size());
            }
        }*/
    }

    @NonNull
    @Override
    public ConcertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new ConcertViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ConcertViewHolder holder, final int position) {

        final User user = getItem(position);
        if (user != null) {
            //holder.bindTo(user);
            //Log.d(TAG, "mama  onBindViewHolder. users key"  +  user.getCreatedLong()+ "name: "+user.getName());
            // click listener using interface

            holder.setItemClickListener(new ItemClickListener(){

                @Override
                public void onClick(View view, int position, boolean isLongClick) {

                    if (view.getId() == R.id.user_image) { // only avatar is clicked
                        //mListener.onTextViewNameClick(view, getBindingAdapterPosition());
                        Log.i(TAG, "user avatar clicked= " + view.getId());
                        NavDirections ProfileDirection = MainFragmentDirections.actionMainToProfile(user.getKey());
                        //NavController navController = Navigation.findNavController(this, R.id.host_fragment);
                        //check if we are on Main Fragment not on complete Profile already
                        Navigation.findNavController(view).navigate(ProfileDirection);

                            /*Navigation.findNavController(this, R.id.host_fragment)
                            .navigate(directions);*/
                        //mListener.onTextViewNameClick(view, getBindingAdapterPosition());
                    } else {//-1 entire row is clicked
                        Log.i(TAG, "user row clicked= " + view.getId());
                        NavDirections MessageDirection = MainFragmentDirections.actionMainFragToMessagesFrag(null, user.getKey(), false);
                        //NavController navController = Navigation.findNavController(this, R.id.host_fragment);
                        //check if we are on Main Fragment not on complete Profile already
                        Navigation.findNavController(view).navigate(MessageDirection);

                            /*Navigation.findNavController(this, R.id.host_fragment)
                            .navigate(directions);*/
                    }

                }
            });

            /*holder.userAvatar.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "user Avatar= "+user.getKey());
                    NavDirections directions = MainFragmentDirections.actionMainToProfile(currentUser.getUid(), user.getKey(), user);
                    //NavController navController = Navigation.findNavController(this, R.id.host_fragment);

                    //check if we are on Main Fragment not on complete Profile already
                    Navigation.findNavController(view).navigate(directions);
                }
            });*/

            // user name text value
            if (null != user.getName()) {
                holder.userName.setText(user.getName());
            }else{
                holder.userName.setText(null);
            }

            // user biography text value
            if (null != user.getBiography()) {
                holder.userBio.setText(user.getBiography());
                holder.userBio.setVisibility(View.VISIBLE);
            }else{
                holder.userBio.setText(null);
                holder.userBio.setVisibility(View.GONE);
            }

            if (null != user.getAvatar()) {
                //final StateListDrawable placeholderList = (StateListDrawable) getContext().getResources().getDrawable(R.drawable.state_list_placeholder);
                holder.userAvatar.setImageResource(R.drawable.ic_round_account_filled_72);
                Picasso.get()
                        .load(user.getAvatar())
                        .placeholder(R.mipmap.ic_round_account_filled_72)
                        .error(R.drawable.ic_round_broken_image_72px)
                        .into(holder.userAvatar, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                // loading avatar succeeded, do nothing
                            }
                            @Override
                            public void onError(Exception e) {
                                // loading avatar failed, lets try to get the avatar from storage instead of database link
                                loadStorageImage(user, holder.userAvatar);
                            }
                        });

            }else{
                // end of user avatar
                holder.userAvatar.setImageResource(R.drawable.ic_round_account_filled_72);
            }


            // birthDate text value and icons
            if(null != user.getBirthDate()){
                holder.userAge.setVisibility(View.VISIBLE);
                holder.ageIcon.setVisibility(View.VISIBLE);

                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(user.getBirthDate());
                holder.userAge.setText(String.valueOf(DateHelper.getAge(c.getTime())));
                holder.ageIcon.setImageResource(R.drawable.ic_cake_black_24dp);
            }else{
                holder.userAge.setVisibility(View.GONE);
                holder.ageIcon.setVisibility(View.GONE);
            }// end of  birthDate text value and icons

            // horoscope text value and icons
            if(null != user.getHoroscope()){
                holder.horoscopeIcon.setVisibility(View.VISIBLE);
                switch (user.getHoroscope()) {
                    case "aries":
                        holder.horoscopeIcon.setImageResource(R.drawable.ic_aries_symbol);
                        break;
                    case "taurus":
                        holder.horoscopeIcon.setImageResource(R.drawable.ic_taurus_zodiac_symbol_of_bull_head_front);
                        break;
                    case"gemini":
                        holder.horoscopeIcon.setImageResource(R.drawable.ic_gemini_zodiac_symbol_of_two_twins_faces);
                        break;
                    case"cancer":
                        holder.horoscopeIcon.setImageResource(R.drawable.ic_cancer_astrological_sign_of_crab_silhouette_simple);
                        break;
                    case"leo":
                        holder.horoscopeIcon.setImageResource(R.drawable.ic_leo_astrological_sign);
                        break;
                    case"virgo":
                        holder.horoscopeIcon.setImageResource(R.drawable.ic_virgo_woman_head_shape_symbol);
                        break;
                    case"libra":
                        holder.horoscopeIcon.setImageResource(R.drawable.ic_libra_scale_balance_symbol);
                        break;
                    case"scorpio":
                        holder.horoscopeIcon.setImageResource(R.drawable.ic_scorpio_animal_shape_of_zodiac_simple);
                        break;
                    case"sagittarius":
                        holder.horoscopeIcon.setImageResource(R.drawable.ic_sagittarius_arch_and_arrow_symbol);
                        break;
                    case"capricorn":
                        holder.horoscopeIcon.setImageResource(R.drawable.ic_capricorn_goat_animal_shape_of_zodiac_sign);
                        break;
                    case"aquarius":
                        holder.horoscopeIcon.setImageResource(R.drawable.ic_aquarius_water_container_symbol);
                        break;
                    case"pisces":
                        holder.horoscopeIcon.setImageResource(R.drawable.ic_pisces_astrological_sign_symbol);
                        break;
                    default:
                        holder.horoscopeIcon.setVisibility(View.GONE);
                        break;
                }
            }else{
                holder.horoscopeIcon.setVisibility(View.GONE);
            }//end of horoscope text value and icons

            //gender text value and icons
            if(null != user.getGender()){
                holder.genderIcon.setVisibility(View.VISIBLE);
                switch (user.getGender()) {
                    case "male":
                        holder.genderIcon.setImageResource(R.drawable.ic_male);
                        break;
                    case "female":
                        holder.genderIcon.setImageResource(R.drawable.ic_female);
                        break;
                    case"transsexual":
                        holder.genderIcon.setImageResource(R.drawable.ic_transsexual);
                        break;
                    default:
                        holder.genderIcon.setVisibility(View.GONE);
                        break;
                }
            }else{
                holder.genderIcon.setVisibility(View.GONE);
            }// end of gender text value and icons

            //interestedIn text value and icons
            if(null != user.getInterestedIn()){
                holder.interestedIcon.setVisibility(View.VISIBLE);
                switch (user.getInterestedIn()) {
                    case "men":
                        holder.interestedIcon.setImageResource(R.drawable.ic_business_man);
                        break;
                    case "women":
                        holder.interestedIcon.setImageResource(R.drawable.ic_business_woman);
                        break;
                    case "both":
                        holder.interestedIcon.setImageResource(R.drawable.ic_wc_men_and_women_24dp);
                        break;
                    default:
                        holder.interestedIcon.setVisibility(View.GONE);
                        break;
                }
            }else{
                holder.interestedIcon.setVisibility(View.GONE);
            }// end of interestedIn text value and icons

            // relationship text value and icons
            if (null != user.getRelationship()) {
                holder.relationIcon.setVisibility(View.VISIBLE);
                switch (user.getRelationship()) {
                    case "single":
                        holder.relationIcon.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        break;
                    case "searching":
                        holder.relationIcon.setImageResource(R.drawable.ic_search_black_24dp);
                        break;
                    case "committed":
                        holder.relationIcon.setImageResource(R.drawable.ic_two_hearts);
                        break;
                    case "engaged":
                        holder.relationIcon.setImageResource(R.drawable.ic_hearts_rings_filled);
                        break;
                    case "married":
                        holder.relationIcon.setImageResource(R.drawable.ic_hearts_rings_filled);
                        break;
                    case "civil union":
                        holder.relationIcon.setImageResource(R.drawable.ic_hearts_rings_filled);
                        break;
                    case "domestic partnership":
                        holder.relationIcon.setImageResource(R.drawable.ic_two_hearts);
                        break;
                    case "open relationship":
                        holder.relationIcon.setImageResource(R.drawable.ic_two_hearts);
                        break;
                    case "open marriage":
                        holder.relationIcon.setImageResource(R.drawable.ic_hearts_rings_filled);
                        break;
                    case "separated":
                        holder.relationIcon.setImageResource(R.drawable.ic_broken_heart_filled);
                        break;
                    case "divorced":
                        holder.relationIcon.setImageResource(R.drawable.ic_broken_heart_filled);
                        break;
                    case "widowed":
                        holder.relationIcon.setImageResource(R.drawable.ic_broken_heart_filled);
                        break;
                    default:
                        holder.relationIcon.setVisibility(View.GONE);
                        break;
                }

            }else{
                holder.relationIcon.setVisibility(View.GONE);
                //end of relationship text value and icons
            }


        } /*else {
            // Null defines a placeholder item - PagedListAdapter automatically
            // invalidates this row when the actual object is loaded from the
            // database.
            //holder.clear();
        }*/
    }

    private void loadStorageImage(User user, ImageView avatar) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        storageRef.child("images/"+user.getKey() +"/"+ AVATAR_THUMBNAIL_NAME ).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Picasso.get()
                        .load(uri)
                        .placeholder(R.mipmap.ic_round_account_filled_72)
                        .error(R.drawable.ic_round_broken_image_72px)
                        .into(avatar);
                //updateAvatarUri(key, uri);
                // Add updated notification to brokenAvatarsList. the list will be used to update broken avatars when fragment stops
                user.setAvatar(String.valueOf(uri));
                brokenAvatarsList.add(user);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                avatar.setImageResource(R.drawable.ic_round_account_filled_72);
            }
        });
    }

    public List<User> getBrokenAvatarsList(){
        return brokenAvatarsList;
    }

    // clear sent messages list after updating the database
    public void clearBrokenAvatarsList(){
        brokenAvatarsList.clear();
    }


    // CALLBACK to calculate the difference between the old item and the new item
    private static final DiffUtil.ItemCallback<User> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<User>() {
                // User details may have changed if reloaded from the database,
                // but ID is fixed.

                // if the two items are the same
                @Override
                public boolean areItemsTheSame(User oldUser, User newUser) {
                    /*Log.d(TAG, " DIFF_CALLBACK areItemsTheSame " + (oldUser.getCreatedLong() == newUser.getCreatedLong()));
                    Log.d(TAG, " DIFF_CALLBACK areItemsTheSame keys= old: " + oldUser.getCreatedLong() +" new: "+ newUser.getCreatedLong());*/
                    //return oldUser.getCreatedLong() == newUser.getCreatedLong();
                    return oldUser.getKey().equals(newUser.getKey());
                    //return true;
                }

                // if the content of two items is the same
                @Override
                public boolean areContentsTheSame(User oldUser, User newUser) {
                   /* Log.d(TAG, "  DIFF_CALLBACK areContentsTheSame object " + (oldUser.equals(newUser)));
                    Log.d(TAG, "  DIFF_CALLBACK areContentsTheSame Names() " + (oldUser.getName().equals(newUser.getName())));
                    Log.d(TAG, "  DIFF_CALLBACK areContentsTheSame old name: " + oldUser.getName() + " new name: "+newUser.getName());
*/
                    // NOTE: if you use equals, your object must properly override Object#equals()
                    // Incorrectly returning false here will result in too many animations.
                    return oldUser.equals(newUser);
                    //return true;
                }
            };


    /// ViewHolder for trips list /////
    public class ConcertViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        View row;
        private TextView userName, userBio, userAge;
        private ImageView userAvatar, ageIcon,interestedIcon,  genderIcon, horoscopeIcon, relationIcon;
        ItemClickListener itemClickListener;


        public ConcertViewHolder(View itemView) {
            super(itemView);
            //itemView = row;

            row = itemView;
            userName = row.findViewById(R.id.user_name);
            userBio = row.findViewById(R.id.item_body);
            userAge  = row.findViewById(R.id.age_value);
            userAvatar =  row.findViewById(R.id.user_image);
            ageIcon =  row.findViewById(R.id.age_icon);
            genderIcon =  row.findViewById(R.id.gender_icon);
            interestedIcon =  row.findViewById(R.id.interested_icon);
            horoscopeIcon =  row.findViewById(R.id.horoscope_icon);
            relationIcon =  row.findViewById(R.id.relationship_icon);

            userAvatar.setOnClickListener(this);
            row.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if(itemClickListener != null && getBindingAdapterPosition() != RecyclerView.NO_POSITION){
                itemClickListener.onClick(view, getBindingAdapterPosition(), false);
                /*switch (view.getId()) {
                    case R.id.profile_image:
                        //mListener.onTextViewNameClick(view, getBindingAdapterPosition());
                        Log.i(TAG, "user avatar clicked= "+view.getId());
                        itemClickListener.onAvatarClick(view, getBindingAdapterPosition(), false);
                        break;
                    default://-1
                        //mListener.onTextViewNameClick(view, getBindingAdapterPosition());
                        Log.i(TAG, "user row clicked= "+view.getId());
                        itemClickListener.onRowClick(view, getBindingAdapterPosition(), false);
                        break;
                }*/
            }
        }

        // needed only if i want the listener to be inside the adapter
        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }
    }

}

