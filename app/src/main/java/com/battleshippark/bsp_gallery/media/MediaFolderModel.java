package com.battleshippark.bsp_gallery.media;

import io.realm.RealmObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 */
@ToString
@EqualsAndHashCode(callSuper = false)
public class MediaFolderModel extends RealmObject {
    public static final int ALL_DIR_ID = 0;

    private int id;
    private long coverMediaId;
    private String coverThumbPath;
    private String name;
    private int count;
    private int coverMediaType; /* MediaStore.File.FileColumns.MEDIA_TYPE_? */

    public static MediaFolderModel copy(MediaFolderModel model) {
        MediaFolderModel result = new MediaFolderModel();
        result.setId(model.getId());
        result.setCoverMediaId(model.getCoverMediaId());
        result.setCoverThumbPath(model.getCoverThumbPath());
        result.setName(model.getName());
        result.setCount(model.getCount());
        result.setCoverMediaType(model.getCoverMediaType());
        return result;
    }

    public int getId() {
        return id;
    }

    public long getCoverMediaId() {
        return coverMediaId;
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

    public int getCoverMediaType() {
        return coverMediaType;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCoverMediaId(long coverMediaId) {
        this.coverMediaId = coverMediaId;
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

    public void setCoverMediaType(int coverMediaType) {
        this.coverMediaType = coverMediaType;
    }
}
