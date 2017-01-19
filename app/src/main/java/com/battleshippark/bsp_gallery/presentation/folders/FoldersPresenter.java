package com.battleshippark.bsp_gallery.presentation.folders;

import com.battleshippark.bsp_gallery.Loader;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import java.util.List;

import rx.Subscriber;

/**
 */

class FoldersPresenter {
    private final Loader foldersLoader;
    private FoldersView foldersView;

    private FoldersPresenter(FoldersView foldersView, MediaFilterModeRepository mediaFilterModeRepository,
                             MediaControllerFactory mediaControllerFactory, CacheControllerFactory cacheControllerFactory) {
        this.foldersView = foldersView;
        foldersLoader = FoldersLoader.create(mediaFilterModeRepository, mediaControllerFactory, cacheControllerFactory);
    }

    public static FoldersPresenter create(FoldersView foldersView, MediaFilterModeRepository mediaFilterModeRepository,
                                          MediaControllerFactory mediaControllerFactory, CacheControllerFactory cacheControllerFactory) {
        return new FoldersPresenter(foldersView, mediaFilterModeRepository, mediaControllerFactory, cacheControllerFactory);
    }

    void load() {
        foldersLoader.execute(new FoldersSubscriber());
        foldersView.showProgress();
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
