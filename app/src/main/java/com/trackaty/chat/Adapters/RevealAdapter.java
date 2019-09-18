package com.trackaty.chat.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.trackaty.chat.Interface.ItemClickListener;
import com.trackaty.chat.R;
import com.trackaty.chat.models.Social;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RevealAdapter extends RecyclerView.Adapter<RevealAdapter.ViewHolder> {

    private final static String TAG = RevealAdapter.class.getSimpleName();

    private  static final int SECTION_SOCIAL_REQUEST = 100;
    private  static final int SECTION_SOCIAL_APPROVE = 200;

    public ArrayList<Social> contactsArrayList;
    public Context context;

    private ItemClickListener itemClickListener;

    public RevealAdapter(Context context, ArrayList<Social> contactsArrayList , ItemClickListener itemClickListener){
        this.contactsArrayList = contactsArrayList;
        this.context = context;
        //socialObj = new SocialObj();
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reveal_request_item, parent, false);
        return new ViewHolder(view , itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        //Log.i(TAG, "onBindViewHolder called="+ habitsArrayList.get(position));


        for (int i = 0; i < contactsArrayList.size(); i++) {
            Log.i(TAG, "contactsArrayList sorted " + contactsArrayList.get(i).getKey());
        }

        if(null != contactsArrayList.get(position).getKey()){
            if(contactsArrayList.get(position).getValue().getPublic()
                    && contactsArrayList.get(position).getSection()== SECTION_SOCIAL_APPROVE){
                // Check box should be selected
                holder.itemCheckBox.setChecked(true);
            }else{
                // Check box should be unchecked
                holder.itemCheckBox.setChecked(false);
            }
        }

        switch (contactsArrayList.get(position).getKey()) {
            case "phone":
                if (null != contactsArrayList.get(position).getValue()) {
                    holder.itemValue.setText(R.string.phone);
                }else{
                    holder.itemValue.setText(null);
                }

                break;
            case "facebook":
                if (null != contactsArrayList.get(position).getValue()) {
                    holder.itemValue.setText(R.string.facebook);
                }else{
                    holder.itemValue.setText(null);
                }
                break;
            case "instagram":
                if (null != contactsArrayList.get(position).getValue()) {
                    holder.itemValue.setText(R.string.instagram);
                }else{
                    holder.itemValue.setText(null);
                }
                break;
            case "twitter":
                if (null != contactsArrayList.get(position).getValue()) {
                    holder.itemValue.setText(R.string.twitter);
                }else{
                    holder.itemValue.setText(null);
                }
                break;
            case "snapchat":
                if (null != contactsArrayList.get(position).getValue()) {
                    holder.itemValue.setText(R.string.snapchat);
                }else{
                    holder.itemValue.setText(null);
                }
                break;
            case "tumblr":
                if (null != contactsArrayList.get(position).getValue()) {
                    holder.itemValue.setText(R.string.tumblr);
                }else{
                    holder.itemValue.setText(null);
                }
                break;
            case "pubg":
                if (null != contactsArrayList.get(position).getValue()) {
                    holder.itemValue.setText(R.string.pubg);
                }else{
                    holder.itemValue.setText(null);
                }
                break;
            case "vk":
                if (null != contactsArrayList.get(position).getValue()) {
                    holder.itemValue.setText(R.string.vk);
                }else{
                    holder.itemValue.setText(null);
                }
                break;
            case "askfm":
                if (null != contactsArrayList.get(position).getValue()) {
                    holder.itemValue.setText(R.string.askfm);
                }else{
                    holder.itemValue.setText(null);
                }
                break;
            case "curiouscat":
                if (null != contactsArrayList.get(position).getValue()) {
                    holder.itemValue.setText(R.string.curiouscat);
                }else{
                    holder.itemValue.setText(null);
                }
                break;
            case "saraha":
                if (null != contactsArrayList.get(position).getValue()) {
                    holder.itemValue.setText(R.string.saraha);
                }else{
                    holder.itemValue.setText(null);
                }
                break;
            case "pinterest":
                if (null != contactsArrayList.get(position).getValue()) {
                    holder.itemValue.setText(R.string.pinterest);
                }else{
                    holder.itemValue.setText(null);
                }
                break;
            case "soundcloud":
                if (null != contactsArrayList.get(position).getValue()) {
                    holder.itemValue.setText(R.string.soundcloud);
                }else{
                    holder.itemValue.setText(null);
                }
                break;
            case "spotify":
                if (null != contactsArrayList.get(position).getValue()) {
                    holder.itemValue.setText(R.string.spotify);
                }else{
                    holder.itemValue.setText(null);
                }
                break;
            case "anghami":
                if (null != contactsArrayList.get(position).getValue()) {
                    holder.itemValue.setText(R.string.anghami);
                }else{
                    holder.itemValue.setText(null);
                }
                break;
            case "twitch":
                if (null != contactsArrayList.get(position).getValue()) {
                    holder.itemValue.setText(R.string.twitch);
                }else{
                    holder.itemValue.setText(null);
                }
                break;
            case "youtube":
                if (null != contactsArrayList.get(position).getValue()) {
                    holder.itemValue.setText(R.string.youtube);
                }else{
                    holder.itemValue.setText(null);
                }
                break;
            case "linkedIn":
                if (null != contactsArrayList.get(position).getValue()) {
                    holder.itemValue.setText(R.string.linkedIn);
                }else{
                    holder.itemValue.setText(null);
                }
                break;
            case "wikipedia":
                if (null != contactsArrayList.get(position).getValue()) {
                    holder.itemValue.setText(R.string.wikipedia);
                }else{
                    holder.itemValue.setText(null);
                }
                break;
            case "website":
                if (null != contactsArrayList.get(position).getValue()) {
                    holder.itemValue.setText(R.string.website);
                }else{
                    holder.itemValue.setText(null);
                }
                break;
        }


        //Log.d(TAG, "onBindViewHolder get value="+ contactsArrayList.get(position).getValue());

        //holder.itemValue.setText(mProfileDataArrayList.indexOf(position));
    }

    @Override
    public int getItemCount() {
        //the size of the list
        Log.d(TAG, "getItemCount ="+ contactsArrayList.size());
        return  contactsArrayList.size();
        //return  10;
    }

    /// ViewHolder for trips list /////
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        View row;
        private TextView itemValue;
        private CheckBox itemCheckBox;
        ItemClickListener itemClickListener;


        public ViewHolder(View itemView, ItemClickListener itemClickListener) {
            super(itemView);
            //itemView = row;
            this.itemClickListener = itemClickListener;


            row = itemView;
            itemValue = row.findViewById(R.id.item_title);
            itemCheckBox = row.findViewById(R.id.contacts_checkBox);
            itemCheckBox.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(itemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION){
                itemClickListener.onClick(view, getAdapterPosition(), false);
                Log.d(TAG, "onClick ="+ getAdapterPosition());

            }
        }

        /*// needed only if i want the listener to be inside the adapter
        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }*/
    }


}
