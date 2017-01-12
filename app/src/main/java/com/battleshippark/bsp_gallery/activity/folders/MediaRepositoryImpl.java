package com.battleshippark.bsp_gallery.activity.folders;

import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;
import com.battleshippark.bsp_gallery.media.folder.MediaFolderController;

import java.util.List;

import rx.Observable;

/**
 */
class MediaRepositoryImpl implements MediaRepository {
    private FolderLoaderFactory factory;

    private MediaRepositoryImpl(FolderLoaderFactory factory) {
        this.factory = factory;
    }

    static MediaRepositoryImpl create(FolderLoaderFactory factory) {
        return new MediaRepositoryImpl(factory);
    }

    @Override
    public Observable<List<MediaFolderModel>> loadFolderList(MediaFilterMode mode) {
//        MediaFolderController folderController = MediaFolderController.create(context, model.getMediaFilterMode());
        MediaFolderController folderController = factory.createLoader(mode);

/*        Subscriber<List<MediaFolderModel>> subscriber = new Subscriber<List<MediaFolderModel>>() {
            @Override
            public void onCompleted() {
                writeCache(model);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(List<MediaFolderModel> mediaFolderModels) {
                model.setMediaFolderModelList(mediaFolderModels);
                eventBus.post(Events.OnMediaFolderListUpdated.EVENT);
            }
        };

        CacheController cacheController = new CacheController(context);

        refreshFolderList(model.getMediaFilterMode(), folderController, cacheController,
                subscriber, Schedulers.io(), AndroidSchedulers.mainThread());*/
        return null;
    }
}
