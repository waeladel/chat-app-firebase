package com.trackaty.chat.Adapters;

import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.trackaty.chat.Fragments.EditProfileFragment;
import com.trackaty.chat.R;
import com.trackaty.chat.ViewModels.EditProfileViewModel;
import com.trackaty.chat.models.Profile;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import static com.trackaty.chat.Utils.StringUtils.setMaxLength;

public class AboutAdapter extends RecyclerView.Adapter<AboutAdapter.ViewHolder> {

    private final static String TAG = AboutAdapter.class.getSimpleName();

    public  final static int  BIG_INPUT_MAX_LENGTH = 80;
    public  final static int  SMALL_INPUT_MAX_LENGTH = 40;

    public  final static int  BIG_INPUT_MAX_LINES = 4;
    public  final static int  SMALL_INPUT_MAX_LINES = 1;


    public ArrayList<Profile> aboutArrayList;
    //public Context fragmentContext;
    public EditProfileFragment fragmentContext;
    private EditProfileViewModel mEditProfileViewModel;



    public AboutAdapter(EditProfileFragment fragmentContext, ArrayList<Profile> aboutArrayList){
        this.aboutArrayList = aboutArrayList;
        //this.fragmentContext = fragmentContext;
        this.fragmentContext = fragmentContext; // To use it as observer

        // get EditProfileViewModel to access user object
        mEditProfileViewModel = ViewModelProviders.of(fragmentContext).get(EditProfileViewModel.class);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_profile_text_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        //Log.i(TAG, "onBindViewHolder called="+ habitsArrayList.get(position));

        switch (aboutArrayList.get(position).getKey()){
            case "nationality":
                if(null != aboutArrayList.get(position).getValue()){
                    holder.itemValue.setText(aboutArrayList.get(position).getValue());
                }else{
                    holder.itemValue.setText(null);
                }
                // set Max length
                //holder.itemValue.setFilters(new InputFilter[] {new InputFilter.LengthFilter(SMALL_INPUT_MAX_LENGTH)});
                setMaxLength(holder.itemValue ,SMALL_INPUT_MAX_LENGTH);
                holder.itemValue.setSingleLine(true);

                // capitalize every first letter
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
                        | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE
                        | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

                // Enable counter
                holder.inputLayout.setCounterEnabled(true);
                holder.inputLayout.setCounterMaxLength(SMALL_INPUT_MAX_LENGTH);

                //Set Hint/label
                holder.inputLayout.setHint(fragmentContext.getString(R.string.user_nationality_headline));

                // Set Helper
                holder.inputLayout.setHelperTextEnabled(true);
                holder.inputLayout.setHelperText(fragmentContext.getString(R.string.user_nationality_helper));
                break;
            case "lives":
                if(null != aboutArrayList.get(position).getValue()){
                    holder.itemValue.setText(aboutArrayList.get(position).getValue());
                }else{
                    holder.itemValue.setText(null);
                }
                // set Max length
                //holder.itemValue.setFilters(new InputFilter[] {new InputFilter.LengthFilter(SMALL_INPUT_MAX_LENGTH)});
                setMaxLength(holder.itemValue ,SMALL_INPUT_MAX_LENGTH);
                holder.itemValue.setSingleLine(true);

                // capitalize every first letter
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
                        | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE
                        | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

                // Enable counter
                holder.inputLayout.setCounterEnabled(true);
                holder.inputLayout.setCounterMaxLength(SMALL_INPUT_MAX_LENGTH);

                //Set Hint/label
                holder.inputLayout.setHint(fragmentContext.getString(R.string.user_lives_headline));

                // Set Helper
                holder.inputLayout.setHelperTextEnabled(true);
                holder.inputLayout.setHelperText(fragmentContext.getString(R.string.user_lives_helper));
                break;
            case "hometown":
                if(null != aboutArrayList.get(position).getValue()){
                    holder.itemValue.setText(aboutArrayList.get(position).getValue());
                }else{
                    holder.itemValue.setText(null);
                }
                // set Max length
                //holder.itemValue.setFilters(new InputFilter[] {new InputFilter.LengthFilter(SMALL_INPUT_MAX_LENGTH)});
                setMaxLength(holder.itemValue ,SMALL_INPUT_MAX_LENGTH);
                holder.itemValue.setSingleLine(true);

                // capitalize every first letter
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
                        | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE
                        | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

                // Enable counter
                holder.inputLayout.setCounterEnabled(true);
                holder.inputLayout.setCounterMaxLength(SMALL_INPUT_MAX_LENGTH);

                //Set Hint/label
                holder.inputLayout.setHint(fragmentContext.getString(R.string.user_hometown_headline));

                // Set Helper
                holder.inputLayout.setHelperTextEnabled(true);
                holder.inputLayout.setHelperText(fragmentContext.getString(R.string.user_hometown_helper));
                break;
            case "religion":
                if(null != aboutArrayList.get(position).getValue()){
                    holder.itemValue.setText(aboutArrayList.get(position).getValue());
                }else{
                    holder.itemValue.setText(null);
                }
                // set Max length
                //holder.itemValue.setFilters(new InputFilter[] {new InputFilter.LengthFilter(BIG_INPUT_MAX_LENGTH )});
                setMaxLength(holder.itemValue ,BIG_INPUT_MAX_LENGTH);
                holder.itemValue.setSingleLine(true);

                // capitalize every first letter
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
                        | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE
                        | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

                // Enable counter
                holder.inputLayout.setCounterEnabled(true);
                holder.inputLayout.setCounterMaxLength(BIG_INPUT_MAX_LENGTH );

                //Set Hint/label
                holder.inputLayout.setHint(fragmentContext.getString(R.string.user_religion_headline));

                // Set Helper
                holder.inputLayout.setHelperTextEnabled(true);
                holder.inputLayout.setHelperText(fragmentContext.getString(R.string.user_religion_helper));
                break;
            case "politics":
                if(null != aboutArrayList.get(position).getValue()){
                    holder.itemValue.setText(aboutArrayList.get(position).getValue());
                }else{
                    holder.itemValue.setText(null);
                }
                // set Max length
                //holder.itemValue.setFilters(new InputFilter[] {new InputFilter.LengthFilter(BIG_INPUT_MAX_LENGTH )});
                setMaxLength(holder.itemValue ,BIG_INPUT_MAX_LENGTH);
                holder.itemValue.setMaxLines(BIG_INPUT_MAX_LINES);
                holder.itemValue.setSingleLine(true);
                // capitalize every first letter
                holder.itemValue.setInputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
                        | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE
                        | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

                // Enable counter
                holder.inputLayout.setCounterEnabled(true);
                holder.inputLayout.setCounterMaxLength(BIG_INPUT_MAX_LENGTH );
                //Set Hint/label
                holder.inputLayout.setHint(fragmentContext.getString(R.string.user_politics_headline));

                // Set Helper
                holder.inputLayout.setHelperTextEnabled(true);
                holder.inputLayout.setHelperText(fragmentContext.getString(R.string.user_politics_helper));
                break;
        }



        //holder.itemValue.setText(mProfileDataArrayList.indexOf(position));
    }

    @Override
    public int getItemCount() {
        //the size of the list
        Log.d(TAG, "getItemCount ="+aboutArrayList.size());
        return  aboutArrayList.size();
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

            itemValue.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence editable, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence editable, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    Log.d(TAG, "Editable Name= "+ editable.toString()+ "position= "+getAdapterPosition());
                    if(TextUtils.isEmpty(editable.toString().trim())){
                        aboutArrayList.get(getAdapterPosition()).setValue(null);

                        // set EditProfileViewModel.user values
                        switch (aboutArrayList.get(getAdapterPosition()).getKey()){
                            case "nationality":
                                mEditProfileViewModel.getUser().setNationality(null);
                                break;
                            case "hometown":
                                mEditProfileViewModel.getUser().setHometown(null);
                                break;
                            case "lives":
                                mEditProfileViewModel.getUser().setLives(null);
                                break;
                            case "politics":
                                mEditProfileViewModel.getUser().setPolitics(null);
                                break;
                            case "religion":
                                mEditProfileViewModel.getUser().setReligion(null);
                                break;
                        }
                    }else{
                        aboutArrayList.get(getAdapterPosition()).setValue(editable.toString());

                        // set EditProfileViewModel.user values
                        switch (aboutArrayList.get(getAdapterPosition()).getKey()){
                            case "nationality":
                                mEditProfileViewModel.getUser().setNationality(String.valueOf(editable).trim());
                                break;
                            case "hometown":
                                mEditProfileViewModel.getUser().setHometown(String.valueOf(editable).trim());
                                break;
                            case "lives":
                                mEditProfileViewModel.getUser().setLives(String.valueOf(editable).trim());
                                break;
                            case "politics":
                                mEditProfileViewModel.getUser().setPolitics(String.valueOf(editable).trim());
                                break;
                            case "religion":
                                mEditProfileViewModel.getUser().setReligion(String.valueOf(editable).trim());
                                break;
                        }
                    }
                }
            });
        }

    }


}
