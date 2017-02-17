package com.battleshippark.bsp_gallery.data.media;

import android.content.Context;
import android.provider.MediaStore;

import com.battleshippark.bsp_gallery.media.MediaFolderModel;

/**
 */
public class MediaAllFileRepository extends AbstractMediaFileRepository {
    public MediaAllFileRepository(Context context, int folderId) {
        super(context, folderId);

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
}
