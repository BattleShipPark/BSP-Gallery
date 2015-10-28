package com.battleshippark.bsp_gallery.activity.folders;

import com.battleshippark.bsp_gallery.Events;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;
import com.battleshippark.bsp_gallery.media.MediaMode;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import lombok.Data;

/**
 */
@Data
public class FoldersModel {
    private final Bus eventBus;
    private List<MediaFolderModel> mediaFolderModelList;
    private MediaMode mediaMode = MediaMode.ALL;

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

    public void setMediaFolderModelList(List<MediaFolderModel> modelList) {
        mediaFolderModelList = modelList;

        eventBus.post(Events.OnMediaDirectoryListUpdated.EVENT);
    }

    public void setMediaMode(MediaMode mode) {
        mediaMode = mode;

        eventBus.post(Events.OnMediaModeUpdated.EVENT);
    }
}
