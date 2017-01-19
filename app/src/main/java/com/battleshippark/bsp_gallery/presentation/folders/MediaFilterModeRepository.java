package com.battleshippark.bsp_gallery.presentation.folders;

import com.battleshippark.bsp_gallery.media.MediaFilterMode;

/**
 */

interface MediaFilterModeRepository {
    MediaFilterMode load();

    void save(MediaFilterMode mode);
}
