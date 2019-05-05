package com.trackaty.chat.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.trackaty.chat.Interface.ItemClickListener;
import com.trackaty.chat.R;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

public class ChatInactiveAlertFragment extends DialogFragment  {
    private final static String TAG = ChatInactiveAlertFragment.class.getSimpleName();

    private final static String PRIVET_CONTACTS_KEY = "privateContacts";


    private Context mActivityContext;
    private Activity activity;

    // click listener to pass click events to parent fragment
    //private static ItemClickListener itemClickListen;

    public ChatInactiveAlertFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ChatInactiveAlertFragment newInstance() {

        // instantiate click listener to pass click events to parent fragment
        //itemClickListen = itemClickListener;

        ChatInactiveAlertFragment fragment = new ChatInactiveAlertFragment();
        Bundle args = new Bundle();
        //args.putParcelableArrayList(PRIVET_CONTACTS_KEY, privateContacts);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(DialogFragment.STYLE_NORMAL, R.style.colorPickerStyle);
        // this setStyle is VERY important.
        // STYLE_NO_FRAME means that I will provide my own layout and style for the whole dialog
        // so for example the size of the default dialog will not get in my way
        // the style extends the default one. see bellow.
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivityContext = context;
        if (context instanceof Activity){// check if fragmentContext is an activity
            activity =(Activity) context;
        }
    }

    @Override

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // created options to select from
        //CharSequence options[] = new CharSequence[]{getString(R.string.alert_dialog_edit), getString(R.string.alert_dialog_unreveal)};
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        //alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setTitle(getString(R.string.chat_inactive_alert_dialog_title));
        alertDialogBuilder.setMessage(R.string.chat_inactive_dialog_message);
        alertDialogBuilder.setPositiveButton(R.string.confirm_dialog_positive_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // do something...
                        dialog.dismiss();
                    }
                }
        );
                /*.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                );*/

        return alertDialogBuilder.create();
    }

    /*@Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(null != getDialog()){
            getDialog().setTitle(R.string.user_request_dialog_title);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }*/


}
