package com.battleshippark.bsp_gallery.data.media;

import com.battleshippark.bsp_gallery.data.cache.CacheController;
import com.battleshippark.bsp_gallery.data.cache.CacheControllerFactory;
import com.battleshippark.bsp_gallery.domain.MediaControllerFactory;
import com.battleshippark.bsp_gallery.domain.folders.MediaFolderController;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import java.util.List;

import lombok.AllArgsConstructor;
import rx.Observable;

/**
 */
@AllArgsConstructor
class MediaRepositoryImpl implements MediaRepository {
    private MediaControllerFactory mediaFactory;
    private CacheControllerFactory cacheFactory;

    static MediaRepositoryImpl create(MediaControllerFactory mediaFactory, CacheControllerFactory cacheFactory) {
        return new MediaRepositoryImpl(mediaFactory, cacheFactory);
    }

    @Override
    public Observable<List<MediaFolderModel>> loadFolderList(MediaFilterMode mode) {
        MediaFolderController folderController = mediaFactory.createFolderController(mode);
        CacheController cacheController = cacheFactory.create();

        return Observable.create(
                _subscriber -> {
                    List<MediaFolderModel> mediaFolderModels = null;

                    mediaFolderModels = cacheController.readCache(mode);
                    _subscriber.onNext(mediaFolderModels);

                    mediaFolderModels = folderController.queryMediaFolderList(mediaFolderModels);
                    _subscriber.onNext(mediaFolderModels);

                    mediaFolderModels = folderController.addMediaFileCount(mediaFolderModels);
                    _subscriber.onNext(mediaFolderModels);

                    mediaFolderModels = folderController.addMediaFileId(mediaFolderModels);
                    _subscriber.onNext(mediaFolderModels);

                    mediaFolderModels = folderController.addMediaThumbPath(mediaFolderModels);
                    _subscriber.onNext(mediaFolderModels);

                    mediaFolderModels = folderController.addAllDirectory(mediaFolderModels);
                    _subscriber.onNext(mediaFolderModels);

                    cacheController.writeCache(mode, mediaFolderModels);

                    _subscriber.onCompleted();
                }
        );
    }
}
