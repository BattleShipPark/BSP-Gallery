package com.battleshippark.bsp_gallery.activity.folders;

import com.battleshippark.bsp_gallery.Events;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import lombok.Data;

/**
 */
@Data
public final class FoldersActivityModel {
    private final Bus eventBus;
    private List<MediaFolderModel> mediaFolderModelList;
    private MediaFilterMode mediaFilterMode = MediaFilterMode.ALL;

    public FoldersActivityModel(Bus eventBus) {
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

        eventBus.post(Events.OnMediaFolderListUpdated.UPDATED);
    }

    public void setMediaFilterMode(MediaFilterMode mode) {
        mediaFilterMode = mode;

        eventBus.post(Events.OnMediaModeUpdated.EVENT);
    }
}
