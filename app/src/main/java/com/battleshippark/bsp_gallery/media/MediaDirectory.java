package com.battleshippark.bsp_gallery.media;

import android.database.Cursor;

import lombok.Data;

/**
 */
@Data
public class MediaDirectory {
    public static final int ALL_DIR_ID = 0;

    private int id;
    private long coverImageId;
    private String coverThumbPath;
    private String name;
    private int count;

    public static MediaDirectory of(Cursor c) {
        MediaDirectory result = new MediaDirectory();

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

    public MediaDirectory copy() {
        MediaDirectory result = new MediaDirectory();
        result.setId(id);
        result.setCoverImageId(coverImageId);
        result.setCoverThumbPath(coverThumbPath);
        result.setName(name);
        result.setCount(count);
        return result;
    }
}
