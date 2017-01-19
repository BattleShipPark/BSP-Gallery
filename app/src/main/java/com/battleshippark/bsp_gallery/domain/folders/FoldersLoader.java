package com.battleshippark.bsp_gallery.domain.folders;

import com.battleshippark.bsp_gallery.Loader;
import com.battleshippark.bsp_gallery.data.cache.CacheController;
import com.battleshippark.bsp_gallery.data.cache.CacheControllerFactory;
import com.battleshippark.bsp_gallery.data.media.MediaFilterModeRepository;
import com.battleshippark.bsp_gallery.domain.MediaControllerFactory;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import java.util.List;

import lombok.AllArgsConstructor;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 */

@AllArgsConstructor
public class FoldersLoader implements Loader {
    private MediaFilterModeRepository mediaFilterModeRepository;
    private MediaControllerFactory mediaFactory;
    private CacheControllerFactory cacheFactory;

    public static Loader create(MediaFilterModeRepository mediaFilterModeRepository,
                                MediaControllerFactory mediaControllerFactory, CacheControllerFactory cacheControllerFactory) {
        return new FoldersLoader(mediaFilterModeRepository, mediaControllerFactory, cacheControllerFactory);
    }

    @Override
    public void execute(Subscriber subscriber) {
        MediaFilterMode mode = mediaFilterModeRepository.load();
        MediaFolderController folderController = mediaFactory.createFolderController(mode);
        CacheController cacheController = cacheFactory.create();

        Observable.create(
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
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
    }
}
