package com.battleshippark.bsp_gallery.data.media;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.battleshippark.bsp_gallery.CursorUtils;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import java.io.IOException;

import lombok.AllArgsConstructor;
import rx.Observable;

/**
 */
@AllArgsConstructor
public class MediaVideoFolderRepository implements MediaFolderRepository {
    private final static Uri uri = MediaStore.Files.getContentUri("external");
    private Context context;

    @Override
    public Observable<MediaFolderModel> queryList() {
        return Observable.create(subscriber -> {
            String[] columns = new String[]{
                    MediaStore.Images.ImageColumns.BUCKET_ID,
                    MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
            };
            String selectionClause = String.format("%s = ? OR %s = ?",
                    MediaStore.Files.FileColumns.MEDIA_TYPE,
                    MediaStore.Files.FileColumns.MEDIA_TYPE
            );
            String[] selectionArgs = new String[]{
                    String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                    String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),

            };

            Uri distinctUri = uri.buildUpon().appendQueryParameter("distinct", "true").build();
            Cursor c = context.getContentResolver().query(distinctUri, columns, selectionClause, selectionArgs, null);
            try {
                if (c != null && c.moveToFirst()) {
                    do {
                        MediaFolderModel model = new MediaFolderModel();
                        model.setId(CursorUtils.getInt(c, columns[0]));
                        model.setName(CursorUtils.getString(c, columns[1]));

                        subscriber.onNext(model);
                    } while (c.moveToNext());
                }
            } catch (Exception e) {
                subscriber.onError(e);
            } finally {
                if (c != null) {
                    c.close();
                }
            }
        });
    }

    @Override
    public int queryFileCount(int folderId) throws IOException {
        String[] countClauses = new String[]{"count(*) AS count"};

        String selectionClause = String.format("%s = ?", MediaStore.Video.VideoColumns.BUCKET_ID);
        String[] selectionArgs = new String[]{String.valueOf(folderId)};

        Cursor c = context.getContentResolver().query(uri, countClauses, selectionClause, selectionArgs, null);
        try {
            if (c != null && c.moveToFirst()) {
                return CursorUtils.getInt(c, "count");
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        throw new IOException();
    }

    @Override
    public MediaFolderModel queryCoverFile(int folderId) throws IOException {
        String[] projectionClauses = new String[]{MediaStore.Video.Media._ID};
        String orderClause = MediaStore.Video.Media._ID + " desc";

        String selectionClause = String.format("%s = ?", MediaStore.Video.VideoColumns.BUCKET_ID);
        String[] selectionArgs = new String[]{String.valueOf(folderId)};

        Cursor c = context.getContentResolver().query(uri, projectionClauses, selectionClause, selectionArgs, orderClause);
        try {
            if (c != null && c.moveToFirst()) {
                MediaFolderModel model = new MediaFolderModel();
                model.setCoverMediaId(CursorUtils.getInt(c, projectionClauses[0]));
                model.setCoverMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
                return model;
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        throw new IOException();
    }
}
