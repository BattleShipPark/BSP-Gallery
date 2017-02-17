package com.battleshippark.bsp_gallery.data.media;

import com.annimon.stream.function.Consumer;
import com.battleshippark.bsp_gallery.media.MediaFileModel;

import java.util.List;

import rx.Observable;

/**
 */
public interface MediaFileRepository {
    int BUFFER_COUNT = 100;

    void queryBufferedList(Consumer<List<MediaFileModel>> consumer);
}
