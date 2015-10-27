package com.battleshippark.bsp_gallery.media;

import android.database.Cursor;

import lombok.Data;

/**
 */
@Data
public class MediaDirectoryModel {
    public static final int ALL_DIR_ID = 0;

    private int id;
    private long coverMediaId;
    private String coverThumbPath;
    private String name;
    private int count;
    private int coverMediaType; /* MediaStore.File.FileColumns.MEDIA_TYPE_? */

    public static MediaDirectoryModel of(Cursor c) {
        MediaDirectoryModel result = new MediaDirectoryModel();

//        CursorUtils.getString(c, MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)
//                MediaStore.Images.ImageColumns.BUCKET_ID,
//                MediaStore.Images.ImageColumns.DATE_TAKEN,
//                MediaStore.Images.ImageColumns.DESCRIPTION,
//                MediaStore.Images.ImageColumns.IS_PRIVATE,
//                MediaStore.Images.ImageColumns._COUNT,
//                MediaStore.Images.ImageColumns.DISPLAY_NAME,
//                MediaStore.Images.ImageColumns.SIZE,
//                MediaStore.Images.ImageColumns.TITLE
//        result
        return null;
    }

    public MediaDirectoryModel copy() {
        MediaDirectoryModel result = new MediaDirectoryModel();
        result.setId(id);
        result.setCoverMediaId(coverMediaId);
        result.setCoverThumbPath(coverThumbPath);
        result.setName(name);
        result.setCount(count);
        result.setCoverMediaType(coverMediaType);
        return result;
    }
}
