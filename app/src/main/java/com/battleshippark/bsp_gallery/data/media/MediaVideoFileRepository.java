package com.battleshippark.bsp_gallery.data.media;

import android.content.Context;
import android.provider.MediaStore;

import com.battleshippark.bsp_gallery.media.MediaFolderModel;

/**
 */
public class MediaVideoFileRepository extends AbstractMediaFileRepository {
    public MediaVideoFileRepository(Context context, int folderId) {
        super(context, folderId);

        columns = new String[]{
                MediaStore.Video.VideoColumns._ID,
                MediaStore.Video.VideoColumns.DISPLAY_NAME,
                MediaStore.Video.VideoColumns.DATA
        };

        if (folderId != MediaFolderModel.ALL_FOLDER_ID) {
            selectionClause = String.format("%s = ?", MediaStore.Video.VideoColumns.BUCKET_ID);
            selectionArgs = new String[]{String.valueOf(folderId)};
        }

        sortClause = MediaStore.Files.FileColumns._ID + " DESC";
    }
}
