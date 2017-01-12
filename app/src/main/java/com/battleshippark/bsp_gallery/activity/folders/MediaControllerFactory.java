package com.battleshippark.bsp_gallery.activity.folders;

import android.content.Context;

import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.media.folder.MediaFolderController;

import lombok.AllArgsConstructor;

/**
 */
@AllArgsConstructor
class MediaControllerFactory {
    private Context context;

    MediaFolderController createFolderController(MediaFilterMode mode) {
        return null;
    }
}
