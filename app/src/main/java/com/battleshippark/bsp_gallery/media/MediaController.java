package com.battleshippark.bsp_gallery.media;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;

import com.battleshippark.bsp_gallery.BspApplication;
import com.battleshippark.bsp_gallery.activity.file.FileActivityModel;
import com.battleshippark.bsp_gallery.activity.files.FilesActivityModel;
import com.battleshippark.bsp_gallery.activity.folders.FoldersActivityModel;
import com.battleshippark.bsp_gallery.media.file.MediaFileController;
import com.battleshippark.bsp_gallery.media.folder.MediaFolderController;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import lombok.Cleanup;
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
    private static final String CACHE_FILENAME = "dirCache";
    private final Context context;

    public MediaController(Context context) {
        this.context = context;
    }

    /**
     * 디렉토리 목록을 갱신한다. 한 번에 전부 가져올 수 없고, 쿼리를 여러번 던져야 해서
     * 쿼리를 던질때마다 FoldersModel을 갱신한다.
     * 전체 쿼리 앞뒤로 캐시 작업이 있다
     */
    public void refreshDirListAsync(FoldersActivityModel model) {
        MediaFolderController directoryController = MediaFolderController.create(context, model.getMediaMode());

        Subject<Void, Void> writeToCacheSubject = PublishSubject.create();
        writeToCacheSubject.subscribeOn(Schedulers.io())
                .subscribe(
                        aVoid -> {
                            String json = toJson(model);
                            writeToCache(json, model);

                        },
                        Throwable::printStackTrace);

        Observable.create((Observable.OnSubscribe<List<MediaFolderModel>>) subscriber -> {
            List<MediaFolderModel> dirs = null;
            MediaFolderModel allDir = null;

            try {
                dirs = getFromCache(context, model);
                subscriber.onNext(dirs);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (dirs != null)
                allDir = dirs.get(0);

            dirs = getDirsWithAllAndNext(allDir, subscriber, directoryController::getMediaDirectoryList);

            dirs = getDirsWithAllAndNext(allDir, dirs, subscriber, directoryController::addMediaFileCount);

            dirs = getDirsWithAllAndNext(allDir, dirs, subscriber, directoryController::addMediaFileId);

            dirs = getDirsWithAllAndNext(allDir, dirs, subscriber, directoryController::addMediaThumbPath);

            dirs = addAllDirectory(dirs);
            subscriber.onNext(dirs);

            subscriber.onCompleted();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        model::setMediaFolderModelList,
                        Throwable::printStackTrace,
                        () -> writeToCacheSubject.onNext(null));
    }

    /**
     * 파일 목록을 갱신해서 FilesModel을 갱신한다. 전체를 한 번에 다 읽는다.
     */
    public void refreshFileListAsync(Activity activity, FileActivityModel model) {
        MediaFileController fileController = MediaFileController.create(context, model.getFolderId(), model.getMediaMode());

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
        MediaFileController fileController = MediaFileController.create(context, model.getFolderId(), model.getMediaMode());

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


    private String toJson(FoldersActivityModel model) {
        return new Gson().toJson(model.getMediaFolderModelList());
    }

    private String getCacheFileName(FoldersActivityModel model) {
        return CACHE_FILENAME + model.getMediaMode();
    }

    private List<MediaFolderModel> getFromCache(Context context, FoldersActivityModel model) throws IOException {
        File cacheDirFile = context.getCacheDir();
        File inputFile = new File(cacheDirFile, getCacheFileName(model));

        @Cleanup FileReader reader = new FileReader(inputFile);

        try {
            Type type = new TypeToken<List<MediaFolderModel>>() {
            }.getType();
            return new Gson().fromJson(reader, type);
        } catch (JsonParseException e) {
            throw new IOException(e);
        } finally {
            Log.d("DEBUG", "MediaController.getFromCache()");
        }
    }

    private void writeToCache(String json, FoldersActivityModel model) {
        File cacheDirFile = context.getCacheDir();
        File outputFile = new File(cacheDirFile, getCacheFileName(model));

        try {
            @Cleanup FileWriter writer = new FileWriter(outputFile);
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Log.d("DEBUG", "MediaController.writeToCache()");
        }
    }
}
