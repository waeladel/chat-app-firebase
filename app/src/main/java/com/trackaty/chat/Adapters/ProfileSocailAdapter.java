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
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.trackaty.chat.R;
import com.trackaty.chat.models.Profile;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.trackaty.chat.Utils.StringUtils.setMaxLength;

public class ProfileSocailAdapter extends RecyclerView.Adapter<ProfileSocailAdapter.ViewHolder> {

    private final static String TAG = ProfileSocailAdapter.class.getSimpleName();


    public ArrayList<Profile> socialArrayList;


    public ProfileSocailAdapter( ArrayList<Profile> socialArrayList){
        this.socialArrayList = socialArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_social_button_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        //Log.i(TAG, "onBindViewHolder called="+ habitsArrayList.get(position));

        holder.button.setText(socialArrayList.get(position).getValue());
        //holder.itemValue.setText(mProfileDataArrayList.indexOf(position));
    }

    @Override
    public int getItemCount() {
        //the size of the list
        Log.d(TAG, "getItemCount ="+ socialArrayList.size());
        return  socialArrayList.size();
        //return  10;
    }

    /// ViewHolder for trips list /////
    public class ViewHolder extends RecyclerView.ViewHolder {

        View row;
        private Button button;


        public ViewHolder(View itemView) {
            super(itemView);
            //itemView = row;

            row = itemView;
            button = row.findViewById(R.id.social_button);

        }

    }


}
