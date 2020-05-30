package com.trackaty.chat.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.trackaty.chat.Fragments.MoreProfileFragment;
import com.trackaty.chat.Interface.ItemClickListener;
import com.trackaty.chat.R;
import com.trackaty.chat.Utils.DateHelper;
import com.trackaty.chat.ViewModels.MoreProfileViewModel;
import com.trackaty.chat.models.Profile;
import com.trackaty.chat.models.Relation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static String TAG = ProfileAdapter.class.getSimpleName();


    private  static final int SECTION_SOCIAL = 500;
    private  static final int SECTION_ABOUT =  600;
    private  static final int SECTION_WORK = 700;
    private  static final int SECTION_HABITS = 800;

    private ArrayList<Profile> userDataArrayList;
    // Map to hold user selections results
    private MutableLiveData<Map<String, Boolean>> mRelationsMap = new MutableLiveData<>();

    public MoreProfileFragment fragmentContext;
    private MoreProfileViewModel mMoreProfileViewModel;
    private MutableLiveData<Relation>  mRelation; //= new MutableLiveData<>();

    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private String currentUserId = currentUser != null ? currentUser.getUid() : null;

    private ItemClickListener itemClickListener;


    public ProfileAdapter(final MoreProfileFragment fragmentContext, ArrayList<Profile> userDataArrayList, String userID, ItemClickListener itemClickListener){
        this.userDataArrayList = userDataArrayList;
        this.fragmentContext = fragmentContext;
        this.itemClickListener = itemClickListener;
        Log.d(TAG, "ProfileAdapter init relation= ");


        // get relations with selected user if any
        mMoreProfileViewModel = new ViewModelProvider(fragmentContext).get(MoreProfileViewModel.class);
        // get relations with selected user if any
        mMoreProfileViewModel.getRelation(currentUserId, userID).observe(fragmentContext, new Observer<Relation>() {
            @Override
            public void onChanged(Relation relation) {
                if (relation != null){
                    // Relation exist
                    Log.i(TAG, "onChanged mProfileViewModel relation not null " +relation);
                    if(mRelation == null){
                        mRelation = new MutableLiveData<>();
                        mRelation.setValue(relation);
                    }else{
                        mRelation.setValue(relation);
                    }
                    mRelation.observe(fragmentContext, new Observer<Relation>() {
                        @Override
                        public void onChanged(Relation relation) {
                            mRelationsMap.setValue(relation.getContacts()) ;
                        }
                    });

                    //Log.d(TAG, "onChanged relation Status= " + mRelation.getStatus() + " size= " + mRelation.getContacts().size());
                }

            }
        });// end of getRelation ViewModel
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == SECTION_SOCIAL){
            View ButtonView = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_social_button_item, parent, false);
            return new ButtonsViewHolder(ButtonView, itemClickListener);
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_info_item, parent, false);
            return new ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        //Log.i(TAG, "onBindViewHolder called="+ userDataArrayList.get(position));

        if (holder instanceof ButtonsViewHolder){
            final ButtonsViewHolder buttonHolder = (ButtonsViewHolder) holder;

            buttonHolder.sectionHeader.setText(R.string.user_social_headline);
            Log.d(TAG, "social section="+ userDataArrayList.get(position).getKey());

            // if not first item check if item above has the same header
            if (position > 0 && userDataArrayList.get(position - 1).getSection()
                    == (userDataArrayList.get(position).getSection())) {
                buttonHolder.divider.setVisibility(View.GONE);
                buttonHolder.sectionHeader.setVisibility(View.GONE);
            } else {
                buttonHolder.divider.setVisibility(View.VISIBLE);
                buttonHolder.sectionHeader.setVisibility(View.VISIBLE);
            }

            switch (userDataArrayList.get(position).getKey()) {
                case "phone":
                    buttonHolder.itemValue.setCompoundDrawablesWithIntrinsicBounds(fragmentContext.getResources().getDrawable(R.drawable.ic_phone_black_24dp) , null, null, null);
                    break;
                case "facebook":
                    buttonHolder.itemValue.setCompoundDrawablesWithIntrinsicBounds(fragmentContext.getResources().getDrawable(R.drawable.ic_facebook) , null, null, null);
                    break;
                case "instagram":
                    buttonHolder.itemValue.setCompoundDrawablesWithIntrinsicBounds(fragmentContext.getResources().getDrawable(R.drawable.ic_instagram) , null, null, null);
                    break;
                case "twitter":
                    buttonHolder.itemValue.setCompoundDrawablesWithIntrinsicBounds(fragmentContext.getResources().getDrawable(R.drawable.ic_twitter) , null, null, null);
                    break;
                case "snapchat":
                    buttonHolder.itemValue.setCompoundDrawablesWithIntrinsicBounds(fragmentContext.getResources().getDrawable(R.drawable.ic_snapchat_simple) , null, null, null);
                    break;
                case "tumblr":
                    buttonHolder.itemValue.setCompoundDrawablesWithIntrinsicBounds(fragmentContext.getResources().getDrawable(R.drawable.ic_tumblr) , null, null, null);
                    break;
                case "pubg":
                    buttonHolder.itemValue.setCompoundDrawablesWithIntrinsicBounds(fragmentContext.getResources().getDrawable(R.mipmap.ic_pubg_text) , null, null, null);
                    //buttonHolder.itemValue.setEnabled(false);
                    break;
                case "vk":
                    buttonHolder.itemValue.setCompoundDrawablesWithIntrinsicBounds(fragmentContext.getResources().getDrawable(R.drawable.ic_vk_social_logotype) , null, null, null);
                    break;
                case "askfm":
                    buttonHolder.itemValue.setCompoundDrawablesWithIntrinsicBounds(fragmentContext.getResources().getDrawable(R.drawable.ic_askfm) , null, null, null);
                    break;
                case "curiouscat":
                    buttonHolder.itemValue.setCompoundDrawablesWithIntrinsicBounds(fragmentContext.getResources().getDrawable(R.drawable.ic_curiouscat) , null, null, null);
                    break;
                case "saraha":
                    buttonHolder.itemValue.setCompoundDrawablesWithIntrinsicBounds(fragmentContext.getResources().getDrawable(R.drawable.ic_saraha_hollow) , null, null, null);
                    break;
                case "pinterest":
                    buttonHolder.itemValue.setCompoundDrawablesWithIntrinsicBounds(fragmentContext.getResources().getDrawable(R.drawable.ic_pinterest) , null, null, null);
                    break;
                case "soundcloud":
                    buttonHolder.itemValue.setCompoundDrawablesWithIntrinsicBounds(fragmentContext.getResources().getDrawable(R.mipmap.ic_soundcloud) , null, null, null);
                    break;
                case "spotify":
                    buttonHolder.itemValue.setCompoundDrawablesWithIntrinsicBounds(fragmentContext.getResources().getDrawable(R.drawable.ic_spotify_logo_simple) , null, null, null);
                    break;
                case "anghami":
                    buttonHolder.itemValue.setCompoundDrawablesWithIntrinsicBounds(fragmentContext.getResources().getDrawable(R.drawable.ic_anghami_black) , null, null, null);
                    break;
                case "twitch":
                    buttonHolder.itemValue.setCompoundDrawablesWithIntrinsicBounds(fragmentContext.getResources().getDrawable(R.drawable.ic_twitch) , null, null, null);
                    break;
                case "youtube":
                    buttonHolder.itemValue.setCompoundDrawablesWithIntrinsicBounds(fragmentContext.getResources().getDrawable(R.drawable.ic_youtube) , null, null, null);
                    break;
                case "linkedIn":
                    buttonHolder.itemValue.setCompoundDrawablesWithIntrinsicBounds(fragmentContext.getResources().getDrawable(R.drawable.ic_linkedin) , null, null, null);
                    break;
                case "wikipedia":
                    buttonHolder.itemValue.setCompoundDrawablesWithIntrinsicBounds(fragmentContext.getResources().getDrawable(R.drawable.ic_wikipedia_logo) , null, null, null);
                    break;
                case "website":
                    buttonHolder.itemValue.setCompoundDrawablesWithIntrinsicBounds(fragmentContext.getResources().getDrawable(R.drawable.ic_website_global) , null, null, null);
                    break;

            }


            // check if user's contacts are public or private
            // If private, check relation between these two users
            if(userDataArrayList.get(position).getSocial().getPublic()){
                    //It's a public contact already
                    Log.d(TAG, "It's a public contact already "+ userDataArrayList.get(position).getKey());
                    buttonHolder.itemValue.setText(userDataArrayList.get(position).getSocial().getUrl());
                    buttonHolder.itemValue.setEnabled(true);
                    buttonHolder.itemValue.setClickable(true);
                }else{
                    //It's a private contact, check if there is a relation between these two
                    Log.d(TAG, "It's a private contact "+ userDataArrayList.get(position).getKey() );
                    if(mRelation != null){
                        // There is a relation established between these two users
                        Log.d(TAG, "There is a relation established between these two users "+ userDataArrayList.get(position).getKey());
                        //mRelationsMap.put()
                        //relation.getContacts().get(userDataArrayList.get(position).getKey()).;
                        mRelationsMap.observe(fragmentContext, new Observer<Map<String, Boolean>>() {
                            @Override
                            public void onChanged(Map<String, Boolean> stringBooleanMap) {
                                Boolean relationValue=  stringBooleanMap.get(userDataArrayList.get(position).getKey());
                                if(null != relationValue && relationValue){
                                    // This private relation is approved
                                    Log.d(TAG, "This private relation is approved "+ userDataArrayList.get(position).getKey());
                                    buttonHolder.itemValue.setText(userDataArrayList.get(position).getSocial().getUrl());
                                    buttonHolder.itemValue.setEnabled(true);
                                    buttonHolder.itemValue.setClickable(true);
                                }else {
                                    Log.d(TAG, "There is no relation or it's not approved, set button to  private contact "+ userDataArrayList.get(position).getKey());
                                    buttonHolder.itemValue.setText(R.string.user_social_private_button);
                                    buttonHolder.itemValue.setEnabled(false);
                                    buttonHolder.itemValue.setClickable(false);
                                }
                            }
                        });
                    }else{
                        // There is no relation
                        Log.i(TAG, "There is no relation, set button to  private contact");
                        buttonHolder.itemValue.setText(R.string.user_social_private_button);
                        buttonHolder.itemValue.setEnabled(false);
                        buttonHolder.itemValue.setClickable(false);
                    }

            }


        }else{
            ViewHolder viewHolder = (ViewHolder) holder;
            Log.d(TAG, "about section="+ userDataArrayList.get(position).getKey());

            switch (userDataArrayList.get(position).getSection()){
                case SECTION_ABOUT:
                    viewHolder.sectionHeader.setText(R.string.about_user_headline);
                    Log.d(TAG, "about section="+ userDataArrayList.get(position).getKey());
                    break;
                case SECTION_WORK:
                    viewHolder.sectionHeader.setText(R.string.user_work_school_headline);
                    Log.d(TAG, "work education section="+ userDataArrayList.get(position).getKey());

                    break;
                case SECTION_HABITS:
                    viewHolder.sectionHeader.setText(R.string.user_habits_hobbies_headline);
                    Log.d(TAG, "habits section="+ userDataArrayList.get(position).getKey());
                    break;
            }

            // if not first item check if item above has the same header
            if (position > 0 && userDataArrayList.get(position - 1).getSection()
                    == (userDataArrayList.get(position).getSection())) {
                viewHolder.divider.setVisibility(View.GONE);
                viewHolder.sectionHeader.setVisibility(View.GONE);
            } else {
                viewHolder.divider.setVisibility(View.VISIBLE);
                viewHolder.sectionHeader.setVisibility(View.VISIBLE);

            }


            // birthDate text value and icons
            if(userDataArrayList.get(position).getKey().equals("birthDate")){
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(Long.parseLong(userDataArrayList.get(position).getValue()));
                viewHolder.itemValue.setText(fragmentContext.getString(R.string.user_age_value, DateHelper.getAge(c.getTime())));
                viewHolder.itemIcon.setImageResource(R.drawable.ic_cake_black_24dp);
            }

            //gender text value and icons
            if(userDataArrayList.get(position).getKey().equals("gender")){

                switch (userDataArrayList.get(position).getValue()) {
                    case "male":
                        viewHolder.itemValue.setText(fragmentContext.getString(R.string.user_gender_value, fragmentContext.getString(R.string.male)));
                        viewHolder.itemIcon.setImageResource(R.drawable.ic_male);
                        viewHolder.itemIcon.setVisibility(View.VISIBLE);
                        break;
                    case "female":
                        viewHolder.itemValue.setText(fragmentContext.getString(R.string.user_gender_value, fragmentContext.getString(R.string.female)));
                        viewHolder.itemIcon.setImageResource(R.drawable.ic_female);
                        viewHolder.itemIcon.setVisibility(View.VISIBLE);
                        break;
                    case"transsexual":
                        viewHolder.itemValue.setText(fragmentContext.getString(R.string.user_gender_value, fragmentContext.getString(R.string.transsexual)));
                        viewHolder.itemIcon.setImageResource(R.drawable.ic_transsexual);
                        viewHolder.itemIcon.setVisibility(View.VISIBLE);
                        break;
                    default:
                        viewHolder.itemValue.setText(R.string.not_specified);
                        viewHolder.itemIcon.setVisibility(View.INVISIBLE);
                        break;
                }
            }// end of gender text value and icons

            // nationality text value and icons
            if(userDataArrayList.get(position).getKey().equals("nationality")){
                viewHolder.itemValue.setText(fragmentContext.getString(R.string.user_country_value, userDataArrayList.get(position).getValue()));
                viewHolder.itemIcon.setImageResource(R.drawable.ic_flag_black_24dp);
            }

            // hometown text value and icons
            if(userDataArrayList.get(position).getKey().equals("hometown")){
                viewHolder.itemValue.setText(fragmentContext.getString(R.string.user_hometown_value, userDataArrayList.get(position).getValue()));
                viewHolder.itemIcon.setImageResource(R.drawable.ic_location_on_black_24dp);
            }

            // horoscope text value and icons
            if(userDataArrayList.get(position).getKey().equals("horoscope")){
                switch (userDataArrayList.get(position).getValue()) {
                    case "aries":
                        viewHolder.itemValue.setText(R.string.aries);
                        viewHolder.itemIcon.setImageResource(R.drawable.ic_aries_symbol);
                        viewHolder.itemIcon.setVisibility(View.VISIBLE);
                        break;
                    case "taurus":
                        viewHolder.itemValue.setText(R.string.taurus);
                        viewHolder.itemIcon.setImageResource(R.drawable.ic_taurus_zodiac_symbol_of_bull_head_front);
                        viewHolder.itemIcon.setVisibility(View.VISIBLE);
                        break;
                    case"gemini":
                        viewHolder.itemValue.setText(R.string.gemini);
                        viewHolder.itemIcon.setImageResource(R.drawable.ic_gemini_zodiac_symbol_of_two_twins_faces);
                        viewHolder.itemIcon.setVisibility(View.VISIBLE);
                        break;
                    case"cancer":
                        viewHolder.itemValue.setText(R.string.cancer);
                        viewHolder.itemIcon.setImageResource(R.drawable.ic_cancer_astrological_sign_of_crab_silhouette_simple);
                        viewHolder.itemIcon.setVisibility(View.VISIBLE);
                        break;
                    case"leo":
                        viewHolder.itemValue.setText(R.string.leo);
                        viewHolder.itemIcon.setImageResource(R.drawable.ic_leo_astrological_sign);
                        viewHolder.itemIcon.setVisibility(View.VISIBLE);
                        break;
                    case"virgo":
                        viewHolder.itemValue.setText(R.string.virgo);
                        viewHolder.itemIcon.setImageResource(R.drawable.ic_virgo_woman_head_shape_symbol);
                        viewHolder.itemIcon.setVisibility(View.VISIBLE);
                        break;
                    case"libra":
                        viewHolder.itemValue.setText(R.string.libra);
                        viewHolder.itemIcon.setImageResource(R.drawable.ic_libra_scale_balance_symbol);
                        viewHolder.itemIcon.setVisibility(View.VISIBLE);
                        break;
                    case"scorpio":
                        viewHolder.itemValue.setText(R.string.scorpio);
                        viewHolder.itemIcon.setImageResource(R.drawable.ic_scorpio_animal_shape_of_zodiac_simple);
                        viewHolder.itemIcon.setVisibility(View.VISIBLE);
                        break;
                    case"sagittarius":
                        viewHolder.itemValue.setText(R.string.sagittarius);
                        viewHolder.itemIcon.setImageResource(R.drawable.ic_sagittarius_arch_and_arrow_symbol);
                        viewHolder.itemIcon.setVisibility(View.VISIBLE);
                        break;
                    case"capricorn":
                        viewHolder.itemValue.setText(R.string.capricorn);
                        viewHolder.itemIcon.setImageResource(R.drawable.ic_capricorn_goat_animal_shape_of_zodiac_sign);
                        viewHolder.itemIcon.setVisibility(View.VISIBLE);
                        break;
                    case"aquarius":
                        viewHolder.itemValue.setText(R.string.aquarius);
                        viewHolder.itemIcon.setImageResource(R.drawable.ic_aquarius_water_container_symbol);
                        viewHolder.itemIcon.setVisibility(View.VISIBLE);
                        break;
                    case"pisces":
                        viewHolder.itemValue.setText(R.string.pisces);
                        viewHolder.itemIcon.setImageResource(R.drawable.ic_pisces_astrological_sign_symbol);
                        viewHolder.itemIcon.setVisibility(View.VISIBLE);
                        break;
                    default:
                        viewHolder.itemValue.setText(R.string.horoscope_not_specified);
                        viewHolder.itemIcon.setVisibility(View.INVISIBLE);
                        break;
                }
            }//end of horoscope text value and icons

            // lives text value and icons
            if(userDataArrayList.get(position).getKey().equals("lives")){
                viewHolder.itemValue.setText(fragmentContext.getString(R.string.user_lives_in_value, userDataArrayList.get(position).getValue()));
                viewHolder.itemIcon.setImageResource(R.drawable.ic_home_black_24dp);
            }

            // politics text value and icons
            if(userDataArrayList.get(position).getKey().equals("politics")){
                viewHolder.itemValue.setText(fragmentContext.getString(R.string.user_politics_value, userDataArrayList.get(position).getValue()));
                viewHolder.itemIcon.setImageResource(R.drawable.ic_voting);
            }

            // religion text value and icons
            if(userDataArrayList.get(position).getKey().equals("religion")){
                viewHolder.itemValue.setText(fragmentContext.getString(R.string.user_religion_value, userDataArrayList.get(position).getValue()));
                viewHolder.itemIcon.setImageResource(R.drawable.ic_praying_hands);
            }

            // work text value and icons
            if(userDataArrayList.get(position).getKey().equals("work")){
                viewHolder.itemValue.setText(fragmentContext.getString(R.string.user_work_value, userDataArrayList.get(position).getValue()));
                viewHolder.itemIcon.setImageResource(R.drawable.ic_work_black_24dp);
            }
            // college text value and icons
            if(userDataArrayList.get(position).getKey().equals("college")){
                viewHolder.itemValue.setText(fragmentContext.getString(R.string.user_college_value, userDataArrayList.get(position).getValue()));
                viewHolder.itemIcon.setImageResource(R.drawable.ic_school_black_24dp);
            }

            // school text value and icons
            if(userDataArrayList.get(position).getKey().equals("school")){
                viewHolder.itemValue.setText(fragmentContext.getString(R.string.user_school_value, userDataArrayList.get(position).getValue()));
                viewHolder.itemIcon.setImageResource(R.drawable.ic_pencil);
            }

            // smoke text value and icons
            if(userDataArrayList.get(position).getKey().equals("smoke")){
                viewHolder.itemIcon.setImageResource(R.drawable.ic_smoking_rooms_black_24dp);
                if(userDataArrayList.get(position).getValue().equals("true")){
                    viewHolder.itemValue.setText(fragmentContext.getString(R.string.user_smoke_value, fragmentContext.getString(R.string.yes)));
                }else{
                    viewHolder.itemValue.setText(fragmentContext.getString(R.string.user_smoke_value, fragmentContext.getString(R.string.no)));
                }
            }

            // shisha text value and icons
            if(userDataArrayList.get(position).getKey().equals("shisha")){
                viewHolder.itemIcon.setImageResource(R.drawable.ic_shisha_one_hand);
                if(userDataArrayList.get(position).getValue().equals("true")){
                    viewHolder.itemValue.setText(fragmentContext.getString(R.string.user_water_pipe_value, fragmentContext.getString(R.string.yes)));
                }else{
                    viewHolder.itemValue.setText(fragmentContext.getString(R.string.user_water_pipe_value, fragmentContext.getString(R.string.no)));
                }
            }

            // drugs text value and icons
            if(userDataArrayList.get(position).getKey().equals("drugs")){
                viewHolder.itemIcon.setImageResource(R.drawable.ic_injection);
                if(userDataArrayList.get(position).getValue().equals("true")){
                    viewHolder.itemValue.setText(fragmentContext.getString(R.string.user_drugs_value, fragmentContext.getString(R.string.yes)));
                }else{
                    viewHolder.itemValue.setText(fragmentContext.getString(R.string.user_drugs_value, fragmentContext.getString(R.string.no)));
                }
            }

            // drink text value and icons
            if(userDataArrayList.get(position).getKey().equals("drink")){
                viewHolder.itemIcon.setImageResource(R.drawable.ic_drinking_24dp);
                if(userDataArrayList.get(position).getValue().equals("true")){
                    viewHolder.itemValue.setText(fragmentContext.getString(R.string.user_drink_value, fragmentContext.getString(R.string.yes)));
                }else{
                    viewHolder.itemValue.setText(fragmentContext.getString(R.string.user_drink_value, fragmentContext.getString(R.string.no)));
                }
            }

            // athlete text value and icons
            if(userDataArrayList.get(position).getKey().equals("athlete")){
                viewHolder.itemIcon.setImageResource(R.drawable.ic_fitness_athlete_24dp);
                if(userDataArrayList.get(position).getValue().equals("true")){
                    viewHolder.itemValue.setText(fragmentContext.getString(R.string.user_athlete_value, fragmentContext.getString(R.string.yes)));
                }else{
                    viewHolder.itemValue.setText(fragmentContext.getString(R.string.user_athlete_value, fragmentContext.getString(R.string.no)));
                }
            }
            // gamer text value and icons
            if(userDataArrayList.get(position).getKey().equals("gamer")){
                viewHolder.itemIcon.setImageResource(R.drawable.ic_games_black_24dp);
                if(userDataArrayList.get(position).getValue().equals("true")){
                    viewHolder.itemValue.setText(fragmentContext.getString(R.string.user_gamer_value, fragmentContext.getString(R.string.yes)));
                }else{
                    viewHolder.itemValue.setText(fragmentContext.getString(R.string.user_gamer_value, fragmentContext.getString(R.string.no)));
                }
            }
            // travel text value and icons
            if(userDataArrayList.get(position).getKey().equals("travel")){
                viewHolder.itemIcon.setImageResource(R.drawable.ic_flight_24dp);
                if(userDataArrayList.get(position).getValue().equals("true")){
                    viewHolder.itemValue.setText(fragmentContext.getString(R.string.user_travel_value, fragmentContext.getString(R.string.yes)));
                }else{
                    viewHolder.itemValue.setText(fragmentContext.getString(R.string.user_travel_value, fragmentContext.getString(R.string.no)));
                }
            }
            // cook text value and icons
            if(userDataArrayList.get(position).getKey().equals("cook")){
                viewHolder.itemIcon.setImageResource(R.drawable.ic_cook_24dp);
                if(userDataArrayList.get(position).getValue().equals("true")){
                    viewHolder.itemValue.setText(fragmentContext.getString(R.string.user_cook_value, fragmentContext.getString(R.string.yes)));
                }else{
                    viewHolder.itemValue.setText(fragmentContext.getString(R.string.user_cook_value, fragmentContext.getString(R.string.no)));
                }
            }
            // read text value and icons
            if(userDataArrayList.get(position).getKey().equals("read")){
                viewHolder.itemIcon.setImageResource(R.drawable.ic_library_book_24dp);
                if(userDataArrayList.get(position).getValue().equals("true")){
                    viewHolder.itemValue.setText(fragmentContext.getString(R.string.user_read_value, fragmentContext.getString(R.string.yes)));
                }else{
                    viewHolder.itemValue.setText(fragmentContext.getString(R.string.user_read_value, fragmentContext.getString(R.string.no)));
                }
            }
        }//end of if instance of

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

    @Override
    public int getItemViewType(int position) {

        Log.d(TAG, "getSection()= "+ (userDataArrayList.get(position).getSection()));
        if(userDataArrayList.get(position).getSection()== SECTION_SOCIAL){
            return SECTION_SOCIAL;
        }else{
            return SECTION_ABOUT;
        }
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

    /// ViewHolder for trips list /////
    public class ButtonsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        View row;
        private Button itemValue;
        private TextView  sectionHeader;
        private View divider;
        ItemClickListener itemClickListener;


        public ButtonsViewHolder(View itemView, ItemClickListener itemClickListener) {
            super(itemView);
            //itemView = row;
            this.itemClickListener = itemClickListener;

            row = itemView;
            itemValue = row.findViewById(R.id.social_button);
            sectionHeader = row.findViewById(R.id.section_header);
            divider = row.findViewById(R.id.section_divider);

            itemValue.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            if(itemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION){
                itemClickListener.onClick(view, getAdapterPosition(), false);
            }
        }

        /*// needed only if i want the listener to be inside the adapter
        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }*/
    }


}
