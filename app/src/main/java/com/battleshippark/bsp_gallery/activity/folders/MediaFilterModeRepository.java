package com.battleshippark.bsp_gallery.activity.folders;

import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.pref.SharedPreferenceController;

/**
 */

interface MediaFilterModeRepository {
    MediaFilterMode load();

    void save(MediaFilterMode mode);
}
