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
public class MediaImageFileRepository extends AbstractMediaFileRepository {
    public MediaImageFileRepository(Context context, int folderId) {
        super(context, folderId);

        columns = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATA
        };

        if (folderId != MediaFolderModel.ALL_FOLDER_ID) {
            selectionClause = String.format("%s = ?", MediaStore.Images.ImageColumns.BUCKET_ID);
        } else {
            selectionClause = null;
        }

        if (folderId != MediaFolderModel.ALL_FOLDER_ID) {
            selectionArgs = new String[]{String.valueOf(folderId),};
        } else {
            selectionArgs = null;
        }

        sortClause = MediaStore.Files.FileColumns._ID + " DESC";
    }
}
