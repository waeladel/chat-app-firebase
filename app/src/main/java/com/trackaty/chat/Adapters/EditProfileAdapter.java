package com.trackaty.chat.Adapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.github.aakira.expandablelayout.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;
import com.trackaty.chat.Interface.ItemClickListener;
import com.trackaty.chat.R;
import com.trackaty.chat.ViewModels.EditProfileViewModel;
import com.trackaty.chat.models.Profile;
import com.trackaty.chat.models.Social;
import com.trackaty.chat.models.Variables;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class EditProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static String TAG = EditProfileAdapter.class.getSimpleName();

    private  static final int SECTION_AVATAR = 100;
    private  static final int SECTION_COVER = 200;
    private  static final int SECTION_EDIT_TEXT = 300;
    private  static final int SECTION_TEXT = 400;
    private  static final int SECTION_SPINNER = 500;
    private  static final int SECTION_ABOUT = 600;
    private  static final int SECTION_WORK = 700;
    private  static final int SECTION_HABITS = 800;
    private  static final int SECTION_SOCIAL = 900;

    private  static final String SECTION_ABOUT_HEADLINE = "about";
    private  static final String SECTION_WORK_HEADLINE  = "work_and_education";
    private  static final String SECTION_HABITS_HEADLINE  = "habits";
    private  final static String SECTION_SOCIAL_HEADLINE  = "social_and_contacts";

    private  static final int  NAME_INPUT_MAX_LENGTH = 40;
    private  static final int  NAME_INPUT_MAX_LINES = 1;

    private  static final int  BIO_INPUT_MAX_LENGTH = 201;
    private  static final int  BIO_INPUT_MAX_LINES = 4;


    private ArrayList<Profile> mProfileDataArrayList;
    private ArrayList<Profile> aboutArrayList ;
    private ArrayList<Profile> workArrayList ;
    private ArrayList<Profile> habitsArrayList;
    private ArrayList<Social> socialArrayList;
    private ArrayList<Variables> variablesArrayList;

    private AboutAdapter aboutAdapter;
    private WorkAdapter workAdapter;
    private HabitsAdapter habitsAdapter;
    private SocialAdapter socialAdapter;

    private Context context;
    private Fragment mFragment;

    private ItemClickListener itemClickListener;

    private EditProfileViewModel mEditProfileViewModel;


    public EditProfileAdapter(Context context
            , ArrayList<Profile> userDataArrayList
            ,ArrayList<Profile> aboutArrayList
            ,ArrayList<Profile> workArrayList
            ,ArrayList<Profile> habitsArrayList
            ,ArrayList<Social> socialArrayList
            ,ArrayList<Variables> variablesArrayList
            , Fragment mFragment
            , ItemClickListener itemClickListener){

        this.mProfileDataArrayList = userDataArrayList;
        this.aboutArrayList = aboutArrayList;
        this.aboutArrayList = workArrayList;
        this.aboutArrayList = habitsArrayList;
        this.socialArrayList = socialArrayList;
        this.variablesArrayList = variablesArrayList;


        this.context = context;
        this.mFragment = mFragment; // To use it as observer
        this.itemClickListener = itemClickListener;

        aboutAdapter = new AboutAdapter(mFragment, aboutArrayList);
        workAdapter = new WorkAdapter(mFragment, workArrayList);
        habitsAdapter = new HabitsAdapter(mFragment, habitsArrayList);
        socialAdapter = new SocialAdapter (mFragment, socialArrayList);

        // get EditProfileViewModel to access user object
        mEditProfileViewModel = ViewModelProviders.of(mFragment).get(EditProfileViewModel.class);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType){
            case 100:
                // SECTION_COVER;
            View avatarImageView = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_profile_avatar_item, parent, false);
            return new avatarImageHolder(avatarImageView , itemClickListener);
            case 200:
                // SECTION_COVER;
                View coverImageView = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_profile_cover_item, parent, false);
                return new coverImageHolder(coverImageView , itemClickListener);
            case 300:
                // SECTION_EDIT_TEXT;
                View textInputView = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_profile_text_item, parent, false);
                return new TextInputHolder(textInputView);
            case 400:
                // SECTION_TEXT;
                View textView = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_profile_text_child_item, parent, false);
                return new TextHolder(textView, itemClickListener);
            case 500:
                // SECTION_SPINNER;
                View spinnerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_profile_spinner_item, parent, false);
                return new SpinnerHolder(spinnerView);
            case 600:
                // SECTION_ABOUT;
            case 700:
                // SECTION_WORK;
            case 800:
                // SECTION_HABITS;
            case 900:
                // SECTION_SOCIAL;
        }
        View expandableView  = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_profile_expandable_parent, parent, false);
        return new ExpandableHolder(expandableView , itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        Log.i(TAG, "onBindViewHolder called="+ mProfileDataArrayList.get(position).getKey());

        if (holder instanceof avatarImageHolder){
            avatarImageHolder imageHolder = (avatarImageHolder) holder;
            ViewGroup.LayoutParams layoutParams;

            if ("avatar".equals(mProfileDataArrayList.get(position).getKey())) {
                // set frame layout to WRAP_CONTENT
                    /*float factor = imageHolder.frameLayout.getContext().getResources().getDisplayMetrics().density;
                    layoutParams = imageHolder.frameLayout.getLayoutParams();
                    layoutParams.width = (int)(100 * factor);
                    layoutParams.height = (int)(100 * factor);
                    imageHolder.frameLayout.setLayoutParams(layoutParams);*/

                imageHolder.itemHeadline.setText(R.string.user_avatar_headline);
                imageHolder.profileImage.setImageResource(R.drawable.ic_round_account_filled_72);
                if (null != mProfileDataArrayList.get(position).getValue()) {
                    Picasso.get()
                            .load(mProfileDataArrayList.get(position).getValue())
                            .placeholder(R.mipmap.ic_round_account_filled_72)
                            .error(R.drawable.ic_round_broken_image_72px)
                            .into(imageHolder.profileImage);
                    imageHolder.profileImage.setVisibility(View.VISIBLE);
                    imageHolder.divider.setVisibility(View.INVISIBLE);

                    // Add black color filter
                    // Move filter outside if statement to activate the black color even if avatar is null
                    int blackColor = context.getResources().getColor(R.color.transparent_edit_image);
                    ColorFilter colorFilter = new PorterDuffColorFilter(blackColor, PorterDuff.Mode.DARKEN);
                    imageHolder.profileImage.setColorFilter(colorFilter);
                }

                    /*case "coverImage":
                    // set frame layout to MATCH_PARENT
                    layoutParams = imageHolder.frameLayout.getLayoutParams();
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    imageHolder.frameLayout.setLayoutParams(layoutParams);

                    imageHolder.itemHeadline.setText(R.string.user_cover_headline);
                    if(null != mProfileDataArrayList.get(position).getValue()){
                        Picasso.get()
                                .load(mProfileDataArrayList.get(position).getValue())
                                .placeholder(R.drawable.ic_picture_gallery_white )
                                .error(R.drawable.ic_broken_image)
                                .into(imageHolder.coverImage);
                        imageHolder.coverImage.setVisibility(View.VISIBLE);
                        imageHolder.profileImage.setVisibility(View.INVISIBLE);
                        imageHolder.profileImage.setImageResource(R.drawable.ic_picture_gallery_white);

                    }
                    break;*/
            }

            // hide progress animation when done
            if(variablesArrayList != null && variablesArrayList.get(position).getValue()){
                imageHolder.progressIcon.setVisibility(View.VISIBLE);
            }else{
                imageHolder.progressIcon.setVisibility(View.GONE);
            }

            // needed only if i want the listener to be inside the adapter
            /*imageHolder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    Log.d(TAG, "item clicked= " + position);
                }
            });*/
        }

        if (holder instanceof coverImageHolder){
            coverImageHolder imageHolder = (coverImageHolder) holder;
            ViewGroup.LayoutParams layoutParams;

            /*case "avatar":
                    // set frame layout to WRAP_CONTENT
                    float factor = imageHolder.frameLayout.getContext().getResources().getDisplayMetrics().density;

                    layoutParams = imageHolder.frameLayout.getLayoutParams();
                    layoutParams.width = (int)(100 * factor);
                    layoutParams.height = (int)(100 * factor);
                    imageHolder.frameLayout.setLayoutParams(layoutParams);

                    imageHolder.itemHeadline.setText(R.string.user_avatar_headline);
                    if(null != mProfileDataArrayList.get(position).getValue()){
                        Picasso.get()
                                .load(mProfileDataArrayList.get(position).getValue())
                                .placeholder(R.drawable.ic_user_account_grey_white )
                                .error(R.drawable.ic_broken_image)
                                .into(imageHolder.profileImage);
                        imageHolder.coverImage.setVisibility(View.INVISIBLE);
                        imageHolder.profileImage.setVisibility(View.VISIBLE);
                        imageHolder.divider.setVisibility(View.INVISIBLE);
                        imageHolder.profileImage.setImageResource(R.drawable.ic_user_account_grey_white);

                        // add black color filter
                        int blackColor = context.getResources().getColor(R.color.transparent_edit_image);
                        ColorFilter colorFilter = new PorterDuffColorFilter(blackColor, PorterDuff.Mode.DARKEN);
                        imageHolder.profileImage.setColorFilter(colorFilter);
                    }
                    break;*/
            if ("coverImage".equals(mProfileDataArrayList.get(position).getKey())) {
                // set frame layout to MATCH_PARENT
                    /*layoutParams = imageHolder.frameLayout.getLayoutParams();
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    imageHolder.frameLayout.setLayoutParams(layoutParams);*/

                imageHolder.itemHeadline.setText(R.string.user_cover_headline);
                imageHolder.coverImage.setImageResource(R.drawable.ic_picture_gallery_white);
                if (null != mProfileDataArrayList.get(position).getValue()) {
                    Picasso.get()
                            .load(mProfileDataArrayList.get(position).getValue())
                            .placeholder(R.mipmap.ic_picture_gallery_white_512px)
                            .error(R.drawable.ic_broken_image_512px)
                            .into(imageHolder.coverImage);
                    imageHolder.coverImage.setVisibility(View.VISIBLE);
                        /*// add black color filter
                        int blackColor = context.getResources().getColor(R.color.transparent_edit_image);
                        ColorFilter colorFilter = new PorterDuffColorFilter(blackColor, PorterDuff.Mode.DARKEN);
                        imageHolder.coverImage.setColorFilter(colorFilter);*/
                }
            }

            // hide progress animation when done
            if(variablesArrayList != null && variablesArrayList.get(position).getValue()){
                imageHolder.progressIcon.setVisibility(View.VISIBLE);
            }else{
                imageHolder.progressIcon.setVisibility(View.GONE);
            }

            // needed only if i want the listener to be inside the adapter
            /*imageHolder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    Log.d(TAG, "item clicked= " + position);
                }
            });*/
        }

        if (holder instanceof TextInputHolder){
            TextInputHolder textInputHolder = (TextInputHolder) holder;

            switch (mProfileDataArrayList.get(position).getKey()){
                case "name":
                    // set Max length
                    if(null != mProfileDataArrayList.get(position).getValue()){
                        textInputHolder.itemValue.setText(mProfileDataArrayList.get(position).getValue());
                    }
                    textInputHolder.itemValue.setFilters(new InputFilter[] {new InputFilter.LengthFilter(NAME_INPUT_MAX_LENGTH)});

                    // capitalize every first letter
                    textInputHolder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

                    // Enable counter
                    textInputHolder.inputLayout.setCounterEnabled(true);
                    textInputHolder.inputLayout.setCounterMaxLength(NAME_INPUT_MAX_LENGTH);

                    //Set Hint/label
                    textInputHolder.inputLayout.setHint(context.getString(R.string.user_name_hint));

                    // Set Helper
                    textInputHolder.inputLayout.setHelperTextEnabled(true);
                    textInputHolder.inputLayout.setHelperText(context.getString(R.string.required_helper));

                    // Parce text to handel configuration change
                    //mProfileDataArrayList.get(position).setValue("mama");
                    break;
                case "biography":
                    if(null != mProfileDataArrayList.get(position).getValue()){
                        textInputHolder.itemValue.setText(mProfileDataArrayList.get(position).getValue());
                    }
                    //textInputHolder.itemValue.setHint(R.string.user_biography_hint);
                    //textInputHolder.itemValue.setMaxLines(BIO_INPUT_MAX_LINES);

                    // set Max length

                    textInputHolder.itemValue.setFilters(new InputFilter[] {new InputFilter.LengthFilter(BIO_INPUT_MAX_LENGTH)});
                    // capitalize every first letter, it's bad because it removes other filters
                    //textInputHolder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);


                    // Enable counter
                    textInputHolder.inputLayout.setCounterEnabled(true);
                    textInputHolder.inputLayout.setCounterMaxLength(BIO_INPUT_MAX_LENGTH);

                    //Set Hint/label
                    textInputHolder.inputLayout.setHint(context.getString(R.string.user_biography_hint));

                    // Set Helper
                    /*textInputHolder.inputLayout.setHelperTextEnabled(true);
                    textInputHolder.inputLayout.setHelperText(fragmentContext.getString(R.string.user_biography_helper));*/
                    break;
            }

        }

        if (holder instanceof TextHolder){
            TextHolder textHolder = (TextHolder) holder;

            if ("birthDate".equals(mProfileDataArrayList.get(position).getKey())) {
                textHolder.itemHeadline.setText(context.getString(R.string.user_birthday_headline));
                if (null != mProfileDataArrayList.get(position).getValue()) {
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(Long.parseLong(mProfileDataArrayList.get(position).getValue()));
                    String birthDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(c.getTime());
                    textHolder.itemValue.setText(birthDate);
                }
            }

        }

        if (holder instanceof SpinnerHolder){
            SpinnerHolder spinnerHolder = (SpinnerHolder) holder;

            switch (mProfileDataArrayList.get(position).getKey()){
                case "relationship":
                    spinnerHolder.itemHeadline.setText(R.string.user_relationship_headline);

                    // set array adapter
                    ArrayAdapter<String> relationArrayAdapter  = new ArrayAdapter<String>(context,
                            android.R.layout.simple_spinner_dropdown_item,
                            context.getResources().getStringArray(R.array.user_relationship));

                    spinnerHolder.itemValue.setAdapter(relationArrayAdapter );

                    if(null != mProfileDataArrayList.get(position).getValue()){
                        // display selected item from the spinner ///
                        /*int spinnerPosition = relationArrayAdapter.getPosition(mProfileDataArrayList.get(position).getValue());
                        spinnerHolder.itemValue.setSelection(spinnerPosition);*/
                        switch (mProfileDataArrayList.get(position).getValue()){ // display sorting option selected from shared preference
                            case "single":
                                spinnerHolder.itemValue.setSelection(0);
                                Log.d(TAG, "display 0 option on sorting spinner");
                                break;
                            case "searching":
                                spinnerHolder.itemValue.setSelection(1);
                                Log.d(TAG, "display 1 option on sorting spinner");
                                break;
                            case "committed":
                                spinnerHolder.itemValue.setSelection(2);
                                Log.d(TAG, "display 2 option on sorting spinner");
                                break;
                            case "engaged":
                                spinnerHolder.itemValue.setSelection(3);
                                Log.d(TAG, "display 3 option on sorting spinner");
                                break;
                            case "married":
                                spinnerHolder.itemValue.setSelection(4);
                                Log.d(TAG, "display 4 option on sorting spinner");
                                break;
                            case "civil union":
                                spinnerHolder.itemValue.setSelection(5);
                                Log.d(TAG, "display 5 option on sorting spinner");
                                break;
                            case "domestic partnership":
                                spinnerHolder.itemValue.setSelection(6);
                                Log.d(TAG, "display 6 option on sorting spinner");
                                break;
                            case "open relationship":
                                spinnerHolder.itemValue.setSelection(7);
                                Log.d(TAG, "display 7 option on sorting spinner");
                                break;
                            case "open marriage":
                                spinnerHolder.itemValue.setSelection(8);
                                Log.d(TAG, "display 8 option on sorting spinner");
                                break;
                            case "separated":
                                spinnerHolder.itemValue.setSelection(9);
                                Log.d(TAG, "display 9 option on sorting spinner");
                                break;
                            case "divorced":
                                spinnerHolder.itemValue.setSelection(10);
                                Log.d(TAG, "display 10 option on sorting spinner");
                                break;
                            case "widowed":
                                spinnerHolder.itemValue.setSelection(11);
                                Log.d(TAG, "display 11 option on sorting spinner");
                                break;
                        }
                    }

                    spinnerHolder.itemValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int selectedItemPosition, long id) {
                        // your code here for onItemSelected
                        switch (selectedItemPosition){ // display sorting option selected from shared preference
                            case 0:
                                mProfileDataArrayList.get(position).setValue("single");
                                mEditProfileViewModel.getUser().setRelationship("single");
                                Log.d(TAG, "spinner item 0 is selected= " +mProfileDataArrayList.get(position).getValue());
                                break;
                            case 1:
                                mProfileDataArrayList.get(position).setValue("searching");
                                mEditProfileViewModel.getUser().setRelationship("searching");
                                Log.d(TAG, "spinner item 1 is selected= " +mProfileDataArrayList.get(position).getValue());
                                break;
                            case 2:
                                mProfileDataArrayList.get(position).setValue("committed");
                                mEditProfileViewModel.getUser().setRelationship("committed");
                                Log.d(TAG, "spinner item 2 is selected= " +mProfileDataArrayList.get(position).getValue());
                                break;
                            case 3:
                                mProfileDataArrayList.get(position).setValue("engaged");
                                mEditProfileViewModel.getUser().setRelationship("engaged");
                                Log.d(TAG, "spinner item 3 is selected= " +mProfileDataArrayList.get(position).getValue());
                                break;
                            case 4:
                                mProfileDataArrayList.get(position).setValue("married");
                                mEditProfileViewModel.getUser().setRelationship("married");
                                Log.d(TAG, "spinner item 4 is selected= " +mProfileDataArrayList.get(position).getValue());
                                break;
                            case 5:
                                mProfileDataArrayList.get(position).setValue("civil union");
                                mEditProfileViewModel.getUser().setRelationship("civil union");
                                Log.d(TAG, "spinner item 5 is selected= " +mProfileDataArrayList.get(position).getValue());
                                break;
                            case 6:
                                mProfileDataArrayList.get(position).setValue("domestic partnership");
                                mEditProfileViewModel.getUser().setRelationship("domestic partnership");
                                Log.d(TAG, "spinner item 6 is selected= " +mProfileDataArrayList.get(position).getValue());
                                break;
                            case 7:
                                mProfileDataArrayList.get(position).setValue("open relationship");
                                mEditProfileViewModel.getUser().setRelationship("open relationship");
                                Log.d(TAG, "spinner item 7 is selected= " +mProfileDataArrayList.get(position).getValue());
                                break;
                            case 8:
                                mProfileDataArrayList.get(position).setValue("open marriage");
                                mEditProfileViewModel.getUser().setRelationship("open marriage");
                                Log.d(TAG, "spinner item 8 is selected= " +mProfileDataArrayList.get(position).getValue());
                                break;
                            case 9:
                                mProfileDataArrayList.get(position).setValue("separated");
                                mEditProfileViewModel.getUser().setRelationship("separated");
                                Log.d(TAG, "spinner item 9 is selected= " +mProfileDataArrayList.get(position).getValue());
                                break;
                            case 10:
                                mProfileDataArrayList.get(position).setValue("divorced");
                                mEditProfileViewModel.getUser().setRelationship("divorced");
                                Log.d(TAG, "spinner item 10 is selected= " +mProfileDataArrayList.get(position).getValue());
                                break;
                            case 11:
                                mProfileDataArrayList.get(position).setValue("widowed");
                                mEditProfileViewModel.getUser().setRelationship("widowed");
                                Log.d(TAG, "spinner item 11 is selected= " +mProfileDataArrayList.get(position).getValue());
                                break;
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // your code here
                    }

                });

                    break;
                case "interestedIn":
                    spinnerHolder.itemHeadline.setText(R.string.user_interested_in_headline);

                    // set array adapter
                    ArrayAdapter<String> interestedArrayAdapter  = new ArrayAdapter<String>(context,
                            android.R.layout.simple_spinner_dropdown_item,
                            context.getResources().getStringArray(R.array.user_interested_in));

                    spinnerHolder.itemValue.setAdapter(interestedArrayAdapter);

                    if(null != mProfileDataArrayList.get(position).getValue()){
                        // display selected item from the spinner ///
                        switch (mProfileDataArrayList.get(position).getValue()){ // display sorting option selected from shared preference
                            case "men":
                                spinnerHolder.itemValue.setSelection(0);
                                Log.d(TAG, "display 0 option on sorting spinner");
                                break;
                            case "women":
                                spinnerHolder.itemValue.setSelection(1);
                                Log.d(TAG, "display 1 option on sorting spinner");
                                break;
                            case "both":
                                spinnerHolder.itemValue.setSelection(2);
                                Log.d(TAG, "display 2 option on sorting spinner");
                                break;
                        }
                    }

                    spinnerHolder.itemValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int selectedItemPosition, long id) {
                            // your code here for onItemSelected
                            switch (selectedItemPosition){ // display sorting option selected from shared preference
                                case 0:
                                    mProfileDataArrayList.get(position).setValue("men");
                                    mEditProfileViewModel.getUser().setInterestedIn("men");
                                    Log.d(TAG, "spinner item 0 is selected= " +mProfileDataArrayList.get(position).getValue());
                                    break;
                                case 1:
                                    mProfileDataArrayList.get(position).setValue("women");
                                    mEditProfileViewModel.getUser().setInterestedIn("women");
                                    Log.d(TAG, "spinner item 1 is selected= " +mProfileDataArrayList.get(position).getValue());
                                    break;
                                case 2:
                                    mProfileDataArrayList.get(position).setValue("both");
                                    mEditProfileViewModel.getUser().setInterestedIn("both");
                                    Log.d(TAG, "spinner item 2 is selected= " +mProfileDataArrayList.get(position).getValue());
                                    break;
                            }

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // your code here
                        }

                    });
                    break;

                case "gender":
                    spinnerHolder.itemHeadline.setText(R.string.user_gender_headline);

                    // set array adapter
                    ArrayAdapter<String> genderArrayAdapter  = new ArrayAdapter<String>(context,
                            android.R.layout.simple_spinner_dropdown_item,
                            context.getResources().getStringArray(R.array.profile_user_gender));

                    spinnerHolder.itemValue.setAdapter(genderArrayAdapter);

                    if(null != mProfileDataArrayList.get(position).getValue()){
                        // display selected item from the spinner ///
                        switch (mProfileDataArrayList.get(position).getValue()){ // display sorting option selected from shared preference
                            case "male":
                                spinnerHolder.itemValue.setSelection(0);
                                Log.d(TAG, "display 0 option on sorting spinner");
                                break;
                            case "female":
                                spinnerHolder.itemValue.setSelection(1);
                                Log.d(TAG, "display 1 option on sorting spinner");
                                break;
                            case "transsexual":
                                spinnerHolder.itemValue.setSelection(2);
                                Log.d(TAG, "display 2 option on sorting spinner");
                                break;
                        }
                    }

                    spinnerHolder.itemValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int selectedItemPosition, long id) {
                            // your code here for onItemSelected
                            switch (selectedItemPosition){ // display sorting option selected from shared preference
                                case 0:
                                    mProfileDataArrayList.get(position).setValue("male");
                                    mEditProfileViewModel.getUser().setGender("male");
                                    Log.d(TAG, "spinner item 0 is selected= " +mProfileDataArrayList.get(position).getValue());
                                    break;
                                case 1:
                                    mProfileDataArrayList.get(position).setValue("female");
                                    mEditProfileViewModel.getUser().setGender("female");
                                    Log.d(TAG, "spinner item 1 is selected= " +mProfileDataArrayList.get(position).getValue());
                                    break;
                                case 2:
                                    mProfileDataArrayList.get(position).setValue("transsexual");
                                    mEditProfileViewModel.getUser().setGender("transsexual");
                                    Log.d(TAG, "spinner item 2 is selected= " +mProfileDataArrayList.get(position).getValue());
                                    break;
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // your code here
                        }
                    });
                    break;
                case "horoscope":
                    spinnerHolder.itemHeadline.setText(R.string.user_horoscope_headline);

                    // set array adapter
                    ArrayAdapter<String> horoscopeArrayAdapter  = new ArrayAdapter<String>(context,
                            android.R.layout.simple_spinner_dropdown_item,
                            context.getResources().getStringArray(R.array.user_horoscope));

                    spinnerHolder.itemValue.setAdapter(horoscopeArrayAdapter);

                    if(null != mProfileDataArrayList.get(position).getValue()){
                        // display selected item from the spinner ///
                        switch (mProfileDataArrayList.get(position).getValue()){ // display sorting option selected from shared preference
                            case "aries":
                                spinnerHolder.itemValue.setSelection(0);
                                Log.d(TAG, "display 0 option on sorting spinner");
                                break;
                            case "taurus":
                                spinnerHolder.itemValue.setSelection(1);
                                Log.d(TAG, "display 1 option on sorting spinner");
                                break;
                            case "gemini":
                                spinnerHolder.itemValue.setSelection(2);
                                Log.d(TAG, "display 2 option on sorting spinner");
                                break;
                            case "cancer":
                                spinnerHolder.itemValue.setSelection(3);
                                Log.d(TAG, "display 3 option on sorting spinner");
                                break;
                            case "leo":
                                spinnerHolder.itemValue.setSelection(4);
                                Log.d(TAG, "display 4 option on sorting spinner");
                                break;
                            case "virgo":
                                spinnerHolder.itemValue.setSelection(5);
                                Log.d(TAG, "display 5 option on sorting spinner");
                                break;
                            case "libra":
                                spinnerHolder.itemValue.setSelection(6);
                                Log.d(TAG, "display 6 option on sorting spinner");
                                break;
                            case "scorpio":
                                spinnerHolder.itemValue.setSelection(7);
                                Log.d(TAG, "display 7 option on sorting spinner");
                                break;
                            case "sagittarius":
                                spinnerHolder.itemValue.setSelection(8);
                                Log.d(TAG, "display 8 option on sorting spinner");
                                break;
                            case "capricorn":
                                spinnerHolder.itemValue.setSelection(9);
                                Log.d(TAG, "display 9 option on sorting spinner");
                                break;
                            case "aquarius":
                                spinnerHolder.itemValue.setSelection(10);
                                Log.d(TAG, "display 10 option on sorting spinner");
                                break;
                            case "pisces":
                                spinnerHolder.itemValue.setSelection(11);
                                Log.d(TAG, "display 11 option on sorting spinner");
                                break;
                        }
                    }

                    spinnerHolder.itemValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int selectedItemPosition, long id) {
                            // your code here for onItemSelected
                            switch (selectedItemPosition){ // display sorting option selected from shared preference
                                case 0:
                                    mProfileDataArrayList.get(position).setValue("aries");
                                    mEditProfileViewModel.getUser().setHoroscope("aries");
                                    Log.d(TAG, "spinner item 0 is selected= " +mProfileDataArrayList.get(position).getValue());
                                    break;
                                case 1:
                                    mProfileDataArrayList.get(position).setValue("taurus");
                                    mEditProfileViewModel.getUser().setHoroscope("taurus");
                                    Log.d(TAG, "spinner item 0 is selected= " +mProfileDataArrayList.get(position).getValue());
                                    break;
                                case 2:
                                    mProfileDataArrayList.get(position).setValue("gemini");
                                    mEditProfileViewModel.getUser().setHoroscope("gemini");
                                    Log.d(TAG, "spinner item 0 is selected= " +mProfileDataArrayList.get(position).getValue());
                                    break;
                                case 3:
                                    mProfileDataArrayList.get(position).setValue("cancer");
                                    mEditProfileViewModel.getUser().setHoroscope("cancer");
                                    Log.d(TAG, "spinner item 0 is selected= " +mProfileDataArrayList.get(position).getValue());
                                    break;
                                case 4:
                                    mProfileDataArrayList.get(position).setValue("leo");
                                    mEditProfileViewModel.getUser().setHoroscope("leo");
                                    Log.d(TAG, "spinner item 0 is selected= " +mProfileDataArrayList.get(position).getValue());
                                    break;
                                case 5:
                                    mProfileDataArrayList.get(position).setValue("virgo");
                                    mEditProfileViewModel.getUser().setHoroscope("virgo");
                                    Log.d(TAG, "spinner item 0 is selected= " +mProfileDataArrayList.get(position).getValue());
                                    break;
                                case 6:
                                    mProfileDataArrayList.get(position).setValue("libra");
                                    mEditProfileViewModel.getUser().setHoroscope("libra");
                                    Log.d(TAG, "spinner item 0 is selected= " +mProfileDataArrayList.get(position).getValue());
                                    break;
                                case 7:
                                    mProfileDataArrayList.get(position).setValue("scorpio");
                                    mEditProfileViewModel.getUser().setHoroscope("scorpio");
                                    Log.d(TAG, "spinner item 0 is selected= " +mProfileDataArrayList.get(position).getValue());
                                    break;
                                case 8:
                                    mProfileDataArrayList.get(position).setValue("sagittarius");
                                    mEditProfileViewModel.getUser().setHoroscope("sagittarius");
                                    Log.d(TAG, "spinner item 0 is selected= " +mProfileDataArrayList.get(position).getValue());
                                    break;
                                case 9:
                                    mProfileDataArrayList.get(position).setValue("capricorn");
                                    mEditProfileViewModel.getUser().setHoroscope("capricorn");
                                    Log.d(TAG, "spinner item 0 is selected= " +mProfileDataArrayList.get(position).getValue());
                                    break;
                                case 10:
                                    mProfileDataArrayList.get(position).setValue("aquarius");
                                    mEditProfileViewModel.getUser().setHoroscope("aquarius");
                                    Log.d(TAG, "spinner item 0 is selected= " +mProfileDataArrayList.get(position).getValue());
                                    break;
                                case 11:
                                    mProfileDataArrayList.get(position).setValue("pisces");
                                    mEditProfileViewModel.getUser().setHoroscope("pisces");
                                    Log.d(TAG, "spinner item 0 is selected= " +mProfileDataArrayList.get(position).getValue());
                                    break;
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // your code here
                        }
                    });
                    break;
            }

        }

        if (holder instanceof ExpandableHolder){
            final ExpandableHolder expandableHolder = (ExpandableHolder) holder;

            switch (mProfileDataArrayList.get(position).getKey()){
                case SECTION_ABOUT_HEADLINE:
                    if(null != mProfileDataArrayList.get(position).getValue()){
                        expandableHolder.sectionHeadline.setText(R.string.about_user_headline);
                        //expandableHolder.expandableLayout.collapse();

                        // Initiate the about RecyclerView
                        expandableHolder.expandableRecycler.setHasFixedSize(true);

                        // Only set the adapter and the layout manager at the first load because if we keep setting the adapter
                        //// the transient data will be lost when expandableHolder is created again when we scroll up then down
                        if(null == expandableHolder.expandableRecycler.getAdapter()){
                            expandableHolder.expandableRecycler.setLayoutManager(new LinearLayoutManager(context));
                            expandableHolder.expandableRecycler.setAdapter(aboutAdapter);
                        }
                        //viewHolder.setIsRecyclable(false);
                        //viewHolder.expandableLayout.setInRecyclerView(true);
                        /*viewHolder.expandableLayout.setListener(new  ExpandableLayoutListenerAdapter() {
                            @Override
                            public void onPreOpen() {

                            }

                            @Override
                            public void onPreClose() {
                            }
                        });*/

                    }
                    break;
                case SECTION_WORK_HEADLINE:
                    if(null != mProfileDataArrayList.get(position).getValue()){
                        expandableHolder.sectionHeadline.setText(R.string.user_work_school_headline);
                        //expandableHolder.expandableLayout.collapse();

                        //aboutAdapter = new AboutAdapter(fragmentContext, habitsArrayList);

                        // Initiate the about RecyclerView
                        expandableHolder.expandableRecycler.setHasFixedSize(true);

                        // Only set the adapter and the layout manager at the first load because if we keep setting the adapter
                        //// the transient data will be lost when expandableHolder is created again when we scroll up then down
                        if(null == expandableHolder.expandableRecycler.getAdapter()){
                            expandableHolder.expandableRecycler.setLayoutManager(new LinearLayoutManager(context));
                            expandableHolder.expandableRecycler.setAdapter(workAdapter);
                        }
                        //viewHolder.setIsRecyclable(false);
                        //viewHolder.expandableLayout.setInRecyclerView(true);
                        /*viewHolder.expandableLayout.setListener(new  ExpandableLayoutListenerAdapter() {
                            @Override
                            public void onPreOpen() {

                            }

                            @Override
                            public void onPreClose() {
                            }
                        });*/

                    }
                    break;
                case SECTION_HABITS_HEADLINE:
                    if(null != mProfileDataArrayList.get(position).getValue()){
                        expandableHolder.sectionHeadline.setText(R.string.user_habits_hobbies_headline);
                        //expandableHolder.expandableLayout.collapse();

                        //aboutAdapter = new AboutAdapter(fragmentContext, habitsArrayList);

                        // Initiate the about RecyclerView
                        expandableHolder.expandableRecycler.setHasFixedSize(true);

                        // Only set the adapter and the layout manager at the first load because if we keep setting the adapter
                        //// the transient data will be lost when expandableHolder is created again when we scroll up then down
                        if(null == expandableHolder.expandableRecycler.getAdapter()){
                            expandableHolder.expandableRecycler.setLayoutManager(new LinearLayoutManager(context));
                            expandableHolder.expandableRecycler.setAdapter(habitsAdapter);
                        }
                        //viewHolder.setIsRecyclable(false);
                        //viewHolder.expandableLayout.setInRecyclerView(true);
                        /*viewHolder.expandableLayout.setListener(new  ExpandableLayoutListenerAdapter() {
                            @Override
                            public void onPreOpen() {

                            }

                            @Override
                            public void onPreClose() {
                            }
                        });*/
                    }
                    break;
                case SECTION_SOCIAL_HEADLINE:
                    if(null != mProfileDataArrayList.get(position).getValue()){
                        expandableHolder.sectionHeadline.setText(R.string.user_social_headline);
                        //expandableHolder.expandableLayout.collapse();
                        Log.d(TAG, "set social adapter. expandableHolder.expandableRecycler= "+expandableHolder.expandableRecycler.getAdapter());
                        //aboutAdapter = new AboutAdapter(fragmentContext, habitsArrayList);

                        // Initiate the about RecyclerView
                        expandableHolder.expandableRecycler.setHasFixedSize(true);

                        // Only set the adapter and the layout manager at the first load because if we keep setting the adapter
                        //// the transient data will be lost when expandableHolder is created again when we scroll up then down
                        if(null == expandableHolder.expandableRecycler.getAdapter()){
                            expandableHolder.expandableRecycler.setLayoutManager(new LinearLayoutManager(context));
                            expandableHolder.expandableRecycler.setAdapter(socialAdapter);
                        }


                        //viewHolder.setIsRecyclable(false);
                        //viewHolder.expandableLayout.setInRecyclerView(true);
                        /*viewHolder.expandableLayout.setListener(new  ExpandableLayoutListenerAdapter() {
                            @Override
                            public void onPreOpen() {

                            }

                            @Override
                            public void onPreClose() {
                            }
                        });*/
                    }
                    break;

            }

            Log.d(TAG, "expandableLayout isExpanded"+expandableHolder.expandableLayout.isExpanded());
            // check if expandable button status
            if(expandableHolder.expandableLayout.isExpanded()){
                expandableHolder.expandableLayout.expand();
            }else{
                expandableHolder.expandableLayout.collapse();
            }


            expandableHolder.expandableLayout.setListener(new  ExpandableLayoutListenerAdapter() {
                @Override
                public void onPreOpen() {
                    Log.i(TAG, "expandableLayout onPreOpen "+expandableHolder.expandableLayout.getClosePosition());
                    toggleRotation(expandableHolder.expandButton, 0f, 180f).start();
                    expandableHolder.expandableLayout.setExpanded(true);
                    if(TextUtils.equals(mProfileDataArrayList.get(position).getKey(), SECTION_SOCIAL_HEADLINE)){
                        // Disable textChange listener when Layout is not opened.
                        // If not disabled it will override transient data when expand with random data from recycled text fields
                        socialAdapter.setListeningTextChange(false);
                    }else if(TextUtils.equals(mProfileDataArrayList.get(position).getKey(), SECTION_HABITS_HEADLINE)){
                        habitsAdapter.setListeningTextChange(false);
                    }

                }

                @Override
                public void onOpened() {
                    Log.i(TAG, "expandableLayout onOpened "+expandableHolder.expandableLayout.getClosePosition());
                    // Enable textChange listener when Layout is opened to get the new input data.
                    if(TextUtils.equals(mProfileDataArrayList.get(position).getKey(), SECTION_SOCIAL_HEADLINE)){
                        socialAdapter.setListeningTextChange(true);
                    }else if(TextUtils.equals(mProfileDataArrayList.get(position).getKey(), SECTION_HABITS_HEADLINE)){
                        habitsAdapter.setListeningTextChange(true);
                    }

                }

                @Override
                public void onPreClose() {
                    Log.i(TAG, "expandableLayout onPreClose "+expandableHolder.expandableLayout.getClosePosition());
                    toggleRotation(expandableHolder.expandButton, 180f, 0f).start();
                    expandableHolder.expandableLayout.setExpanded(false);
                    // Disable textChange listener when Layout is not opened.
                    // If not disabled it will override transient data when expand with random data from recycled text fields
                    if(TextUtils.equals(mProfileDataArrayList.get(position).getKey(), SECTION_SOCIAL_HEADLINE)){
                        socialAdapter.setListeningTextChange(false);
                    }else if(TextUtils.equals(mProfileDataArrayList.get(position).getKey(), SECTION_HABITS_HEADLINE)){
                        habitsAdapter.setListeningTextChange(false);
                    }
                }

                @Override
                public void onClosed() {
                    Log.i(TAG, "expandableLayout onClosed "+expandableHolder.expandableLayout.getClosePosition());
                    // Disable textChange listener when Layout is not opened.
                    // If not disabled it will override transient data when expand with random data from recycled text fields
                    if(TextUtils.equals(mProfileDataArrayList.get(position).getKey(), SECTION_SOCIAL_HEADLINE)){
                        socialAdapter.setListeningTextChange(false);
                    }else if(TextUtils.equals(mProfileDataArrayList.get(position).getKey(), SECTION_HABITS_HEADLINE)){
                        habitsAdapter.setListeningTextChange(false);
                    }
                }
            });

            // click listener using interface
            expandableHolder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    Log.i(TAG, "expandableLayout expandToggle clicked= position= "+position);
                    Log.i(TAG, "expandableLayout onToggle "+expandableHolder.expandableLayout.getCurrentPosition());
                    expandableHolder.expandableLayout.toggle();
                    /*for (Social social : socialArrayList) {
                        System.out.println(social);
                        if(social != null && null != social.getValue()){
                            Log.d(TAG, "socialArrayList loop= " +social.getKey()+" url= "+social.getValue().getUrl());
                        }
                    }*/
                }
            });

            // bad way for ClickListener without using interface
            /*expandableHolder.row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "expandToggle id clicked= ");
                    expandableHolder.expandableLayout.toggle();

                }
            });*/

        }

        /*switch (mProfileDataArrayList.get(position).getSection()){
            case 1:
                holder.sectionHeader.setText(R.string.about_user_headline);
                Log.i(TAG, "about section="+ mProfileDataArrayList.get(position).getKey());
                break;
            case 2:
                holder.sectionHeader.setText(R.string.user_work_school_headline);
                Log.i(TAG, "work education section="+ mProfileDataArrayList.get(position).getKey());

                break;
            case 3:
                holder.sectionHeader.setText(R.string.user_Habits_Hobbies_headline);
                Log.i(TAG, "habits section="+ mProfileDataArrayList.get(position).getKey());
                break;
        }

        // if not first item check if item above has the same header
        if (position > 0 && mProfileDataArrayList.get(position - 1).getSection()
                == (mProfileDataArrayList.get(position).getSection())) {
            holder.divider.setVisibility(View.GONE);
            holder.sectionHeader.setVisibility(View.GONE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
            holder.sectionHeader.setVisibility(View.VISIBLE);

        }

        // birthDate text value and icons
        if(mProfileDataArrayList.get(position).getKey().equals("birthDate")){
            holder.itemValue.setText(fragmentContext.getString(R.string.user_age_value, mProfileDataArrayList.get(position).getValue()));
            holder.itemIcon.setImageResource(R.drawable.ic_cake_black_24dp);
        }

        //gender text value and icons
        if(mProfileDataArrayList.get(position).getKey().equals("gender")){

            switch (mProfileDataArrayList.get(position).getValue()) {
                case "female":
                    holder.itemValue.setText(R.string.female);
                    holder.itemIcon.setImageResource(R.drawable.ic_female);
                    break;
                case "male":
                    holder.itemValue.setText(R.string.male);
                    holder.itemIcon.setImageResource(R.drawable.ic_male);
                    break;
                case"transsexual":
                    holder.itemValue.setText(R.string.transsexual);
                    holder.itemIcon.setImageResource(R.drawable.ic_transsexual);
                    break;
                default:
                    holder.itemValue.setText(R.string.not_specified);
                    holder.itemIcon.setVisibility(View.INVISIBLE);
                    break;
            }
        }// end of gender text value and icons

        // nationality text value and icons
        if(mProfileDataArrayList.get(position).getKey().equals("nationality")){
            holder.itemValue.setText(fragmentContext.getString(R.string.user_country_value, mProfileDataArrayList.get(position).getValue()));
            holder.itemIcon.setImageResource(R.drawable.ic_flag_black_24dp);
        }

        // hometown text value and icons
        if(mProfileDataArrayList.get(position).getKey().equals("hometown")){
            holder.itemValue.setText(fragmentContext.getString(R.string.user_hometown_value, mProfileDataArrayList.get(position).getValue()));
            holder.itemIcon.setImageResource(R.drawable.ic_location_on_black_24dp);
        }

        // horoscope text value and icons
        if(mProfileDataArrayList.get(position).getKey().equals("horoscope")){
            switch (mProfileDataArrayList.get(position).getValue()) {
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
        if(mProfileDataArrayList.get(position).getKey().equals("lives")){
            holder.itemValue.setText(fragmentContext.getString(R.string.user_lives_in_value, mProfileDataArrayList.get(position).getValue()));
            holder.itemIcon.setImageResource(R.drawable.ic_home_black_24dp);
        }

        // politics text value and icons
        if(mProfileDataArrayList.get(position).getKey().equals("politics")){
            holder.itemValue.setText(fragmentContext.getString(R.string.user_politics_value, mProfileDataArrayList.get(position).getValue()));
            holder.itemIcon.setImageResource(R.drawable.ic_voting);
        }

        // religion text value and icons
        if(mProfileDataArrayList.get(position).getKey().equals("religion")){
            holder.itemValue.setText(fragmentContext.getString(R.string.user_religion_value, mProfileDataArrayList.get(position).getValue()));
            holder.itemIcon.setImageResource(R.drawable.ic_praying_hands);
        }

        // work text value and icons
        if(mProfileDataArrayList.get(position).getKey().equals("work")){
            holder.itemValue.setText(fragmentContext.getString(R.string.user_work_value, mProfileDataArrayList.get(position).getValue()));
            holder.itemIcon.setImageResource(R.drawable.ic_work_black_24dp);
        }
        // college text value and icons
        if(mProfileDataArrayList.get(position).getKey().equals("college")){
            holder.itemValue.setText(fragmentContext.getString(R.string.user_college_value, mProfileDataArrayList.get(position).getValue()));
            holder.itemIcon.setImageResource(R.drawable.ic_school_black_24dp);
        }

    // college text value and icons
        if(mProfileDataArrayList.get(position).getKey().equals("school")){
        holder.itemValue.setText(fragmentContext.getString(R.string.user_school_value, mProfileDataArrayList.get(position).getValue()));
        holder.itemIcon.setImageResource(R.drawable.ic_school_black_24dp);
    }

        // smoke text value and icons
        if(mProfileDataArrayList.get(position).getKey().equals("smoke")){
            holder.itemIcon.setImageResource(R.drawable.ic_smoking_rooms_black_24dp);
            if(mProfileDataArrayList.get(position).getValue().equals("true")){
                holder.itemValue.setText(fragmentContext.getString(R.string.user_smoke_value, fragmentContext.getString(R.string.yes)));
            }else{
                holder.itemValue.setText(fragmentContext.getString(R.string.user_smoke_value, fragmentContext.getString(R.string.no)));
            }
        }

        // shisha text value and icons
        if(mProfileDataArrayList.get(position).getKey().equals("shisha")){
            holder.itemIcon.setImageResource(R.drawable.ic_shisha);
            if(mProfileDataArrayList.get(position).getValue().equals("true")){
                holder.itemValue.setText(fragmentContext.getString(R.string.user_water_pipe_value, fragmentContext.getString(R.string.yes)));
            }else{
                holder.itemValue.setText(fragmentContext.getString(R.string.user_water_pipe_value, fragmentContext.getString(R.string.no)));
            }
        }

        // drugs text value and icons
        if(mProfileDataArrayList.get(position).getKey().equals("drugs")){
            holder.itemIcon.setImageResource(R.drawable.ic_injection);
            if(mProfileDataArrayList.get(position).getValue().equals("true")){
                holder.itemValue.setText(fragmentContext.getString(R.string.user_drugs_value, fragmentContext.getString(R.string.yes)));
            }else{
                holder.itemValue.setText(fragmentContext.getString(R.string.user_drugs_value, fragmentContext.getString(R.string.no)));
            }
        }

        // drink text value and icons
        if(mProfileDataArrayList.get(position).getKey().equals("drink")){
            holder.itemIcon.setImageResource(R.drawable.ic_drinking_24dp);
            if(mProfileDataArrayList.get(position).getValue().equals("true")){
                holder.itemValue.setText(fragmentContext.getString(R.string.user_drink_value, fragmentContext.getString(R.string.yes)));
            }else{
                holder.itemValue.setText(fragmentContext.getString(R.string.user_drink_value, fragmentContext.getString(R.string.no)));
            }
        }

        // athlete text value and icons
        if(mProfileDataArrayList.get(position).getKey().equals("athlete")){
            holder.itemIcon.setImageResource(R.drawable.ic_fitness_athlete_24dp);
            if(mProfileDataArrayList.get(position).getValue().equals("true")){
                holder.itemValue.setText(fragmentContext.getString(R.string.user_athlete_value, fragmentContext.getString(R.string.yes)));
            }else{
                holder.itemValue.setText(fragmentContext.getString(R.string.user_athlete_value, fragmentContext.getString(R.string.no)));
            }
        }
        // gamer text value and icons
        if(mProfileDataArrayList.get(position).getKey().equals("gamer")){
            holder.itemIcon.setImageResource(R.drawable.ic_games_black_24dp);
            if(mProfileDataArrayList.get(position).getValue().equals("true")){
                holder.itemValue.setText(fragmentContext.getString(R.string.user_gamer_value, fragmentContext.getString(R.string.yes)));
            }else{
                holder.itemValue.setText(fragmentContext.getString(R.string.user_gamer_value, fragmentContext.getString(R.string.no)));
            }
        }
        // travel text value and icons
        if(mProfileDataArrayList.get(position).getKey().equals("travel")){
            holder.itemIcon.setImageResource(R.drawable.ic_flight_24dp);
            if(mProfileDataArrayList.get(position).getValue().equals("true")){
                holder.itemValue.setText(fragmentContext.getString(R.string.user_travel_value, fragmentContext.getString(R.string.yes)));
            }else{
                holder.itemValue.setText(fragmentContext.getString(R.string.user_travel_value, fragmentContext.getString(R.string.no)));
            }
        }
        // cook text value and icons
        if(mProfileDataArrayList.get(position).getKey().equals("cook")){
            holder.itemIcon.setImageResource(R.drawable.ic_cook_24dp);
            if(mProfileDataArrayList.get(position).getValue().equals("true")){
                holder.itemValue.setText(fragmentContext.getString(R.string.user_cook_value, fragmentContext.getString(R.string.yes)));
            }else{
                holder.itemValue.setText(fragmentContext.getString(R.string.user_cook_value, fragmentContext.getString(R.string.no)));
            }
        }
        // read text value and icons
        if(mProfileDataArrayList.get(position).getKey().equals("read")){
            holder.itemIcon.setImageResource(R.drawable.ic_library_book_24dp);
            if(mProfileDataArrayList.get(position).getValue().equals("true")){
                holder.itemValue.setText(fragmentContext.getString(R.string.user_read_value, fragmentContext.getString(R.string.yes)));
            }else{
                holder.itemValue.setText(fragmentContext.getString(R.string.user_read_value, fragmentContext.getString(R.string.no)));
            }
        }*/

       /* holder.itemValue.setText(mProfileDataArrayList.get(position).getKey()+" value= "
                +mProfileDataArrayList.get(position).getValue());*/


        Log.i(TAG, "onBindViewHolder get value="+ mProfileDataArrayList.get(position).getValue());

        //holder.itemValue.setText(mProfileDataArrayList.indexOf(position));
    }

    private ObjectAnimator toggleRotation(ImageView expandButton, float from, float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(expandButton, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
        return  animator;
    }

    @Override
    public int getItemCount() {
        //the size of the list

        /*for (int i = 0; i < mProfileDataArrayList.size(); i++) {
            Log.d(TAG, "getItemCount ="+mProfileDataArrayList.size());

            if (mProfileDataArrayList.get(i).getSection() == SECTION_ABOUT
                    && !mProfileDataArrayList.get(i).getKey().equals(SECTION_ABOUT_HEADLINE)) {
                Log.d(TAG, "mProfileDataArrayList SECTION_ABOUT= " + mProfileDataArrayList.get(i).getKey());
                aboutArrayList.add(new Profile(mProfileDataArrayList.get(i).getKey()
                        ,mProfileDataArrayList.get(i).getValue(), mProfileDataArrayList.get(i).getSection()));

                mProfileDataArrayList.remove(mProfileDataArrayList.get(i));
            }

            if (mProfileDataArrayList.get(i).getSection() == SECTION_WORK
                    && !mProfileDataArrayList.get(i).getKey().equals(SECTION_WORK_HEADLINE)) {
                Log.d(TAG, "mProfileDataArrayList SECTION_WORK= " + mProfileDataArrayList.get(i).getKey());
                contactsArrayList.add(new Profile(mProfileDataArrayList.get(i).getKey()
                        ,mProfileDataArrayList.get(i).getValue(), mProfileDataArrayList.get(i).getSection()));

                mProfileDataArrayList.remove(mProfileDataArrayList.get(i));
            }

            if (mProfileDataArrayList.get(i).getSection() == SECTION_HABITS
                    && !mProfileDataArrayList.get(i).getKey().equals(SECTION_HABITS_HEADLINE)) {
                Log.d(TAG, "mProfileDataArrayList SECTION_HABITS= " + mProfileDataArrayList.get(i).getKey());
               habitsArrayList.add(new Profile(mProfileDataArrayList.get(i).getKey()
                        ,mProfileDataArrayList.get(i).getValue(), mProfileDataArrayList.get(i).getSection()));

                mProfileDataArrayList.remove(mProfileDataArrayList.get(i));
            }

        }*/
        Log.d(TAG, "getItemCount ="+ mProfileDataArrayList.size());
        return  mProfileDataArrayList.size();
        //return  10;
    }

    @Override
    public int getItemViewType(int position) {

        switch (mProfileDataArrayList.get(position).getSection()){
            case 100:
                return SECTION_AVATAR;
            case 200:
                return SECTION_COVER;
            case 300:
                return SECTION_EDIT_TEXT;
            case 400:
                return SECTION_TEXT;
            case 500:
                return SECTION_SPINNER;
            case 600:
                return SECTION_ABOUT;
            case 700:
                return SECTION_WORK;
            case 800:
                return SECTION_HABITS;
            case 900:
                return SECTION_SOCIAL;
        }
        return 1000;
    }

    // ViewHolder for user info list /////
    public class ExpandableHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        View row;
        private TextView  sectionHeadline;
        private ImageView expandButton;
        private RecyclerView expandableRecycler;
        private ExpandableRelativeLayout expandableLayout;
        ItemClickListener itemClickListener;


        public ExpandableHolder(View itemView, ItemClickListener itemClickListener) {
            super(itemView);
            this.itemClickListener = itemClickListener;

            row = itemView;
            sectionHeadline = row.findViewById(R.id.section_headline);
            expandButton = row.findViewById(R.id.expand_button);
            expandableLayout = row.findViewById(R.id.expandableLayout);
            expandableRecycler = row.findViewById(R.id.expandable_recycler);

            row.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if(itemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION){
                itemClickListener.onClick(view, getAdapterPosition(), false);
            }

        }

        // needed only if i want the listener to be inside the adapter
        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }
    }

    // ViewHolder for user avatar images list /////
    public class avatarImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        View row;
        private TextView itemHeadline;
        private ImageView profileImage, icon, progressIcon;
        private View divider;
        private FrameLayout frameLayout;
        ItemClickListener itemClickListener;


        public avatarImageHolder(View itemView, ItemClickListener itemClickListener) {
            super(itemView);
            this.itemClickListener = itemClickListener;

            row = itemView;
            itemHeadline = row.findViewById(R.id.item_title);
            profileImage = row.findViewById(R.id.user_image);
            icon = row.findViewById(R.id.edit_profile_icon);
            progressIcon = row.findViewById(R.id.progress_icon);
            frameLayout = row.findViewById(R.id.image_frameLayout);

            divider = row.findViewById(R.id.top_divider);

            row.setOnClickListener(this);
        }



        @Override
        public void onClick(View view) {
            if(itemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION){
                itemClickListener.onClick(view, getAdapterPosition(), false);
            }

        }

        // needed only if i want the listener to be inside the adapter
        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }
    }

    // ViewHolder for user cover images list /////
    public class coverImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        View row;
        private TextView itemHeadline;
        private ImageView  coverImage, icon, progressIcon;
        private View divider;
        private FrameLayout frameLayout;
        ItemClickListener itemClickListener;


        public coverImageHolder(View itemView, ItemClickListener itemClickListener) {
            super(itemView);
            this.itemClickListener = itemClickListener;

            row = itemView;
            itemHeadline = row.findViewById(R.id.item_title);
            coverImage = row.findViewById(R.id.cover_image);
            icon = row.findViewById(R.id.edit_profile_icon);
            progressIcon = row.findViewById(R.id.progress_icon);
            frameLayout = row.findViewById(R.id.image_frameLayout);

            divider = row.findViewById(R.id.top_divider);

            row.setOnClickListener(this);
        }



        @Override
        public void onClick(View view) {
            if(itemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION){
                itemClickListener.onClick(view, getAdapterPosition(), false);
            }

        }

        // needed only if i want the listener to be inside the adapter
        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }
    }


    // ViewHolder for user textInputs list /////
    public class TextInputHolder extends RecyclerView.ViewHolder {

        View row;
        private TextInputEditText itemValue;
        private TextInputLayout inputLayout;


        public TextInputHolder(View itemView ) {
            super(itemView);
            //itemView = row;
            row = itemView;
            itemValue = row.findViewById(R.id.edit_profile_value);
            inputLayout = row.findViewById(R.id.edit_profile_InputLayout);
            itemValue.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence editable, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence editable, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    Log.d(TAG, "Editable Name= "+ editable.toString()+ "position= "+getAdapterPosition()+ " key= "+mProfileDataArrayList.get(getAdapterPosition()).getKey());
                    if(TextUtils.isEmpty((String.valueOf(editable).trim()))){
                        mProfileDataArrayList.get(getAdapterPosition()).setValue(null);
                        // set EditProfileViewModel.user values
                        switch (mProfileDataArrayList.get(getAdapterPosition()).getKey()){
                            case "name":
                                mEditProfileViewModel.getUser().setName(null);
                                break;
                            case "biography":
                                mEditProfileViewModel.getUser().setBiography(null);
                                break;
                        }
                    }else{
                        mProfileDataArrayList.get(getAdapterPosition()).setValue(editable.toString());

                        // set EditProfileViewModel.user values
                        switch (mProfileDataArrayList.get(getAdapterPosition()).getKey()){
                            case "name":
                                mEditProfileViewModel.getUser().setName(String.valueOf(editable).trim());
                                break;
                            case "biography":
                                mEditProfileViewModel.getUser().setBiography(String.valueOf(editable).trim());
                                break;
                        }
                    }

                }
            });

        }

    }

    // ViewHolder for user textInputs list /////
    public class TextHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View row;
        private TextView itemHeadline, itemValue;
        ItemClickListener itemClickListener;

        public TextHolder(View itemView, ItemClickListener itemClickListener) {
            super(itemView);
            //itemView = row;
            this.itemClickListener = itemClickListener;

            row = itemView;
            itemHeadline = row.findViewById(R.id.section_header);
            itemValue = row.findViewById(R.id.item_value);

            row.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            if(itemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION){
                itemClickListener.onClick(view, getAdapterPosition(), false);
            }

        }

        // needed only if i want the listener to be inside the adapter
        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }
    }

    // ViewHolder for user SpinnerHolder list /////
    public class SpinnerHolder extends RecyclerView.ViewHolder {

        View row;
        private TextView itemHeadline;
        private Spinner itemValue;


        public SpinnerHolder(View itemView) {
            super(itemView);
            //itemView = row;
            row = itemView;
            itemHeadline = row.findViewById(R.id.item_title);
            itemValue = row.findViewById(R.id.edit_profile_value);

        }

    }


}
