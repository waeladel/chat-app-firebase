package com.trackaty.chat.receivers;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;

import com.trackaty.chat.R;

public class MutedDeviceReceiver extends BroadcastReceiver {

    private final static String TAG = MutedDeviceReceiver.class.getSimpleName();
    private static final int REQUEST_CODE_ALARM = 13; // To detect if alarm is already set or not

    private static final String PREFERENCE_KEY_VISIBLE = "visible" ; // if alarm is stopped due to muted device, we need to uncheck visibility switch
    private static final String PREFERENCE_KEY_HEADSET_SHOWN = "headsetShownKey" ; // to check if headset alert fragment was shown or not from alarm broadcast receiver
    private static final String PREFERENCE_KEY_SPEAKER_ON_SHOWN = "SpeakerOnShownKey"; // to check if turn on speaker alert fragment was shown or not from alarm broadcast receiver
    private static final String PREFERENCE_KEY_VOLUME_SHOWN = "volumeShownKey"; // to check if increase volume fragment was shown or not from alarm broadcast receiver

    private Intent alarmIntent;
    private Context context;
    private NotificationManager mNotificationManager;
    private static final int VISIBILITY_NOTIFICATION_ID = 7; // channel Id for visibility notification, Used to cancel it too
    private AlarmManager alarmManager;
    private SharedPreferences sharedPreferences; // to update preferences when stops the alarm

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "AudioManager device mute changed onReceive");
        this.context = context;
        // To schedule the next alarm, this is better than repeating alarm in case of one of the alarms was delayed
        alarmManager =  (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context/*Activity context*/);

        if (NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED.equals(intent.getAction())) {
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mNotificationManager != null && isAlarmExist()) {
                Log.d(TAG, "NotificationManager device is muted= "+ mNotificationManager.getCurrentInterruptionFilter());
                if(mNotificationManager.getCurrentInterruptionFilter() == NotificationManager.INTERRUPTION_FILTER_NONE){
                    stopAlarm();
                    Intent deviceMutedIntent = new Intent("com.basbes.dating.deviceMuted");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(deviceMutedIntent);
                    Toast.makeText(context, R.string.muted_device_toast , Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    // check if alarm PendingIntent is already exists or not
    private boolean isAlarmExist() {
        alarmIntent  = new Intent(context, SoundIdAlarm.class);
        PendingIntent checkPendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_ALARM, alarmIntent, PendingIntent.FLAG_NO_CREATE);
        if (checkPendingIntent != null){
            Log.d(TAG , "isAlarmExist: yet it is exist. checkPendingIntent= "+checkPendingIntent);
            return true;
        }else{
            Log.d(TAG , "isAlarmExist: not exist. checkPendingIntent= "+checkPendingIntent);
            return false;
        }
    }

    // Stops the alarm
    private void stopAlarm() {
        Log.d(TAG , "stopAlarm()");
        alarmIntent = new Intent(context, SoundIdAlarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_ALARM, alarmIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
        cancelNotification();
        // To show the alert message again
        sharedPreferences.edit().putBoolean(PREFERENCE_KEY_HEADSET_SHOWN,false).apply();
        // To not show the alert message of toast over and over again
        sharedPreferences.edit().putBoolean(PREFERENCE_KEY_SPEAKER_ON_SHOWN,false).apply();
        // To not show the alert message of toast over and over again
        sharedPreferences.edit().putBoolean(PREFERENCE_KEY_VOLUME_SHOWN,false).apply();
        // To uncheck the visibility switch
        sharedPreferences.edit().putBoolean(PREFERENCE_KEY_VISIBLE,false).apply();
    }

    // Cancel Ongoing notification when user click visibility again
    private void cancelNotification() {
        mNotificationManager.cancel(VISIBILITY_NOTIFICATION_ID);
    }
}

