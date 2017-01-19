package com.battleshippark.bsp_gallery.presentation.folders;

import android.content.Context;

import com.battleshippark.bsp_gallery.cache.CacheController;

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
