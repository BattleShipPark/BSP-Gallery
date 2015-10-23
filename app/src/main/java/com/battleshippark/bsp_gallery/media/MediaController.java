package com.battleshippark.bsp_gallery.media;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.battleshippark.bsp_gallery.CursorUtils;
import com.battleshippark.bsp_gallery.MainModel;

import java.util.ArrayList;
import java.util.List;

import lombok.Cleanup;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 */
public class MediaController {
    private final Context context;
    private final MainModel mainModel;

    public MediaController(Context context, MainModel mainModel) {
        this.context = context;
        this.mainModel = mainModel;
    }

    /**
     * 디렉토리 목록을 갱신한다. 한 번에 전부 가져올 수 없고, 쿼리를 여러번 던져야 해서
     * 쿼리를 던질때마다 MainModel을 갱신한다
     */
    public void refreshDirListAsync() {
        Observable.create(new Observable.OnSubscribe<List<MediaDirectory>>() {
            @Override
            public void call(Subscriber<? super List<MediaDirectory>> subscriber) {
                List<MediaDirectory> dir;

                dir = getMediaDirectoryList(context);
                subscriber.onNext(dir);

                dir = addMediaFileCount(context, dir);
                subscriber.onNext(dir);

                dir = addMediaFileId(context, dir);
                subscriber.onNext(dir);

                dir = addMediaThumbPath(context, dir);
                subscriber.onNext(dir);

                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<MediaDirectory>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<MediaDirectory> mediaDirectories) {
                        mainModel.setMediaDirectoryList(mediaDirectories);
                    }
                });
    }

    /**
     * 디렉토리 구조를 가져온다.
     *
     * @return ID와 이름만 유효하다
     */
    static List<MediaDirectory> getMediaDirectoryList(Context context) {
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
    static List<MediaDirectory> addMediaFileCount(Context context, List<MediaDirectory> dirs) {
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
    static List<MediaDirectory> addMediaFileId(Context context, List<MediaDirectory> dirs) {
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
    static List<MediaDirectory> addMediaThumbPath(Context context, List<MediaDirectory> dirs) {
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
}
