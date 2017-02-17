package com.battleshippark.bsp_gallery.domain.folders;

import com.battleshippark.bsp_gallery.data.cache.CacheController;
import com.battleshippark.bsp_gallery.data.cache.CacheControllerFactory;
import com.battleshippark.bsp_gallery.domain.MediaControllerFactory;
import com.battleshippark.bsp_gallery.domain.UseCase;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;

/**
 */

public class FoldersLoader implements UseCase<MediaFilterMode, List<MediaFolderModel>> {
    private final MediaControllerFactory mediaFactory;
    private final CacheControllerFactory cacheFactory;
    private final Scheduler scheduler;
    private final Scheduler postScheduler;

    public FoldersLoader(MediaControllerFactory mediaFactory,
                         CacheControllerFactory cacheFactory, Scheduler scheduler, Scheduler postScheduler) {
        this.mediaFactory = mediaFactory;
        this.cacheFactory = cacheFactory;
        this.scheduler = scheduler;
        this.postScheduler = postScheduler;
    }

    @Override
    public void execute(MediaFilterMode filterMode, Subscriber<List<MediaFolderModel>> subscriber) {
        Observable.create(
                (Subscriber<? super List<MediaFolderModel>> _subscriber) -> {
                    MediaFolderController folderController = mediaFactory.createFolderController(filterMode);
                    CacheController cacheController = cacheFactory.create();

                    List<MediaFolderModel> mediaFolderModels = null;

                    mediaFolderModels = cacheController.readCache(filterMode);
                    _subscriber.onNext(mediaFolderModels);

                    mediaFolderModels = folderController.addList(mediaFolderModels);
                    _subscriber.onNext(mediaFolderModels);

                    mediaFolderModels = folderController.addFileCount(mediaFolderModels);
                    _subscriber.onNext(mediaFolderModels);

                    mediaFolderModels = folderController.addCoverFile(mediaFolderModels);
                    _subscriber.onNext(mediaFolderModels);

                    mediaFolderModels = folderController.addAllFolder(mediaFolderModels);
                    _subscriber.onNext(mediaFolderModels);

                    cacheController.writeCache(filterMode, mediaFolderModels);

                    _subscriber.onCompleted();
                }
        ).subscribeOn(scheduler).observeOn(postScheduler).subscribe(subscriber);
    }
}
