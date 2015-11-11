package com.battleshippark.bsp_gallery.media.file;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

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
public class MediaAllFileController extends MediaFileController {
    private Uri uri = MediaStore.Files.getContentUri("external");
    private String[] columns;
    private String selectionClause;
    private String[] selectionArgs;
    private String sortClause;

    public MediaAllFileController(Context context, int dirId) {
        super(context, dirId);

        columns = new String[]{
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.DATA
        };

        if (dirId == MediaFolderModel.ALL_DIR_ID) {
            selectionClause = String.format("%s = ? OR %s = ?",
                    MediaStore.Files.FileColumns.MEDIA_TYPE,
                    MediaStore.Files.FileColumns.MEDIA_TYPE
            );
        } else {
            selectionClause = String.format("%s = ? AND (%s = ? OR %s = ?)",
                    MediaStore.Images.ImageColumns.BUCKET_ID,
                    MediaStore.Files.FileColumns.MEDIA_TYPE,
                    MediaStore.Files.FileColumns.MEDIA_TYPE
            );
        }

        if (dirId == MediaFolderModel.ALL_DIR_ID) {
            selectionArgs = new String[]{
                    String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                    String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
            };
        } else {
            selectionArgs = new String[]{
                    String.valueOf(dirId),
                    String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                    String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
            };
        }

        sortClause = MediaStore.Files.FileColumns._ID + " DESC";
    }

    @Override
    public List<MediaFileModel> getMediaFileList() {
        List<MediaFileModel> result = new ArrayList<>();

        @Cleanup
        Cursor c = context.getContentResolver().query(uri, columns, selectionClause, selectionArgs, sortClause);
        if (c != null && c.moveToFirst()) {
            do {
                MediaFileModel model = new MediaFileModel();
                model.setId(CursorUtils.getInt(c, columns[0]));
                model.setName(CursorUtils.getString(c, columns[1]));
                model.setMediaType(CursorUtils.getInt(c, columns[2]));
                model.setPath(CursorUtils.getString(c, columns[3]));
                result.add(model);
            } while (c.moveToNext());
        }

//        Log.d("", "getMediaFileList(): " + result.size());
        return result;
    }

    @Override
    public void getMediaFileList(Subscriber<? super List<MediaFileModel>> subscriber) {
        @Cleanup
        Cursor c = context.getContentResolver().query(uri, columns, selectionClause, selectionArgs, sortClause);
        if (c != null && c.moveToFirst()) {
            Observable.create((Observable.OnSubscribe<MediaFileModel>) _subscriber -> {
                do {
                    MediaFileModel model = new MediaFileModel();
                    model.setId(CursorUtils.getInt(c, columns[0]));
                    model.setName(CursorUtils.getString(c, columns[1]));
                    model.setMediaType(CursorUtils.getInt(c, columns[2]));
                    model.setPath(CursorUtils.getString(c, columns[3]));

                    _subscriber.onNext(model);
                } while (c.moveToNext());

                _subscriber.onCompleted();
            }).buffer(BUFFER_COUNT)
                    .subscribe(subscriber::onNext);
        }
    }

    @Override
    public List<MediaFileModel> addMediaThumbPath(List<MediaFileModel> files) {
        String[] projectionClauses = new String[]{MediaStore.Images.Thumbnails.DATA,};

        List<MediaFileModel> result = new ArrayList<>();

        for (MediaFileModel file : files) {
            @Cleanup Cursor c = null;
            if (file.getMediaType() == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
                c = MediaStore.Images.Thumbnails.queryMiniThumbnail(context.getContentResolver(), file.getId(), MediaStore.Images.Thumbnails.MINI_KIND, projectionClauses);
            else
                c = queryVideoMiniThumbnail(context.getContentResolver(), file.getId(), MediaStore.Video.Thumbnails.MINI_KIND, projectionClauses);

            if (c != null && c.moveToFirst()) {
                MediaFileModel model = file.copy();
                model.setThumbPath(CursorUtils.getString(c, projectionClauses[0]));
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
