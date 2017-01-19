package com.battleshippark.bsp_gallery.media.folder;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import com.battleshippark.bsp_gallery.CursorUtils;
import com.battleshippark.bsp_gallery.presentation.folders.MediaFolderRepository;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import java.io.IOException;

import rx.Subscriber;

/**
 */
public class MediaAllFolderController extends MediaFolderController {
    private Uri uri = MediaStore.Files.getContentUri("external");
    Context context;

    public MediaAllFolderController(MediaFolderRepository repository) {
        super(repository);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void queryMediaFolderAndOnNext(Subscriber<? super MediaFolderModel> subscriber) {
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
        try (Cursor c = context.getContentResolver().query(distinctUri, columns, selectionClause, selectionArgs, null)) {
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

        String selectionClause = String.format("%s = ? AND (%s = ? OR %s = ?)",
                MediaStore.Images.ImageColumns.BUCKET_ID,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MEDIA_TYPE
        );
        String[] selectionArgs = new String[]{
                String.valueOf(mediaFolderModel.getId()),
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
        };

        try (Cursor c = context.getContentResolver().query(uri, countClauses, selectionClause, selectionArgs, null)) {
            if (c != null && c.moveToFirst()) {
                MediaFolderModel model = mediaFolderModel.copy();
                model.setCount(CursorUtils.getInt(c, "count"));
                return model;
            }
        }
        throw new IOException();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected MediaFolderModel queryMediaFileId(MediaFolderModel mediaFolderModel) throws IOException {
        String[] projectionClauses = new String[]{MediaStore.Images.Media._ID, MediaStore.Files.FileColumns.MEDIA_TYPE};
        String orderClause = MediaStore.Images.Media._ID + " desc";

        String selectionClause = String.format("%s = ? AND (%s = ? OR %s = ?)",
                MediaStore.Images.ImageColumns.BUCKET_ID,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MEDIA_TYPE
        );
        String[] selectionArgs = new String[]{
                String.valueOf(mediaFolderModel.getId()),
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
        };

        try (Cursor c = context.getContentResolver().query(uri, projectionClauses, selectionClause, selectionArgs, orderClause)) {
            if (c != null && c.moveToFirst()) {
                MediaFolderModel model = mediaFolderModel.copy();
                model.setCoverMediaId(CursorUtils.getInt(c, projectionClauses[0]));
                model.setCoverMediaType(CursorUtils.getInt(c, projectionClauses[1]));
                return model;
            }
        }
        throw new IOException();
    }

    @Override
    protected MediaFolderModel queryMediaThumbPath(MediaFolderModel mediaFolderModel) {
        String[] projectionClauses = new String[]{MediaStore.Images.Thumbnails.DATA,};

        Cursor c = null;
        try {
            if (mediaFolderModel.getCoverMediaType() == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                return mediaFolderModel.copy();
            } else {
                c = queryVideoMiniThumbnail(context.getContentResolver(), mediaFolderModel.getCoverMediaId(), MediaStore.Images.Thumbnails.MINI_KIND, projectionClauses);

                if (c != null && c.moveToFirst()) {
                    MediaFolderModel model = mediaFolderModel.copy();
                    model.setCoverThumbPath(CursorUtils.getString(c, projectionClauses[0]));
                    return model;
                } else {
                    Log.w(MediaFolderController.class.getSimpleName(),
                            "folder " + mediaFolderModel.getName() + "does not have thumbnail");
                    return mediaFolderModel.copy();
                }
            }
        } finally {
            if (c != null)
                c.close();
        }
    }

    private Cursor queryVideoMiniThumbnail(ContentResolver cr, long origId, int kind, String[] projection) {
        return cr.query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, projection,
                MediaStore.Video.Thumbnails.VIDEO_ID + " = " + origId + " AND " +
                        MediaStore.Video.Thumbnails.KIND + " = " + kind, null, null);
    }
}
