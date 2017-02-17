package com.battleshippark.bsp_gallery.data.media;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.annimon.stream.function.Consumer;
import com.battleshippark.bsp_gallery.CursorUtils;
import com.battleshippark.bsp_gallery.media.MediaFileModel;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import java.util.List;

import rx.Observable;

/**
 */
public class MediaAllFileRepository implements MediaFileRepository {
    private final static Uri uri = MediaStore.Files.getContentUri("external");
    private final Context context;
    private final int folderId;
    private final String[] columns;
    private final String selectionClause;
    private final String[] selectionArgs;
    private final String sortClause;

    public MediaAllFileRepository(Context context, int folderId) {
        this.context = context;
        this.folderId = folderId;

        columns = new String[]{
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.DATA
        };

        if (folderId == MediaFolderModel.ALL_FOLDER_ID) {
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

        if (folderId == MediaFolderModel.ALL_FOLDER_ID) {
            selectionArgs = new String[]{
                    String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                    String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
            };
        } else {
            selectionArgs = new String[]{
                    String.valueOf(folderId),
                    String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                    String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
            };
        }

        sortClause = MediaStore.Files.FileColumns._ID + " DESC";
    }

    @Override
    public void queryBufferedList(Consumer<List<MediaFileModel>> consumer) {
        Cursor c = context.getContentResolver().query(uri, columns, selectionClause, selectionArgs, sortClause);
        if (c != null) {
            if (c.moveToFirst()) {
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
                        .subscribe(consumer::accept);
            }
            c.close();
        }
    }
}
