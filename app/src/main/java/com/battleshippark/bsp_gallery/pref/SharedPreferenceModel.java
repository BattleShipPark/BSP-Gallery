package com.battleshippark.bsp_gallery.pref;

import com.battleshippark.bsp_gallery.activity.folders.FoldersModel;
import com.battleshippark.bsp_gallery.media.MediaMode;

import lombok.Data;

/**
 */
@Data
public class SharedPreferenceModel {
    public static final String KEY_MEDIA_MODE = "mediaMode";

    private MediaMode mediaMode;
}
