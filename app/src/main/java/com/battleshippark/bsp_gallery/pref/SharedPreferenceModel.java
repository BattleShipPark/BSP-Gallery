package com.battleshippark.bsp_gallery.pref;

import com.battleshippark.bsp_gallery.media.MediaFilterMode;

import lombok.Data;

/**
 */
@Data
public class SharedPreferenceModel {
    public static final String KEY_MEDIA_MODE = "mediaFilterMode";

    private MediaFilterMode mediaFilterMode;
}
