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

    interface IOExceptionFunc1<T1, R> {
        R call(T1 t1) throws IOException;
    }

    /**
     * 디렉토리에 파일 갯수를 추가한다
     */
    public List<MediaFolderModel> addMediaFileCount(List<MediaFolderModel> mediaFolderModels) {
        return callAndMergeWithAll(mediaFolderModels, this::queryMediaFileCount);
    }

    /**
     * mediaFolderModels에 대해 func 파라미터를 호출해서 필요한 정보를 갱신한다 . All 폴더는 유지한다
     */
    List<MediaFolderModel> callAndMergeWithAll(List<MediaFolderModel> mediaFolderModels, IOExceptionFunc1<MediaFolderModel, MediaFolderModel> func) {
        List<MediaFolderModel> result = new ArrayList<>();

        for (MediaFolderModel mediaFolderModel : mediaFolderModels) {
            if (mediaFolderModel.getId() == MediaFolderModel.ALL_DIR_ID) {
                result.add(mediaFolderModel.copy());
                continue;
            }

            MediaFolderModel newMediaFolderModel = null;
            try {
                newMediaFolderModel = func.call(mediaFolderModel);
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
    public List<MediaFolderModel> addMediaFileId(List<MediaFolderModel> mediaFolderModels) {
        return callAndMergeWithAll(mediaFolderModels, this::queryMediaFileId);
    }

    protected abstract MediaFolderModel queryMediaFileId(MediaFolderModel mediaFolderModel) throws IOException;

    /**
     * 디렉토리에 가장 최근 파일의 손톱 이미지 경로를 추가한다
     */
    public List<MediaFolderModel> addMediaThumbPath(List<MediaFolderModel> mediaFolderModels) {
        return callAndMergeWithAll(mediaFolderModels, this::queryMediaThumbPath);
    }

    protected abstract MediaFolderModel queryMediaThumbPath(MediaFolderModel mediaFolderModel);

    public List<MediaFolderModel> addAllDirectory(List<MediaFolderModel> directories) {
        MediaFolderModel allDir = new MediaFolderModel();

        allDir.setId(MediaFolderModel.ALL_DIR_ID);
        allDir.setName("All");

        if (directories.get(0).getId() == MediaFolderModel.ALL_DIR_ID)
            directories.remove(0);

        int count = Observable.from(directories)
                .map(MediaFolderModel::getCount)
                .reduce((_sum, _count) -> _sum + _count)
                .toBlocking()
                .last();
        allDir.setCount(count);

        MediaFolderModel dir = Observable.from(directories)
                .reduce((_dir1, _dir2) -> {
                    if (_dir1.getCoverMediaId() >= _dir2.getCoverMediaId())
                        return _dir1;
                    else
                        return _dir2;
                })
                .toBlocking()
                .last();
        allDir.setCoverMediaId(dir.getCoverMediaId());
        allDir.setCoverThumbPath(dir.getCoverThumbPath());
        allDir.setCoverMediaType(dir.getCoverMediaType());

        directories.add(0, allDir);
        return directories;
    }
}
