package com.trackaty.chat.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;

import com.trackaty.chat.App;
import com.trackaty.chat.BuildConfig;
import com.trackaty.chat.R;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import io.chirp.chirpsdk.ChirpSDK;
import io.chirp.chirpsdk.interfaces.SettingsContentObserverReady;
import io.chirp.chirpsdk.models.ChirpError;
import io.chirp.chirpsdk.interfaces.ChirpEventListener;

import static android.content.Context.AUDIO_SERVICE;

public class SoundIdAlarm extends BroadcastReceiver {

    private final static String TAG = SoundIdAlarm.class.getSimpleName();
    //private String userID;
    private int mCurrentSoundId;
    private String mCurrentUserId;
    byte[] payload ; // = {0, 0, 0, 0};
    //byte[] stringPayload;

    //Get chirp secret keys from the keystore
    String CHIRP_APP_KEY = BuildConfig.CHIRP_APP_KEY;
    String CHIRP_APP_SECRET = BuildConfig.CHIRP_APP_SECRET;
    String CHIRP_APP_CONFIG = BuildConfig.CHIRP_APP_CONFIG;

    private ChirpSDK chirp;

    private AlarmManager alarmManager;
    private Intent alarmIntent;
    private PendingIntent pendingIntent;
    private static int mRandomAlarmInterval ;//= 60- 120 *1000;
    private static int mRandomChannel ;// 1-8;
    private static final int REQUEST_CODE_ALARM = 13;

    private static final String USER_SOUND_ID_KEY = "userSoundId";
    private static final String USER_ID_KEY = "userId";
    final Handler handler = new Handler();
    private float mOriginalSystemVolume;
    private AudioManager mAudioManager;

    private Context context;
    private App mApplication;
    private SharedPreferences sharedPreferences; // to know if dialogs were shown before or not

    private static final String PREFERENCE_KEY_VISIBLE = "visible" ; // if alarm is stopped due to muted device, we need to uncheck visibility switch
    private static final String PREFERENCE_KEY_HEADSET_SHOWN = "headsetShownKey" ; // to check if headset alert fragment was shown or not from alarm broadcast receiver
    private static final String PREFERENCE_KEY_SPEAKER_ON_SHOWN = "SpeakerOnShownKey"; // to check if turn on speaker alert fragment was shown or not from alarm broadcast receiver
    private static final String PREFERENCE_KEY_VOLUME_SHOWN = "volumeShownKey"; // to check if increase volume fragment was shown or not from alarm broadcast receiver
    private static final String ALERT_MESSAGE_EXTRA_KEY = "AudioChangeKey";

    private NotificationManagerCompat notificationManager;
    private static final int VISIBILITY_NOTIFICATION_ID = 7; // channel Id for visibility notification, Used to cancel it too

    // A listener for sound id
    ChirpEventListener  chirpEventListener = new ChirpEventListener () {
        @Override
        public void onSent(@NotNull byte[] data, int channel) {
            Log.d(TAG, "onSent");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    closeSDK();
                }
            }, 1000);
        }

        @Override
        public void onSending(byte[] data, int channel) {
            Log.v(TAG, "ChirpSDK Sending data...");
        }

        // onReceived is not used hear
        @Override
        public void onReceived(byte[] data, int channel) {
            if (data != null) {
                Log.v(TAG, "ChirpSDK Received data");

            } else {
                Log.e(TAG, "ChirpErrorDecode failed");
            }
        }

        @Override
        public void onReceiving(int channel) {
            Log.v(TAG, "ChirpSDK Receiving data...");
        }

        @Override
        public void onStateChanged(int oldState, int newState) {
            Log.v(TAG, "onStateChanged oldState= "+oldState + " newState="+ newState);
        }

        @Override
        public void onSystemVolumeChanged(float oldVolume, float newVolume) {
            Log.v(TAG, "Volume changed from: " + oldVolume +" to: " + newVolume);
        }
    };

    // On alarm intent Receive
    @Override
    public void onReceive(Context context, Intent intent) {
        // an Intent broadcast.

        /*Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }*/
        this.context = context;
        mApplication = ((App)context.getApplicationContext());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context/*Activity context*/);
        notificationManager = NotificationManagerCompat.from(context); // To cancel notification

        mAudioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        /*if(mAudioManager != null){
            Log.d(TAG, "AudioManager isMusicActive =" + mAudioManager.isMusicActive());
            Log.d(TAG, "AudioManager isMicrophoneMute =" + mAudioManager.isMicrophoneMute());
            Log.d(TAG, "AudioManager isBluetoothScoAvailableOffCall =" + mAudioManager.isBluetoothScoAvailableOffCall());
            Log.d(TAG, "AudioManager isBluetoothScoOn =" + mAudioManager.isBluetoothScoOn());
            Log.d(TAG, "AudioManager isBluetoothA2dpOn =" + mAudioManager.isBluetoothA2dpOn());
            Log.d(TAG, "AudioManager isWiredHeadsetOn =" + mAudioManager.isWiredHeadsetOn());
            Log.d(TAG, "AudioManager isSpeakerphoneOn =" + mAudioManager.isSpeakerphoneOn());
            Log.d(TAG, "AudioManager getMode =" + mAudioManager.getMode());
            //mAudioManager.setMode(AudioManager.STREAM_RING);
            //mAudioManager.setSpeakerphoneOn(true);
            Log.d(TAG, "AudioManager isSpeakerphoneOn =" + mAudioManager.isSpeakerphoneOn());
            Log.d(TAG, "AudioManager isHeadsetOn() =" + isHeadsetOn(mAudioManager));
            if((mAudioManager.getMode()== AudioManager.MODE_IN_CALL)||(mAudioManager.getMode()== AudioManager.MODE_IN_COMMUNICATION)){
                Log.d(TAG, "AudioManager is in call or Communication");
            }
            if(mAudioManager.getMode()== AudioManager.MODE_IN_COMMUNICATION){
                Log.d(TAG, "AudioManager is Communication, VIOP or coive chat");
            }
            if(mAudioManager.getMode()== AudioManager.MODE_IN_CALL){
                Log.d(TAG, "AudioManager is in call");
            }

            if((mAudioManager.getMode()== AudioManager.MODE_NORMAL)){
                Log.d(TAG, "AudioManager is in Normal audio mode: not ringing and no call established");
            }

            if((mAudioManager.getMode()== AudioManager.MODE_RINGTONE)){
                Log.d(TAG, "AudioManager is in Ringing audio mode. An incoming is being signaled");
            }

        }*/

        //Toast.makeText(context, R.string.mic_muted_toast, Toast.LENGTH_LONG).show();

        //get the attached bundle from the intent
        Bundle extras = intent.getExtras();

        if(extras != null ){
            mCurrentSoundId = extras.getInt(USER_SOUND_ID_KEY);
            mCurrentUserId = extras.getString(USER_ID_KEY);
            //userID = intent.getStringExtra(USER_SOUND_ID_KEY);
        }

        if(mCurrentSoundId == 0){
            Log.d(TAG, "SoundId is not specified. no need to send data. SoundId= "+ mCurrentSoundId+ " mCurrentUserId= "+ mCurrentUserId);
            return;
        }
        // you can do the processing here.
        Log.d(TAG, "onReceive. StringExtra soundId= "+ mCurrentSoundId);
        //stringPayload = userID.getBytes(Charset.forName("UTF-8"));
        payload = intToBytes(mCurrentSoundId);

        // create random alarm interval between 60 - 120
        Random alarmRandom = new Random();
        //mRandomAlarmInterval = (alarmRandom.nextInt(61) + 60) *1000;
        mRandomAlarmInterval =  60*1000;
        Log.d(TAG, "randomInteger. mRandomAlarmInterval= "+ mRandomAlarmInterval);

        // create random channel id between 1 - 8
        Random channelRandom = new Random();
        //mRandomChannel = channelRandom.nextInt(8) ; // from 0 - 7
        mRandomChannel = 0;
        Log.d(TAG, "mRandomChannel= "+ mRandomChannel);

        // To schedule the next alarm, this is better than repeating alarm in case of one of the alarms was delayed
        alarmManager =  (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        scheduleNextAlarm(alarmManager);

       /* SettingsContentObserver settingsContentObserver = new SettingsContentObserver(context, new Handler(), new Handler());
        settingsContentObserver.setAudioFocusType(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
        settingsContentObserver.gainAudioFocus(15.0f);
        //settingsContentObserver.onChange(false);
            Log.v(TAG, "ChirpSDK settingsContentObserver is gained AudioFocus= "+ settingsContentObserver.gainAudioFocus(15.0f)) ;
        //settingsContentObserver.onChange();
        chirp = new ChirpSDK(context, CHIRP_APP_KEY, CHIRP_APP_SECRET, settingsContentObserver);


        Log.v(TAG, "ChirpSDK ContentObserverReadyListener is ready. chirp volume= "+chirp.getSystemVolume());
        Log.v(TAG, "ChirpSDK getAudioFocusType= "+chirp.getAudioFocusType());
        mOriginalSystemVolume = chirp.getSystemVolume();
        if(mOriginalSystemVolume == 0.0){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                chirp.setAudioFocusType(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE);
            }else{
                chirp.setAudioFocusType(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            }
        }else{
            // AUDIOFOCUS_REQUEST_FAILED is the value as AUDIOFOCUS_NONE = 0, but AUDIOFOCUS_NONE requires api 26
            chirp.setAudioFocusType(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
            Log.v(TAG, "ChirpSDK getAudioFocusType= "+chirp.getAudioFocusType());
        }

        // Move start SDK here to gain AudioFocus first
        ChirpError error = chirp.setConfig(CHIRP_APP_CONFIG);
        if (error.getCode() == 0) {
            Log.v(TAG, "ChirpSDK Configured ChirpSDK");
            startSDK();
        } else {
            Log.e(TAG, "ChirpSDK setConfig ChirpError: "+ error.getMessage());
        }*/

        chirp = new ChirpSDK(context, CHIRP_APP_KEY, CHIRP_APP_SECRET);
        chirp.setContentObserverReadyListener(new SettingsContentObserverReady() {
            @Override
            public void onReady() {
                //You can set and get volume now
                Log.v(TAG, "ChirpSDK ContentObserverReadyListener is ready. chirp volume= "+chirp.getSystemVolume());
                Log.v(TAG, "ChirpSDK getAudioFocusType= "+chirp.getAudioFocusType());
                mOriginalSystemVolume = chirp.getSystemVolume();
                // All audio logic here
                // update sharedPreferences values to show alerts again when values is changed
                if(!isHeadsetOn(mAudioManager)){// To show the alert message again
                    sharedPreferences.edit().putBoolean(PREFERENCE_KEY_HEADSET_SHOWN,false).apply();
                }
                if(mAudioManager.getMode()!= AudioManager.MODE_IN_CALL && mAudioManager.getMode()!= AudioManager.MODE_IN_COMMUNICATION){
                    // To not show the alert message of toast over and over again
                    sharedPreferences.edit().putBoolean(PREFERENCE_KEY_SPEAKER_ON_SHOWN,false).apply();
                }
                if (!mAudioManager.isMusicActive()&& mOriginalSystemVolume <= 0.6f){
                    // To not show the alert message of toast over and over again
                    sharedPreferences.edit().putBoolean(PREFERENCE_KEY_VOLUME_SHOWN,false).apply();
                }
                // check if headset is connected or not. If so we must suggest to unplug or route to speaker or at least not wear
                // if user still plug it, handel volume as usual.
                if(isHeadsetOn(mAudioManager)){
                    Log.d(TAG, "onContentObserverReady : Headset is plugged");
                    if(!mAudioManager.isSpeakerphoneOn()){
                        sendAlertMessage(PREFERENCE_KEY_HEADSET_SHOWN );
                    }
                    adjustVolume();
                }else if((mAudioManager.getMode()== AudioManager.MODE_IN_CALL)||(mAudioManager.getMode()== AudioManager.MODE_IN_COMMUNICATION)){
                    Log.d(TAG, "onContentObserverReady : In call or VoIP");
                    // Only ask for activate speaker if it's off
                    if(!mAudioManager.isSpeakerphoneOn()){
                        Log.d(TAG, "onContentObserverReady : speaker is off, lets ask for activating it");
                        sendAlertMessage(PREFERENCE_KEY_SPEAKER_ON_SHOWN);
                    }
                    adjustVolume();
                }else{
                    Log.d(TAG, "onContentObserverReady : normal mode, phone uses it's default speaker, no headset or phone calls");
                    adjustVolume();
                }

                // Move start SDK here to gain AudioFocus first
                ChirpError error = chirp.setConfig(CHIRP_APP_CONFIG);
                if (error.getCode() == 0) {
                    Log.v(TAG, "ChirpSDK Configured ChirpSDK");
                    Log.v(TAG, "ChirpSDK get ChannelCount= "+ chirp.getChannelCount());

                    // Set the random channel before starting the SDK
                    ChirpError channelError = chirp.setTransmissionChannel(mRandomChannel);
                    if (channelError.getCode() > 0) {
                        Log.e(TAG, "ChirpError. ChannelError"+ channelError.getMessage());
                    }
                    Log.d(TAG, "getTransmissionChannel= "+chirp.getTransmissionChannel());
                    startSDK();
                } else {
                    Log.e(TAG, "ChirpSDK setConfig ChirpError: "+ error.getMessage());
                }

            }
        });




        /* Handler handler = new Handler();
        Runnable periodicUpdate = new Runnable() {
            @Override
            public void run() {
                // whatever you want to do
                playSoundId();
                Log.i(TAG, "Playing sound ID");

            }
        };
        handler.post(periodicUpdate);

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.i(TAG, "Playing sound is Complete");
                closeSDK();
            }
        });

        // you can do the processing here.
        userID = intent.getStringExtra("userID");

        //Toast.makeText(context,"BroadcastReceiver Received",Toast.LENGTH_LONG).show();

        chirpConnect = new ChirpConnect(context, APP_KEY, APP_SECRET);
        Log.d(TAG , "BroadcastReceiver Received. mCurrentUserId= "+ userID+ " hash= "+chirpConnect.hashCode());

        //chirpConnect.setConfigFromNetwork(new ConnectSetConfigListener() {
        ChirpError error = chirpConnect.setConfig(APP_CONFIG);
        if (error.getCode() == 0) {
            Log.i(TAG , "setConfig= Config succesfully set.");

            // Start ChirpSDK sender and receiver, if no arguments are passed both sender and receiver are started
            chirpConnect.start(true, false);
            Log.i(TAG, "getConnectState: " + chirpConnect.getState());

            long maxPayloadLength = chirpConnect.maxPayloadLength();
            //long size = (long) new Random().nextInt((int) maxPayloadLength) + 1;
            //byte[] payload = chirpConnect.randomPayload(size);

            payload = userID;

            Log.i(TAG, "maxPayloadLength= "+ maxPayloadLength+ "payload ="+ payload.getBytes().length);

            if (maxPayloadLength < payload.getBytes().length) {
                Log.e(TAG, "ConnectError: Invalid Payload");
            } else {
                //ChirpError sendError = chirpConnect.send(payload.getBytes());
                sendPayload();
            }
        } else {// If there is a configuration error
            Log.e("ChirpError: ", error.getMessage());
        }
*/
        //throw new UnsupportedOperationException("Not yet implemented");

       /* ConnectEventListener connectEventListener = new ConnectEventListener() {


            @Override
            public void onSending(@NotNull byte[] bytes, int i) {
                Log.d(TAG, "onSending: "+  i);
            }

            @Override
            public void onSent(@NotNull byte[] bytes, int i) {
                Log.d(TAG, "onSent: "+  i);
                chirpConnect.stop();
            }

            @Override
            public void onReceiving(int i) {
                Log.d(TAG, "onReceiving: "+  i);
            }

            @Override
            public void onReceived(byte[] data, int channel) {
                if (data != null) {
                    String identifier = new String(data);
                    Log.d("ChirpSDK: ", "Received " + identifier);
                } else {
                    Log.e("ChirpError: ", "Decode failed");
                }

            }

            @Override
            public void onStateChanged(int i, int i1) {
                Log.d(TAG, "onStateChanged: old="+  i+ " new= " +i1);
                *//*if(i == 4 && i1 == 3){
                    chirpConnect.stop();
                    try {
                        chirpConnect.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }*//*

            }

            @Override
            public void onSystemVolumeChanged(float v, float v1) {
                Log.d(TAG, "onSystemVolumeChanged: old="+ v + "new= "+v1);
            }
        };

        chirpConnect.setListener(connectEventListener);*/
    }
    /*private void playSoundId() {
        mMediaPlayer.setLooping(false);
        mMediaPlayer.start();
    }*/

    private void startSDK() {
        if (chirp == null) {
            return;
        }
        // Start ChirpSDK sender and receiver, if no arguments are passed both sender and receiver are started
        ChirpError error = chirp.start(true, false);
        if (error.getCode() > 0) {
            Log.e(TAG, "start ChirpError: " + error.getMessage());
        } else {
            Log.v(TAG, "ChirpSDK: Started ChirpSDK");
            chirp.setListener(chirpEventListener);
            /*// Set the Chirp SDK "software" volume. Not important
            ChirpError setVolumeError = chirp.setVolume(1.7f);
            Log.e(TAG, "setVolumeError= "+setVolumeError);*/
            sendPayload();
        }
    }

    private void sendPayload() {

        // Schedule a task to run every 60 seconds with no initial delay.
        Log.i(TAG , "maxPayload= "+ chirp.maxPayloadLength());
        Log.i(TAG , "sending Payload");
        //chirpConnect.setVolume(0.1f);

        //You can set and get volume now after we got AudioFocus
        /*if (chirp.getSystemVolume() <= 0.9f) {
            chirp.setSystemVolume(1.0f);
        }*/
        ChirpError error = chirp.send(payload);

        Log.d(TAG, "sending data: Payload size= "+payload.length);
        if (error.getCode() > 0) {
            Log.e(TAG, "ConnectError: " + error.getMessage());
            if(error.getCode() == 207){ // Device is muted, cannot send data
                // Show error toast
                stopAlarm();
                Intent deviceMutedIntent = new Intent("com.basbes.dating.deviceMuted");
                LocalBroadcastManager.getInstance(context).sendBroadcast(deviceMutedIntent);
                Toast.makeText(context,R.string.muted_device_toast,Toast.LENGTH_LONG).show();
            }
            closeSDK();
        }else{
            Log.i(TAG, "Data send successfully" );
            // Close after 3 second. Don't close immediately so that we don't interrupt the sending process
            /*final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    closeSDK();
                }
            }, 3000);*/
        }
    }

    public byte[] intToBytes(int value) {
        /*byte[] payload  = {0, 0, 0, 0};

        payload[0] = (byte)(value);
        payload[1] = (byte)(value >> 8);
        payload[2] = (byte)(value >> 16);
        payload[3] = (byte)(value >> 24);
        return payload;*/

        return  ByteBuffer.allocate(4).putInt(value).array();

        /*return new byte[] {
                (byte)(value >> 24),
                (byte)(value >> 16),
                (byte)(value >> 8),
                (byte)value };*/
    }

    private void closeSDK(){
        chirp.stop();
        try {
            chirp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // release the lock
        /*mMediaPlayer.stop();
        mMediaPlayer .reset();
        mMediaPlayer.release();*/
        /*wl.release();
        wl = null;*/

        // Return system volume to its original values
        Log.v(TAG, "ChirpSDK mOriginalSystemVolume = "+mOriginalSystemVolume);
        chirp.setSystemVolume(mOriginalSystemVolume);

        Log.i(TAG, "chirpConnect stopped" );
    }

    // To check if headset is connected or not on
    private boolean isHeadsetOn(AudioManager am) {
        if (am == null)
            return false;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return am.isWiredHeadsetOn() || am.isBluetoothScoOn() || am.isBluetoothA2dpOn();
        } else {
            AudioDeviceInfo[] devices = am.getDevices(AudioManager.GET_DEVICES_OUTPUTS);

            for (int i = 0; i < devices.length; i++) {
                AudioDeviceInfo device = devices[i];

                if (device.getType() == AudioDeviceInfo.TYPE_WIRED_HEADSET
                        || device.getType() == AudioDeviceInfo.TYPE_WIRED_HEADPHONES
                        || device.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP
                        || device.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_SCO) {
                    return true;
                }
            }
        }
        return false;

    }

    private void adjustVolume() {
        if(mOriginalSystemVolume == 0.0f){
            Log.d(TAG, "adjustVolume: volume is 0, we must rise it to 1 and gian exclusive focus");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                chirp.setAudioFocusType(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE);
            }else{
                chirp.setAudioFocusType(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            }
            chirp.setSystemVolume(1.0f);
        }else{
            // Volume is not 0, we must check if something is playing of not
            // No need to check for MODE_IN_CALL and MODE_IN_COMMUNICATION because chirp only increase music stream sound
            if(mAudioManager.isMusicActive()){
                Log.d(TAG, "adjustVolume: volume is on and something is playing. lets abandon focus and keep same volume and suggest increasing volume");
                chirp.setAudioFocusType(AudioManager.AUDIOFOCUS_REQUEST_FAILED);
                // suggest increasing volume because it's too low
                if(mOriginalSystemVolume <= 0.6f){
                    Log.d(TAG, "adjustVolume: suggest increasing volume because it's too low. mOriginalSystemVolume= "+ mOriginalSystemVolume);
                    sendAlertMessage(PREFERENCE_KEY_VOLUME_SHOWN);
                }
            }else{
                Log.d(TAG, "adjustVolume: volume is on and nothing is playing. gain MAY_DUCK focus and raise volume to 1");
                chirp.setAudioFocusType(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
                // Raise volume to 1 as nothing is playing any way.
                if(mOriginalSystemVolume < 1.0f){
                    chirp.setSystemVolume(1.0f);
                }
            }
        }
    }

    private void sendAlertMessage(String message) {
        switch (message){
            case PREFERENCE_KEY_HEADSET_SHOWN:
                Intent intent;
                Log.d(TAG, "sendAlertMessage : is app in InForeground= "+ mApplication.isInForeground);
                // Only show message or toast if it wan's shown before
                boolean isHeadsetAlertShown = sharedPreferences.getBoolean(PREFERENCE_KEY_HEADSET_SHOWN, false);
                if(!isHeadsetAlertShown){
                    Log.d(TAG, "sendAlertMessage : Only show message or toast if it wasn't shown. isHeadsetAlertShown= "+ isHeadsetAlertShown);
                    // Only display toast when app is in background, we already have dialog when app is in foreground
                    if(mApplication.isInForeground){
                        // App is in foreground, show dialog instead of toast
                        intent = new Intent("com.basbes.dating.audioChanged");
                        intent.putExtra(ALERT_MESSAGE_EXTRA_KEY, PREFERENCE_KEY_HEADSET_SHOWN);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }else{
                        Toast.makeText(context, R.string.headset_plugged_toast, Toast.LENGTH_LONG).show();
                    }
                    // To not show the alert message of toast over and over again
                    sharedPreferences.edit().putBoolean(PREFERENCE_KEY_HEADSET_SHOWN,true).apply();
                }
                break;
            case PREFERENCE_KEY_SPEAKER_ON_SHOWN:
                Log.d(TAG, "sendAlertMessage : is app in InForeground= "+ mApplication.isInForeground);
                // Only show message or toast if it wan's shown before
                boolean isSpeakerAlertShown = sharedPreferences.getBoolean(PREFERENCE_KEY_SPEAKER_ON_SHOWN, false);
                if(!isSpeakerAlertShown){
                    Log.d(TAG, "sendAlertMessage : Only show message or toast if it wasn't shown. isSpeakerAlertShown= "+ isSpeakerAlertShown);
                    // Only display toast when app is in background, we already have dialog when app is in foreground
                    if(mApplication.isInForeground){
                        // App is in foreground, show dialog instead of toast
                        intent = new Intent("com.basbes.dating.audioChanged");
                        intent.putExtra(ALERT_MESSAGE_EXTRA_KEY, PREFERENCE_KEY_SPEAKER_ON_SHOWN);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }else{
                        Toast.makeText(context, R.string.turn_speaker_on_toast, Toast.LENGTH_LONG).show();
                    }
                    // To not show the alert message of toast over and over again
                    sharedPreferences.edit().putBoolean(PREFERENCE_KEY_SPEAKER_ON_SHOWN,true).apply();
                }
                break;
            case PREFERENCE_KEY_VOLUME_SHOWN :
                Log.d(TAG, "sendAlertMessage : is app in InForeground= "+ mApplication.isInForeground);
                // Only show message or toast if it wan's shown before
                boolean isVolumeAlertShown = sharedPreferences.getBoolean(PREFERENCE_KEY_VOLUME_SHOWN, false);
                if(!isVolumeAlertShown){
                    // Only display toast when app is in background, we already have dialog when app is in foreground
                    if(mApplication.isInForeground){
                        // App is in foreground, show dialog instead of toast
                        intent = new Intent("com.basbes.dating.audioChanged");
                        intent.putExtra(ALERT_MESSAGE_EXTRA_KEY, PREFERENCE_KEY_VOLUME_SHOWN );
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }else{
                        Toast.makeText(context, R.string.increase_volume_toast, Toast.LENGTH_LONG).show();
                    }
                    // To not show the alert message of toast over and over again
                    sharedPreferences.edit().putBoolean(PREFERENCE_KEY_VOLUME_SHOWN,true).apply();
                }
                break;
        }
    }

    private void scheduleNextAlarm(AlarmManager alarmManager) {

        alarmIntent = new Intent(context, SoundIdAlarm.class);
        alarmIntent.putExtra(USER_SOUND_ID_KEY, mCurrentSoundId);
        pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_ALARM,alarmIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime()+mRandomAlarmInterval-SystemClock.elapsedRealtime()%1000, pendingIntent);
        }else{
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime()+mRandomAlarmInterval-SystemClock.elapsedRealtime()%1000 , pendingIntent);
        }

    }

    // Stops the alarm
    private void stopAlarm() {
        Log.d(TAG , "stopAlarm()");
        alarmIntent = new Intent(context, SoundIdAlarm.class);
        pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_ALARM, alarmIntent,PendingIntent.FLAG_UPDATE_CURRENT);
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
        notificationManager.cancel(VISIBILITY_NOTIFICATION_ID);
    }
}
