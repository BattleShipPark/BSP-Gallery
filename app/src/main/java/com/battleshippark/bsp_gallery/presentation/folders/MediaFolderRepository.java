package com.battleshippark.bsp_gallery.presentation.folders;

import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import rx.Observable;

/**
 */
public interface MediaFolderRepository {
    Observable<MediaFolderModel> queryFolderList();
}
