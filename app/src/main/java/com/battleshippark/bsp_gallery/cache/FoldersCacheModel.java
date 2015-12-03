package com.battleshippark.bsp_gallery.cache;

import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import io.realm.RealmObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 */
@ToString
@EqualsAndHashCode(callSuper = false)
public class FoldersCacheModel extends RealmObject {
    private String mediaFilterMode; /* MediaFilterMode */
    private MediaFolderModel folderModel;

    public String getMediaFilterMode() {
        return mediaFilterMode;
    }

    public void setMediaFilterMode(String mediaFilterMode) {
        this.mediaFilterMode = mediaFilterMode;
    }

    public MediaFolderModel getFolderModel() {
        return folderModel;
    }

    public void setFolderModel(MediaFolderModel folderModel) {
        this.folderModel = folderModel;
    }
}
