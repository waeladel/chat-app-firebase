package com.trackaty.chat.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Log.i(TAG, "onBindViewHolder called="+ habitsArrayList.get(position));

        if(null != habitsArrayList.get(position).getValue()){
            switch (habitsArrayList.get(position).getValue()){ // display sorting option selected from shared preference
                case "true":
                    holder.itemValue.setSelection(0);
                    Log.d(TAG, "display 0 option on sorting spinner");
                    break;
                case "false":
                    holder.itemValue.setSelection(1);
                    Log.d(TAG, "display 1 option on sorting spinner");
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

        Log.i(TAG, "onBindViewHolder get value="+ habitsArrayList.get(position).getValue());

        //holder.itemValue.setText(userDataArrayList.indexOf(position));
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

        }

    }


}
