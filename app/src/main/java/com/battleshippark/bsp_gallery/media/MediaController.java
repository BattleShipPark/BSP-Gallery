package com.battleshippark.bsp_gallery.media;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.battleshippark.bsp_gallery.BspApplication;
import com.battleshippark.bsp_gallery.Events;
import com.battleshippark.bsp_gallery.activity.file.FileActivityModel;
import com.battleshippark.bsp_gallery.activity.files.FilesActivityModel;
import com.battleshippark.bsp_gallery.activity.folders.FoldersActivityModel;
import com.battleshippark.bsp_gallery.cache.CacheController;
import com.battleshippark.bsp_gallery.media.file.MediaFileController;
import com.battleshippark.bsp_gallery.media.folder.MediaFolderController;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
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

    public MediaController(Context context, Bus eventBus) {
        this.context = context;
        this.eventBus = eventBus;
    }

    /**
     * 디렉토리 목록을 갱신한다. 한 번에 전부 가져올 수 없고, 쿼리를 여러번 던져야 해서
     * 쿼리를 던질때마다 FoldersModel을 갱신한다.
     * 전체 쿼리 앞뒤로 캐시 작업이 있다
     */
    public void refreshFolderListAsync(FoldersActivityModel model) {
        MediaFolderController folderController = MediaFolderController.create(context, model.getMediaFilterMode());

        refreshFolderList(model, folderController);
    }

    @VisibleForTesting
    public void refreshFolderList(FoldersActivityModel model, MediaFolderController folderController) {
        Subject<Void, Void> writeToCacheSubject = PublishSubject.create();
        writeToCacheSubject.subscribeOn(Schedulers.io())
                .subscribe(
                        aVoid -> CacheController.writeCache(context, model.getMediaFilterMode(), model.getMediaFolderModelList()),
                        Throwable::printStackTrace);

        Observable.create((Observable.OnSubscribe<List<MediaFolderModel>>) subscriber -> {
            List<MediaFolderModel> dirs = null;
            MediaFolderModel allDir = null;

            dirs = CacheController.readCache(context, model.getMediaFilterMode());
            if (!dirs.isEmpty()) {
                subscriber.onNext(dirs);

                allDir = dirs.get(0);
            }

            dirs = getDirsWithAllAndNext(allDir, subscriber, folderController::getMediaDirectoryList);

            dirs = getDirsWithAllAndNext(allDir, dirs, subscriber, folderController::addMediaFileCount);

            dirs = getDirsWithAllAndNext(allDir, dirs, subscriber, folderController::addMediaFileId);

            dirs = getDirsWithAllAndNext(allDir, dirs, subscriber, folderController::addMediaThumbPath);

            dirs = addAllDirectory(dirs);
            subscriber.onNext(dirs);

            subscriber.onCompleted();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<MediaFolderModel>>() {
                    @Override
                    public void onCompleted() {
                        writeToCacheSubject.onNext(null);
                        eventBus.post(Events.OnMediaFolderListUpdated.FINISHED);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        eventBus.post(Events.OnMediaFolderListUpdated.FINISHED);
                    }

                    @Override
                    public void onNext(List<MediaFolderModel> mediaFolderModels) {
                        model.setMediaFolderModelList(mediaFolderModels);
                    }
                });
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

    /*
     * func을 수행해서 디렉토리 목록을 구하고, 거기에 전체 디렉토리를 붙여서 발행한다.
     * 반환할 때는 전체 디렉토리없이 한다
     */
    private List<MediaFolderModel> getDirsWithAllAndNext(@Nullable MediaFolderModel allDir,
                                                         Subscriber<? super List<MediaFolderModel>> subscriber,
                                                         Func0<List<MediaFolderModel>> func) {
        List<MediaFolderModel> dirs = func.call();
        List<MediaFolderModel> result = new ArrayList<>();

        result.clear();
        if (allDir != null)
            result.add(allDir);
        result.addAll(dirs);
        subscriber.onNext(result);

        return dirs;
    }

    private List<MediaFolderModel> getDirsWithAllAndNext(@Nullable MediaFolderModel allDir, List<MediaFolderModel> dirs,
                                                         Subscriber<? super List<MediaFolderModel>> subscriber,
                                                         Func1<List<MediaFolderModel>, List<MediaFolderModel>> func) {
        dirs = func.call(dirs);
        List<MediaFolderModel> result = new ArrayList<>();

        result.clear();
        if (allDir != null)
            result.add(allDir);
        result.addAll(dirs);
        subscriber.onNext(result);

        return dirs;
    }

    List<MediaFolderModel> addAllDirectory(List<MediaFolderModel> directories) {
        MediaFolderModel allDir = new MediaFolderModel();

        allDir.setId(MediaFolderModel.ALL_DIR_ID);

        allDir.setName("All");

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
