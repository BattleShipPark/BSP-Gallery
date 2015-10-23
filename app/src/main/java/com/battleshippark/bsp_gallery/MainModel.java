package com.battleshippark.bsp_gallery;

import com.battleshippark.bsp_gallery.media.MediaDirectory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import lombok.Data;

/**
 */
@Data
public class MainModel {
    private final Bus eventBus;
    private List<MediaDirectory> mediaDirectoryList;

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

    public void setMediaDirectoryList(List<MediaDirectory> arg) {
        mediaDirectoryList = arg;

        eventBus.post(Events.OnMediaDirectoryListUpdated.EVENT);
    }
}
