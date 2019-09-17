package com.trackaty.chat.adapters;

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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.trackaty.chat.R;
import com.trackaty.chat.ViewModels.EditProfileViewModel;
import com.trackaty.chat.models.Social;
import com.trackaty.chat.models.SocialObj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.trackaty.chat.utils.StringUtils.setMaxLength;

public class SocialAdapter extends RecyclerView.Adapter<SocialAdapter.ViewHolder> {

    private final static String TAG = SocialAdapter.class.getSimpleName();

    public  final static int  BIG_INPUT_MAX_LENGTH = 300;
    public  final static int  SMALL_INPUT_MAX_LENGTH = 50;

    public  final static int  BIG_INPUT_MAX_LINES = 4;
    public  final static int  SMALL_INPUT_MAX_LINES = 1;

    public ArrayList<Social> socialArrayList;
    public Fragment fragmentContext;
    private EditProfileViewModel mEditProfileViewModel;

    Map<String, SocialObj> socialMap = new HashMap<>();

    public SocialAdapter(Fragment fragmentContext, ArrayList<Social> socialArrayList){
        this.socialArrayList = socialArrayList;
        this.fragmentContext = fragmentContext;
        //socialObj = new SocialObj();
        // get EditProfileViewModel to access user object
        mEditProfileViewModel = ViewModelProviders.of(fragmentContext).get(EditProfileViewModel.class);

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

        // set spinner value
        if (null != socialArrayList.get(position).getValue()) {
            Log.d(TAG, "onBindViewHolder socialObj key ="+socialArrayList.get(position).getKey()+" url= "+socialArrayList.get(position).getValue().getUrl()+ " socialObj public= "+socialArrayList.get(position).getValue().getPublic()+" position= "+position);
            if(socialArrayList.get(position).getValue().getPublic()){
                // public is true
                holder.spinnerValue.setSelection(0);
                Log.d(TAG, "display 0 option on sorting spinner");
            }else {
                // public is false
                holder.spinnerValue.setSelection(1);
                Log.d(TAG, "display 1 option on sorting spinner");
            }
        }

        switch (socialArrayList.get(position).getKey()) {
            case "phone":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue().getUrl());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, SMALL_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(fragmentContext.getString(R.string.user_phone_hint));//Set Hint/label
                holder.inputLayout.setHelperText(fragmentContext.getString(R.string.user_Phone_helper)); // Set Helper

                // phone numbers only
                holder.itemValue.setInputType(InputType.TYPE_CLASS_PHONE);
                break;
            case "facebook":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue().getUrl());
                    Log.d(TAG, "socialObj facebook url= "+socialArrayList.get(position).getValue().getUrl()+" public= "+socialArrayList.get(position).getValue().getPublic());

                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(fragmentContext.getString(R.string.user_facebook_hint));//Set Hint/label
                holder.inputLayout.setHelperText(fragmentContext.getString(R.string.user_facebook_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                break;
            case "instagram":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue().getUrl());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(fragmentContext.getString(R.string.user_instagram_hint));//Set Hint/label
                holder.inputLayout.setHelperText(fragmentContext.getString(R.string.user_instagram_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                break;
            case "twitter":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue().getUrl());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(fragmentContext.getString(R.string.user_twitter_hint));//Set Hint/label
                holder.inputLayout.setHelperText(fragmentContext.getString(R.string.user_twitter_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                break;
            case "snapchat":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue().getUrl());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(fragmentContext.getString(R.string.user_snapchat_hint));//Set Hint/label
                holder.inputLayout.setHelperText(fragmentContext.getString(R.string.user_snapchat_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                break;
            case "tumblr":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue().getUrl());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(fragmentContext.getString(R.string.user_tumblr_hint));//Set Hint/label
                holder.inputLayout.setHelperText(fragmentContext.getString(R.string.user_tumblr_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                break;
            case "pubg":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue().getUrl());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(fragmentContext.getString(R.string.user_pubg_hint));//Set Hint/label
                holder.inputLayout.setHelperText(fragmentContext.getString(R.string.user_pubg_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                break;
            case "vk":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue().getUrl());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(fragmentContext.getString(R.string.user_vk_hint));//Set Hint/label
                holder.inputLayout.setHelperText(fragmentContext.getString(R.string.user_vk_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                break;
            case "askfm":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue().getUrl());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(fragmentContext.getString(R.string.user_askfm_hint));//Set Hint/label
                holder.inputLayout.setHelperText(fragmentContext.getString(R.string.user_askfm_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                break;
            case "curiouscat":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue().getUrl());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(fragmentContext.getString(R.string.user_curiouscat_hint));//Set Hint/label
                holder.inputLayout.setHelperText(fragmentContext.getString(R.string.user_curiouscat_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                break;
            case "saraha":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue().getUrl());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(fragmentContext.getString(R.string.user_saraha_hint));//Set Hint/label
                holder.inputLayout.setHelperText(fragmentContext.getString(R.string.user_saraha_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                break;
            case "pinterest":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue().getUrl());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(fragmentContext.getString(R.string.user_pinterest_hint));//Set Hint/label
                holder.inputLayout.setHelperText(fragmentContext.getString(R.string.user_pinterest_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                break;
            case "soundcloud":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue().getUrl());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(fragmentContext.getString(R.string.user_soundcloud_hint));//Set Hint/label
                holder.inputLayout.setHelperText(fragmentContext.getString(R.string.user_soundcloud_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                break;
            case "spotify":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue().getUrl());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(fragmentContext.getString(R.string.user_spotify_hint));//Set Hint/label
                holder.inputLayout.setHelperText(fragmentContext.getString(R.string.user_spotify_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                break;
            case "anghami":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue().getUrl());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(fragmentContext.getString(R.string.user_anghami_hint));//Set Hint/label
                holder.inputLayout.setHelperText(fragmentContext.getString(R.string.user_anghami_helper)); // Set Helper

                // numbers only
                holder.itemValue.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case "twitch":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue().getUrl());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(fragmentContext.getString(R.string.user_twitch_hint));//Set Hint/label
                holder.inputLayout.setHelperText(fragmentContext.getString(R.string.user_twitch_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                break;
            case "youtube":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue().getUrl());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(fragmentContext.getString(R.string.user_youtube_hint));//Set Hint/label
                holder.inputLayout.setHelperText(fragmentContext.getString(R.string.user_youtube_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_URI);
                break;
            case "linkedIn":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue().getUrl());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(fragmentContext.getString(R.string.user_linkedIn_hint));//Set Hint/label
                holder.inputLayout.setHelperText(fragmentContext.getString(R.string.user_linkedIn_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case "wikipedia":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue().getUrl());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(fragmentContext.getString(R.string.user_wikipedia_hint));//Set Hint/label
                holder.inputLayout.setHelperText(fragmentContext.getString(R.string.user_wikipedia_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_URI);
                break;
            case "website":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue().getUrl());
                }else{
                    holder.itemValue.setText(null);
                }
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(fragmentContext.getString(R.string.user_website_hint));//Set Hint/label
                holder.inputLayout.setHelperText(fragmentContext.getString(R.string.user_website_helper)); // Set Helper
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_URI);
                break;
        }


        //Log.d(TAG, "onBindViewHolder get value="+ contactsArrayList.get(position).getValue());

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
        private SocialObj socialObj;

        public ViewHolder(View itemView) {
            super(itemView);
            //itemView = row;

            row = itemView;
            itemValue = row.findViewById(R.id.edit_profile_value);
            inputLayout = row.findViewById(R.id.edit_profile_InputLayout);
            spinnerValue = row.findViewById(R.id.social_spinner);
            socialObj = new SocialObj();
            Log.d(TAG, "Adapter position = "+getAdapterPosition());
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
                    if(TextUtils.isEmpty(String.valueOf(editable).trim())){
                        //socialArrayList.get(getAdapterPosition()).setValue(null);
                        // set EditProfileViewModel.user as null
                        setUserSocial(socialArrayList.get(getAdapterPosition()).getKey(), null);
                        //setUserSocial(socialArrayList.get(getAdapterPosition()).getKey(), null);
                    }else{
                        //contactsArrayList.get(getAdapterPosition()).setValue(editable.toString());
                        //SocialObj socialObj= new SocialObj();
                        socialObj.setUrl(editable.toString());
                        // get selected spinner position
                        switch (spinnerValue.getSelectedItemPosition()){
                            case 0:
                                Log.d(TAG, "afterTextChanged spinner item 0 is selected");
                                socialObj.setPublic(true);
                                break;
                            case 1:
                                Log.d(TAG, "afterTextChanged spinner item 1 is selected");
                                socialObj.setPublic(false);
                                break;
                        }

                        //socialMap.put("facebook", socialObj);
                        Log.d(TAG, "afterTextChanged socialObj url= "+socialObj.getUrl()+ " socialObj public= "+socialObj.getPublic()+" key= "+socialArrayList.get(getAdapterPosition()).getKey());
                        //socialArrayList.get(getAdapterPosition()).setValue(socialObj);
                        // set EditProfileViewModel.user values
                        setUserSocial(socialArrayList.get(getAdapterPosition()).getKey(), socialObj);
                    }
                }
            });

            spinnerValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int selectedItemPosition, long id) {
                    // your code here for onItemSelected
                    switch (selectedItemPosition){ // display sorting option selected from shared preference
                        case 0:
                            //contactsArrayList.get(getAdapterPosition()).setValue(true);

                            /*socialPrivacy.setPublic(true);
                            socialMap.put("social", socialPrivacy);*/
                            if(TextUtils.isEmpty(String.valueOf(itemValue.getText()).trim())){
                                socialArrayList.get(getAdapterPosition()).setValue(null);
                                setUserSocial(socialArrayList.get(getAdapterPosition()).getKey(), null);
                            }else{
                                socialObj.setPublic(true);
                                socialObj.setUrl(String.valueOf(itemValue.getText()));
                            }

                            Log.d(TAG, "spinner socialObj url= "+socialObj.getUrl()+ " socialObj public= "+socialObj.getPublic()+" key= "+socialArrayList.get(getAdapterPosition()).getKey());
                            //contactsArrayList.get(getAdapterPosition()).setValue(socialObj);
                            Log.d(TAG, "spinner item 0 is selected= " );
                            break;
                        case 1:
                            //contactsArrayList.get(getAdapterPosition()).setPublic(false);
                            /*socialPrivacy.setPublic(false);
                            socialMap.put("social", socialPrivacy);*/
                            if(TextUtils.isEmpty(String.valueOf(itemValue.getText()).trim())){
                                socialArrayList.get(getAdapterPosition()).setValue(null);
                            }else{
                                socialObj.setPublic(false);
                                socialObj.setUrl(String.valueOf(itemValue.getText()));
                            }
                            Log.d(TAG, "spinner socialObj url= "+socialObj.getUrl()+ " socialObj public= "+socialObj.getPublic()+" key= "+socialArrayList.get(getAdapterPosition()).getKey());
                            //contactsArrayList.get(getAdapterPosition()).setValue(socialObj);
                            Log.d(TAG, "spinner item 1 is selected= " );
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

    private void setUserSocial(String key, SocialObj socialObj) {

        switch (key){
            case "phone":
                mEditProfileViewModel.getUser().setPhone(socialObj);
                if(socialObj != null){
                    Log.d(TAG, "setUserSocial key= "+ key+ " socialObj url= "+socialObj.getUrl()+" privacy= "+socialObj.getPublic());
                }
                break;
            case "facebook":
                mEditProfileViewModel.getUser().setFacebook(socialObj);
                if(socialObj != null){
                    Log.d(TAG, "setUserSocial key= "+ key+ " socialObj url= "+socialObj.getUrl()+" privacy= "+socialObj.getPublic());
                }
                break;
            case "instagram":
                mEditProfileViewModel.getUser().setInstagram(socialObj);
                if(socialObj != null){
                    Log.d(TAG, "setUserSocial key= "+ key+ " socialObj url= "+socialObj.getUrl()+" privacy= "+socialObj.getPublic());
                }
                break;
            case "twitter":
                mEditProfileViewModel.getUser().setTwitter(socialObj);
                if(socialObj != null){
                    Log.d(TAG, "setUserSocial key= "+ key+ " socialObj url= "+socialObj.getUrl()+" privacy= "+socialObj.getPublic());
                }
                break;
            case "snapchat":
                mEditProfileViewModel.getUser().setSnapchat(socialObj);
                if(socialObj != null){
                    Log.d(TAG, "setUserSocial key= "+ key+ " socialObj url= "+socialObj.getUrl()+" privacy= "+socialObj.getPublic());
                }
                break;
            case "tumblr":
                mEditProfileViewModel.getUser().setTumblr(socialObj);
                if(socialObj != null){
                    Log.d(TAG, "setUserSocial key= "+ key+ " socialObj url= "+socialObj.getUrl()+" privacy= "+socialObj.getPublic());
                }
                break;
            case "pubg":
                mEditProfileViewModel.getUser().setPubg(socialObj);
                if(socialObj != null){
                    Log.d(TAG, "setUserSocial key= "+ key+ " socialObj url= "+socialObj.getUrl()+" privacy= "+socialObj.getPublic());
                }
                break;
            case "vk":
                mEditProfileViewModel.getUser().setVk(socialObj);
                if(socialObj != null){
                    Log.d(TAG, "setUserSocial key= "+ key+ " socialObj url= "+socialObj.getUrl()+" privacy= "+socialObj.getPublic());
                }
                break;
            case "askfm":
                mEditProfileViewModel.getUser().setAskfm(socialObj);
                if(socialObj != null){
                    Log.d(TAG, "setUserSocial key= "+ key+ " socialObj url= "+socialObj.getUrl()+" privacy= "+socialObj.getPublic());
                }
                break;
            case "curiouscat":
                mEditProfileViewModel.getUser().setCuriouscat(socialObj);
                if(socialObj != null){
                    Log.d(TAG, "setUserSocial key= "+ key+ " socialObj url= "+socialObj.getUrl()+" privacy= "+socialObj.getPublic());
                }
                break;
            case "saraha":
                mEditProfileViewModel.getUser().setSaraha(socialObj);
                if(socialObj != null){
                    Log.d(TAG, "setUserSocial key= "+ key+ " socialObj url= "+socialObj.getUrl()+" privacy= "+socialObj.getPublic());
                }
                break;
            case "pinterest":
                mEditProfileViewModel.getUser().setPinterest(socialObj);
                if(socialObj != null){
                    Log.d(TAG, "setUserSocial key= "+ key+ " socialObj url= "+socialObj.getUrl()+" privacy= "+socialObj.getPublic());
                }
                break;
            case "soundcloud":
                mEditProfileViewModel.getUser().setSoundcloud(socialObj);
                if(socialObj != null){
                    Log.d(TAG, "setUserSocial key= "+ key+ " socialObj url= "+socialObj.getUrl()+" privacy= "+socialObj.getPublic());
                }
                break;
            case "spotify":
                mEditProfileViewModel.getUser().setSpotify(socialObj);
                if(socialObj != null){
                    Log.d(TAG, "setUserSocial key= "+ key+ " socialObj url= "+socialObj.getUrl()+" privacy= "+socialObj.getPublic());
                }
                break;
            case "anghami":
                mEditProfileViewModel.getUser().setAnghami(socialObj);
                if(socialObj != null){
                    Log.d(TAG, "setUserSocial key= "+ key+ " socialObj url= "+socialObj.getUrl()+" privacy= "+socialObj.getPublic());
                }
                break;
            case "twitch":
                mEditProfileViewModel.getUser().setTwitch(socialObj);
                if(socialObj != null){
                    Log.d(TAG, "setUserSocial key= "+ key+ " socialObj url= "+socialObj.getUrl()+" privacy= "+socialObj.getPublic());
                }
                break;
            case "youtube":
                mEditProfileViewModel.getUser().setYoutube(socialObj);
                if(socialObj != null){
                    Log.d(TAG, "setUserSocial key= "+ key+ " socialObj url= "+socialObj.getUrl()+" privacy= "+socialObj.getPublic());
                }
                break;
            case "linkedIn":
                mEditProfileViewModel.getUser().setLinkedIn(socialObj);
                if(socialObj != null){
                    Log.d(TAG, "setUserSocial key= "+ key+ " socialObj url= "+socialObj.getUrl()+" privacy= "+socialObj.getPublic());
                }
                break;
            case "wikipedia":
                mEditProfileViewModel.getUser().setWikipedia(socialObj);
                if(socialObj != null){
                    Log.d(TAG, "setUserSocial key= "+ key+ " socialObj url= "+socialObj.getUrl()+" privacy= "+socialObj.getPublic());
                }
                break;
            case "website":
                mEditProfileViewModel.getUser().setWebsite(socialObj);
                if(socialObj != null){
                    Log.d(TAG, "setUserSocial key= "+ key+ " socialObj url= "+socialObj.getUrl()+" privacy= "+socialObj.getPublic());
                }
                break;
        }

    }


}
