package com.battleshippark.bsp_gallery.cache;

import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import io.realm.RealmList;
import io.realm.RealmObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 */
@ToString
@EqualsAndHashCode(callSuper = false)
public class FoldersCacheModel extends RealmObject {
    private String mediaFilterMode; /* MediaFilterMode */
    private RealmList<MediaFolderModel> folderModels;

    public String getMediaFilterMode() {
        return mediaFilterMode;
    }

    public void setMediaFilterMode(String mediaFilterMode) {
        this.mediaFilterMode = mediaFilterMode;
    }

    public RealmList<MediaFolderModel> getFolderModels() {
        return folderModels;
    }

    public void setFolderModels(RealmList<MediaFolderModel> folderModels) {
        this.folderModels = folderModels;
    }
}
