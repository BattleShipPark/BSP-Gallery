package com.battleshippark.bsp_gallery.pref;

import com.battleshippark.bsp_gallery.activity.folders.FoldersModel;

import lombok.Data;

/**
 */
@Data
public class SharedPreferenceModel {
    public static final String KEY_MEDIA_MODE = "mediaMode";

    private FoldersModel.MEDIA_MODE mediaMode = FoldersModel.MEDIA_MODE.ALL;
}
