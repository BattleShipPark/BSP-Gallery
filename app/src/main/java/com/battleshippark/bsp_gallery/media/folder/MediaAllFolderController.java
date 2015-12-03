package com.battleshippark.bsp_gallery.media.folder;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.battleshippark.bsp_gallery.CursorUtils;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import java.util.ArrayList;
import java.util.List;

import lombok.Cleanup;

/**
 */
public class MediaAllFolderController extends MediaFolderController {
    private Uri uri = MediaStore.Files.getContentUri("external");

    public MediaAllFolderController(Context context) {
        super(context);
    }

    @Override
    public List<MediaFolderModel> getMediaDirectoryList() {
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

        List<MediaFolderModel> result = new ArrayList<>();

        Uri distinctUri = uri.buildUpon().appendQueryParameter("distinct", "true").build();
        @Cleanup
        Cursor c = context.getContentResolver().query(distinctUri, columns, selectionClause, selectionArgs, null);
        if (c != null && c.moveToFirst()) {
            do {
                MediaFolderModel model = new MediaFolderModel();
                model.setId(CursorUtils.getInt(c, columns[0]));
                model.setName(CursorUtils.getString(c, columns[1]));
                result.add(model);
            } while (c.moveToNext());
        }

        return result;
    }

    @Override
    public List<MediaFolderModel> addMediaFileCount(List<MediaFolderModel> dirs) {
        String[] countClauses = new String[]{"count(*) AS count"};

        List<MediaFolderModel> result = new ArrayList<>();

        for (MediaFolderModel dir : dirs) {
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
                    MediaFolderModel model = MediaFolderModel.copy(dir);
                    model.setCount(CursorUtils.getInt(c, "count"));
                    result.add(model);
                } while (c.moveToNext());
            }
        }

        return result;
    }

    @Override
    public List<MediaFolderModel> addMediaFileId(List<MediaFolderModel> dirs) {
        String[] projectionClauses = new String[]{MediaStore.Images.Media._ID, MediaStore.Files.FileColumns.MEDIA_TYPE};
        String orderClause = MediaStore.Images.Media._ID + " desc";

        List<MediaFolderModel> result = new ArrayList<>();

        for (MediaFolderModel dir : dirs) {
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
                MediaFolderModel model = MediaFolderModel.copy(dir);
                model.setCoverMediaId(CursorUtils.getInt(c, projectionClauses[0]));
                model.setCoverMediaType(CursorUtils.getInt(c, projectionClauses[1]));
                result.add(model);
            }
        }

        return result;
    }

    @Override
    public List<MediaFolderModel> addMediaThumbPath(List<MediaFolderModel> dirs) {
        String[] projectionClauses = new String[]{MediaStore.Images.Thumbnails.DATA,};

        List<MediaFolderModel> result = new ArrayList<>();

        for (MediaFolderModel dir : dirs) {
            @Cleanup Cursor c = null;
            if (dir.getCoverMediaType() == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
                c = MediaStore.Images.Thumbnails.queryMiniThumbnail(context.getContentResolver(), dir.getCoverMediaId(), MediaStore.Images.Thumbnails.MINI_KIND, projectionClauses);
            else
                c = queryVideoMiniThumbnail(context.getContentResolver(), dir.getCoverMediaId(), MediaStore.Images.Thumbnails.MINI_KIND, projectionClauses);

            if (c != null && c.moveToFirst()) {
                MediaFolderModel model = MediaFolderModel.copy(dir);
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
