package com.battleshippark.bsp_gallery.presentation.folders;

import android.content.Context;

import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.media.folder.MediaAllFolderController;
import com.battleshippark.bsp_gallery.media.folder.MediaFolderController;
import com.battleshippark.bsp_gallery.media.folder.MediaImageFolderController;
import com.battleshippark.bsp_gallery.media.folder.MediaVideoFolderController;

import lombok.AllArgsConstructor;

/**
 */
@AllArgsConstructor
class MediaControllerFactory {
    private Context context;

    MediaFolderController createFolderController(MediaFilterMode mode) {
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
