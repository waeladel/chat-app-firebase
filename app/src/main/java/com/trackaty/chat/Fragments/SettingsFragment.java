package com.trackaty.chat.Fragments;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavDeepLinkBuilder;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trackaty.chat.R;
import com.trackaty.chat.Utils.FilesHelper;
import com.trackaty.chat.activities.MainActivity;
import com.trackaty.chat.models.User;
import com.trackaty.chat.receivers.SoundIdAlarm;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.POWER_SERVICE;
import static androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode;
import static com.trackaty.chat.App.VISIBILITY_CHANNEL_ID;

public class SettingsFragment extends PreferenceFragmentCompat {
    private final static String TAG = SettingsFragment.class.getSimpleName();

    // click listener to pass click events to parent fragment

    private static final String PREFERENCE_KEY_VISIBLE = "visible" ;
    private static final String PREFERENCE_KEY_NIGHT = "night" ;
    private static final String PREFERENCE_KEY_RINGTONE = "notification";
    private static final String PREFERENCE_KEY_VERSION = "version";

    private static final String NIGHT_VALUE_LIGHT = "light";
    private static final String NIGHT_VALUE_DARK = "dark";
    private static final String NIGHT_VALUE_BATTERY = "battery";
    private static final String NIGHT_VALUE_SYSTEM = "system";

    private FragmentManager fragmentManager;
    private  static final String PERMISSION_RATIONALE_FRAGMENT = "permissionFragment";
    private  static final String BATTERY_ALERT_FRAGMENT = "batteryFragment";
    private  static final int RESULT_REQUEST_RECORD_AUDIO = 21; // for record audio permission
    private static final int REQUEST_CODE_ALARM = 13; // To detect if alarm is already set or not
    private static final int RINGTONE_PICKER_REQUEST_CODE = 164;
    private static final String USER_SOUND_ID_KEY = "userSoundId";
    private static final String USER_ID_KEY = "userId";

    private static final String PREFERENCE_BATTERY_ALERT_SHOWING_TIMES_KEY = "batteryAlert"; // Key for number of showing battery optimization dialog

    private final static int NOTIFICATION_PENDING_INTENT_REQUEST_CODE = 55; // For visibility notification
    private static final int VISIBILITY_NOTIFICATION_ID = 7; // channel Id for visibility notification, Used to cancel it too


    private Preference versionPreference, ringTonePreference;
    private ListPreference nightPreference;
    private SwitchPreference visiblePreference;

    private Context mActivityContext;
    private Activity activity;
    private SharedPreferences sharedPreferences;
    private File mOutputFile;
    private RingtoneManager ringtoneManager;

    // [START declare_database_ref]
    private DatabaseReference mDatabaseRef, mUsersRef;
    private User mCurrentUser;
    private FirebaseUser mFirebaseCurrentUser;
    private String mCurrentUserId;

    private static final String NOTIFICATION_SOUND_DEFAULT_NAME = "Basbes";

    private AlarmManager alarmManager;
    private Intent alarmIntent;
    private PendingIntent pendingIntent;
    private NotificationManagerCompat notificationManager;
    private Notification mNotification;

    public SettingsFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static SettingsFragment newInstance() {


        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        //args.putParcelableArrayList(PRIVET_CONTACTS_KEY, privateContacts);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Log.d(TAG,"onCreatePreferences");
        setPreferencesFromResource(R.xml.setting_preferences, rootKey);

        // Step 4: Set output file for notification audio
        mOutputFile = FilesHelper.getOutputMediaFile(FilesHelper.MEDIA_TYPE_Audio);


        //Get current logged in user
        mFirebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mCurrentUserId = mFirebaseCurrentUser != null ? mFirebaseCurrentUser.getUid() : null;

        // [START declare_database_ref]
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mUsersRef = mDatabaseRef.child("users");

        // Create alarm manager and Intent to be used when user set the visibility alarm
        alarmManager  =  (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        alarmIntent  = new Intent(activity, SoundIdAlarm.class);

        // Display app version
        versionPreference = findPreference(PREFERENCE_KEY_VERSION);
        if (versionPreference != null && mActivityContext != null) {
            versionPreference.setSummary(getAppVersionName(mActivityContext));
        }

        //RingtoneManager instance
        ringtoneManager = new RingtoneManager(activity);
        ringtoneManager.setType(RingtoneManager.TYPE_NOTIFICATION);

        // Display selected ringTone
        ringTonePreference = findPreference(PREFERENCE_KEY_RINGTONE);
        if (ringTonePreference != null && mActivityContext != null) {

            // No need to summary for api above 26. on api > 26 we pick sound from settings page
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                // notification sound value is empty, never exist
                if(null == sharedPreferences.getString(PREFERENCE_KEY_RINGTONE, null)){
                    Log.d(TAG, "ringTonePreference is empty");
                    // set summery to basbes. No need to change ringTonePreference value as we need it null
                    // To use the raw file when sending a notification, as long as user didn't change it.
                    ringTonePreference.setSummary(R.string.app_name);
                }else{
                    Log.d(TAG, "ringTonePreference exists");
                    ringTonePreference.setSummary(getFileName(getCurrentRingtoneUri()));
                }
            }
        }

        if (ringTonePreference != null) {
            ringTonePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        ringTonePicker();
                    }else{
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, mActivityContext.getPackageName());
                        startActivity(intent);
                    }

                    return true;
                }
            });
        }

        visiblePreference = findPreference(PREFERENCE_KEY_VISIBLE);
        if(visiblePreference != null){
            visiblePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Log.d(TAG, "Pending Preference value is: " + newValue);
                    // get current user sound id to pass it to the alarm
                    if(mCurrentUserId != null){
                        mUsersRef.child(mCurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    // Get user value
                                    Log.d(TAG, "getUser dataSnapshot key: " + dataSnapshot.getKey());
                                    mCurrentUser = dataSnapshot.getValue(User.class);
                                    if(mCurrentUser != null){
                                        mCurrentUser.setKey(dataSnapshot.getKey());
                                        // Create an alarm with current sound id if switch is checked
                                        if(newValue.equals(true)){
                                            Log.d(TAG, "switch is checked. value= " + newValue);
                                            if(mCurrentUser.getSoundId()!= 0){
                                                startAlarm(); // start the alarm .
                                            }else{
                                                Log.d(TAG , "current user doesn't have a sound id");
                                                Toast.makeText(mActivityContext, R.string.no_sound_id_found_error,
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }else{
                                            // stop the alarm if already started
                                            if (isAlarmExist()) {
                                                stopAlarm();
                                            }
                                        }

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Getting Post failed, log a message
                                Log.w(TAG, "get current user:onCancelled", databaseError.toException());
                            }
                        });

                        return true; // to change the preference value

                    }else{
                        // mCurrentUserId is null, user is not logged in
                        Toast.makeText(mActivityContext,R.string.user_visible_error, Toast.LENGTH_SHORT).show();
                        return false; // don't change the preference value
                    }
                }
            });
        }

        nightPreference = findPreference(PREFERENCE_KEY_NIGHT);
        if (nightPreference != null ) {
            // include "user system default" on android q API 29 and above
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                nightPreference.setEntries(R.array.settings_dark_list_with_system);
                nightPreference.setEntryValues(R.array.settings_dark_values_with_system);
                // Only set the index to the third option when it's not set
                if(nightPreference.getValue()== null){
                    nightPreference.setValueIndex(2);
                }

            }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                // include "Set by Battery Saver" on android less than pis API 28 and above API 21
                nightPreference.setEntries(R.array.settings_dark_list_with_battery);
                nightPreference.setEntryValues(R.array.settings_dark_values_with_battery);
                // Only set the index to the third option when it's not set
                if(nightPreference.getValue()== null){
                    nightPreference.setValueIndex(2);
                }
            }else{
                // remove "use system default" and "Set by Battery Saver" on android less than pis API 21
                nightPreference.setEntries(R.array.settings_dark_list);
                nightPreference.setEntryValues(R.array.settings_dark_values);
                // Only set the index to the first option when it's not set
                if(nightPreference.getValue()== null){
                    nightPreference.setValueIndex(0);
                }
            }

            nightPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Log.d(TAG, "Pending Preference value is: " + newValue);
                    switch (newValue.toString()){
                        case NIGHT_VALUE_LIGHT:
                            setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            break;
                        case NIGHT_VALUE_DARK:
                            setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            break;
                        case NIGHT_VALUE_BATTERY:
                            setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                            break;
                        case NIGHT_VALUE_SYSTEM:
                            setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                            break;
                    }
                    return true;
                }
            });
        }else {// End of if nightPreference != null. Now what to do when nightPreference is null
            Log.i(TAG, "darkMode value is not set yet");
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Set the default value to FOLLOW_SYSTEM because it's API 29 and above
                if(nightPreference.getValue()== null){
                    nightPreference.setValueIndex(2);
                }
            }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                // Set the default value to AUTO_BATTERY because we are below api 29
                if(nightPreference.getValue()== null){
                    nightPreference.setValueIndex(2);
                }
            }else{
                // Set the default value to NIGHT_NO because
                // "system default" and "Battery Saver" not supported on api below 21
                if(nightPreference.getValue()== null){
                    nightPreference.setValueIndex(0);
                }
            }

        }

    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        mActivityContext = context;

        if (context instanceof Activity){// check if fragmentContext is an activity
            activity =(Activity) context;
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivityContext /* Activity context */);
        notificationManager = NotificationManagerCompat.from(context); // To notify the manager to create or cancel notification
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "chats onActivityCreated");

        if(getActivity()!= null) {
            ActionBar actionbar = ((MainActivity) getActivity()).getSupportActionBar();
            if (actionbar != null) {
                actionbar.setTitle(R.string.settings_frag_title);
                actionbar.setDisplayHomeAsUpEnabled(true);
                actionbar.setHomeButtonEnabled(true);
                actionbar.setDisplayShowCustomEnabled(false);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check if the request code is same as what is passed  here it is 1
        if (requestCode == RINGTONE_PICKER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Make sure the request was successful
            Uri ringtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            Log.d(TAG, "selected sound= "+ ringtoneUri +" sound name= "+getFileName(ringtoneUri));

            ringTonePreference.setSummary(getFileName(ringtoneUri));

            // Save the selected ringtone
            sharedPreferences.edit().putString(PREFERENCE_KEY_RINGTONE,String.valueOf(ringtoneUri)).apply();

        }
    }

    private String getAppVersionName(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private void ringTonePicker() {

        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, R.string.notification_picker_title);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, getCurrentRingtoneUri());
        //intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Uri.parse("android.resource://" + mActivityContext.getPackageName() + "/" + R.raw.basbes)); //my custom sound
        //intent.putExtra(ringtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Uri.parse(mOutputFile.getPath()));
        //intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + File.pathSeparator + File.separator + mActivityContext.getPackageName() + "/raw/" + "basbes"));

        this.startActivityForResult(intent, RINGTONE_PICKER_REQUEST_CODE);

    }

    // Returns ringtone url from settings property
    @Nullable
    private Uri getCurrentRingtoneUri() {
        Log.d(TAG, "getCurrentRingtoneUri= "+ sharedPreferences.getString(PREFERENCE_KEY_RINGTONE, "android.resource://" + mActivityContext.getPackageName() + "/" + R.raw.basbes));
        return Uri.parse(sharedPreferences.getString(PREFERENCE_KEY_RINGTONE, "android.resource://" + mActivityContext.getPackageName() + "/" + R.raw.basbes));
    }

    // To get the ringtone name from it's uri
    private String getFileName(Uri uri) {
        int ringtonePosition = ringtoneManager.getRingtonePosition(uri);
        String result ;
         if(ringtonePosition == -1){
             result = mActivityContext.getResources().getString(R.string.notification_picker_default_sound);
         }else{
             Cursor cursor = ringtoneManager.getCursor();
             cursor.moveToPosition(ringtonePosition);
             result = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
         }


        /*if (TextUtils.equals(uri.getScheme(), "content")) {
            try (Cursor cursor = mActivityContext.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = 0;
            if (result != null) {
                cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
        }*/
        return result;
    }

    // Start the alarm
    private void startAlarm() {
        if(!isDozeWhiteList()){ // check if the app is already exempted from battery optimization
            int batteryAlertShowTimes = sharedPreferences.getInt(PREFERENCE_BATTERY_ALERT_SHOWING_TIMES_KEY, 0);
            if(batteryAlertShowTimes < 3){
                // show the dialog only if it wasn't shown 3 times before
                showBatteryDialog();
                sharedPreferences.edit().putInt(PREFERENCE_BATTERY_ALERT_SHOWING_TIMES_KEY, batteryAlertShowTimes+1).apply();
            }
        }
        setAlarm();
    }

    // check if alarm PendingIntent is already exists or not
    private boolean isAlarmExist() {
        PendingIntent checkPendingIntent = PendingIntent.getBroadcast(activity, REQUEST_CODE_ALARM, alarmIntent, PendingIntent.FLAG_NO_CREATE);
        if (checkPendingIntent != null){
            Log.d(TAG , "isAlarmExist: yet it is exist. checkPendingIntent= "+checkPendingIntent);
            return true;
        }else{
            Log.d(TAG , "isAlarmExist: not exist. checkPendingIntent= "+checkPendingIntent);
            return false;
        }
    }

    private void setAlarm() {
        Log.d(TAG , "setAlarm(). mCurrentUserId= "+mCurrentUser.getKey() + " soundId="+ mCurrentUser.getSoundId());
        //create a Bundle object
        Bundle extras = new Bundle();
        extras.putString(USER_ID_KEY, mCurrentUser.getKey());
        extras.putInt(USER_SOUND_ID_KEY, mCurrentUser.getSoundId());
        //attach the bundle to the Intent object
        alarmIntent.putExtras(extras);

        //alarmIntent.putExtra(USER_SOUND_ID_KEY, mCurrentUser.getSoundId());
        //alarmIntent.putExtra(USER_ID_KEY, mCurrentUserId);
        pendingIntent = PendingIntent.getBroadcast(activity, REQUEST_CODE_ALARM, alarmIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        if (alarmManager != null) {
            Log.d(TAG , "setAlarm: alarmManager= "+alarmManager);
            //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),60000, pendingIntent);
            //alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() , 60000, pendingIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), pendingIntent);
            }else{
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() , pendingIntent);
            }
        }
        setNotification(); // Create Ongoing notification when alarm is set
        /*//alarmManager.setAndAllowWhileIdle();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC, System.currentTimeMillis(), pendingIntent);
        }*/

    }

    // Stops the alarm
    private void stopAlarm() {
        Log.d(TAG , "stopAlarm()");
        alarmManager =  (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        alarmIntent = new Intent(activity, SoundIdAlarm.class);
        pendingIntent = PendingIntent.getBroadcast(activity, REQUEST_CODE_ALARM, alarmIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
        cancelNotification();
    }

    // check if user had whiteList this app and exempted it from doze mode
    private boolean isDozeWhiteList() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = activity.getPackageName();
            PowerManager pm = (PowerManager) activity.getSystemService(POWER_SERVICE);
            if (pm != null) {
                // is exempt from power optimization
                // Not exempt from power optimization
                return pm.isIgnoringBatteryOptimizations(packageName);
            }else{
                return false;
            }
        }else{
            // API is below android marshmallow anyway
            return true;
        }
    }

    //Show a dialog to confirm blocking user
    private void showBatteryDialog() {
        BatteryAlertFragment batteryFragment = BatteryAlertFragment.newInstance(mActivityContext);
        if (getFragmentManager() != null) {
            fragmentManager = getFragmentManager();
            batteryFragment.show(fragmentManager, BATTERY_ALERT_FRAGMENT);
            Log.i(TAG, "BatteryAlertFragment show clicked ");
        }
    }

    // Create Ongoing notification when alarm is set
    private void setNotification() {
        Intent NotificationClickIntent = new Intent(mActivityContext, MainActivity.class);
        //PendingIntent pendingIntent = PendingIntent.getActivity(mActivityContext, NOTIFICATION_PENDING_INTENT_REQUEST_CODE, NotificationClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent notificationPendingIntent = new NavDeepLinkBuilder(mActivityContext)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.settingsFragment)
                //.setArguments(bundle)
                .createPendingIntent();

        mNotification = new NotificationCompat.Builder(mActivityContext, VISIBILITY_CHANNEL_ID)
                .setContentTitle(getString(R.string.notification_visibility_title))
                .setContentText(getString(R.string.notification_visibility_body))
                .setSmallIcon(R.mipmap.ic_notification)
                .setColor(getResources().getColor(R.color.color_primary))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_SOCIAL)
                .setContentIntent(notificationPendingIntent)
                .setOngoing(true)
                .build();

        notificationManager.notify(VISIBILITY_NOTIFICATION_ID, mNotification);

    }

    // Cancel Ongoing notification when user click visibility again
    private void cancelNotification() {
        notificationManager.cancel(VISIBILITY_NOTIFICATION_ID);
    }

}
