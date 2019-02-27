package com.trackaty.chat.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.trackaty.chat.R;
import com.trackaty.chat.Utils.DateHelper;
import com.trackaty.chat.models.User;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class UsersAdapter extends PagedListAdapter<User, UsersAdapter.ConcertViewHolder> {

    private final static String TAG = UsersAdapter.class.getSimpleName();

    public UsersAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ConcertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);

        return new ConcertViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ConcertViewHolder holder, final int position) {

        User user = getItem(position);
        if (user != null) {
            //holder.bindTo(user);
            //Log.d(TAG, "mama  onBindViewHolder. users key"  +  user.getCreatedLong()+ "name: "+user.getName());

            // user name text value
            if (null != user.getName()) {
                holder.userName.setText(user.getName());
            }

            if (null != user.getAvatar()) {
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
                    Log.d(TAG, " DIFF_CALLBACK areItemsTheSame " + (oldUser.getCreatedLong() == newUser.getCreatedLong()));
                    Log.d(TAG, " DIFF_CALLBACK areItemsTheSame keys= old: " + oldUser.getCreatedLong() +" new: "+ newUser.getCreatedLong());
                    return oldUser.getCreatedLong() == newUser.getCreatedLong();
                    //return true;
                }

                // if the content of two items is the same
                @Override
                public boolean areContentsTheSame(User oldUser, User newUser) {
                    Log.d(TAG, "  DIFF_CALLBACK areContentsTheSame object " + (oldUser.equals(newUser)));
                    Log.d(TAG, "  DIFF_CALLBACK areContentsTheSame Names() " + (oldUser.getName().equals(newUser.getName())));
                    Log.d(TAG, "  DIFF_CALLBACK areContentsTheSame old name: " + oldUser.getName() + " new name: "+newUser.getName());

                    // NOTE: if you use equals, your object must properly override Object#equals()
                    // Incorrectly returning false here will result in too many animations.
                    //TODO override equals method on User object
                    return oldUser.getName().equals(newUser.getName());
                    //return false;
                }
            };


    /// ViewHolder for trips list /////
    public class ConcertViewHolder extends RecyclerView.ViewHolder {

        View row;
        private TextView userName, userAge;
        private ImageView userAvatar, ageIcon, genderIcon, horoscopeIcon, relationIcon;

        public ConcertViewHolder(View itemView) {
            super(itemView);
            //itemView = row;

            row = itemView;
            userName = row.findViewById(R.id.user_name);
            userAge  = row.findViewById(R.id.age_value);
            userAvatar =  row.findViewById(R.id.profile_image);
            ageIcon =  row.findViewById(R.id.age_icon);
            genderIcon =  row.findViewById(R.id.gender_icon);
            horoscopeIcon =  row.findViewById(R.id.horoscope_icon);
            relationIcon =  row.findViewById(R.id.relationship_icon);

        }

    }

}

