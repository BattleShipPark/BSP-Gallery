package com.battleshippark.bsp_gallery.media.folder;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.battleshippark.bsp_gallery.CursorUtils;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import java.io.IOException;

import rx.Subscriber;

/**
 */
public class MediaImageFolderController extends MediaFolderController {
    private Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    public MediaImageFolderController(Context context) {
        super(context);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void queryMediaFolderAndOnNext(Subscriber<? super MediaFolderModel> subscriber) {
        String[] columns = new String[]{
                MediaStore.Images.ImageColumns.BUCKET_ID,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
        };

        Uri distinctUri = uri.buildUpon().appendQueryParameter("distinct", "true").build();
        try (Cursor c = context.getContentResolver().query(distinctUri, columns, null, null, null)) {
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
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected MediaFolderModel queryMediaFileCount(MediaFolderModel mediaFolderModel) throws IOException {
        String[] countClauses = new String[]{"count(*) AS count"};

        String selectionClause = String.format("%s = ?", MediaStore.Images.ImageColumns.BUCKET_ID);
        String[] selectionArgs = new String[]{String.valueOf(mediaFolderModel.getId())};

        try (Cursor c = context.getContentResolver().query(uri, countClauses, selectionClause, selectionArgs, null)) {
            if (c != null && c.moveToFirst()) {
                MediaFolderModel model = MediaFolderModel.copy(mediaFolderModel);
                model.setCount(CursorUtils.getInt(c, "count"));
                return model;
            }
        }
        throw new IOException();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected MediaFolderModel queryMediaFileId(MediaFolderModel mediaFolderModel) throws IOException {
        String[] projectionClauses = new String[]{MediaStore.Images.Media._ID};
        String orderClause = MediaStore.Images.Media._ID + " desc";

        String selectionClause = String.format("%s = ?", MediaStore.Images.ImageColumns.BUCKET_ID);
        String[] selectionArgs = new String[]{String.valueOf(mediaFolderModel.getId())};

        try (Cursor c = context.getContentResolver().query(uri, projectionClauses, selectionClause, selectionArgs, orderClause)) {
            if (c != null && c.moveToFirst()) {
                MediaFolderModel model = MediaFolderModel.copy(mediaFolderModel);
                model.setCoverMediaId(CursorUtils.getInt(c, projectionClauses[0]));
                model.setCoverMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
                return model;
            }
        }
        throw new IOException();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected MediaFolderModel queryMediaThumbPath(MediaFolderModel mediaFolderModel) throws IOException {
        String[] projectionClauses = new String[]{MediaStore.Images.Thumbnails.DATA,};

        try (Cursor c = MediaStore.Images.Thumbnails.queryMiniThumbnail(context.getContentResolver(), mediaFolderModel.getCoverMediaId(), MediaStore.Images.Thumbnails.MINI_KIND, projectionClauses)) {
            if (c != null && c.moveToFirst()) {
                MediaFolderModel model = MediaFolderModel.copy(mediaFolderModel);
                model.setCoverThumbPath(CursorUtils.getString(c, projectionClauses[0]));
                return model;
            }
        }

        throw new IOException();
    }
}
