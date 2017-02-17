package com.battleshippark.bsp_gallery.presentation.files;

import android.support.annotation.VisibleForTesting;

import com.battleshippark.bsp_gallery.EventBusHelper;
import com.battleshippark.bsp_gallery.Events;
import com.battleshippark.bsp_gallery.media.MediaFileModel;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.squareup.otto.Bus;

import java.util.List;

import lombok.Data;

/**
 */
@Data
@org.parceler.Parcel(org.parceler.Parcel.Serialization.BEAN)
public final class FilesActivityModel {
    @org.parceler.Transient
    private final Bus eventBus;
    private int folderId;
    private String folderName;
    private List<MediaFileModel> mediaFileModelList;
    private MediaFilterMode mediaFilterMode;

    public FilesActivityModel() {
        this(EventBusHelper.eventBus);
    }

    @VisibleForTesting
    FilesActivityModel(Bus eventBus) {
        this.eventBus = eventBus;
    }

    public void setMediaFileModelList(List<MediaFileModel> modelList) {
        mediaFileModelList = modelList;

        eventBus.post(Events.OnMediaFileListUpdated.EVENT);
    }
}
