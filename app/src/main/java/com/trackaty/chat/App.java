package com.trackaty.chat;


import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

/**
 * Created on 25/03/2017.
 */

public class App extends Application {

    private final static String TAG = App.class.getSimpleName();
    private static Context sApplicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sApplicationContext = getApplicationContext();
        Log.i(TAG, "Application class onCreate");
        // Initialize the SDK before executing any other operations,
        //FacebookSdk.sdkInitialize(sApplicationContext);

        // [START Firebase Database enable persistence]
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        // [END rtdb_enable_persistence]

        // [START Picasso enable persistence]
        /*Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);*/
        // [END Picasso enable persistence]
    }

    public static Context getContext() {
        return sApplicationContext;
        //return instance.getApplicationContext();
    }
}

