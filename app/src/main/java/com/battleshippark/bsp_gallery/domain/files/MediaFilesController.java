package com.battleshippark.bsp_gallery.domain.files;

import com.annimon.stream.function.Consumer;
import com.battleshippark.bsp_gallery.data.media.MediaFileRepository;
import com.battleshippark.bsp_gallery.media.MediaFileModel;

import java.util.List;

import lombok.AllArgsConstructor;

/**
 */
@AllArgsConstructor
public class MediaFilesController {
    private final MediaFileRepository mediaRepository;

    public void loadBufferedList(Consumer<List<MediaFileModel>> consumer) {
        mediaRepository.queryBufferedList(consumer);
    }
}
