package com.battleshippark.bsp_gallery.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.battleshippark.bsp_gallery.BspApplication;
import com.battleshippark.bsp_gallery.Events;
import com.battleshippark.bsp_gallery.activity.folders.FoldersModel;
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
    private final Context context;
    private final FoldersModel foldersModel;
    private final SharedPreferenceModel model;
    private final Map<String, Action1<String>> readingProcessMap = new HashMap<>();
    private final Map<String, Action1<String>> writingProcessMap = new HashMap<>();

    public SharedPreferenceController(Context context, FoldersModel foldersModel) {
        this.context = context;
        this.foldersModel = foldersModel;

        foldersModel.getEventBus().register(this);

        model = new SharedPreferenceModel();

        readingProcessMap.put(SharedPreferenceModel.KEY_MEDIA_MODE, this::readMediaMode);
        writingProcessMap.put(SharedPreferenceModel.KEY_MEDIA_MODE, this::writeMediaMode);
    }

    @Subscribe
    public void OnActivityCreated(Events.OnActivityCreated event) {
        Log.d("", getClass().getSimpleName() + ".OnActivityCreated()");

        Observable.from(readingProcessMap.keySet())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        key -> readingProcessMap.get(key).call(key),
                        Throwable::printStackTrace,
                        this::sendEvent
                );
    }

    @Subscribe
    public void OnActivityDestroyed(Events.OnActivityDestroyed event) {
        foldersModel.getEventBus().unregister(this);
    }

    @Subscribe
    public void OnMediaModeUpdated(Events.OnMediaModeUpdated event) {
        Log.d("", getClass().getSimpleName() + ".OnMediaModeUpdated()");

        String key = SharedPreferenceModel.KEY_MEDIA_MODE;
        writingProcessMap.get(key).call(key);
    }

    private void sendEvent() {
        BspApplication.getHandler().post(
                () -> foldersModel.getEventBus().post(new Events.OnSharedPreferenceRead(model))
        );
    }

    private void readMediaMode(String key) {
        SharedPreferences p = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        model.setMediaMode(FoldersModel.MEDIA_MODE.valueOf(p.getString(key, FoldersModel.MEDIA_MODE.ALL.name())));
    }

    private void writeMediaMode(String key) {
        SharedPreferences p = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        p.edit().putString(key, foldersModel.getMediaMode().name()).apply();
    }
}
