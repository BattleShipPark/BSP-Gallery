package com.battleshippark.bsp_gallery.data.cache;

import lombok.AllArgsConstructor;

/**
 */
@AllArgsConstructor
public class CacheControllerFactory {
    public CacheController create() {
        return new CacheController();
    }
}
