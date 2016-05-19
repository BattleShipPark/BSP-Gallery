package com.battleshippark.bsp_gallery;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.battleshippark.bsp_gallery.pref.SharedPreferenceController;


/**
 */
public class BspApplication extends Application {
    private static final Handler handler = new Handler(Looper.getMainLooper());

    public BspApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        AnalyticsTrackers.initialize(this);
        SharedPreferenceController.create(this);
    }

    public static Handler getHandler() {
        return handler;
    }
}
