package com.trackaty.chat.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.trackaty.chat.R;
import com.trackaty.chat.activities.MainActivity;

import org.jetbrains.annotations.NotNull;

import static androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode;

public class SettingsFragment extends PreferenceFragmentCompat {
    private final static String TAG = SettingsFragment.class.getSimpleName();

    // click listener to pass click events to parent fragment

    private static final String PREFERENCE_KEY_NIGHT = "night" ;
    private static final String PREFERENCE_KEY_RINGTONE = "ringtone";
    private static final String PREFERENCE_KEY_VERSION = "version";

    private static final String NIGHT_VALUE_LIGHT = "light";
    private static final String NIGHT_VALUE_DARK = "dark";
    private static final String NIGHT_VALUE_BATTERY = "battery";
    private static final String NIGHT_VALUE_SYSTEM = "system";

    private Preference versionPreference;
    private ListPreference nightPreference;

    private Context mActivityContext;
    private Activity activity;

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

        // Display app version
        versionPreference = findPreference(PREFERENCE_KEY_VERSION);
        if (versionPreference != null && mActivityContext != null) {
            versionPreference.setSummary(getAppVersionName(mActivityContext));
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
        }else {// End of (if nightPreference != null)
            Log.i(TAG, "darkModeValue is not set yet");
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


    private String getAppVersionName(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }


}
