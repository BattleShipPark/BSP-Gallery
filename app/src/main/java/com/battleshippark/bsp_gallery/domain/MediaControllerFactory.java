package com.battleshippark.bsp_gallery.domain;

import android.content.Context;

import com.battleshippark.bsp_gallery.data.media.MediaAllFolderRepository;
import com.battleshippark.bsp_gallery.data.media.MediaImageFolderRepository;
import com.battleshippark.bsp_gallery.data.media.MediaVideoFolderRepository;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.domain.folders.MediaAllFolderController;
import com.battleshippark.bsp_gallery.domain.folders.MediaFolderController;
import com.battleshippark.bsp_gallery.domain.folders.MediaImageFolderController;
import com.battleshippark.bsp_gallery.domain.folders.MediaVideoFolderController;

import lombok.AllArgsConstructor;

/**
 */
@AllArgsConstructor
public class MediaControllerFactory {
    private Context context;

    public MediaFolderController createFolderController(MediaFilterMode mode) {
        switch (mode) {
            case ALL:
                return new MediaAllFolderController(new MediaAllFolderRepository(context));
            case IMAGE:
                return new MediaImageFolderController(new MediaImageFolderRepository(context));
            case VIDEO:
                return new MediaVideoFolderController(new MediaVideoFolderRepository(context));
            default:
                throw new IllegalArgumentException();
        }
    }
}
