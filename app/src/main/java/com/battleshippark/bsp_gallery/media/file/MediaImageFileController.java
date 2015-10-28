package com.battleshippark.bsp_gallery.media.file;

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
public class MediaImageFileController extends MediaFileController {
    private Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    public MediaImageFileController(Context context, int dirId) {
        super(context, dirId);
    }

    @Override
    public List<MediaFileModel> getMediaFileList() {
        String[] columns = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATA
        };

        String selectionClause = null;
        String[] selectionArgs = null;
        if (dirId != MediaFolderModel.ALL_DIR_ID) {
            selectionClause = String.format("%s = ?", MediaStore.Images.ImageColumns.BUCKET_ID);
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
                model.setMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
                model.setPathName(CursorUtils.getString(c, columns[2]));
                result.add(model);
            } while (c.moveToNext());
        }

        return result;
    }

    @Override
    public void getMediaFileList(Subscriber<? super List<MediaFileModel>> subscriber) {
        String[] columns = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATA
        };

        String selectionClause = null;
        if (dirId != MediaFolderModel.ALL_DIR_ID) {
            selectionClause = String.format("%s = ?", MediaStore.Images.ImageColumns.BUCKET_ID);
        }

        String[] selectionArgs = null;
        if (dirId != MediaFolderModel.ALL_DIR_ID) {
            selectionArgs = new String[]{String.valueOf(dirId),};
        }

        @Cleanup
        Cursor c = context.getContentResolver().query(uri, columns, selectionClause, selectionArgs, null);
        if (c != null && c.moveToFirst()) {
            Observable.create((Observable.OnSubscribe<MediaFileModel>) _subscriber -> {
                do {
                    MediaFileModel model = new MediaFileModel();
                    model.setId(CursorUtils.getInt(c, columns[0]));
                    model.setName(CursorUtils.getString(c, columns[1]));
                    model.setMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
                    model.setPathName(CursorUtils.getString(c, columns[2]));

                    _subscriber.onNext(model);
                } while (c.moveToNext());

                _subscriber.onCompleted();
            }).buffer(300)
                    .subscribe(subscriber::onNext);
        }
    }

    @Override
    public List<MediaFileModel> addMediaThumbPath(List<MediaFileModel> files) {
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
