package com.trackaty.chat.Adapters;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.trackaty.chat.R;
import com.trackaty.chat.models.Profile;
import com.trackaty.chat.models.Social;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.trackaty.chat.Utils.StringUtils.setMaxLength;

public class SocialAdapter extends RecyclerView.Adapter<SocialAdapter.ViewHolder> {

    private final static String TAG = SocialAdapter.class.getSimpleName();

    public  final static int  BIG_INPUT_MAX_LENGTH = 300;
    public  final static int  SMALL_INPUT_MAX_LENGTH = 50;

    public  final static int  BIG_INPUT_MAX_LINES = 4;
    public  final static int  SMALL_INPUT_MAX_LINES = 1;

    public ArrayList<Social> socialArrayList;
    public Context context;


    public SocialAdapter(Context context, ArrayList<Social> socialArrayList){
        this.socialArrayList = socialArrayList;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_profile_social_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        //Log.i(TAG, "onBindViewHolder called="+ habitsArrayList.get(position));

        switch (socialArrayList.get(position).getKey()) {
            case "phone":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, SMALL_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_phone_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_Phone_helper)); // Set Helper

                // phone numbers only
                holder.itemValue.setInputType(InputType.TYPE_CLASS_PHONE);
                break;
            case "facebook":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_facebook_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_facebook_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                break;
            case "instagram":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_instagram_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_instagram_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                break;
            case "twitter":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_twitter_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_twitter_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                break;
            case "snapchat":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_snapchat_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_snapchat_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                break;
            case "tumblr":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_tumblr_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_tumblr_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                break;
            case "pubg":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_pubg_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_pubg_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                break;
            case "vk":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_vk_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_vk_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                break;
            case "askfm":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_askfm_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_askfm_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                break;
            case "curiouscat":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_curiouscat_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_curiouscat_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                break;
            case "saraha":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_saraha_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_saraha_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                break;
            case "pinterest":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_pinterest_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_pinterest_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                break;
            case "soundcloud":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_soundcloud_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_soundcloud_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                break;
            case "spotify":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_spotify_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_spotify_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                break;
            case "anghami":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_anghami_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_anghami_helper)); // Set Helper

                // numbers only
                holder.itemValue.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case "twitch":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_twitch_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_twitch_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                break;
            case "youtube":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_youtube_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_youtube_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_URI);
                break;
            case "linkedIn":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_linkedIn_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_linkedIn_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case "wikipedia":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_wikipedia_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_wikipedia_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_URI);
                break;
            case "website":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_website_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_website_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_URI);
                break;
        }


        //Log.d(TAG, "onBindViewHolder get value="+ socialArrayList.get(position).getValue());

        //holder.itemValue.setText(mProfileDataArrayList.indexOf(position));
    }

    @Override
    public int getItemCount() {
        //the size of the list
        Log.d(TAG, "getItemCount ="+socialArrayList.size());
        return  socialArrayList.size();
        //return  10;
    }

    /// ViewHolder for trips list /////
    public class ViewHolder extends RecyclerView.ViewHolder {

        View row;
        private TextInputEditText itemValue;
        private TextInputLayout inputLayout;
        private Spinner spinnerValue;


        public ViewHolder(View itemView) {
            super(itemView);
            //itemView = row;

            row = itemView;
            itemValue = row.findViewById(R.id.edit_profile_value);
            inputLayout = row.findViewById(R.id.edit_profile_InputLayout);
            spinnerValue = row.findViewById(R.id.social_spinner);

            itemValue.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence editable, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence editable, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    Log.d(TAG, "Editable = "+ editable.toString()+ " position= "+getAdapterPosition()+" key= "+socialArrayList.get(getAdapterPosition()).getKey());
                    if(TextUtils.isEmpty(editable)){
                        socialArrayList.get(getAdapterPosition()).setValue(null);
                    }else{
                        socialArrayList.get(getAdapterPosition()).setValue(editable.toString());
                    }
                }
            });

            spinnerValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int selectedItemPosition, long id) {
                    // your code here for onItemSelected
                    switch (selectedItemPosition){ // display sorting option selected from shared preference
                        case 1:
                            socialArrayList.get(getAdapterPosition()).setValue("true");
                            Log.d(TAG, "spinner item 0 is selected= " +socialArrayList.get(getAdapterPosition()).getValue());
                            break;
                        case 2:
                            socialArrayList.get(getAdapterPosition()).setValue("false");
                            Log.d(TAG, "spinner item 1 is selected= " +socialArrayList.get(getAdapterPosition()).getValue());
                            break;
                        default:
                            socialArrayList.get(getAdapterPosition()).setValue(null);
                            break;
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });
        }

    }


}