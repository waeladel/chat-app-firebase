package com.trackaty.chat.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.trackaty.chat.Fragments.MainFragmentDirections;
import com.trackaty.chat.Interface.ItemClickListener;
import com.trackaty.chat.R;
import com.trackaty.chat.Utils.DateHelper;
import com.trackaty.chat.models.User;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class UsersAdapter extends PagedListAdapter<User, UsersAdapter.ConcertViewHolder> {

    private final static String TAG = UsersAdapter.class.getSimpleName();
    private FirebaseUser currentUser;
    private String currentUserId;

    public UsersAdapter() {
        super(DIFF_CALLBACK);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            currentUserId = currentUser.getUid();
        }
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
                    switch (view.getId()) {
                        case R.id.user_image: // only avatar is clicked
                            //mListener.onTextViewNameClick(view, getAdapterPosition());
                            Log.i(TAG, "user avatar clicked= "+view.getId());
                            NavDirections ProfileDirection = MainFragmentDirections.actionMainToProfile( user.getKey());
                            //NavController navController = Navigation.findNavController(this, R.id.host_fragment);
                            //check if we are on Main Fragment not on complete Profile already
                            Navigation.findNavController(view).navigate(ProfileDirection);

                            /*Navigation.findNavController(this, R.id.host_fragment)
                            .navigate(directions);*/
                            break;
                        default://-1 entire row is clicked
                            //mListener.onTextViewNameClick(view, getAdapterPosition());
                            Log.i(TAG, "user row clicked= "+view.getId());
                            NavDirections MessageDirection = MainFragmentDirections.actionMainFragToMessagesFrag(null, user.getKey(),false);
                            //NavController navController = Navigation.findNavController(this, R.id.host_fragment);
                            //check if we are on Main Fragment not on complete Profile already
                            Navigation.findNavController(view).navigate(MessageDirection);

                            /*Navigation.findNavController(this, R.id.host_fragment)
                            .navigate(directions);*/
                            break;
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
                holder.userName.setText(user.getName()+user.getKey());
            }else{
                holder.userName.setText(null);
            }

            // user biography text value
            if (null != user.getBiography()) {
                holder.userBio.setText(user.getBiography());
            }else{
                holder.userBio.setText(null);
            }

            if (null != user.getAvatar()) {
                holder.userAvatar.setImageResource(R.drawable.ic_user_account_grey_white);
                Picasso.get()
                        .load(user.getAvatar())
                        .placeholder(R.drawable.ic_user_account_grey_white)
                        .error(R.drawable.ic_broken_image)
                        .into(holder.userAvatar);
            }else{
                // end of user avatar
                holder.userAvatar.setImageResource(R.drawable.ic_user_account_grey_white);
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
                holder.userAge.setVisibility(View.INVISIBLE);
                holder.ageIcon.setVisibility(View.INVISIBLE);
            }// end of  birthDate text value and icons

            // horoscope text value and icons
            if(null != user.getHoroscope()){
                holder.horoscopeIcon.setVisibility(View.VISIBLE);
                switch (user.getHoroscope()) {
                    case "aries":
                        holder.horoscopeIcon.setImageResource(R.drawable.ic_aries_zodiac_symbol_of_frontal_goat_head);
                        break;
                    case "taurus":
                        holder.horoscopeIcon.setImageResource(R.drawable.ic_taurus_zodiac_symbol_of_bull_head_front);
                        break;
                    case"gemini":
                        holder.horoscopeIcon.setImageResource(R.drawable.ic_gemini_zodiac_symbol_of_two_twins_faces);
                        break;
                    case"cancer":
                        holder.horoscopeIcon.setImageResource(R.drawable.ic_cancer_astrological_sign_of_crab_silhouette);
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
                        holder.horoscopeIcon.setImageResource(R.drawable.ic_scorpio_vertical_animal_shape_of_zodiac_symbol);
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
                        holder.horoscopeIcon.setVisibility(View.INVISIBLE);
                        break;
                }
            }else{
                holder.horoscopeIcon.setVisibility(View.INVISIBLE);
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
                        holder.genderIcon.setVisibility(View.INVISIBLE);
                        break;
                }
            }else{
                holder.genderIcon.setVisibility(View.INVISIBLE);
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
                        holder.interestedIcon.setVisibility(View.INVISIBLE);
                        break;
                }
            }else{
                holder.interestedIcon.setVisibility(View.INVISIBLE);
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
                        holder.relationIcon.setImageResource(R.drawable.ic_hearts_rings);
                        break;
                    case "married":
                        holder.relationIcon.setImageResource(R.drawable.ic_hearts_rings);
                        break;
                    case "civil union":
                        holder.relationIcon.setImageResource(R.drawable.ic_hearts_rings);
                        break;
                    case "domestic partnership":
                        holder.relationIcon.setImageResource(R.drawable.ic_two_hearts);
                        break;
                    case "open relationship":
                        holder.relationIcon.setImageResource(R.drawable.ic_two_hearts);
                        break;
                    case "open marriage":
                        holder.relationIcon.setImageResource(R.drawable.ic_hearts_rings);
                        break;
                    case "separated":
                        holder.relationIcon.setImageResource(R.drawable.ic_broken_heart);
                        break;
                    case "divorced":
                        holder.relationIcon.setImageResource(R.drawable.ic_broken_heart);
                        break;
                    case "widowed":
                        holder.relationIcon.setImageResource(R.drawable.ic_broken_heart);
                        break;
                    default:
                        holder.relationIcon.setVisibility(View.INVISIBLE);
                        break;
                }

            }else{
                holder.relationIcon.setVisibility(View.INVISIBLE);
                //end of relationship text value and icons
            }


        } else {
            // Null defines a placeholder item - PagedListAdapter automatically
            // invalidates this row when the actual object is loaded from the
            // database.
            //holder.clear();
        }
    }


    // CALLBACK to calculate the difference between the old item and the new item
    public static final DiffUtil.ItemCallback<User> DIFF_CALLBACK =
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
                    //TODO override equals method on User object
                    return oldUser.getName().equals(newUser.getName());
                    //return false;
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
            userBio = row.findViewById(R.id.notification_text);
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
            if(itemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION){
                itemClickListener.onClick(view, getAdapterPosition(), false);
                /*switch (view.getId()) {
                    case R.id.profile_image:
                        //mListener.onTextViewNameClick(view, getAdapterPosition());
                        Log.i(TAG, "user avatar clicked= "+view.getId());
                        itemClickListener.onAvatarClick(view, getAdapterPosition(), false);
                        break;
                    default://-1
                        //mListener.onTextViewNameClick(view, getAdapterPosition());
                        Log.i(TAG, "user row clicked= "+view.getId());
                        itemClickListener.onRowClick(view, getAdapterPosition(), false);
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

