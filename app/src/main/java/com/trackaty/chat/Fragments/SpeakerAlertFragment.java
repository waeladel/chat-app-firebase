package com.trackaty.chat.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.trackaty.chat.R;

import static android.content.Context.AUDIO_SERVICE;

public class SpeakerAlertFragment extends DialogFragment{
    private final static String TAG = SpeakerAlertFragment.class.getSimpleName();
    private Context context;
    private AudioManager mAudioManager;

    private SpeakerAlertFragment(Context context) {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
        this.context = context;
        mAudioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
    }

    public static SpeakerAlertFragment newInstance(Context context) {

        SpeakerAlertFragment fragment = new SpeakerAlertFragment(context);
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
        alertDialogBuilder.setMessage(R.string.speaker_dialog_message);

        // set click listener for Yes button
        alertDialogBuilder.setPositiveButton(R.string.user_confirm_dialog_positive,  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // enable the microphone clicked
                // on success
                if(!mAudioManager.isSpeakerphoneOn()){
                    mAudioManager.setMode(AudioManager.STREAM_MUSIC);
                    mAudioManager.setSpeakerphoneOn(true);
                }
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
