package com.battleshippark.bsp_gallery;

import com.battleshippark.bsp_gallery.media.MediaDirectoryModel;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import lombok.Data;

/**
 */
@Data
public class MainModel {
    private final Bus eventBus;
    private List<MediaDirectoryModel> mediaDirectoryModelList;
    private MEDIA_MODE mediaMode = MEDIA_MODE.ALL;

    public MainModel(Bus eventBus) {
        this.eventBus = eventBus;
    }

    @Subscribe
    public void OnActivityCreated(Events.OnActivityCreated event) {
        eventBus.register(this);
    }

    @Subscribe
    public void OnActivityDestroyed(Events.OnActivityDestroyed event) {
        eventBus.unregister(this);
    }

    public void setMediaDirectoryModelList(List<MediaDirectoryModel> arg) {
        mediaDirectoryModelList = arg;

        eventBus.post(Events.OnMediaDirectoryListUpdated.EVENT);
    }

    public enum MEDIA_MODE {
        ALL, IMAGE, VIDEO
    }
}
