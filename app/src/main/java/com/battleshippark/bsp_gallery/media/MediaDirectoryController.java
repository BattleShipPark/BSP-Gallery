package com.battleshippark.bsp_gallery.media;

import android.content.Context;

import com.battleshippark.bsp_gallery.activity.folders.FoldersModel;

import java.util.List;

/**
 */
public abstract class MediaDirectoryController {
    final Context context;

    public MediaDirectoryController(Context context) {
        this.context = context;
    }

    public static MediaDirectoryController create(Context context, MediaMode mediaMode) {
        switch (mediaMode) {
            case ALL:
                return new MediaAllDirectoryController(context);
            case IMAGE:
                return new MediaImageDirectoryController(context);
            case VIDEO:
                return new MediaVideoDirectoryController(context);
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * 디렉토리 구조를 가져온다.
     *
     * @return ID와 이름만 유효하다
     */
    abstract List<MediaDirectoryModel> getMediaDirectoryList();

    /**
     * 디렉토리에 파일 갯수를 추가한다
     */
    abstract List<MediaDirectoryModel> addMediaFileCount(List<MediaDirectoryModel> dirs);

    /**
     * 디렉토리에 가장 최근 파일의 ID를 추가한다
     */
    abstract List<MediaDirectoryModel> addMediaFileId(List<MediaDirectoryModel> dirs);

    /**
     * 디렉토리에 가장 최근 파일의 손톱 이미지 경로를 추가한다
     */
    abstract List<MediaDirectoryModel> addMediaThumbPath(List<MediaDirectoryModel> dirs);
}
