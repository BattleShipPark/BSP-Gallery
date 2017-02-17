package com.battleshippark.bsp_gallery.data.media;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.annimon.stream.function.Consumer;
import com.battleshippark.bsp_gallery.CursorUtils;
import com.battleshippark.bsp_gallery.media.MediaFileModel;

import java.util.List;

import rx.Observable;

/**
 */
public abstract class AbstractMediaFileRepository implements MediaFileRepository {
    protected final static Uri uri = MediaStore.Files.getContentUri("external");
    protected final Context context;
    protected final int folderId;
    protected String[] columns;
    protected String selectionClause;
    protected String[] selectionArgs;
    protected String sortClause;

    public AbstractMediaFileRepository(Context context, int folderId) {
        this.context = context;
        this.folderId = folderId;
    }

    @Override
    public void queryBufferedList(Consumer<List<MediaFileModel>> consumer) {
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
                    .subscribe(consumer::accept);
        }
    }
}
