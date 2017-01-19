package com.battleshippark.bsp_gallery.data.media;

import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import rx.Observable;

/**
 */
public interface MediaFolderRepository {
    Observable<MediaFolderModel> queryFolderList();
}
