package com.trackaty.chat.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.trackaty.chat.R;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {
    private final static String TAG = DatePickerFragment.class.getSimpleName();
    private DatePickerDialog.OnDateSetListener ondateSet;
    private Context context;
    private static Long birth;
    public DatePickerFragment(Context context) {
        // Required empty public constructor
        this.context = context;
    }

    public static DatePickerFragment newInstance(Context context, Long birthday) {
        birth = birthday;
        Log.d(TAG, "mParamUserId birth= "+ birthday);
        DatePickerFragment fragment = new DatePickerFragment(context);
        Bundle args = new Bundle();
        args.putLong("birthday", birthday);
        fragment.setArguments(args);
        return fragment;
    }

    void setCallBack(DatePickerDialog.OnDateSetListener ondate) {
        ondateSet = ondate;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int year ,month ,day;
        if(birth != null){
            c.setTimeInMillis(birth);
        }
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(context , R.style.DatePickerMyTheme, ondateSet, year,month,day );

        //return new DatePickerDialog(getActivity(), ondateSet, 1990,2,1 );
        //return new DatePickerDialog(getActivity(), this, year, month, day);
    }
}
