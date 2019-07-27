package com.trackaty.chat.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.trackaty.chat.R;

public class ChatBlockedAlertFragment extends DialogFragment  {
    private final static String TAG = ChatBlockedAlertFragment.class.getSimpleName();

    private final static String PRIVET_CONTACTS_KEY = "privateContacts";


    // click listener to pass click events to parent fragment
    //private static ItemClickListener itemClickListen;

    public ChatBlockedAlertFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ChatBlockedAlertFragment newInstance() {

        // instantiate click listener to pass click events to parent fragment
        //itemClickListen = itemClickListener;

        ChatBlockedAlertFragment fragment = new ChatBlockedAlertFragment();
        Bundle args = new Bundle();
        //args.putParcelableArrayList(PRIVET_CONTACTS_KEY, privateContacts);
        fragment.setArguments(args);
        return fragment;
    }

    // Alert dialog appears after user click on send message while there is a block relation
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // created options to select from
        //CharSequence options[] = new CharSequence[]{getString(R.string.alert_dialog_edit), getString(R.string.alert_dialog_unreveal)};
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        //alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setTitle(getString(R.string.chat_blocked_alert_dialog_title));
        alertDialogBuilder.setMessage(R.string.chat_blocked_dialog_message);
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
