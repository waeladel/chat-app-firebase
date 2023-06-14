package com.trackaty.chat.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.trackaty.chat.R;
import com.trackaty.chat.ViewModels.EditProfileViewModel;
import com.trackaty.chat.models.Profile;

import java.util.ArrayList;

public class HabitsAdapter extends RecyclerView.Adapter<HabitsAdapter.ViewHolder> {

    private final static String TAG = HabitsAdapter.class.getSimpleName();

    public ArrayList<Profile> habitsArrayList;


    //public Context fragmentContext;
    public Fragment fragmentContext;
    private EditProfileViewModel mEditProfileViewModel;

    private boolean isListeningTextChange;

    public HabitsAdapter(Fragment fragmentContext, ArrayList<Profile> habitsArrayList){
        this.habitsArrayList = habitsArrayList;
        this.fragmentContext = fragmentContext; // To use it as observer

        // get EditProfileViewModel to access user object
        mEditProfileViewModel = new ViewModelProvider(fragmentContext).get(EditProfileViewModel.class);

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
        }else{
            // Value is null due to user selected "Not specified" or it wasn't set
            holder.itemValue.setSelection(0);
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

    // Only enable textChange listener when Layout is opened to get the new input data.
    // And we have to disable textChange listener when Layout is not opened.
    // If not disabled it will override transient data when expand with random data from recycled text fields
    public void setListeningTextChange(boolean isListeningTextChange) {
        this.isListeningTextChange = isListeningTextChange;
        Log.d(TAG, "isListeningTextChange ="+isListeningTextChange);

    }
    public boolean isListeningTextChange() {
        Log.d(TAG, "isListeningTextChange ="+isListeningTextChange);
        return  isListeningTextChange;
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
                    // Don't update any thing unless the layout is expanded
                    // If not disabled it will override transient data when expanded with random data from recycled data
                    if(isListeningTextChange){
                        Log.d(TAG, "mama spinner selected ");
                        switch (selectedItemPosition){ // display sorting option selected from shared preference
                            case 1:
                                habitsArrayList.get(getBindingAdapterPosition()).setValue("true");
                                Log.d(TAG, "spinner item 0 is selected= " +habitsArrayList.get(getBindingAdapterPosition()).getKey()+ " Value= "+ habitsArrayList.get(getBindingAdapterPosition()).getValue());

                                // set EditProfileViewModel.user values
                                switch (habitsArrayList.get(getBindingAdapterPosition()).getKey()){
                                    case "athlete":
                                        mEditProfileViewModel.getUser().setAthlete(true);
                                        break;
                                    case "smoke":
                                        mEditProfileViewModel.getUser().setSmoke(true);
                                        break;
                                    case "travel":
                                        mEditProfileViewModel.getUser().setTravel(true);
                                        break;
                                    case "shisha":
                                        mEditProfileViewModel.getUser().setShisha(true);
                                        break;
                                    case "cook":
                                        mEditProfileViewModel.getUser().setCook(true);
                                        break;
                                    case "drink":
                                        mEditProfileViewModel.getUser().setDrink(true);
                                        break;
                                    case "drugs":
                                        mEditProfileViewModel.getUser().setDrugs(true);
                                        break;
                                    case "gamer":
                                        mEditProfileViewModel.getUser().setGamer(true);
                                        break;
                                    case "read":
                                        mEditProfileViewModel.getUser().setRead(true);
                                        break;
                                }
                                break;
                            case 2:
                                habitsArrayList.get(getBindingAdapterPosition()).setValue("false");
                                Log.d(TAG, "spinner item 1 is selected= " +habitsArrayList.get(getBindingAdapterPosition()).getKey()+ " Value= "+ habitsArrayList.get(getBindingAdapterPosition()).getValue());

                                // set EditProfileViewModel.user values
                                switch (habitsArrayList.get(getBindingAdapterPosition()).getKey()){
                                    case "athlete":
                                        mEditProfileViewModel.getUser().setAthlete(false);
                                        break;
                                    case "smoke":
                                        mEditProfileViewModel.getUser().setSmoke(false);
                                        break;
                                    case "travel":
                                        mEditProfileViewModel.getUser().setTravel(false);
                                        break;
                                    case "shisha":
                                        mEditProfileViewModel.getUser().setShisha(false);
                                        break;
                                    case "cook":
                                        mEditProfileViewModel.getUser().setCook(false);
                                        break;
                                    case "drink":
                                        mEditProfileViewModel.getUser().setDrink(false);
                                        break;
                                    case "drugs":
                                        mEditProfileViewModel.getUser().setDrugs(false);
                                        break;
                                    case "gamer":
                                        mEditProfileViewModel.getUser().setGamer(false);
                                        break;
                                    case "read":
                                        mEditProfileViewModel.getUser().setRead(false);
                                        break;
                                }
                                break;
                            default:
                                habitsArrayList.get(getBindingAdapterPosition()).setValue(null);
                                Log.d(TAG, "spinner item default is selected= " +habitsArrayList.get(getBindingAdapterPosition()).getKey()+ " Value= "+ habitsArrayList.get(getBindingAdapterPosition()).getValue());
                                // set EditProfileViewModel.user values
                                switch (habitsArrayList.get(getBindingAdapterPosition()).getKey()){
                                    case "athlete":
                                        mEditProfileViewModel.getUser().setAthlete(null);
                                        break;
                                    case "smoke":
                                        mEditProfileViewModel.getUser().setSmoke(null);
                                        break;
                                    case "travel":
                                        mEditProfileViewModel.getUser().setTravel(null);
                                        break;
                                    case "shisha":
                                        mEditProfileViewModel.getUser().setShisha(null);
                                        break;
                                    case "cook":
                                        mEditProfileViewModel.getUser().setCook(null);
                                        break;
                                    case "drink":
                                        mEditProfileViewModel.getUser().setDrink(null);
                                        break;
                                    case "drugs":
                                        mEditProfileViewModel.getUser().setDrugs(null);
                                        break;
                                    case "gamer":
                                        mEditProfileViewModel.getUser().setGamer(null);
                                        break;
                                    case "read":
                                        mEditProfileViewModel.getUser().setRead(null);
                                        break;
                                }
                                break;
                        }
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
