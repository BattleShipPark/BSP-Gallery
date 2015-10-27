package com.battleshippark.bsp_gallery.activity.folders;

import com.battleshippark.bsp_gallery.Events;
import com.battleshippark.bsp_gallery.media.MediaDirectoryModel;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import lombok.Data;

/**
 */
@Data
public class FoldersModel {
    private final Bus eventBus;
    private List<MediaDirectoryModel> mediaDirectoryModelList;
    private MEDIA_MODE mediaMode = MEDIA_MODE.ALL;

    public FoldersModel(Bus eventBus) {
        this.eventBus = eventBus;
    }

    @Subscribe
    public void OnActivityCreated(Events.OnActivityCreated event) {
//        eventBus.register(this);
    }

    @Subscribe
    public void OnActivityDestroyed(Events.OnActivityDestroyed event) {
//        eventBus.unregister(this);
    }

    public void setMediaDirectoryModelList(List<MediaDirectoryModel> modelList) {
        mediaDirectoryModelList = modelList;

        eventBus.post(Events.OnMediaDirectoryListUpdated.EVENT);
    }

    public void setMediaMode(MEDIA_MODE mode) {
        mediaMode = mode;

        eventBus.post(Events.OnMediaModeUpdated.EVENT);
    }

    public enum MEDIA_MODE {
        ALL, IMAGE, VIDEO
    }
}
