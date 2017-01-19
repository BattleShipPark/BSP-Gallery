package com.battleshippark.bsp_gallery.data.mode;

import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.pref.SharedPreferenceController;

import lombok.AllArgsConstructor;

/**
 */

@AllArgsConstructor
public class MediaFilterModeRepositoryImpl implements MediaFilterModeRepository {
    private SharedPreferenceController pref;

    @Override
    public MediaFilterMode load() {
        return pref.readMediaFilterMode();
    }

    @Override
    public void save(MediaFilterMode mode) {
        pref.writeMediaFilterMode(mode);
    }
}
