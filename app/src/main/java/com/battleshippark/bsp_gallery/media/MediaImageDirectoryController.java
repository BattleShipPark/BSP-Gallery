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
public class MediaImageDirectoryController extends MediaDirectoryController {
    private Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    public MediaImageDirectoryController(Context context) {
        super(context);
    }

    List<MediaDirectoryModel> getMediaDirectoryList() {
        String[] columns = new String[]{
                MediaStore.Images.ImageColumns.BUCKET_ID,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
        };

        List<MediaDirectoryModel> result = new ArrayList<>();

        Uri distinctUri = uri.buildUpon().appendQueryParameter("distinct", "true").build();
        @Cleanup Cursor c = context.getContentResolver().query(distinctUri, columns, null, null, null);
        if (c != null && c.moveToFirst()) {
            do {
                MediaDirectoryModel model = new MediaDirectoryModel();
                model.setId(CursorUtils.getInt(c, columns[0]));
                model.setName(CursorUtils.getString(c, columns[1]));
                result.add(model);
            } while (c.moveToNext());
        }

        return result;
    }

    List<MediaDirectoryModel> addMediaFileCount(List<MediaDirectoryModel> dirs) {
        String[] countClauses = new String[]{"count(*) AS count"};

        List<MediaDirectoryModel> result = new ArrayList<>();

        for (MediaDirectoryModel dir : dirs) {
            String selectionClause = String.format("%s = ?", MediaStore.Images.ImageColumns.BUCKET_ID);
            String[] selectionArgs = new String[]{String.valueOf(dir.getId())};

            @Cleanup Cursor c = context.getContentResolver().query(uri, countClauses, selectionClause, selectionArgs, null);
            if (c != null && c.moveToFirst()) {
                do {
                    MediaDirectoryModel model = dir.copy();
                    model.setCount(CursorUtils.getInt(c, "count"));
                    result.add(model);
                } while (c.moveToNext());
            }
        }

        return result;
    }

    List<MediaDirectoryModel> addMediaFileId(List<MediaDirectoryModel> dirs) {
        String[] projectionClauses = new String[]{MediaStore.Images.Media._ID};
        String orderClause = MediaStore.Images.Media._ID + " desc";

        List<MediaDirectoryModel> result = new ArrayList<>();

        for (MediaDirectoryModel dir : dirs) {
            String selectionClause = String.format("%s = ?", MediaStore.Images.ImageColumns.BUCKET_ID);
            String[] selectionArgs = new String[]{String.valueOf(dir.getId())};

            @Cleanup Cursor c = context.getContentResolver().query(uri, projectionClauses, selectionClause, selectionArgs, orderClause);
            if (c != null && c.moveToFirst()) {
                MediaDirectoryModel model = dir.copy();
                model.setCoverMediaId(CursorUtils.getInt(c, projectionClauses[0]));
                model.setCoverMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
                result.add(model);
            }
        }

        return result;
    }

    List<MediaDirectoryModel> addMediaThumbPath(List<MediaDirectoryModel> dirs) {
        String[] projectionClauses = new String[]{MediaStore.Images.Thumbnails.DATA,};

        List<MediaDirectoryModel> result = new ArrayList<>();

        for (MediaDirectoryModel dir : dirs) {
            @Cleanup Cursor c = MediaStore.Images.Thumbnails.queryMiniThumbnail(context.getContentResolver(), dir.getCoverMediaId(), MediaStore.Images.Thumbnails.MINI_KIND, projectionClauses);
            if (c != null && c.moveToFirst()) {
                MediaDirectoryModel model = dir.copy();
                model.setCoverThumbPath(CursorUtils.getString(c, projectionClauses[0]));
                result.add(model);
            }
        }

        return result;
    }
}
