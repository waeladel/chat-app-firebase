package com.trackaty.chat.Adapters;

import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.trackaty.chat.R;
import com.trackaty.chat.models.Profile;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.trackaty.chat.Utils.StringUtils.setMaxLength;

public class SocialAdapter extends RecyclerView.Adapter<SocialAdapter.ViewHolder> {

    private final static String TAG = SocialAdapter.class.getSimpleName();

    public  final static int  BIG_INPUT_MAX_LENGTH = 80;
    public  final static int  SMALL_INPUT_MAX_LENGTH = 50;

    public  final static int  BIG_INPUT_MAX_LINES = 4;
    public  final static int  SMALL_INPUT_MAX_LINES = 1;

    public ArrayList<Profile> socialArrayList;
    public Context context;


    public SocialAdapter(Context context, ArrayList<Profile> socialArrayList){
        this.socialArrayList = socialArrayList;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_profile_text_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Log.i(TAG, "onBindViewHolder called="+ habitsArrayList.get(position));

        switch (socialArrayList.get(position).getKey()) {
            case "phone":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }
                setMaxLength(holder.itemValue, SMALL_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_phone_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_Phone_helper)); // Set Helper
                break;
            case "facebook":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }
                setMaxLength(holder.itemValue, SMALL_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_facebook_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_facebook_helper)); // Set Helper
                break;
            case "instagram":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }
                setMaxLength(holder.itemValue, SMALL_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_instagram_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_instagram_helper)); // Set Helper
                break;
            case "twitter":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }
                setMaxLength(holder.itemValue, SMALL_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_twitter_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_twitter_helper)); // Set Helper
                break;
            case "snapchat":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }
                setMaxLength(holder.itemValue, SMALL_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_snapchat_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_snapchat_helper)); // Set Helper
                break;
            case "tumblr":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }
                setMaxLength(holder.itemValue, SMALL_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_tumblr_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_tumblr_helper)); // Set Helper
                break;
            case "vk":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }
                setMaxLength(holder.itemValue, SMALL_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_vk_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_vk_helper)); // Set Helper
                break;
            case "askfm":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }
                setMaxLength(holder.itemValue, SMALL_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_askfm_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_askfm_helper)); // Set Helper
                break;
            case "curiouscat":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }
                setMaxLength(holder.itemValue, SMALL_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_curiouscat_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_curiouscat_helper)); // Set Helper
                break;
            case "saraha":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }
                setMaxLength(holder.itemValue, SMALL_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_saraha_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_saraha_helper)); // Set Helper
                break;
            case "pinterest":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }
                setMaxLength(holder.itemValue, SMALL_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_pinterest_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_pinterest_helper)); // Set Helper
                break;
            case "soundcloud":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }
                setMaxLength(holder.itemValue, SMALL_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_soundcloud_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_soundcloud_helper)); // Set Helper
                break;
            case "spotify":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }
                setMaxLength(holder.itemValue, SMALL_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_spotify_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_spotify_helper)); // Set Helper
                break;
            case "anghami":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }
                setMaxLength(holder.itemValue, SMALL_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_anghami_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_anghami_helper)); // Set Helper
                break;
            case "twitch":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }
                setMaxLength(holder.itemValue, SMALL_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_twitch_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_twitch_helper)); // Set Helper
                break;
            case "youtube":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }
                setMaxLength(holder.itemValue, SMALL_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_youtube_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_youtube_helper)); // Set Helper
                break;
            case "linkedIn":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }
                setMaxLength(holder.itemValue, SMALL_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_linkedIn_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_linkedIn_helper)); // Set Helper
                break;
            case "wikipedia":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }
                setMaxLength(holder.itemValue, SMALL_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_wikipedia_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_wikipedia_helper)); // Set Helper
                break;
            case "website":
                if (null != socialArrayList.get(position).getValue()) {
                    holder.itemValue.setText(socialArrayList.get(position).getValue());
                }
                setMaxLength(holder.itemValue, SMALL_INPUT_MAX_LENGTH);// set Max length
                holder.inputLayout.setHint(context.getString(R.string.user_website_hint));//Set Hint/label
                holder.inputLayout.setHelperText(context.getString(R.string.user_website_helper)); // Set Helper
                break;
        }

        // capitalize every first letter
        holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE
                | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        Log.d(TAG, "onBindViewHolder get value="+ socialArrayList.get(position).getValue());

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


        public ViewHolder(View itemView) {
            super(itemView);
            //itemView = row;

            row = itemView;
            itemValue = row.findViewById(R.id.edit_profile_value);
            inputLayout = row.findViewById(R.id.edit_profile_InputLayout);

        }

    }


}
