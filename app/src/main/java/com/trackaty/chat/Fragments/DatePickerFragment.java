package com.trackaty.chat.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DatePickerFragment extends DialogFragment {
    private final static String TAG = DatePickerFragment.class.getSimpleName();
    DatePickerDialog.OnDateSetListener ondateSet;

    public void setCallBack(DatePickerDialog.OnDateSetListener ondate) {
        ondateSet = ondate;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        Log.d(TAG, "datePicker onCreateDialog");

        return new DatePickerDialog(getActivity(), ondateSet, year,month,day );
        //return new DatePickerDialog(getActivity(), this, year, month, day);
    }
}
