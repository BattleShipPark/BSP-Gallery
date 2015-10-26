package com.battleshippark.bsp_gallery.media;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.battleshippark.bsp_gallery.CursorUtils;
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

    private Subject<Void, Void> writeToCacheSubject = PublishSubject.create();

    public MediaController(Context context, MainModel mainModel) {
        this.context = context;
        this.mainModel = mainModel;

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
        Observable.create((Observable.OnSubscribe<List<MediaDirectory>>) subscriber -> {
            List<MediaDirectory> dirs = null;
            MediaDirectory allDir;

            try {
                dirs = getFromCache(context);
                subscriber.onNext(dirs);
            } catch (IOException e) {
                e.printStackTrace();
            }

            allDir = dirs.get(0);

            dirs = getDirsWithAllAndNext(allDir, subscriber, () -> getMediaDirectoryList(context));

            dirs = getDirsWithAllAndNext(allDir, dirs, subscriber, (_dirs) -> addMediaFileCount(context, _dirs));

            dirs = getDirsWithAllAndNext(allDir, dirs, subscriber, (_dirs) -> addMediaFileId(context, _dirs));

            dirs = getDirsWithAllAndNext(allDir, dirs, subscriber, (_dirs) -> addMediaThumbPath(context, _dirs));

            dirs = addAllDirectory(dirs);
            subscriber.onNext(dirs);

            subscriber.onCompleted();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        mainModel::setMediaDirectoryList,
                        Throwable::printStackTrace,
                        () -> writeToCacheSubject.onNext(null));
    }

    /*
     * func을 수행해서 디렉토리 목록을 구하고, 거기에 전체 디렉토리를 붙여서 발행한다.
     * 반환할 때는 전체 디렉토리없이 한다
     */
    private List<MediaDirectory> getDirsWithAllAndNext(MediaDirectory allDir,
                                                       Subscriber<? super List<MediaDirectory>> subscriber,
                                                       Func0<List<MediaDirectory>> func) {
        List<MediaDirectory> dirs = func.call();
        List<MediaDirectory> result = new ArrayList<>();

        result.clear();
        result.add(allDir);
        result.addAll(dirs);
        subscriber.onNext(result);

        return dirs;
    }

    private List<MediaDirectory> getDirsWithAllAndNext(MediaDirectory allDir, List<MediaDirectory> dirs,
                                                       Subscriber<? super List<MediaDirectory>> subscriber,
                                                       Func1<List<MediaDirectory>, List<MediaDirectory>> func) {
        dirs = func.call(dirs);
        List<MediaDirectory> result = new ArrayList<>();

        result.clear();
        result.add(allDir);
        result.addAll(dirs);
        subscriber.onNext(result);

        return dirs;
    }

    /**
     * 디렉토리 구조를 가져온다.
     *
     * @return ID와 이름만 유효하다
     */
    List<MediaDirectory> getMediaDirectoryList(Context context) {
        String[] columns = new String[]{
                MediaStore.Images.ImageColumns.BUCKET_ID,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
        };

        List<MediaDirectory> result = new ArrayList<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendQueryParameter("distinct", "true").build();
        @Cleanup Cursor c = context.getContentResolver().query(uri, columns, null, null, null);
        if (c != null && c.moveToFirst()) {
            do {
                MediaDirectory model = new MediaDirectory();
                model.setId(CursorUtils.getInt(c, columns[0]));
                model.setName(CursorUtils.getString(c, columns[1]));
                result.add(model);
            } while (c.moveToNext());
        }

        return result;
    }

    /**
     * 디렉토리에 파일 갯수를 추가한다
     */
    List<MediaDirectory> addMediaFileCount(Context context, List<MediaDirectory> dirs) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] countClauses = new String[]{"count(*) AS count"};

        List<MediaDirectory> result = new ArrayList<>();

        for (MediaDirectory dir : dirs) {
            String selectionClause = String.format("%s = ?", MediaStore.Images.ImageColumns.BUCKET_ID);
            String[] selectionArgs = new String[]{String.valueOf(dir.getId())};

            @Cleanup Cursor c = context.getContentResolver().query(uri, countClauses, selectionClause, selectionArgs, null);
            if (c != null && c.moveToFirst()) {
                do {
                    MediaDirectory model = dir.copy();
                    model.setCount(CursorUtils.getInt(c, "count"));
                    result.add(model);
                } while (c.moveToNext());
            }
        }

        return result;
    }

    /**
     * 디렉토리에 가장 최근 파일의 ID를 추가한다
     */
    List<MediaDirectory> addMediaFileId(Context context, List<MediaDirectory> dirs) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projectionClauses = new String[]{MediaStore.Images.Media._ID};
        String orderClause = MediaStore.Images.Media._ID + " desc";

        List<MediaDirectory> result = new ArrayList<>();

        for (MediaDirectory dir : dirs) {
            String selectionClause = String.format("%s = ?", MediaStore.Images.ImageColumns.BUCKET_ID);
            String[] selectionArgs = new String[]{String.valueOf(dir.getId())};

            @Cleanup Cursor c = context.getContentResolver().query(uri, projectionClauses, selectionClause, selectionArgs, orderClause);
            if (c != null && c.moveToFirst()) {
                MediaDirectory model = dir.copy();
                model.setCoverImageId(CursorUtils.getInt(c, projectionClauses[0]));
                result.add(model);
            }
        }

        return result;
    }

    /**
     * 디렉토리에 가장 최근 파일의 손톱 이미지 경로를 추가한다
     */
    List<MediaDirectory> addMediaThumbPath(Context context, List<MediaDirectory> dirs) {
        String[] projectionClauses = new String[]{MediaStore.Images.Thumbnails.DATA,};

        List<MediaDirectory> result = new ArrayList<>();

        for (MediaDirectory dir : dirs) {
            @Cleanup Cursor c = MediaStore.Images.Thumbnails.queryMiniThumbnail(context.getContentResolver(), dir.getCoverImageId(), MediaStore.Images.Thumbnails.MINI_KIND, projectionClauses);
            if (c != null && c.moveToFirst()) {
                MediaDirectory model = dir.copy();
                model.setCoverThumbPath(CursorUtils.getString(c, projectionClauses[0]));
                result.add(model);
            }
        }

        return result;
    }

    List<MediaDirectory> addAllDirectory(List<MediaDirectory> directories) {
        MediaDirectory allDir = new MediaDirectory();

        allDir.setId(MediaDirectory.ALL_DIR_ID);

        allDir.setName("All");

        int count = Observable.from(directories)
                .map(MediaDirectory::getCount)
                .reduce((_sum, _count) -> _sum + _count)
                .toBlocking()
                .last();
        allDir.setCount(count);

        MediaDirectory dir = Observable.from(directories)
                .reduce((_dir1, _dir2) -> {
                    if (_dir1.getCoverImageId() >= _dir2.getCoverImageId())
                        return _dir1;
                    else
                        return _dir2;
                })
                .toBlocking()
                .last();
        allDir.setCoverImageId(dir.getCoverImageId());
        allDir.setCoverThumbPath(dir.getCoverThumbPath());

        directories.add(0, allDir);
        return directories;
    }

    private String toJson() {
        return new Gson().toJson(mainModel.getMediaDirectoryList());
    }

    private List<MediaDirectory> getFromCache(Context context) throws IOException {
        File cacheDirFile = context.getCacheDir();
        File inputFile = new File(cacheDirFile, CACHE_FILENAME);

        @Cleanup FileReader reader = new FileReader(inputFile);

        try {
            Type type = new TypeToken<List<MediaDirectory>>() {
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
