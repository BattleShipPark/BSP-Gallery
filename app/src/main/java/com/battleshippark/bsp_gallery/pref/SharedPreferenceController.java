package com.battleshippark.bsp_gallery.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.MainThread;
import android.util.Log;

import com.battleshippark.bsp_gallery.EventBusHelper;
import com.battleshippark.bsp_gallery.Events;
import com.battleshippark.bsp_gallery.activity.folders.FoldersActivityModel;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 */
public class SharedPreferenceController {
    private static final String NAME = "sp";
    private static SharedPreferenceController INSTANCE;
    private final Context context;
    private final SharedPreferenceModel model;
    private final Map<String, Action1<String>> writingProcessMap = new HashMap<>();

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

    @Subscribe
    public void OnMediaModeUpdated(Events.OnMediaModeUpdated event) {
        Log.d("", getClass().getSimpleName() + ".OnMediaModeUpdated()");

        String key = SharedPreferenceModel.KEY_MEDIA_MODE;
        writingProcessMap.get(key).call(key);
    }

    public MediaFilterMode readMediaMode(String key) {
        if (model.getMediaFilterMode() == null) {
            SharedPreferences p = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
            model.setMediaFilterMode(MediaFilterMode.valueOf(p.getString(key, MediaFilterMode.ALL.name())));
        }
        return model.getMediaFilterMode();
    }

    public void writeMediaMode(String key, FoldersActivityModel model) {
        SharedPreferences p = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        p.edit().putString(key, model.getMediaFilterMode().name()).apply();
        model.setMediaFilterMode(model.getMediaFilterMode());
    }

    private void sendEvent() {
        EventBusHelper.eventBus.post(new Events.OnSharedPreferenceRead(model));
    }
}
