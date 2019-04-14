package com.deckerth.thomas.eggservice;

import android.app.Application;
import android.content.Context;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Android Application class. Used for accessing singletons.
 */
public class BasicApp extends Application {

    private AppExecutors mAppExecutors;
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mAppExecutors = new AppExecutors();
        mContext = this;
    }

    public static Context getContext() { return mContext; }

    public static String getContextString( int id ) {return mContext.getString(id);}
}
