package com.battleshippark.bsp_gallery.activity.folders;

import android.content.Context;

import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.media.folder.MediaFolderController;

/**
 */
class FolderLoaderFactory {
    private Context context;

    FolderLoaderFactory(Context context) {
        this.context = context;
    }

    static FolderLoaderFactory create(Context context) {
        return new FolderLoaderFactory(context);
    }

    MediaFolderController createLoader(MediaFilterMode mode) {
        return null;
    }
}
