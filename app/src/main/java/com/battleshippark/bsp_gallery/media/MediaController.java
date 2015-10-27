package com.battleshippark.bsp_gallery.media;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.battleshippark.bsp_gallery.MainModel;
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
    private final MainModel mainModel;
    private final MediaDirectoryController directoryController;

    private Subject<Void, Void> writeToCacheSubject = PublishSubject.create();

    public MediaController(Context context, MainModel mainModel) {
        this.context = context;
        this.mainModel = mainModel;

        directoryController = MediaDirectoryController.create(context, mainModel.getMediaMode());

        writeToCacheSubject.subscribeOn(Schedulers.io())
                .subscribe(
                        aVoid -> {
                            String json = toJson();
                            writeToCache(json);

                        },
                        Throwable::printStackTrace);
    }

    /**
     * 디렉토리 목록을 갱신한다. 한 번에 전부 가져올 수 없고, 쿼리를 여러번 던져야 해서
     * 쿼리를 던질때마다 MainModel을 갱신한다
     */
    public void refreshDirListAsync() {
        Observable.create((Observable.OnSubscribe<List<MediaDirectoryModel>>) subscriber -> {
            List<MediaDirectoryModel> dirs = null;
            MediaDirectoryModel allDir = null;

            try {
                dirs = getFromCache(context);
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
                        mainModel::setMediaDirectoryModelList,
                        Throwable::printStackTrace,
                        () -> writeToCacheSubject.onNext(null));
    }

    /*
     * func을 수행해서 디렉토리 목록을 구하고, 거기에 전체 디렉토리를 붙여서 발행한다.
     * 반환할 때는 전체 디렉토리없이 한다
     */
    private List<MediaDirectoryModel> getDirsWithAllAndNext(@Nullable MediaDirectoryModel allDir,
                                                            Subscriber<? super List<MediaDirectoryModel>> subscriber,
                                                            Func0<List<MediaDirectoryModel>> func) {
        List<MediaDirectoryModel> dirs = func.call();
        List<MediaDirectoryModel> result = new ArrayList<>();

        result.clear();
        if (allDir != null)
            result.add(allDir);
        result.addAll(dirs);
        subscriber.onNext(result);

        return dirs;
    }

    private List<MediaDirectoryModel> getDirsWithAllAndNext(@Nullable MediaDirectoryModel allDir, List<MediaDirectoryModel> dirs,
                                                            Subscriber<? super List<MediaDirectoryModel>> subscriber,
                                                            Func1<List<MediaDirectoryModel>, List<MediaDirectoryModel>> func) {
        dirs = func.call(dirs);
        List<MediaDirectoryModel> result = new ArrayList<>();

        result.clear();
        if (allDir != null)
            result.add(allDir);
        result.addAll(dirs);
        subscriber.onNext(result);

        return dirs;
    }

    List<MediaDirectoryModel> addAllDirectory(List<MediaDirectoryModel> directories) {
        MediaDirectoryModel allDir = new MediaDirectoryModel();

        allDir.setId(MediaDirectoryModel.ALL_DIR_ID);

        allDir.setName("All");

        int count = Observable.from(directories)
                .map(MediaDirectoryModel::getCount)
                .reduce((_sum, _count) -> _sum + _count)
                .toBlocking()
                .last();
        allDir.setCount(count);

        MediaDirectoryModel dir = Observable.from(directories)
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


    private String toJson() {
        return new Gson().toJson(mainModel.getMediaDirectoryModelList());
    }

    private List<MediaDirectoryModel> getFromCache(Context context) throws IOException {
        File cacheDirFile = context.getCacheDir();
        File inputFile = new File(cacheDirFile, CACHE_FILENAME);

        @Cleanup FileReader reader = new FileReader(inputFile);

        try {
            Type type = new TypeToken<List<MediaDirectoryModel>>() {
            }.getType();
            return new Gson().fromJson(reader, type);
        } catch (JsonParseException e) {
            throw new IOException(e);
        } finally {
            Log.d("DEBUG", "MediaController.getFromCache()");
        }
    }

    private void writeToCache(String json) {
        File cacheDirFile = context.getCacheDir();
        File outputFile = new File(cacheDirFile, CACHE_FILENAME);

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
