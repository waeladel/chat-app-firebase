package com.trackaty.chat.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.trackaty.chat.R;
import com.trackaty.chat.models.Profile;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HabitsAdapter extends RecyclerView.Adapter<HabitsAdapter.ViewHolder> {

    private final static String TAG = HabitsAdapter.class.getSimpleName();

    public ArrayList<Profile> habitsArrayList;
    public Context context;


    public HabitsAdapter(Context context, ArrayList<Profile> habitsArrayList){
        this.habitsArrayList = habitsArrayList;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edite_profile_child_spinner_item
                , parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        //Log.i(TAG, "onBindViewHolder called="+ habitsArrayList.get(position));

        if(null != habitsArrayList.get(position).getValue()){
            switch (habitsArrayList.get(position).getValue()){ // display sorting option selected from shared preference
                case "true":
                    holder.itemValue.setSelection(1);
                    Log.d(TAG, "display 0 option on sorting spinner");
                    break;
                case "false":
                    holder.itemValue.setSelection(2);
                    Log.d(TAG, "display 1 option on sorting spinner");
                    break;
                default:
                    holder.itemValue.setSelection(0);
                    break;
            }
        }

        switch (habitsArrayList.get(position).getKey()){
            case "smoke":
                holder.itemTitle.setText(R.string.user_smoke_headline);
                break;
            case "shisha":
                holder.itemTitle.setText(R.string.user_water_pipe_headline);
                break;
            case "drugs":
                holder.itemTitle.setText(R.string.user_drugs_headline);
                break;
            case "drink":
                holder.itemTitle.setText(R.string.user_drink_headline);
                break;
            case "athlete":
                holder.itemTitle.setText(R.string.user_athlete_headline);
                break;
            case "gamer":
                holder.itemTitle.setText(R.string.user_gamer_headline);
                break;
            case "travel":
                holder.itemTitle.setText(R.string.user_travel_headline);
                break;
            case "cook":
                holder.itemTitle.setText(R.string.user_cook_headline);
                break;
            case "read":
                holder.itemTitle.setText(R.string.user_read_headline);
                break;
        }

        //holder.itemValue.setText(mProfileDataArrayList.indexOf(position));
    }

    @Override
    public int getItemCount() {
        //the size of the list
        Log.d(TAG, "getItemCount ="+ habitsArrayList.size());
        return  habitsArrayList.size();
        //return  10;
    }

    /// ViewHolder for trips list /////
    public class ViewHolder extends RecyclerView.ViewHolder {

        View row;
        private TextView itemTitle;
        private Spinner itemValue;


        public ViewHolder(View itemView) {
            super(itemView);
            //itemView = row;

            row = itemView;
            itemValue = row.findViewById(R.id.item_value);
            itemTitle = row.findViewById(R.id.item_title);

            itemValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int selectedItemPosition, long id) {
                    // your code here for onItemSelected
                    switch (selectedItemPosition){ // display sorting option selected from shared preference
                        case 1:
                            habitsArrayList.get(getAdapterPosition()).setValue("true");
                            Log.d(TAG, "spinner item 0 is selected= " +habitsArrayList.get(getAdapterPosition()).getValue());
                            break;
                        case 2:
                            habitsArrayList.get(getAdapterPosition()).setValue("false");
                            Log.d(TAG, "spinner item 1 is selected= " +habitsArrayList.get(getAdapterPosition()).getValue());
                            break;
                        default:
                            habitsArrayList.get(getAdapterPosition()).setValue(null);
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
