package com.battleshippark.bsp_gallery.media.file;

import android.content.Context;

import com.battleshippark.bsp_gallery.media.MediaFileModel;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;

import java.util.List;

import rx.Subscriber;

/**
 */
public abstract class MediaFileController {
    static final int BUFFER_COUNT = 100;
    final Context context;
    final int dirId;

    public MediaFileController(Context context, int dirId) {
        this.context = context;
        this.dirId = dirId;
    }

    public static MediaFileController create(Context context, int dirId, MediaFilterMode mediaFilterMode) {
        switch (mediaFilterMode) {
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
    public abstract List<MediaFileModel> getMediaFileList();

    /**
     * 디렉토리내의 파일 목록을 가져온다. n건씩 끊어서 넘어온다
     */
    public abstract void getMediaFileList(Subscriber<? super List<MediaFileModel>> subscriber);

    /**
     * 파일의 손톱 이미지 경로를 추가한다
     */
    public abstract List<MediaFileModel> addMediaThumbPath(List<MediaFileModel> files);
}
