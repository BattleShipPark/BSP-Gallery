package com.battleshippark.bsp_gallery.presentation.folders;

import android.support.annotation.VisibleForTesting;

import com.battleshippark.bsp_gallery.domain.Loader;
import com.battleshippark.bsp_gallery.data.cache.CacheControllerFactory;
import com.battleshippark.bsp_gallery.data.mode.MediaFilterModeRepository;
import com.battleshippark.bsp_gallery.domain.MediaControllerFactory;
import com.battleshippark.bsp_gallery.domain.folders.FoldersLoader;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import java.util.List;

import lombok.AllArgsConstructor;
import rx.Scheduler;
import rx.Subscriber;

/**
 */

class FoldersPresenter {
    private final Loader foldersLoader;
    private final FoldersView foldersView;

    FoldersPresenter(FoldersView foldersView, MediaFilterModeRepository mediaFilterModeRepository,
                     MediaControllerFactory mediaControllerFactory, CacheControllerFactory cacheControllerFactory,
                     Scheduler scheduler, Scheduler postScheduler) {
        this.foldersView = foldersView;
        this.foldersLoader = new FoldersLoader(mediaFilterModeRepository, mediaControllerFactory,
                cacheControllerFactory, scheduler, postScheduler);
    }

    void load() {
        load(new FoldersSubscriber(foldersView));
    }

    @VisibleForTesting
    void load(FoldersSubscriber subscriber) {
        foldersLoader.execute(subscriber);
        foldersView.showProgress();
    }

    @AllArgsConstructor
    @VisibleForTesting
    static class FoldersSubscriber extends Subscriber<List<MediaFolderModel>> {
        private final FoldersView foldersView;

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
