package com.trackaty.chat.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.trackaty.chat.BuildConfig;
import com.trackaty.chat.R;

import io.chirp.chirpsdk.ChirpSDK;
import io.chirp.chirpsdk.interfaces.SettingsContentObserverReady;

import static android.content.Context.AUDIO_SERVICE;

public class VolumeAlertFragment extends DialogFragment{
    private final static String TAG = VolumeAlertFragment.class.getSimpleName();
    private Context context;

    //Get chirp secret keys from the keystore
    String CHIRP_APP_KEY = BuildConfig.CHIRP_APP_KEY;
    String CHIRP_APP_SECRET = BuildConfig.CHIRP_APP_SECRET;
    String CHIRP_APP_CONFIG = BuildConfig.CHIRP_APP_CONFIG;
    private ChirpSDK chirp;

    private VolumeAlertFragment(Context context) {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
        this.context = context;
    }

    public static VolumeAlertFragment newInstance(Context context) {

        VolumeAlertFragment fragment = new VolumeAlertFragment(context);
        Bundle args = new Bundle();
        //args.putParcelableArrayList(PRIVET_CONTACTS_KEY, privateContacts);
        fragment.setArguments(args);
        return fragment;
    }

    // Alert dialog appears after user click "block and delete conversation" popup menu
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //CharSequence options[] = new CharSequence[]{getString(R.string.alert_dialog_edit), getString(R.string.alert_dialog_unreveal)};
        // AlertDialog.Builder to create the dialog without custom xml layout
        //AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(context);
        //alertDialogBuilder.setTitle(getString(R.string.edit_unreveal_alert_dialog_title));
        alertDialogBuilder.setMessage(R.string.volume_dialog_message);

        // set click listener for Yes button
        alertDialogBuilder.setPositiveButton(R.string.user_confirm_dialog_positive,  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // enable the microphone clicked
                // on success
                chirp = new ChirpSDK(context, CHIRP_APP_KEY, CHIRP_APP_SECRET);
                chirp.setContentObserverReadyListener(new SettingsContentObserverReady() {
                    @Override
                    public void onReady() {
                        //You can set and get volume now
                        if (chirp.getSystemVolume() <= 0.9f) {
                            chirp.setSystemVolume(1.0f);
                        }
                    }
                });
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

}
