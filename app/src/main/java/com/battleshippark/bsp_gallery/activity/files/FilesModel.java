package com.battleshippark.bsp_gallery.activity.files;

import com.battleshippark.bsp_gallery.Events;
import com.battleshippark.bsp_gallery.media.MediaDirectoryModel;
import com.battleshippark.bsp_gallery.media.MediaFileModel;
import com.battleshippark.bsp_gallery.media.MediaMode;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import lombok.Data;

/**
 */
@Data
public class FilesModel {
    private Bus eventBus;
    private int dirId;
    private List<MediaFileModel> mediaFileModelList;
    private MediaMode mediaMode;

    public FilesModel() {
    }

    public FilesModel(Bus eventBus) {
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

    public void setMediaFileModelList(List<MediaFileModel> modelList) {
        mediaFileModelList = modelList;

        eventBus.post(Events.OnMediaFileListUpdated.EVENT);
    }
}
