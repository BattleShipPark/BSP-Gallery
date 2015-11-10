package com.battleshippark.bsp_gallery.media.file;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.battleshippark.bsp_gallery.CursorUtils;
import com.battleshippark.bsp_gallery.media.MediaFileModel;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import java.util.ArrayList;
import java.util.List;

import lombok.Cleanup;
import rx.Observable;
import rx.Subscriber;

/**
 */
public class MediaVideoFileController extends MediaFileController {
    private Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

    public MediaVideoFileController(Context context, int dirId) {
        super(context, dirId);
    }

    @Override
    public List<MediaFileModel> getMediaFileList() {
        String[] columns = new String[]{
                MediaStore.Video.VideoColumns._ID,
                MediaStore.Video.VideoColumns.DISPLAY_NAME,
                MediaStore.Video.VideoColumns.DATA
        };

        String selectionClause = null;
        String[] selectionArgs = null;
        if (dirId != MediaFolderModel.ALL_DIR_ID) {
            selectionClause = String.format("%s = ?", MediaStore.Video.VideoColumns.BUCKET_ID);
            selectionArgs = new String[]{String.valueOf(dirId)};
        }

        List<MediaFileModel> result = new ArrayList<>();

        @Cleanup
        Cursor c = context.getContentResolver().query(uri, columns, selectionClause, selectionArgs, null);
        if (c != null && c.moveToFirst()) {
            do {
                MediaFileModel model = new MediaFileModel();
                model.setId(CursorUtils.getInt(c, columns[0]));
                model.setName(CursorUtils.getString(c, columns[1]));
                model.setMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
                model.setPath(CursorUtils.getString(c, columns[2]));
                result.add(model);
            } while (c.moveToNext());
        }

        return result;
    }

    @Override
    public void getMediaFileList(Subscriber<? super List<MediaFileModel>> subscriber) {
        String[] columns = new String[]{
                MediaStore.Video.VideoColumns._ID,
                MediaStore.Video.VideoColumns.DISPLAY_NAME,
                MediaStore.Video.VideoColumns.DATA
        };

        String selectionClause = null;
        String[] selectionArgs = null;
        if (dirId != MediaFolderModel.ALL_DIR_ID) {
            selectionClause = String.format("%s = ?", MediaStore.Video.VideoColumns.BUCKET_ID);
            selectionArgs = new String[]{String.valueOf(dirId)};
        }

        String sortClause = MediaStore.Files.FileColumns._ID + " DESC";

        @Cleanup
        Cursor c = context.getContentResolver().query(uri, columns, selectionClause, selectionArgs, sortClause);
        if (c != null && c.moveToFirst()) {
            Observable.create((Observable.OnSubscribe<MediaFileModel>) _subscriber -> {
                do {
                    MediaFileModel model = new MediaFileModel();
                    model.setId(CursorUtils.getInt(c, columns[0]));
                    model.setName(CursorUtils.getString(c, columns[1]));
                    model.setMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
                    model.setPath(CursorUtils.getString(c, columns[2]));

                    _subscriber.onNext(model);
                } while (c.moveToNext());

                _subscriber.onCompleted();
            }).buffer(BUFFER_COUNT)
                    .subscribe(subscriber::onNext);
        }
    }

    @Override
    public List<MediaFileModel> addMediaThumbPath(List<MediaFileModel> files) {
        String[] projectionClauses = new String[]{MediaStore.Video.Thumbnails.DATA,};

        List<MediaFileModel> result = new ArrayList<>();

        for (MediaFileModel file : files) {
            @Cleanup Cursor c = queryVideoMiniThumbnail(context.getContentResolver(), file.getId(), MediaStore.Video.Thumbnails.MINI_KIND, projectionClauses);
            if (c != null) {
                MediaFileModel model = file.copy();
                if (c.moveToFirst()) {
                    model.setThumbPath(CursorUtils.getString(c, projectionClauses[0]));
                }
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
