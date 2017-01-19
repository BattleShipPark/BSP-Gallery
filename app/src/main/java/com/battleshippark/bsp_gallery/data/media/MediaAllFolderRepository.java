package com.battleshippark.bsp_gallery.data.media;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.battleshippark.bsp_gallery.CursorUtils;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import lombok.AllArgsConstructor;
import rx.Observable;

/**
 */
@AllArgsConstructor
public class MediaAllFolderRepository implements MediaFolderRepository {
    private final static Uri uri = MediaStore.Files.getContentUri("external");
    private Context context;

    @Override
    public Observable<MediaFolderModel> queryFolderList() {
        return Observable.create(subscriber -> {
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

            Uri distinctUri = uri.buildUpon().appendQueryParameter("distinct", "true").build();
            Cursor c = context.getContentResolver().query(distinctUri, columns, selectionClause, selectionArgs, null);
            try {
                if (c != null && c.moveToFirst()) {
                    do {
                        MediaFolderModel model = new MediaFolderModel();
                        model.setId(CursorUtils.getInt(c, columns[0]));
                        model.setName(CursorUtils.getString(c, columns[1]));

                        subscriber.onNext(model);
                    } while (c.moveToNext());
                }
            } catch (Exception e) {
                subscriber.onError(e);
            } finally {
                if (c != null) {
                    c.close();
                }
            }
        });
    }
}
