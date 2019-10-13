package com.trackaty.chat.receivers;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.trackaty.chat.R;
import com.trackaty.chat.services.FindNearbyService;

import static android.content.Context.AUDIO_SERVICE;

public class MicMuteChangedReceiver extends BroadcastReceiver {

    private final static String TAG = MicMuteChangedReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "AudioManager microphone mute onReceive");
        /*if (AudioManager.ACTION_MICROPHONE_MUTE_CHANGED.equals(intent.getAction())) {
            Log.d(TAG, "AudioManager microphone mute changed");
            // To check if mic is muted or not
            AudioManager mAudioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
            if(mAudioManager != null && mAudioManager.isMicrophoneMute()){
            Toast.makeText(context, R.string.mic_muted_toast ,Toast.LENGTH_LONG).show();
                /*if (isMyServiceRunning(context, FindNearbyService.class)) {
                     Log.d(TAG, "startStopSearchService: is Service running true= "+isMyServiceRunning(context, FindNearbyService.class));
                     // Stop the searching service if it's running
                    Intent serviceIntent = new Intent(context, FindNearbyService.class);
                    context.stopService(serviceIntent);
                }
            }
        }*/
    }

    //A method to check if the service is running or not
    /*private boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if(manager != null){
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }*/

}

