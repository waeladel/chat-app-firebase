package com.trackaty.chat.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.trackaty.chat.R;
import com.trackaty.chat.Utils.DateHelper;
import com.trackaty.chat.models.Profile;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    private final static String TAG = ProfileAdapter.class.getSimpleName();

    public ArrayList<Profile> userDataArrayList;
    public Context context;


    public ProfileAdapter(Context context, ArrayList<Profile> userDataArrayList){
        this.userDataArrayList = userDataArrayList;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_info_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder called="+ userDataArrayList.get(position));

        switch (userDataArrayList.get(position).getSection()){
            case 1:
                holder.sectionHeader.setText(R.string.about_user_headline);
                Log.i(TAG, "about section="+ userDataArrayList.get(position).getKey());
                break;
            case 2:
                holder.sectionHeader.setText(R.string.user_work_school_headline);
                Log.i(TAG, "work education section="+ userDataArrayList.get(position).getKey());

                break;
            case 3:
                holder.sectionHeader.setText(R.string.user_habits_hobbies_headline);
                Log.i(TAG, "habits section="+ userDataArrayList.get(position).getKey());
                break;
        }

        // if not first item check if item above has the same header
        if (position > 0 && userDataArrayList.get(position - 1).getSection()
                == (userDataArrayList.get(position).getSection())) {
            holder.divider.setVisibility(View.GONE);
            holder.sectionHeader.setVisibility(View.GONE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
            holder.sectionHeader.setVisibility(View.VISIBLE);

        }

        // birthDate text value and icons
        if(userDataArrayList.get(position).getKey().equals("birthDate")){
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(Long.parseLong(userDataArrayList.get(position).getValue()));
            holder.itemValue.setText(context.getString(R.string.user_age_value, DateHelper.getAge(c.getTime())));
            holder.itemIcon.setImageResource(R.drawable.ic_cake_black_24dp);
        }

        //gender text value and icons
        if(userDataArrayList.get(position).getKey().equals("gender")){

            switch (userDataArrayList.get(position).getValue()) {
                case "male":
                    holder.itemValue.setText(context.getString(R.string.user_gender_value, context.getString(R.string.male)));
                    holder.itemIcon.setImageResource(R.drawable.ic_male);
                    break;
                case "female":
                    holder.itemValue.setText(context.getString(R.string.user_gender_value, context.getString(R.string.female)));
                    holder.itemIcon.setImageResource(R.drawable.ic_female);
                    break;
                case"transsexual":
                    holder.itemValue.setText(context.getString(R.string.user_gender_value, context.getString(R.string.transsexual)));
                    holder.itemIcon.setImageResource(R.drawable.ic_transsexual);
                    break;
                default:
                    holder.itemValue.setText(R.string.not_specified);
                    holder.itemIcon.setVisibility(View.INVISIBLE);
                    break;
            }
        }// end of gender text value and icons

        // nationality text value and icons
        if(userDataArrayList.get(position).getKey().equals("nationality")){
            holder.itemValue.setText(context.getString(R.string.user_country_value, userDataArrayList.get(position).getValue()));
            holder.itemIcon.setImageResource(R.drawable.ic_flag_black_24dp);
        }

        // hometown text value and icons
        if(userDataArrayList.get(position).getKey().equals("hometown")){
            holder.itemValue.setText(context.getString(R.string.user_hometown_value, userDataArrayList.get(position).getValue()));
            holder.itemIcon.setImageResource(R.drawable.ic_location_on_black_24dp);
        }

        // horoscope text value and icons
        if(userDataArrayList.get(position).getKey().equals("horoscope")){
            switch (userDataArrayList.get(position).getValue()) {
                case "aries":
                    holder.itemValue.setText(R.string.aries);
                    holder.itemIcon.setImageResource(R.drawable.ic_aries_zodiac_symbol_of_frontal_goat_head);
                    break;
                case "taurus":
                    holder.itemValue.setText(R.string.taurus);
                    holder.itemIcon.setImageResource(R.drawable.ic_taurus_zodiac_symbol_of_bull_head_front);
                    break;
                case"gemini":
                    holder.itemValue.setText(R.string.gemini);
                    holder.itemIcon.setImageResource(R.drawable.ic_gemini_zodiac_symbol_of_two_twins_faces);
                    break;
                case"cancer":
                    holder.itemValue.setText(R.string.cancer);
                    holder.itemIcon.setImageResource(R.drawable.ic_cancer_astrological_sign_of_crab_silhouette);
                    break;
                case"leo":
                    holder.itemValue.setText(R.string.leo);
                    holder.itemIcon.setImageResource(R.drawable.ic_leo_astrological_sign);
                    break;
                case"virgo":
                    holder.itemValue.setText(R.string.virgo);
                    holder.itemIcon.setImageResource(R.drawable.ic_virgo_woman_head_shape_symbol);
                    break;
                case"libra":
                    holder.itemValue.setText(R.string.libra);
                    holder.itemIcon.setImageResource(R.drawable.ic_libra_scale_balance_symbol);
                    break;
                case"scorpio":
                    holder.itemValue.setText(R.string.scorpio);
                    holder.itemIcon.setImageResource(R.drawable.ic_scorpio_vertical_animal_shape_of_zodiac_symbol);
                    break;
                case"sagittarius":
                    holder.itemValue.setText(R.string.sagittarius);
                    holder.itemIcon.setImageResource(R.drawable.ic_sagittarius_arch_and_arrow_symbol);
                    break;
                case"capricorn":
                    holder.itemValue.setText(R.string.capricorn);
                    holder.itemIcon.setImageResource(R.drawable.ic_capricorn_goat_animal_shape_of_zodiac_sign);
                    break;
                case"aquarius":
                    holder.itemValue.setText(R.string.aquarius);
                    holder.itemIcon.setImageResource(R.drawable.ic_aquarius_water_container_symbol);
                    break;
                case"pisces":
                    holder.itemValue.setText(R.string.pisces);
                    holder.itemIcon.setImageResource(R.drawable.ic_pisces_astrological_sign_symbol);
                    break;
                default:
                    holder.itemValue.setText(R.string.horoscope_not_specified);
                    holder.itemIcon.setVisibility(View.INVISIBLE);
                    break;
            }
        }//end of horoscope text value and icons

        // lives text value and icons
        if(userDataArrayList.get(position).getKey().equals("lives")){
            holder.itemValue.setText(context.getString(R.string.user_lives_in_value, userDataArrayList.get(position).getValue()));
            holder.itemIcon.setImageResource(R.drawable.ic_home_black_24dp);
        }

        // politics text value and icons
        if(userDataArrayList.get(position).getKey().equals("politics")){
            holder.itemValue.setText(context.getString(R.string.user_politics_value, userDataArrayList.get(position).getValue()));
            holder.itemIcon.setImageResource(R.drawable.ic_voting);
        }

        // religion text value and icons
        if(userDataArrayList.get(position).getKey().equals("religion")){
            holder.itemValue.setText(context.getString(R.string.user_religion_value, userDataArrayList.get(position).getValue()));
            holder.itemIcon.setImageResource(R.drawable.ic_praying_hands);
        }

        // work text value and icons
        if(userDataArrayList.get(position).getKey().equals("work")){
            holder.itemValue.setText(context.getString(R.string.user_work_value, userDataArrayList.get(position).getValue()));
            holder.itemIcon.setImageResource(R.drawable.ic_work_black_24dp);
        }
        // college text value and icons
        if(userDataArrayList.get(position).getKey().equals("college")){
            holder.itemValue.setText(context.getString(R.string.user_college_value, userDataArrayList.get(position).getValue()));
            holder.itemIcon.setImageResource(R.drawable.ic_school_black_24dp);
        }

    // college text value and icons
        if(userDataArrayList.get(position).getKey().equals("school")){
        holder.itemValue.setText(context.getString(R.string.user_school_value, userDataArrayList.get(position).getValue()));
        holder.itemIcon.setImageResource(R.drawable.ic_school_black_24dp);
    }

        // smoke text value and icons
        if(userDataArrayList.get(position).getKey().equals("smoke")){
            holder.itemIcon.setImageResource(R.drawable.ic_smoking_rooms_black_24dp);
            if(userDataArrayList.get(position).getValue().equals("true")){
                holder.itemValue.setText(context.getString(R.string.user_smoke_value, context.getString(R.string.yes)));
            }else{
                holder.itemValue.setText(context.getString(R.string.user_smoke_value, context.getString(R.string.no)));
            }
        }

        // shisha text value and icons
        if(userDataArrayList.get(position).getKey().equals("shisha")){
            holder.itemIcon.setImageResource(R.drawable.ic_shisha);
            if(userDataArrayList.get(position).getValue().equals("true")){
                holder.itemValue.setText(context.getString(R.string.user_water_pipe_value, context.getString(R.string.yes)));
            }else{
                holder.itemValue.setText(context.getString(R.string.user_water_pipe_value, context.getString(R.string.no)));
            }
        }

        // drugs text value and icons
        if(userDataArrayList.get(position).getKey().equals("drugs")){
            holder.itemIcon.setImageResource(R.drawable.ic_injection);
            if(userDataArrayList.get(position).getValue().equals("true")){
                holder.itemValue.setText(context.getString(R.string.user_drugs_value, context.getString(R.string.yes)));
            }else{
                holder.itemValue.setText(context.getString(R.string.user_drugs_value, context.getString(R.string.no)));
            }
        }

        // drink text value and icons
        if(userDataArrayList.get(position).getKey().equals("drink")){
            holder.itemIcon.setImageResource(R.drawable.ic_drinking_24dp);
            if(userDataArrayList.get(position).getValue().equals("true")){
                holder.itemValue.setText(context.getString(R.string.user_drink_value, context.getString(R.string.yes)));
            }else{
                holder.itemValue.setText(context.getString(R.string.user_drink_value, context.getString(R.string.no)));
            }
        }

        // athlete text value and icons
        if(userDataArrayList.get(position).getKey().equals("athlete")){
            holder.itemIcon.setImageResource(R.drawable.ic_fitness_athlete_24dp);
            if(userDataArrayList.get(position).getValue().equals("true")){
                holder.itemValue.setText(context.getString(R.string.user_athlete_value, context.getString(R.string.yes)));
            }else{
                holder.itemValue.setText(context.getString(R.string.user_athlete_value, context.getString(R.string.no)));
            }
        }
        // gamer text value and icons
        if(userDataArrayList.get(position).getKey().equals("gamer")){
            holder.itemIcon.setImageResource(R.drawable.ic_games_black_24dp);
            if(userDataArrayList.get(position).getValue().equals("true")){
                holder.itemValue.setText(context.getString(R.string.user_gamer_value, context.getString(R.string.yes)));
            }else{
                holder.itemValue.setText(context.getString(R.string.user_gamer_value, context.getString(R.string.no)));
            }
        }
        // travel text value and icons
        if(userDataArrayList.get(position).getKey().equals("travel")){
            holder.itemIcon.setImageResource(R.drawable.ic_flight_24dp);
            if(userDataArrayList.get(position).getValue().equals("true")){
                holder.itemValue.setText(context.getString(R.string.user_travel_value, context.getString(R.string.yes)));
            }else{
                holder.itemValue.setText(context.getString(R.string.user_travel_value, context.getString(R.string.no)));
            }
        }
        // cook text value and icons
        if(userDataArrayList.get(position).getKey().equals("cook")){
            holder.itemIcon.setImageResource(R.drawable.ic_cook_24dp);
            if(userDataArrayList.get(position).getValue().equals("true")){
                holder.itemValue.setText(context.getString(R.string.user_cook_value, context.getString(R.string.yes)));
            }else{
                holder.itemValue.setText(context.getString(R.string.user_cook_value, context.getString(R.string.no)));
            }
        }
        // read text value and icons
        if(userDataArrayList.get(position).getKey().equals("read")){
            holder.itemIcon.setImageResource(R.drawable.ic_library_book_24dp);
            if(userDataArrayList.get(position).getValue().equals("true")){
                holder.itemValue.setText(context.getString(R.string.user_read_value, context.getString(R.string.yes)));
            }else{
                holder.itemValue.setText(context.getString(R.string.user_read_value, context.getString(R.string.no)));
            }
        }

       /* holder.itemValue.setText(mProfileDataArrayList.get(position).getKey()+" value= "
                +mProfileDataArrayList.get(position).getValue());*/


        Log.i(TAG, "onBindViewHolder get value="+ userDataArrayList.get(position).getValue());

        //holder.itemValue.setText(mProfileDataArrayList.indexOf(position));
    }

    @Override
    public int getItemCount() {
        //the size of the list
        return  userDataArrayList.size();
        //return  10;
    }

    /// ViewHolder for trips list /////
    public class ViewHolder extends RecyclerView.ViewHolder {

        View row;
        private TextView itemValue, sectionHeader;
        private ImageView itemIcon;
        private View divider;


        public ViewHolder(View itemView) {
            super(itemView);
            //itemView = row;

            row = itemView;
            itemValue = row.findViewById(R.id.item_value);
            itemIcon = row.findViewById(R.id.item_icon);
            sectionHeader = row.findViewById(R.id.section_header);
            divider = row.findViewById(R.id.section_divider);

        }

    }


}
