package com.battleshippark.bsp_gallery.presentation.files;

import android.support.annotation.VisibleForTesting;

import com.battleshippark.bsp_gallery.domain.UseCase;
import com.battleshippark.bsp_gallery.media.MediaFileModel;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;

import java.util.List;

import lombok.AllArgsConstructor;
import rx.Subscriber;

/**
 */

@AllArgsConstructor
class FilesPresenter {
    private final FilesView filesView;
    private final UseCase<Void, List<MediaFileModel>> filesLoader;

    void loadList() {
        filesLoader.execute(null, new FilesSubscriber(filesView));
    }

    @VisibleForTesting
    void loadList(Subscriber<List<MediaFileModel>> subscriber) {
        filesLoader.execute(null, subscriber);
    }


    @AllArgsConstructor
    @VisibleForTesting
    static class FilesSubscriber extends Subscriber<List<MediaFileModel>> {
        private final FilesView filesView;

        @Override
        public void onCompleted() {
            filesView.hideProgress();
        }

        @Override
        public void onError(Throwable e) {
            filesView.hideProgress();
        }

        @Override
        public void onNext(List<MediaFileModel> mediaFileModels) {
            filesView.refreshList(mediaFileModels);
        }
    }
}
