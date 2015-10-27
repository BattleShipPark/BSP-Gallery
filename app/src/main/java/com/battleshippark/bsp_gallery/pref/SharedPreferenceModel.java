package com.battleshippark.bsp_gallery.pref;

import com.battleshippark.bsp_gallery.MainModel;

import lombok.Data;

/**
 */
@Data
public class SharedPreferenceModel {
    public static final String KEY_MEDIA_MODE = "mediaMode";

    private MainModel.MEDIA_MODE mediaMode = MainModel.MEDIA_MODE.ALL;
}
