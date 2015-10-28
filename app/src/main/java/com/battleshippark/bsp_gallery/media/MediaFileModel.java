package com.battleshippark.bsp_gallery.media;

import lombok.Data;

/**
 */
@Data
public class MediaFileModel {
    private int id;
    private String name;
    private int mediaType; /* MediaStore.File.FileColumns.MEDIA_TYPE_? */
    private String pathName;
    private String thumbPath;

    public MediaFileModel copy() {
        MediaFileModel result = new MediaFileModel();
        result.setId(id);
        result.setName(name);
        result.setMediaType(mediaType);
        result.setPathName(pathName);
        return result;
    }
}
