package com.battleshippark.bsp_gallery.media;

import io.realm.RealmObject;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 */
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public class MediaFolderModel extends RealmObject {
    public static final int ALL_DIR_ID = 0;

    private int id;
    private long coverMediaId;
    private int coverMediaType; /* MediaStore.File.FileColumns.MEDIA_TYPE_? */
    private String coverThumbPath;
    private String name;
    private int count;

    public MediaFolderModel copy() {
        MediaFolderModel result = new MediaFolderModel();
        result.setId(this.getId());
        result.setCoverMediaId(this.getCoverMediaId());
        result.setCoverMediaType(this.getCoverMediaType());
        result.setCoverThumbPath(this.getCoverThumbPath());
        result.setName(this.getName());
        result.setCount(this.getCount());
        return result;
    }

    public int getId() {
        return id;
    }

    public long getCoverMediaId() {
        return coverMediaId;
    }

    public int getCoverMediaType() {
        return coverMediaType;
    }

    public String getCoverThumbPath() {
        return coverThumbPath;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCoverMediaId(long coverMediaId) {
        this.coverMediaId = coverMediaId;
    }

    public void setCoverMediaType(int coverMediaType) {
        this.coverMediaType = coverMediaType;
    }

    public void setCoverThumbPath(String coverThumbPath) {
        this.coverThumbPath = coverThumbPath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
