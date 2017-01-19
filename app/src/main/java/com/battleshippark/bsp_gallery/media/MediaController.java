package com.battleshippark.bsp_gallery.media;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.battleshippark.bsp_gallery.BspApplication;
import com.battleshippark.bsp_gallery.EventBusHelper;
import com.battleshippark.bsp_gallery.Events;
import com.battleshippark.bsp_gallery.activity.file.FileActivityModel;
import com.battleshippark.bsp_gallery.activity.files.FilesActivityModel;
import com.battleshippark.bsp_gallery.activity.folders.FoldersActivityModel;
import com.battleshippark.bsp_gallery.cache.CacheController;
import com.battleshippark.bsp_gallery.media.file.MediaFileController;
import com.battleshippark.bsp_gallery.media.folder.MediaFolderController;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 */
public class MediaController {
    private final Context context;
    private final Bus eventBus;

    public MediaController(Context context) {
        this(context, EventBusHelper.eventBus);
    }

    @VisibleForTesting
    MediaController(Context context, Bus eventBus) {
        this.context = context;
        this.eventBus = eventBus;
    }

    /**
     * 디렉토리 목록을 갱신한다. 한 번에 전부 가져올 수 없고, 쿼리를 여러번 던져야 해서
     * 쿼리를 던질때마다 FoldersModel을 갱신하고 {@link com.battleshippark.bsp_gallery.Events.OnMediaFolderListUpdated}를 이벤트로 발생시킨다
     * 전체 쿼리 앞뒤로 캐시 작업이 있다
     */
    public void refreshFolderListAsync(FoldersActivityModel model) {
        MediaFolderController folderController = new MediaFolderController(model.getMediaFilterMode());

        Subscriber<List<MediaFolderModel>> subscriber = new Subscriber<List<MediaFolderModel>>() {
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
                subscriber, Schedulers.io(), AndroidSchedulers.mainThread());
    }

    private void writeCache(FoldersActivityModel model) {
        Executors.newSingleThreadExecutor().execute(() ->
                new CacheController(context).writeCache(model.getMediaFilterMode(),
                        model.getMediaFolderModelList()));
    }

    @VisibleForTesting
    public void refreshFolderList(MediaFilterMode mediaFilterMode, MediaFolderController folderController,
                                  CacheController cacheController, Subscriber<List<MediaFolderModel>> subscriber,
                                  Scheduler subscribeOnScheduler, Scheduler observeOnScheduler) {
        Observable.create(
                (Observable.OnSubscribe<List<MediaFolderModel>>) _subscriber -> {
                    List<MediaFolderModel> mediaFolderModels = null;

                    mediaFolderModels = MediaController.this.readCache(cacheController, _subscriber, mediaFilterMode);

                    mediaFolderModels = getFoldersAndOnNext(mediaFolderModels, _subscriber, folderController::queryMediaFolderList);

                    mediaFolderModels = getFoldersAndOnNext(mediaFolderModels, _subscriber, folderController::addMediaFileCount);

                    mediaFolderModels = getFoldersAndOnNext(mediaFolderModels, _subscriber, folderController::addMediaFileId);

                    mediaFolderModels = getFoldersAndOnNext(mediaFolderModels, _subscriber, folderController::addMediaThumbPath);

                    mediaFolderModels = MediaController.this.addAllDirectory(mediaFolderModels);
                    _subscriber.onNext(mediaFolderModels);

                    _subscriber.onCompleted();
                }
        ).subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler)
                .subscribe(subscriber);
    }

    @VisibleForTesting
    public List<MediaFolderModel> readCache(CacheController cacheController, Subscriber<? super List<MediaFolderModel>> subscriber, MediaFilterMode mediaFilterMode) {
        List<MediaFolderModel> mediaFolderModels = cacheController.readCache(mediaFilterMode);
        if (!mediaFolderModels.isEmpty()) {
            subscriber.onNext(mediaFolderModels);
        }
        return mediaFolderModels;
    }

    /**
     * 파일 목록을 갱신해서 FilesModel을 갱신한다. 전체를 한 번에 다 읽는다.
     */
    public void refreshFileListAsync(Activity activity, FileActivityModel model) {
        MediaFileController fileController = MediaFileController.create(context, model.getFolderId(), model.getMediaFilterMode());

        Observable.create((Observable.OnSubscribe<List<MediaFileModel>>) subscriber -> {
            subscriber.onNext(fileController.getMediaFileList());

            subscriber.onCompleted();
        }).subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<MediaFileModel>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<MediaFileModel> mediaFileModels) {
                        if (isActivityInvalid()) return;

                        BspApplication.getHandler().post(() -> {
                            if (isActivityInvalid()) return;

                            model.setMediaFileModelList(mediaFileModels);
                        });
                    }

                    boolean isActivityInvalid() {
                        return activity == null || activity.isFinishing() || (Build.VERSION.SDK_INT >= 17 && activity.isDestroyed());
                    }
                });
    }

    /**
     * 파일 목록을 갱신해서 FilesModel을 갱신한다. 내부적으로는 N건씩 끊어서 FilesModel이 갱신되고,
     * 엄지 손톱의 값을 가지고 있다
     */
    public void refreshFileListWithThumbAsync(Activity activity, FilesActivityModel model) {
        MediaFileController fileController = MediaFileController.create(context, model.getFolderId(), model.getMediaFilterMode());

        /* 파일 목록이 너무 많을 수 있으므로 n건씩 끊어서 받는다 */
        Observable.create((Observable.OnSubscribe<List<MediaFileModel>>) subscriber -> {
            fileController.getMediaFileList(subscriber);

            subscriber.onCompleted();
        }).subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<MediaFileModel>>() {
                    List<MediaFileModel> mediaFileModelList = new ArrayList<>();

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<MediaFileModel> mediaFileModels) {
                        if (isActivityInvalid()) return;

                        List<MediaFileModel> files = fileController.addMediaThumbPath(mediaFileModels);
                        mediaFileModelList.addAll(files);


                        BspApplication.getHandler().post(() -> {
                            if (isActivityInvalid()) return;

                            model.setMediaFileModelList(mediaFileModelList);
                        });
                    }

                    boolean isActivityInvalid() {
                        return activity == null || activity.isFinishing() || (Build.VERSION.SDK_INT >= 17 && activity.isDestroyed());
                    }
                });
    }

    /**
     * func을 수행해서 폴더 목록을 구하고, mediaFolderModels에 없던 폴더는 추가해서 발행 및 반환
     *
     * @param mediaFolderModels all 폴더 포함
     */
    private List<MediaFolderModel> getFoldersAndOnNext(@Nullable List<MediaFolderModel> mediaFolderModels,
                                                       Subscriber<? super List<MediaFolderModel>> subscriber,
                                                       Func0<List<MediaFolderModel>> func) {
        List<MediaFolderModel> newMediaFolderModels = func.call();
        if (mediaFolderModels == null)
            return newMediaFolderModels;

        /* map을 사용해서 중복 폴더를 제외한다 */
        Map<Integer, MediaFolderModel> map = new HashMap<>();
        for (MediaFolderModel mediaFolderModel : mediaFolderModels) {
            map.put(mediaFolderModel.getId(), mediaFolderModel);
        }

        for (MediaFolderModel mediaFolderModel : newMediaFolderModels) {
            if (map.containsKey(mediaFolderModel.getId()))
                continue;

            map.put(mediaFolderModel.getId(), mediaFolderModel);
        }

        List<MediaFolderModel> result = new ArrayList<>();
        for (MediaFolderModel mediaFolderModel : map.values()) {
            result.add(mediaFolderModel);
        }

        Collections.sort(result, (lhs, rhs) -> {
            if (lhs.getId() == MediaFolderModel.ALL_DIR_ID) return 1;
            if (rhs.getId() == MediaFolderModel.ALL_DIR_ID) return -1;
            return lhs.getId() - rhs.getId();
        });

        subscriber.onNext(result);

        return result;
    }

    private List<MediaFolderModel> getFoldersAndOnNext(List<MediaFolderModel> mediaFolderModels,
                                                       Subscriber<? super List<MediaFolderModel>> subscriber,
                                                       Func1<List<MediaFolderModel>, List<MediaFolderModel>> func) {
        List<MediaFolderModel> result = func.call(mediaFolderModels);

        subscriber.onNext(result);

        return result;
    }

    List<MediaFolderModel> addAllDirectory(List<MediaFolderModel> directories) {
        MediaFolderModel allDir = new MediaFolderModel();

        allDir.setId(MediaFolderModel.ALL_DIR_ID);
        allDir.setName("All");

        if (directories.get(0).getId() == MediaFolderModel.ALL_DIR_ID)
            directories.remove(0);

        int count = Observable.from(directories)
                .map(MediaFolderModel::getCount)
                .reduce((_sum, _count) -> _sum + _count)
                .toBlocking()
                .last();
        allDir.setCount(count);

        MediaFolderModel dir = Observable.from(directories)
                .reduce((_dir1, _dir2) -> {
                    if (_dir1.getCoverMediaId() >= _dir2.getCoverMediaId())
                        return _dir1;
                    else
                        return _dir2;
                })
                .toBlocking()
                .last();
        allDir.setCoverMediaId(dir.getCoverMediaId());
        allDir.setCoverThumbPath(dir.getCoverThumbPath());
        allDir.setCoverMediaType(dir.getCoverMediaType());

        directories.add(0, allDir);
        return directories;
    }
}
