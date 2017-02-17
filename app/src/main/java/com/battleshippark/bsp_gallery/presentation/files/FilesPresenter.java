package com.battleshippark.bsp_gallery.presentation.files;

import android.support.annotation.VisibleForTesting;

import com.battleshippark.bsp_gallery.domain.UseCase;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import java.util.List;

import lombok.AllArgsConstructor;
import rx.Subscriber;

/**
 */

@AllArgsConstructor
class FilesPresenter {
    private final FilesView filesView;
    private final UseCase<Void, MediaFilterMode> filerModeLoader;
    private final UseCase<MediaFilterMode, MediaFilterMode> filterModeSaver;
    private final UseCase<MediaFilterMode, List<MediaFolderModel>> foldersLoader;

    void loadFilterMode() {
        filesView.showProgress();
        filerModeLoader.execute(null, new FilterModeSubscriber(filesView));
    }

    void changeFilterMode(MediaFilterMode mediaFilterMode) {
        filesView.showProgress();
        filterModeSaver.execute(mediaFilterMode, new FilterModeSubscriber(filesView));
    }

    void loadList(MediaFilterMode mediaFilterMode) {
        foldersLoader.execute(mediaFilterMode, new FoldersSubscriber(filesView));
    }

    @VisibleForTesting
    void loadList(MediaFilterMode mediaFilterMode, Subscriber subscriber) {
        foldersLoader.execute(mediaFilterMode, subscriber);
    }

    @AllArgsConstructor
    @VisibleForTesting
    static class FilterModeSubscriber extends Subscriber<MediaFilterMode> {
        private final FilesView foldersView;

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            foldersView.hideProgress();
        }

        @Override
        public void onNext(MediaFilterMode mediaFilterMode) {
            foldersView.updateFilterMode(mediaFilterMode);
            foldersView.refreshList();
        }
    }

    @AllArgsConstructor
    @VisibleForTesting
    static class FoldersSubscriber extends Subscriber<List<MediaFolderModel>> {
        private final FilesView foldersView;

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
