package com.battleshippark.bsp_gallery.media.folder;

import android.content.Context;

import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import java.util.List;

/**
 */
public abstract class MediaFolderController {
    final Context context;

    public MediaFolderController(Context context) {
        this.context = context;
    }

    public static MediaFolderController create(Context context, MediaFilterMode mediaFilterMode) {
        switch (mediaFilterMode) {
            case ALL:
                return new MediaAllFolderController(context);
            case IMAGE:
                return new MediaImageFolderController(context);
            case VIDEO:
                return new MediaVideoFolderController(context);
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * 디렉토리 구조를 가져온다.
     *
     * @return ID와 이름만 유효하다
     */
    public abstract List<MediaFolderModel> getMediaDirectoryList(List<MediaFolderModel> folders);

    /**
     * 디렉토리에 파일 갯수를 추가한다
     */
    public abstract List<MediaFolderModel> addMediaFileCount(List<MediaFolderModel> folders);

    /**
     * 디렉토리에 가장 최근 파일의 ID를 추가한다
     */
    public abstract List<MediaFolderModel> addMediaFileId(List<MediaFolderModel> folders);

    /**
     * 디렉토리에 가장 최근 파일의 손톱 이미지 경로를 추가한다
     */
    public abstract List<MediaFolderModel> addMediaThumbPath(List<MediaFolderModel> folders);
}
