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
public class MediaVideoFolderController extends MediaFolderController {
    private Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

    public MediaVideoFolderController(Context context) {
        super(context);
    }

    @Override
    public List<MediaFolderModel> getMediaDirectoryList() {
        String[] columns = new String[]{
                MediaStore.Video.VideoColumns.BUCKET_ID,
                MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME,
        };

        List<MediaFolderModel> result = new ArrayList<>();

        Uri distinctUri = uri.buildUpon().appendQueryParameter("distinct", "true").build();
        @Cleanup Cursor c = context.getContentResolver().query(distinctUri, columns, null, null, null);
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
            String selectionClause = String.format("%s = ?", MediaStore.Video.VideoColumns.BUCKET_ID);
            String[] selectionArgs = new String[]{String.valueOf(dir.getId())};

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
        String[] projectionClauses = new String[]{MediaStore.Video.Media._ID};
        String orderClause = MediaStore.Video.Media._ID + " desc";

        List<MediaFolderModel> result = new ArrayList<>();

        for (MediaFolderModel dir : dirs) {
            String selectionClause = String.format("%s = ?", MediaStore.Video.VideoColumns.BUCKET_ID);
            String[] selectionArgs = new String[]{String.valueOf(dir.getId())};

            @Cleanup Cursor c = context.getContentResolver().query(uri, projectionClauses, selectionClause, selectionArgs, orderClause);
            if (c != null && c.moveToFirst()) {
                MediaFolderModel model = MediaFolderModel.copy(dir);
                model.setCoverMediaId(CursorUtils.getInt(c, projectionClauses[0]));
                model.setCoverMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
                result.add(model);
            }
        }

        return result;
    }

    @Override
    public List<MediaFolderModel> addMediaThumbPath(List<MediaFolderModel> dirs) {
        String[] projectionClauses = new String[]{MediaStore.Video.Thumbnails.DATA,};

        List<MediaFolderModel> result = new ArrayList<>();

        for (MediaFolderModel dir : dirs) {
            @Cleanup Cursor c = queryVideoMiniThumbnail(context.getContentResolver(), dir.getCoverMediaId(), MediaStore.Images.Thumbnails.MINI_KIND, projectionClauses);
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
