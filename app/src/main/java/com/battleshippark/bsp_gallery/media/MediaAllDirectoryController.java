package com.battleshippark.bsp_gallery.media;

import android.content.ContentResolver;
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
public class MediaAllDirectoryController extends MediaDirectoryController {
    private Uri uri = MediaStore.Files.getContentUri("external");

    public MediaAllDirectoryController(Context context) {
        super(context);
    }

    List<MediaDirectoryModel> getMediaDirectoryList() {
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

        List<MediaDirectoryModel> result = new ArrayList<>();

        Uri distinctUri = uri.buildUpon().appendQueryParameter("distinct", "true").build();
        @Cleanup
        Cursor c = context.getContentResolver().query(distinctUri, columns, selectionClause, selectionArgs, null);
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
            String selectionClause = String.format("%s = ? AND (%s = ? OR %s = ?)",
                    MediaStore.Images.ImageColumns.BUCKET_ID,
                    MediaStore.Files.FileColumns.MEDIA_TYPE,
                    MediaStore.Files.FileColumns.MEDIA_TYPE
            );
            String[] selectionArgs = new String[]{
                    String.valueOf(dir.getId()),
                    String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                    String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
            };

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
        String[] projectionClauses = new String[]{MediaStore.Images.Media._ID, MediaStore.Files.FileColumns.MEDIA_TYPE};
        String orderClause = MediaStore.Images.Media._ID + " desc";

        List<MediaDirectoryModel> result = new ArrayList<>();

        for (MediaDirectoryModel dir : dirs) {
            String selectionClause = String.format("%s = ? AND (%s = ? OR %s = ?)",
                    MediaStore.Images.ImageColumns.BUCKET_ID,
                    MediaStore.Files.FileColumns.MEDIA_TYPE,
                    MediaStore.Files.FileColumns.MEDIA_TYPE
            );
            String[] selectionArgs = new String[]{
                    String.valueOf(dir.getId()),
                    String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                    String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
            };

            @Cleanup Cursor c = context.getContentResolver().query(uri, projectionClauses, selectionClause, selectionArgs, orderClause);
            if (c != null && c.moveToFirst()) {
                MediaDirectoryModel model = dir.copy();
                model.setCoverMediaId(CursorUtils.getInt(c, projectionClauses[0]));
                model.setCoverMediaType(CursorUtils.getInt(c, projectionClauses[1]));
                result.add(model);
            }
        }

        return result;
    }

    List<MediaDirectoryModel> addMediaThumbPath(List<MediaDirectoryModel> dirs) {
        String[] projectionClauses = new String[]{MediaStore.Images.Thumbnails.DATA,};

        List<MediaDirectoryModel> result = new ArrayList<>();

        for (MediaDirectoryModel dir : dirs) {
            @Cleanup Cursor c = null;
            if (dir.getCoverMediaType() == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
                c = MediaStore.Images.Thumbnails.queryMiniThumbnail(context.getContentResolver(), dir.getCoverMediaId(), MediaStore.Images.Thumbnails.MINI_KIND, projectionClauses);
            else
                c = queryVideoMiniThumbnail(context.getContentResolver(), dir.getCoverMediaId(), MediaStore.Images.Thumbnails.MINI_KIND, projectionClauses);

            if (c != null && c.moveToFirst()) {
                MediaDirectoryModel model = dir.copy();
                model.setCoverThumbPath(CursorUtils.getString(c, projectionClauses[0]));
                result.add(model);
            }
        }

        return result;
    }

    private Cursor queryVideoMiniThumbnail(ContentResolver cr, long origId, int kind, String[] projection) {
        return cr.query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, projection,
                MediaStore.Video.Thumbnails.VIDEO_ID + " = " + origId + " AND " +
                        MediaStore.Video.Thumbnails.KIND + " = " + kind, null, null);
    }
}
