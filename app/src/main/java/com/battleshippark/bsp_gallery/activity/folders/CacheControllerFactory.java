package com.battleshippark.bsp_gallery.activity.folders;

import android.content.Context;

import com.battleshippark.bsp_gallery.cache.CacheController;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.media.folder.MediaFolderController;

import lombok.AllArgsConstructor;

/**
 */
@AllArgsConstructor
class CacheControllerFactory {
    private Context context;

    CacheController create() {
        return new CacheController(context);
    }
}
