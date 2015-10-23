package com.battleshippark.bsp_gallery.media;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.battleshippark.bsp_gallery.CursorUtils;

import java.util.ArrayList;
import java.util.List;

import lombok.Cleanup;

/**
 */
public class MediaController {
    /**
     * 디렉토리 구조를 가져온다.
     *
     * @return ID와 이름만 유효하다
     */
    public static List<MediaDirectory> getMediaDirectoryList(Context context) {
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
    public static List<MediaDirectory> addMediaFileCount(Context context, List<MediaDirectory> dirs) {
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
    public static List<MediaDirectory> addMediaFileId(Context context, List<MediaDirectory> dirs) {
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
    public static List<MediaDirectory> addMediaThumbPath(Context context, List<MediaDirectory> dirs) {
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
