package com.battleshippark.bsp_gallery.domain.folders;

import android.annotation.SuppressLint;
import android.util.ArraySet;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.BiFunction;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.UnaryOperator;
import com.battleshippark.bsp_gallery.data.media.MediaFolderRepository;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import rx.Observable;

/**
 */
@AllArgsConstructor
public class MediaFolderController {
    private final MediaFolderRepository mediaRepository;

    /**
     * 폴더 구조를 가져온다. cachedModels 파라미터가 All 폴더를 가지고 있을 수 있고,
     * 기존 폴더 는 파일 갯수나 썸네일 등을 가지고 있으므로 기존에 없던 폴더만 선택적으로 추가한다
     *
     * @return ID와 이름만 유효하다
     */
    public List<MediaFolderModel> addList(List<MediaFolderModel> cachedModels) {
        Set<Integer> idSet = Stream.of(cachedModels)
                .map(MediaFolderModel::getId)
                .collect(Collectors.toSet());

        Map<Integer, MediaFolderModel> cachedMap = Stream.of(cachedModels)
                .collect(Collectors.toMap(MediaFolderModel::getId, UnaryOperator.Util.identity()));

        @SuppressLint("UseSparseArrays") Map<Integer, MediaFolderModel> queriedMap = new HashMap<>();
        mediaRepository.queryList().subscribe(mediaFolderModel -> {
            queriedMap.put(mediaFolderModel.getId(), mediaFolderModel);
            idSet.add(mediaFolderModel.getId());
        }, Throwable::printStackTrace);

        return Stream.of(idSet).filter(id -> queriedMap.containsKey(id) || cachedMap.containsKey(id))
                .map(id -> {
                    if (queriedMap.containsKey(id)) {
                        return queriedMap.get(id);
                    }
                    if (cachedMap.containsKey(id)) {
                        return cachedMap.get(id);
                    }
                    throw new IllegalArgumentException();
                }).sorted((lhs, rhs) -> {
                    if (lhs.getId() == MediaFolderModel.ALL_DIR_ID) return -1;
                    if (rhs.getId() == MediaFolderModel.ALL_DIR_ID) return 1;
                    return lhs.getId() - rhs.getId();
                }).collect(Collectors.toList());
    }

    interface IOExceptionFunc1<T1, R> {
        R call(T1 t1) throws IOException;
    }

    /**
     * 디렉토리에 파일 갯수를 추가한다
     */
    public List<MediaFolderModel> addFileCount(List<MediaFolderModel> mediaFolderModels) {
        List<MediaFolderModel> result = new ArrayList<>();

        for (MediaFolderModel mediaFolderModel : mediaFolderModels) {
            if (mediaFolderModel.getId() == MediaFolderModel.ALL_DIR_ID) {
                result.add(mediaFolderModel.copy());
                continue;
            }

            MediaFolderModel newMediaFolderModel = mediaFolderModel.copy();
            try {
                newMediaFolderModel.setCount(mediaRepository.queryFileCount(mediaFolderModel.getId()));
                result.add(newMediaFolderModel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
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

    /**
     * 디렉토리에 가장 최근 파일의 ID를 추가한다
     */
    public List<MediaFolderModel> addCoverFile(List<MediaFolderModel> mediaFolderModels) {
        List<MediaFolderModel> result = new ArrayList<>();

        for (MediaFolderModel mediaFolderModel : mediaFolderModels) {
            if (mediaFolderModel.getId() == MediaFolderModel.ALL_DIR_ID) {
                result.add(mediaFolderModel.copy());
                continue;
            }

            MediaFolderModel newMediaFolderModel = mediaFolderModel.copy();
            try {
                MediaFolderModel folderModel = mediaRepository.queryCoverFile(mediaFolderModel.getId());
                newMediaFolderModel.setCoverMediaId(folderModel.getCoverMediaId());
                newMediaFolderModel.setCoverMediaType(folderModel.getCoverMediaType());
                result.add(newMediaFolderModel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

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
