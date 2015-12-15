package com.battleshippark.bsp_gallery.media.folder;

import android.content.Context;

import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;

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
     * 폴더 구조를 가져온다. mediaFolderModels 파라미터가 All 폴더를 가지고 있을 수 있고,
     * 기존 폴더 는 파일 갯수나 썸네일 등을 가지고 있으므로 기존에 없던 폴더만 선택적으로 추가한다
     *
     * @return ID와 이름만 유효하다
     */
    public List<MediaFolderModel> queryMediaFolderList(List<MediaFolderModel> mediaFolderModels) {
        Map<Integer, MediaFolderModel> map = new HashMap<>();
        if (!mediaFolderModels.isEmpty()) {
            for (MediaFolderModel mediaFolderModel : mediaFolderModels) {
                map.put(mediaFolderModel.getId(), mediaFolderModel);
            }
        }

        Observable.create((Observable.OnSubscribe<MediaFolderModel>) subscriber -> {
            queryMediaFolderAndOnNext(subscriber);
            subscriber.onCompleted();
        }).subscribe(new Subscriber<MediaFolderModel>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(MediaFolderModel mediaFolderModel) {
                if (!map.containsKey(mediaFolderModel.getId()))
                    map.put(mediaFolderModel.getId(), mediaFolderModel);
            }
        });

        List<MediaFolderModel> result = new ArrayList<>(map.values());
        Collections.sort(result, (lhs, rhs) -> {
            if (lhs.getId() == MediaFolderModel.ALL_DIR_ID) return -1;
            if (rhs.getId() == MediaFolderModel.ALL_DIR_ID) return 1;
            return lhs.getId() - rhs.getId();
        });

        return result;

    }

    protected abstract void queryMediaFolderAndOnNext(Subscriber<? super MediaFolderModel> subscriber);

    /**
     * 디렉토리에 파일 갯수를 추가한다
     */
    public List<MediaFolderModel> addMediaFileCount(List<MediaFolderModel> mediaFolderModels) {
        List<MediaFolderModel> result = new ArrayList<>();

        for (MediaFolderModel mediaFolderModel : mediaFolderModels) {
            if (mediaFolderModel.getId() == MediaFolderModel.ALL_DIR_ID) {
                result.add(MediaFolderModel.copy(mediaFolderModel));
                continue;
            }

            MediaFolderModel newMediaFolderModel = null;
            try {
                newMediaFolderModel = queryMediaFileCount(mediaFolderModel);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (newMediaFolderModel != null) {
                result.add(newMediaFolderModel);
            }
        }

        return result;

    }

    protected abstract MediaFolderModel queryMediaFileCount(MediaFolderModel mediaFolderModel) throws IOException;

    /**
     * 디렉토리에 가장 최근 파일의 ID를 추가한다
     */
    public abstract List<MediaFolderModel> addMediaFileId(List<MediaFolderModel> folders);

    /**
     * 디렉토리에 가장 최근 파일의 손톱 이미지 경로를 추가한다
     */
    public abstract List<MediaFolderModel> addMediaThumbPath(List<MediaFolderModel> folders);
}
