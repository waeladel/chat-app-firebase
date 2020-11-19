package com.trackaty.chat.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.trackaty.chat.Interface.ItemClickListener;
import com.trackaty.chat.R;

public class PermissionAlertFragment extends DialogFragment  {
    private final static String TAG = PermissionAlertFragment.class.getSimpleName();

    private  static final int RESULT_REQUEST_RECORD_AUDIO = 21; // for record audio permission

    private Context mActivityContext;
    private Activity activity;
    private Context context;
    // click listener to pass click events to parent fragment
    private static ItemClickListener sItemClickListen;

    // click listener to pass click events to parent fragment
    //private static ItemClickListener itemClickListen;

    private PermissionAlertFragment(Context context) {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
        this.context = context;
    }

    public static PermissionAlertFragment newInstance(Context context , ItemClickListener itemClickListener) {

        // instantiate click listener to pass click events to parent fragment
        sItemClickListen = itemClickListener;

        PermissionAlertFragment fragment = new PermissionAlertFragment(context);
        Bundle args = new Bundle();
        //args.putParcelableArrayList(PRIVET_CONTACTS_KEY, privateContacts);
        fragment.setArguments(args);
        return fragment;
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(context);
        alertDialogBuilder.setTitle(getString(R.string.audio_permission_dialog_title));
        alertDialogBuilder.setMessage(R.string.audio_permission_dialog_message);
        alertDialogBuilder.setPositiveButton(R.string.confirm_dialog_positive_button,  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // on success
                Log.i(TAG, "onClick on success: request Permissions");
                //ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, RESULT_REQUEST_RECORD_AUDIO);
                // We will request permission from fragment to recieve the result on fragment
                if(sItemClickListen != null){
                    sItemClickListen.onClick(null, 1, false);
                }
            }
        });

        alertDialogBuilder.setNegativeButton(R.string.user_confirm_dialog_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
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

    /*@Override
    public void onClick(View view, int position, boolean isLongClick) {
        Log.i(TAG, "onClick view= " + view.getTag()+ " position= " +position);

        if(itemClickListen != null && position != RecyclerView.NO_POSITION){
            itemClickListen.onClick(view, position, false);
        }
    }

    // needed only if i want the listener to be inside the adapter
        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListen = itemClickListener;
        }*/

}
