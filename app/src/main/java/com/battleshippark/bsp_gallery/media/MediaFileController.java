package com.battleshippark.bsp_gallery.media;

import android.content.Context;

import java.util.List;

/**
 */
public abstract class MediaFileController {
    final Context context;
    final int dirId;

    public MediaFileController(Context context, int dirId) {
        this.context = context;
        this.dirId = dirId;
    }

    public static MediaFileController create(Context context, int dirId, MediaMode mediaMode) {
        switch (mediaMode) {
            case ALL:
                return new MediaAllFileController(context, dirId);
            case IMAGE:
                return new MediaImageFileController(context, dirId);
            case VIDEO:
                return new MediaVideoFileController(context, dirId);
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * 디렉토리내의 파일 목록을 가져온다.
     */
    abstract List<MediaFileModel> getMediaFileList();

    /**
     * 파일의 손톱 이미지 경로를 추가한다
     */
    abstract List<MediaFileModel> addMediaThumbPath(List<MediaFileModel> files);
}
