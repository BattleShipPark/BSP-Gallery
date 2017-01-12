package com.battleshippark.bsp_gallery.activity.folders;

import com.battleshippark.bsp_gallery.Loader;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.pref.SharedPreferenceController;

import rx.Subscriber;

/**
 */

class FoldersLoader implements Loader {
    private MediaFilterModeRepository mediaModeRepository;
    private MediaRepository mediaRepository;

    private FoldersLoader(MediaFilterModeRepository mediaModeRepository, MediaRepository mediaRepository) {
        this.mediaModeRepository = mediaModeRepository;
        this.mediaRepository = mediaRepository;
    }

    public static Loader create(MediaFilterModeRepository mediaFilterModeRepository, MediaRepository mediaRepository) {
        return new FoldersLoader(mediaFilterModeRepository, mediaRepository);
    }

    @Override
    public void execute(Subscriber subscriber) {
        MediaFilterMode mode = mediaModeRepository.load();
//        model.setMediaFilterMode(mode);
        mediaRepository.loadFolderList(mode);
//        mediaController.refreshFolderListAsync(model);

        SharedPreferenceController.instance().writeMediaFilterMode(mode);
    }
}
