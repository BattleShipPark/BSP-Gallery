package com.battleshippark.bsp_gallery;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.battleshippark.bsp_gallery.pref.SharedPreferenceController;

import io.realm.Realm;
import io.realm.RealmConfiguration;


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
        initRealm();
        SharedPreferenceController.create(this);
    }

    private void initRealm() {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this)
                .schemaVersion(RealmHelper.SCHEMA_VERSION)
                .migration(RealmHelper.migration).build();
        Realm.setDefaultConfiguration(realmConfig);
    }

    public static Handler getHandler() {
        return handler;
    }
}
