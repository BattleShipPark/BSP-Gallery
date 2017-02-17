package com.battleshippark.bsp_gallery.domain;

import android.content.Context;

import com.battleshippark.bsp_gallery.data.media.MediaAllFileRepository;
import com.battleshippark.bsp_gallery.data.media.MediaAllFolderRepository;
import com.battleshippark.bsp_gallery.data.media.MediaFileRepository;
import com.battleshippark.bsp_gallery.data.media.MediaFolderRepository;
import com.battleshippark.bsp_gallery.data.media.MediaImageFileRepository;
import com.battleshippark.bsp_gallery.data.media.MediaImageFolderRepository;
import com.battleshippark.bsp_gallery.data.media.MediaVideoFileRepository;
import com.battleshippark.bsp_gallery.data.media.MediaVideoFolderRepository;
import com.battleshippark.bsp_gallery.domain.files.MediaFilesController;
import com.battleshippark.bsp_gallery.domain.folders.MediaFolderController;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;

import lombok.AllArgsConstructor;

/**
 */
@AllArgsConstructor
public class MediaControllerFactoryImpl implements MediaControllerFactory {
    private Context context;

    public MediaFolderController createFolderController(MediaFilterMode mode) {
        MediaFolderRepository repository;
        switch (mode) {
            case ALL:
                repository = new MediaAllFolderRepository(context);
                break;
            case IMAGE:
                repository = new MediaImageFolderRepository(context);
                break;
            case VIDEO:
                repository = new MediaVideoFolderRepository(context);
                break;
            default:
                throw new IllegalArgumentException();
        }
        return new MediaFolderController(repository);
    }

    @Override
    public MediaFilesController createFilesController(MediaFilterMode mode, int folderId) {
        MediaFileRepository repository;
        switch (mode) {
            case ALL:
                repository = new MediaAllFileRepository(context, folderId);
                break;
            case IMAGE:
                repository = new MediaImageFileRepository(context, folderId);
                break;
            case VIDEO:
                repository = new MediaVideoFileRepository(context, folderId);
                break;
            default:
                throw new IllegalArgumentException();
        }
        return new MediaFilesController(repository);
    }
}
