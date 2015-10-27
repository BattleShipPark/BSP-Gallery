package com.battleshippark.bsp_gallery;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;


/**
 */
public class BspApplication extends Application {
    private static final Handler handler = new Handler(Looper.getMainLooper());

    public BspApplication() {
        super();
    }

    public static Handler getHandler() {
        return handler;
    }
}
