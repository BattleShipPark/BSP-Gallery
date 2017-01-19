package com.battleshippark.bsp_gallery.presentation.folders;

import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import java.util.List;

import rx.Observable;

/**
 */
interface MediaRepository {
    Observable<List<MediaFolderModel>> loadFolderList(MediaFilterMode mode);
}
