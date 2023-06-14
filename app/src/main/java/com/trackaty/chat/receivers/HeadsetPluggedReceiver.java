package com.trackaty.chat.receivers;

import static com.trackaty.chat.Utils.PendingIntentFlags.pendingIntentNoCreateFlag;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;

import com.trackaty.chat.App;
import com.trackaty.chat.R;

public class HeadsetPluggedReceiver extends BroadcastReceiver {

    private final static String TAG = HeadsetPluggedReceiver.class.getSimpleName();
    private  static final int HEADSET_STATE_PLUGGED = 1;
    private  static final int HEADSET_STATE_UNPLUGGED = 0;
    private static final int REQUEST_CODE_ALARM = 13; // To detect if alarm is already set or not
    private Intent alarmIntent;
    private App mApplication;
    private SharedPreferences sharedPreferences; // to know if dialogs were shown before or not

    private static final String PREFERENCE_KEY_HEADSET_SHOWN = "headsetShownKey" ; // to check if headset alert fragment was shown or not from alarm broadcast receiver
    private static final String ALERT_MESSAGE_EXTRA_KEY = "AudioChangeKey";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "AudioManager headset plugged onReceive");

        if (AudioManager.ACTION_HEADSET_PLUG.equals(intent.getAction())) {

            mApplication = ((App)context.getApplicationContext());
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context/*Activity context*/);

            //get the attached bundle from the intent
            Bundle extras = intent.getExtras();
            if(extras != null ){
                Log.d(TAG, "getExtra: state= "+extras.getInt("state")+ " name ="+ extras.getString("name ")+ " microphone  = "+extras.getInt("microphone") );
                //userID = intent.getStringExtra(USER_SOUND_ID_KEY);
                if(extras.getInt("state") == HEADSET_STATE_PLUGGED){
                    Log.d(TAG, "AudioManager headset is plugged");
                    Log.d(TAG, "is alarm exist? "+ isAlarmExist(context));
                    if(isAlarmExist(context)){
                        // Always show message or toast here ever if it was shown before, Broadcast acts like live updats
                        // Only display toast when app is in background, we already have dialog when app is in foreground
                        if(mApplication.isInForeground){
                            // App is in foreground, show dialog instead of toast
                            Intent headsetIntent = new Intent("com.basbes.dating.audioChanged");
                            headsetIntent.putExtra(ALERT_MESSAGE_EXTRA_KEY, PREFERENCE_KEY_HEADSET_SHOWN);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(headsetIntent);
                        }else{
                            Toast.makeText(context, R.string.headset_plugged_toast, Toast.LENGTH_LONG).show();
                        }
                        // To not show the alert message of toast over and over again
                        sharedPreferences.edit().putBoolean(PREFERENCE_KEY_HEADSET_SHOWN,true).apply();
                    }
                }
            }
        }
    }

    // check if alarm PendingIntent is already exists or not
    private boolean isAlarmExist(Context context) {
        alarmIntent  = new Intent(context, SoundIdAlarm.class);
        PendingIntent checkPendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_ALARM, alarmIntent, pendingIntentNoCreateFlag());
        if (checkPendingIntent != null){
            Log.d(TAG , "isAlarmExist: yet it is exist. checkPendingIntent= "+checkPendingIntent);
            return true;
        }else{
            Log.d(TAG , "isAlarmExist: not exist. checkPendingIntent= "+checkPendingIntent);
            return false;
        }
    }
}

