package com.battleshippark.bsp_gallery.presentation.folders;

import android.support.annotation.VisibleForTesting;

import com.battleshippark.bsp_gallery.domain.Loader;
import com.battleshippark.bsp_gallery.domain.folders.FoldersLoader;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import java.util.List;

import lombok.AllArgsConstructor;
import rx.Subscriber;

/**
 */

@AllArgsConstructor
class FoldersPresenter {
    private final FoldersView foldersView;
    private final Loader<MediaFilterMode> filerModeLoader;
    private final Loader<List<MediaFolderModel>> foldersLoader;

    void loadFilterMode() {
        filerModeLoader.execute(new FilterModeSubscriber(foldersView));
    }

    void loadFilterMode(FilterModeSubscriber subscriber) {
        filerModeLoader.execute(subscriber);
        foldersView.showProgress();
    }

    void loadList(MediaFilterMode mediaFilterMode) {
        loadList(mediaFilterMode, new FoldersSubscriber(foldersView));
    }

    @VisibleForTesting
    void loadList(MediaFilterMode mediaFilterMode, FoldersSubscriber subscriber) {
        ((FoldersLoader) foldersLoader).setFilterMode(mediaFilterMode);
        foldersLoader.execute(subscriber);
    }

    @AllArgsConstructor
    @VisibleForTesting
    static class FilterModeSubscriber extends Subscriber<MediaFilterMode> {
        private final FoldersView foldersView;

        @Override
        public void onCompleted() {
            foldersView.refreshList();
        }

        @Override
        public void onError(Throwable e) {
            foldersView.hideProgress();
        }

        @Override
        public void onNext(MediaFilterMode mediaFilterMode) {
            foldersView.updateFilterMode(mediaFilterMode);
        }
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
