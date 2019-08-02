package com.trackaty.chat.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

import io.chirp.connect.ChirpConnect;
import io.chirp.connect.interfaces.ConnectEventListener;
import io.chirp.connect.models.ChirpError;

public class SoundIdAlarm extends BroadcastReceiver {

    private final static String TAG = SoundIdAlarm.class.getSimpleName();
    //private String userID;
    private int mCurrentSoundId;
    private String mCurrentUserId;
    byte[] payload ; // = {0, 0, 0, 0};
    //byte[] stringPayload;

    String CHIRP_APP_KEY = "3FE3cDeFecFc9091b2a3D197A";
    String CHIRP_APP_SECRET = "1D7c6591bBb356e68Fd00EB1AAdDdD1aeFabC9b7020aaB1EFd";
    String CHIRP_APP_CONFIG = "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";

    private ChirpConnect chirp;

    private AlarmManager alarmManager;
    private Intent alarmIntent;
    private PendingIntent pendingIntent;
    private final static int ALARM_INTERVAL = 60*1000;
    private static final int REQUEST_CODE_ALARM = 13;

    private static final String USER_SOUND_ID_KEY = "userSoundId";
    private static final String USER_ID_KEY = "userId";
    final Handler handler = new Handler();
    // A listener for sound id
    ConnectEventListener chirpEventListener = new ConnectEventListener() {
        @Override
        public void onSent(@NotNull byte[] bytes, int i) {
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
        public void onSending(@NotNull byte[] bytes, int i) {
            Log.d(TAG, "onSending");
        }

        // After we received a sound Id
        @Override
        public void onReceived(@Nullable byte[] bytes, int i) {
            Log.d(TAG, "onReceived");
        }

        @Override
        public void onReceiving(int i) {
            Log.d(TAG, "onReceiving");
        }

        @Override
        public void onStateChanged(int i, int i1) {
            Log.d(TAG, "onStateChanged");
        }

        @Override
        public void onSystemVolumeChanged(float v, float v1) {
            Log.d(TAG, "onSystemVolumeChanged");
        }
    };

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

        // To schedule the next alarm, this is better than repeating alarm in case of one of the alarms was delayed
        alarmManager =  (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        scheduleNextAlarm(context, alarmManager);

        chirp = new ChirpConnect(context, CHIRP_APP_KEY, CHIRP_APP_SECRET);

        ChirpError error = chirp.setConfig(CHIRP_APP_CONFIG);
        if (error.getCode() == 0) {
            Log.v("ChirpSDK: ", "Configured ChirpSDK");
            startSDK();
        } else {
            Log.e("ChirpError: ", error.getMessage());
        }

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

        pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK  , " chirptest:mywakelocktag");

        // acquire the lock
        wl.acquire(10 *1000L *//*10 minutes*//*);

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

    private void scheduleNextAlarm(Context context, AlarmManager alarmManager) {

        alarmIntent  = new Intent(context, SoundIdAlarm.class);
        alarmIntent.putExtra(USER_SOUND_ID_KEY, mCurrentSoundId);
        pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_ALARM,alarmIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime()+ALARM_INTERVAL-SystemClock.elapsedRealtime()%1000, pendingIntent);
        }else{
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime()+ALARM_INTERVAL-SystemClock.elapsedRealtime()%1000 , pendingIntent);
        }

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
            Log.e("ChirpError: ", error.getMessage());
        } else {
            Log.v("ChirpSDK: ", "Started ChirpSDK");
            chirp.setListener(chirpEventListener);
            sendPayload();
        }
    }

    private void sendPayload() {

        // Schedule a task to run every 60 seconds with no initial delay.
        Log.i(TAG , "maxPayload= "+ chirp.maxPayloadLength());
        Log.i(TAG , "sending Payload= ");
        //chirpConnect.setVolume(0.1f);

        ChirpError error = chirp.send(payload);

        Log.d(TAG, "sending data: Payload size= "+payload.length);
        if (error.getCode() > 0) {
            Log.e(TAG, "ConnectError: " + error.getMessage());
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
        Log.i(TAG, "chirpConnect stopped and closed and wakelock is released" );
    }
}
