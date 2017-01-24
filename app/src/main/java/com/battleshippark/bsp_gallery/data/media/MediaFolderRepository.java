package com.battleshippark.bsp_gallery.data.media;

import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import java.io.IOException;

import rx.Observable;

/**
 */
public interface MediaFolderRepository {
    Observable<MediaFolderModel> queryList();

    int queryFileCount(int folderId) throws IOException;

    MediaFolderModel queryCoverFile(int folderId) throws IOException;
}
