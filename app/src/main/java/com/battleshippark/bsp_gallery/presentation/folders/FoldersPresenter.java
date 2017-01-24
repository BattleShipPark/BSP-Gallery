package com.battleshippark.bsp_gallery.presentation.folders;

import com.battleshippark.bsp_gallery.domain.Loader;
import com.battleshippark.bsp_gallery.data.cache.CacheControllerFactory;
import com.battleshippark.bsp_gallery.data.mode.MediaFilterModeRepository;
import com.battleshippark.bsp_gallery.domain.MediaControllerFactory;
import com.battleshippark.bsp_gallery.domain.folders.FoldersLoader;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import java.util.List;

import rx.Subscriber;

/**
 */

class FoldersPresenter {
    private final Loader foldersLoader;
    private FoldersView foldersView;

    FoldersPresenter(FoldersView foldersView, MediaFilterModeRepository mediaFilterModeRepository,
                     MediaControllerFactory mediaControllerFactory, CacheControllerFactory cacheControllerFactory) {
        this.foldersView = foldersView;
        foldersLoader = FoldersLoader.create(mediaFilterModeRepository, mediaControllerFactory, cacheControllerFactory);
    }

    void load() {
        foldersLoader.execute(new FoldersSubscriber());
        foldersView.showProgress();
    }

    private class FoldersSubscriber extends Subscriber<List<MediaFolderModel>> {
        @Override
        public void onCompleted() {
            foldersView.hideProgress();
        }

        @Override
        public void onError(Throwable e) {
            foldersView.hideProgress();
        }

        @Override
        public void onNext(List<MediaFolderModel> mediaFolderModels) {
            foldersView.refreshList(mediaFolderModels);
        }
    }
}