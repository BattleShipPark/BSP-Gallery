package com.battleshippark.bsp_gallery.media;

import org.parceler.Parcel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@org.parceler.Parcel(Parcel.Serialization.BEAN)
public class MediaFileModel implements Cloneable {
    private int id;
    private String name;
    private int mediaType; /* MediaStore.File.FileColumns.MEDIA_TYPE_? */
    private String path;
    private String thumbPath;

    public MediaFileModel copy() {
        try {
            return (MediaFileModel) this.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
