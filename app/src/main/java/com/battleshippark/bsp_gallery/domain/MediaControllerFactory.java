package com.battleshippark.bsp_gallery.domain;

import com.battleshippark.bsp_gallery.domain.files.MediaFilesController;
import com.battleshippark.bsp_gallery.domain.folders.MediaFolderController;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;

/**
 */
public interface MediaControllerFactory {
    MediaFolderController createFolderController(MediaFilterMode mode);

    MediaFilesController createFilesController(MediaFilterMode mode, int folderId);
}
