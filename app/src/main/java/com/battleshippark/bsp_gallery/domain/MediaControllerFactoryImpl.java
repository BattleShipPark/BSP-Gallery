package com.battleshippark.bsp_gallery.domain;

import android.content.Context;

import com.battleshippark.bsp_gallery.data.media.MediaAllFolderRepository;
import com.battleshippark.bsp_gallery.data.media.MediaFolderRepository;
import com.battleshippark.bsp_gallery.data.media.MediaImageFolderRepository;
import com.battleshippark.bsp_gallery.data.media.MediaVideoFolderRepository;
import com.battleshippark.bsp_gallery.domain.folders.MediaFolderController;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;

import lombok.AllArgsConstructor;

/**
 */
@AllArgsConstructor
public class MediaControllerFactoryImpl implements MediaControllerFactory {
    private Context context;

    public MediaFolderController createFolderController(MediaFilterMode mode) {
        MediaFolderRepository repository;
        switch (mode) {
            case ALL:
                repository = new MediaAllFolderRepository(context);
                break;
            case IMAGE:
                repository = new MediaImageFolderRepository(context);
                break;
            case VIDEO:
                repository = new MediaVideoFolderRepository(context);
                break;
            default:
                throw new IllegalArgumentException();
        }
        return new MediaFolderController(repository);
    }
}
