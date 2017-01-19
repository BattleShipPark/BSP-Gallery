package com.battleshippark.bsp_gallery.activity.folders;

import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import rx.Observable;

/**
 */
public interface MediaFolderRepository {
    Observable<MediaFolderModel> queryFolderList();
}
