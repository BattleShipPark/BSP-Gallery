package com.battleshippark.bsp_gallery.activity.folders;

import com.battleshippark.bsp_gallery.Loader;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
        mediaRepository.loadFolderList(mode).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
}
