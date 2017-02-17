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
public class MediaVideoFileRepository implements MediaFileRepository {
    private final static Uri uri = MediaStore.Files.getContentUri("external");
    private final Context context;
    private final String[] columns;
    private final String selectionClause;
    private final String[] selectionArgs;
    private final String sortClause;

    public MediaVideoFileRepository(Context context, int folderId) {
        this.context = context;

        columns = new String[]{
                MediaStore.Video.VideoColumns._ID,
                MediaStore.Video.VideoColumns.DISPLAY_NAME,
                MediaStore.Video.VideoColumns.DATA
        };

        if (folderId != MediaFolderModel.ALL_FOLDER_ID) {
            selectionClause = String.format("%s = ?", MediaStore.Video.VideoColumns.BUCKET_ID);
            selectionArgs = new String[]{String.valueOf(folderId)};
        } else {
            selectionClause = null;
            selectionArgs = null;
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
                        model.setMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
                        model.setPath(CursorUtils.getString(c, columns[2]));

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
