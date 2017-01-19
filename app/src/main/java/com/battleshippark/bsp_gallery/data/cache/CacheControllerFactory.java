package com.battleshippark.bsp_gallery.data.cache;

import android.content.Context;

import lombok.AllArgsConstructor;

/**
 */
@AllArgsConstructor
public class CacheControllerFactory {
    private Context context;

    public CacheController create() {
        return new CacheController(context);
    }
}
