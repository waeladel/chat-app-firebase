package com.trackaty.chat.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.trackaty.chat.Interface.ItemClickListener;
import com.trackaty.chat.R;

public class BlockAlertFragment extends DialogFragment implements ItemClickListener {
    private final static String TAG = BlockAlertFragment.class.getSimpleName();

    // click listener to pass click events to parent fragment
    private static ItemClickListener itemClickListen;
    private Context context;

    private BlockAlertFragment(Context context) {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
        this.context = context;
    }

    public static BlockAlertFragment newInstance(Context context, ItemClickListener itemClickListener) {

        // instantiate click listener to pass click events to parent fragment
        itemClickListen = itemClickListener;

        BlockAlertFragment fragment = new BlockAlertFragment(context);
        Bundle args = new Bundle();
        //args.putParcelableArrayList(PRIVET_CONTACTS_KEY, privateContacts);
        fragment.setArguments(args);
        return fragment;
    }


    // Alert dialog appears after user click block popup menu
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //CharSequence options[] = new CharSequence[]{getString(R.string.alert_dialog_edit), getString(R.string.alert_dialog_unreveal)};
        // AlertDialog.Builder to create the dialog without custom xml layout
        //AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(context);
        //alertDialogBuilder.setTitle(getString(R.string.edit_unreveal_alert_dialog_title));
        alertDialogBuilder.setMessage(R.string.confirm_block_dialog_message);

        // set click listener for Yes button
        alertDialogBuilder.setPositiveButton(R.string.user_confirm_blocking_dialog,  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // on success
                if(itemClickListen != null){
                    // trigger click event when yes is selected
                    itemClickListen.onClick(null, 6, false);
                }
                // set click listener for Cancel button
            }
        }).setNegativeButton(R.string.user_confirm_dialog_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // on cancel
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        return alertDialogBuilder.create();
    }

    @Override
    public void onClick(View view, int position, boolean isLongClick) {
        Log.i(TAG, "onClick view= " + view.getTag()+ " position= " +position);

        if(itemClickListen != null && position != RecyclerView.NO_POSITION){
            itemClickListen.onClick(view, position, false);
        }
    }

    // needed only if i want the listener to be inside the adapter
        public void setItemClickListener(ItemClickListener itemClickListener) {
            itemClickListen = itemClickListener;
        }

}
