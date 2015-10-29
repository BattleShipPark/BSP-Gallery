package com.battleshippark.bsp_gallery;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.battleshippark.bsp_gallery.media.MediaFileModel;

import java.util.List;


/**
 */
public class BspApplication extends Application {
    private static final Handler handler = new Handler(Looper.getMainLooper());

    public BspApplication() {
        super();

        AnalyticsTrackers.initialize(this);
    }

    public static Handler getHandler() {
        return handler;
    }

    /* 액티비티간의 데이터 교환을 위해 사용 */
    public static class TempStorage {
        public static List<MediaFileModel> mediaFileModelList;
    }
}
