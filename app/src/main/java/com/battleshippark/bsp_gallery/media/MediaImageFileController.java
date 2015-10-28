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
public class MediaImageFileController extends MediaFileController {
    private Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    public MediaImageFileController(Context context, int dirId) {
        super(context, dirId);
    }

    @Override
    List<MediaFileModel> getMediaFileList() {
        String[] columns = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATA
        };
        String selectionClause = String.format("%s = ?", MediaStore.Images.ImageColumns.BUCKET_ID);
        String[] selectionArgs = new String[]{
                String.valueOf(dirId),
        };

        List<MediaFileModel> result = new ArrayList<>();

        @Cleanup
        Cursor c = context.getContentResolver().query(uri, columns, selectionClause, selectionArgs, null);
        if (c != null && c.moveToFirst()) {
            do {
                MediaFileModel model = new MediaFileModel();
                model.setId(CursorUtils.getInt(c, columns[0]));
                model.setName(CursorUtils.getString(c, columns[1]));
                model.setMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
                model.setPathName(CursorUtils.getString(c, columns[2]));
                result.add(model);
            } while (c.moveToNext());
        }

        return result;
    }

    @Override
    List<MediaFileModel> addMediaThumbPath(List<MediaFileModel> files) {
        String[] projectionClauses = new String[]{MediaStore.Images.Thumbnails.DATA,};

        List<MediaFileModel> result = new ArrayList<>();

        for (MediaFileModel file : files) {
            @Cleanup Cursor c = MediaStore.Images.Thumbnails.queryMiniThumbnail(context.getContentResolver(), file.getId(), MediaStore.Images.Thumbnails.MINI_KIND, projectionClauses);
            if (c != null && c.moveToFirst()) {
                MediaFileModel model = file.copy();
                model.setThumbPath(CursorUtils.getString(c, projectionClauses[0]));
                result.add(model);
            }
        }

        return result;
    }
}
