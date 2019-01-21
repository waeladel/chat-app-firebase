package com.trackaty.chat.Adapters;

import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.trackaty.chat.R;
import com.trackaty.chat.models.Profile;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.trackaty.chat.Utils.StringUtils.setMaxLength;

public class WorkAdapter extends RecyclerView.Adapter<WorkAdapter.ViewHolder> {

    private final static String TAG = WorkAdapter.class.getSimpleName();

    public  final static int  BIG_INPUT_MAX_LENGTH = 80;
    public  final static int  SMALL_INPUT_MAX_LENGTH = 40;

    public  final static int  BIG_INPUT_MAX_LINES = 4;
    public  final static int  SMALL_INPUT_MAX_LINES = 1;

    public ArrayList<Profile> workArrayList;
    public Context context;


    public WorkAdapter(Context context, ArrayList<Profile> workArrayList){
        this.workArrayList = workArrayList;
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

        switch (workArrayList.get(position).getKey()) {
            case "work":
                if (null != workArrayList.get(position).getValue()) {
                    holder.itemValue.setText(workArrayList.get(position).getValue());
                }

                // set Max length
                //holder.itemValue.setFilters(new InputFilter[] {new InputFilter.LengthFilter(SMALL_INPUT_MAX_LENGTH)});
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);
                holder.itemValue.setSingleLine(true);

                // capitalize every first letter
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
                        | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE
                        | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

                // Enable counter
                holder.inputLayout.setCounterEnabled(true);
                holder.inputLayout.setCounterMaxLength(BIG_INPUT_MAX_LENGTH);

                //Set Hint/label
                holder.inputLayout.setHint(context.getString(R.string.user_work_headline));

                // Set Helper
                holder.inputLayout.setHelperTextEnabled(true);
                holder.inputLayout.setHelperText(context.getString(R.string.user_work_helper));
                break;
            case "college":
                if (null != workArrayList.get(position).getValue()) {
                    holder.itemValue.setText(workArrayList.get(position).getValue());
                }

                // set Max length
                //holder.itemValue.setFilters(new InputFilter[] {new InputFilter.LengthFilter(SMALL_INPUT_MAX_LENGTH)});
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);
                holder.itemValue.setSingleLine(true);

                // capitalize every first letter
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
                        | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE
                        | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

                // Enable counter
                holder.inputLayout.setCounterEnabled(true);
                holder.inputLayout.setCounterMaxLength(BIG_INPUT_MAX_LENGTH);

                //Set Hint/label
                holder.inputLayout.setHint(context.getString(R.string.user_college_headline));

                // Set Helper
                holder.inputLayout.setHelperTextEnabled(true);
                holder.inputLayout.setHelperText(context.getString(R.string.user_college_helper));
                break;
            case "school":
                if (null != workArrayList.get(position).getValue()) {
                    holder.itemValue.setText(workArrayList.get(position).getValue());
                }

                // set Max length
                //holder.itemValue.setFilters(new InputFilter[] {new InputFilter.LengthFilter(SMALL_INPUT_MAX_LENGTH)});
                setMaxLength(holder.itemValue, BIG_INPUT_MAX_LENGTH);
                holder.itemValue.setSingleLine(true);

                // capitalize every first letter
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
                        | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE
                        | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

                // Enable counter
                holder.inputLayout.setCounterEnabled(true);
                holder.inputLayout.setCounterMaxLength(BIG_INPUT_MAX_LENGTH);

                //Set Hint/label
                holder.inputLayout.setHint(context.getString(R.string.user_school_headline));

                // Set Helper
                holder.inputLayout.setHelperTextEnabled(true);
                holder.inputLayout.setHelperText(context.getString(R.string.user_school_helper));
                break;
        }

        Log.i(TAG, "onBindViewHolder get value="+ workArrayList.get(position).getValue());

        //holder.itemValue.setText(mProfileDataArrayList.indexOf(position));
    }

    @Override
    public int getItemCount() {
        //the size of the list
        Log.d(TAG, "getItemCount ="+workArrayList.size());
        return  workArrayList.size();
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
