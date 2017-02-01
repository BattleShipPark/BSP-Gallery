package com.battleshippark.bsp_gallery.domain.folders;

import com.battleshippark.bsp_gallery.data.cache.CacheController;
import com.battleshippark.bsp_gallery.data.cache.CacheControllerFactory;
import com.battleshippark.bsp_gallery.data.mode.MediaFilterModeRepository;
import com.battleshippark.bsp_gallery.domain.Loader;
import com.battleshippark.bsp_gallery.domain.MediaControllerFactory;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import java.util.List;

import lombok.AllArgsConstructor;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;

/**
 */

@AllArgsConstructor
public class FoldersLoader implements Loader {
    private final MediaFilterModeRepository mediaFilterModeRepository;
    private final MediaControllerFactory mediaFactory;
    private final CacheControllerFactory cacheFactory;
    private final Scheduler scheduler;
    private final Scheduler postScheduler;

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

                    mediaFolderModels = folderController.addList(mediaFolderModels);
                    _subscriber.onNext(mediaFolderModels);

                    mediaFolderModels = folderController.addFileCount(mediaFolderModels);
                    _subscriber.onNext(mediaFolderModels);

                    mediaFolderModels = folderController.addCoverFile(mediaFolderModels);
                    _subscriber.onNext(mediaFolderModels);

                    mediaFolderModels = folderController.addAllFolder(mediaFolderModels);
                    _subscriber.onNext(mediaFolderModels);

                    cacheController.writeCache(mode, mediaFolderModels);

                    _subscriber.onCompleted();
                }
        ).subscribeOn(scheduler).observeOn(postScheduler).subscribe(subscriber);
    }
}
