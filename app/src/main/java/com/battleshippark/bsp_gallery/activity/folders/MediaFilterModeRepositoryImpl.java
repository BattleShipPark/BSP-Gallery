package com.battleshippark.bsp_gallery.activity.folders;

import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.pref.SharedPreferenceController;

/**
 */

class MediaFilterModeRepositoryImpl implements MediaFilterModeRepository {
    private SharedPreferenceController pref;

    private MediaFilterModeRepositoryImpl(SharedPreferenceController pref) {
        this.pref = pref;
    }

    static MediaFilterModeRepositoryImpl create() {
        return new MediaFilterModeRepositoryImpl(SharedPreferenceController.instance());
    }

    @Override
    public MediaFilterMode load() {
        return pref.readMediaMode();
    }

    @Override
    public void save(MediaFilterMode mode) {

    }
}
