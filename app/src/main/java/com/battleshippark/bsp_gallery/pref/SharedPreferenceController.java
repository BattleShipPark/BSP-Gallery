package com.battleshippark.bsp_gallery.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.MainThread;

import com.battleshippark.bsp_gallery.EventBusHelper;
import com.battleshippark.bsp_gallery.presentation.folders.FoldersActivityModel;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;

/**
 */
public class SharedPreferenceController {
    private static final String NAME = "sp";
    private static SharedPreferenceController INSTANCE;
    private final Context context;
    private final SharedPreferenceModel model;

    private SharedPreferenceController(Context context) {
        this.context = context;
        this.model = new SharedPreferenceModel();
        EventBusHelper.eventBus.register(this);
    }

    @MainThread
    public static void create(Context context) {
        if (INSTANCE != null) {
            throw new IllegalStateException();
        }
        INSTANCE = new SharedPreferenceController(context);
    }

    public static SharedPreferenceController instance() {
        return INSTANCE;
    }

    public MediaFilterMode readMediaFilterMode() {
        if (model.getMediaFilterMode() == null) {
            SharedPreferences p = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
            model.setMediaFilterMode(MediaFilterMode.valueOf(
                    p.getString(SharedPreferenceModel.KEY_MEDIA_MODE, MediaFilterMode.ALL.name())));
        }
        return model.getMediaFilterMode();
    }

    public void writeMediaFilterMode(MediaFilterMode mode) {
        SharedPreferences p = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        p.edit().putString(SharedPreferenceModel.KEY_MEDIA_MODE, mode.name()).apply();
        model.setMediaFilterMode(mode);
    }
}
