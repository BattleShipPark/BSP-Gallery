package com.battleshippark.bsp_gallery.activity.folders;

import com.battleshippark.bsp_gallery.Loader;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import java.util.List;

import rx.Subscriber;

/**
 */

class FoldersPresenter {
    private final Loader foldersLoader;
    private FoldersView foldersView;

    private FoldersPresenter(FoldersView foldersView, MediaFilterModeRepository mediaFilterModeRepository, MediaRepository mediaRepository) {
        this.foldersView = foldersView;
        foldersLoader = FoldersLoader.create(mediaFilterModeRepository, mediaRepository);
    }

    void load() {
        foldersLoader.execute(new FoldersSubscriber());
        foldersView.showProgress();
    }

    public static FoldersPresenter create(FoldersView foldersView, MediaFilterModeRepository mediaFilterModeRepository, MediaRepository mediaRepository) {
        return new FoldersPresenter(foldersView, mediaFilterModeRepository, mediaRepository);
    }

    private class FoldersSubscriber extends Subscriber<List<MediaFolderModel>> {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(List<MediaFolderModel> mediaFolderModels) {

        }
    }
}
