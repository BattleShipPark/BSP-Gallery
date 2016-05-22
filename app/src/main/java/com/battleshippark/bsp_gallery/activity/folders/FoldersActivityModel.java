package com.battleshippark.bsp_gallery.activity.folders;

import android.support.annotation.VisibleForTesting;

import com.battleshippark.bsp_gallery.EventBusHelper;
import com.battleshippark.bsp_gallery.Events;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;
import com.squareup.otto.Bus;

import java.util.List;

import lombok.Data;

/**
 */
@Data
public final class FoldersActivityModel {
    private final Bus eventBus;
    private List<MediaFolderModel> mediaFolderModelList;
    private MediaFilterMode mediaFilterMode = MediaFilterMode.ALL;

    public FoldersActivityModel() {
        this(EventBusHelper.eventBus);
    }

    @VisibleForTesting
    FoldersActivityModel(Bus eventBus) {
        this.eventBus = eventBus;
    }

    public void setMediaFolderModelList(List<MediaFolderModel> modelList) {
        mediaFolderModelList = modelList;
    }

    public void setMediaFilterMode(MediaFilterMode mode) {
        mediaFilterMode = mode;
    }
}
