package com.battleshippark.bsp_gallery.data.media;

import com.battleshippark.bsp_gallery.media.MediaFilterMode;

/**
 */

public interface MediaFilterModeRepository {
    MediaFilterMode load();

    void save(MediaFilterMode mode);
}
