package com.trackaty.chat.fragments;

import android.app.Activity;
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
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.trackaty.chat.R;
import com.trackaty.chat.utils.FilesHelper;
import com.trackaty.chat.activities.MainActivity;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import static android.app.Activity.RESULT_OK;
import static androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode;

public class SettingsFragment extends PreferenceFragmentCompat {
    private final static String TAG = SettingsFragment.class.getSimpleName();

    // click listener to pass click events to parent fragment

    private static final String PREFERENCE_KEY_NIGHT = "night" ;
    private static final String PREFERENCE_KEY_RINGTONE = "notification";
    private static final String PREFERENCE_KEY_VERSION = "version";

    private static final String NIGHT_VALUE_LIGHT = "light";
    private static final String NIGHT_VALUE_DARK = "dark";
    private static final String NIGHT_VALUE_BATTERY = "battery";
    private static final String NIGHT_VALUE_SYSTEM = "system";

    private static final int RINGTONE_PICKER_REQUEST_CODE = 164;

    private Preference versionPreference, ringTonePreference;
    private ListPreference nightPreference;

    private Context mActivityContext;
    private Activity activity;
    private SharedPreferences sharedPreferences;
    private File mOutputFile;
    private RingtoneManager ringtoneManager;

    private static final String NOTIFICATION_SOUND_DEFAULT_NAME = "Basbes";

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

}
