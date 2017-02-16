package com.battleshippark.bsp_gallery.domain.folders;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.UnaryOperator;
import com.battleshippark.bsp_gallery.data.media.MediaFolderRepository;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;

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
        Set<Integer> cachedIdSet = Stream.of(cachedModels)
                .map(MediaFolderModel::getId)
                .collect(Collectors.toSet());

        Map<Integer, MediaFolderModel> cachedMap = Stream.of(cachedModels)
                .collect(Collectors.toMap(MediaFolderModel::getId, UnaryOperator.Util.identity()));

        List<MediaFolderModel> queriedModels = new ArrayList<>();
        mediaRepository.queryList().subscribe(queriedModels::add, Throwable::printStackTrace);

        Set<Integer> queriedIdSet = Stream.of(queriedModels)
                .map(MediaFolderModel::getId)
                .collect(Collectors.toSet());

        cachedIdSet.removeAll(queriedIdSet);

        Stream.of(cachedIdSet).map(cachedMap::get).forEach(queriedModels::add);

        return Stream.of(queriedModels)
                .sorted((lhs, rhs) -> {
                    if (lhs.getId() == MediaFolderModel.ALL_FOLDER_ID) return -1;
                    if (rhs.getId() == MediaFolderModel.ALL_FOLDER_ID) return 1;
                    return lhs.getId() - rhs.getId();
                }).collect(Collectors.toList());
    }

    /**
     * 디렉토리에 파일 갯수를 추가한다
     */
    public List<MediaFolderModel> addFileCount(List<MediaFolderModel> mediaFolderModels) {
        List<MediaFolderModel> result = new ArrayList<>();

        for (MediaFolderModel mediaFolderModel : mediaFolderModels) {
            if (mediaFolderModel.getId() == MediaFolderModel.ALL_FOLDER_ID) {
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
     * 디렉토리에 가장 최근 파일의 ID를 추가한다
     */
    public List<MediaFolderModel> addCoverFile(List<MediaFolderModel> mediaFolderModels) {
        List<MediaFolderModel> result = new ArrayList<>();

        for (MediaFolderModel mediaFolderModel : mediaFolderModels) {
            if (mediaFolderModel.getId() == MediaFolderModel.ALL_FOLDER_ID) {
                result.add(mediaFolderModel.copy());
                continue;
            }

            MediaFolderModel newMediaFolderModel = mediaFolderModel.copy();
            try {
                MediaFolderModel folderModel = mediaRepository.queryCoverFile(mediaFolderModel.getId());
                newMediaFolderModel.setCoverMediaId(folderModel.getCoverMediaId());
                newMediaFolderModel.setCoverMediaType(folderModel.getCoverMediaType());
                newMediaFolderModel.setCoverThumbPath(folderModel.getCoverThumbPath());
                result.add(newMediaFolderModel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public List<MediaFolderModel> addAllFolder(List<MediaFolderModel> folders) {
        List<MediaFolderModel> result = new ArrayList<>(folders);
        if (!result.isEmpty() && result.get(0).getId() == MediaFolderModel.ALL_FOLDER_ID)
            result.remove(0);
        if (result.isEmpty()) {
            return result;
        }

        MediaFolderModel allFolder = new MediaFolderModel();
        allFolder.setId(MediaFolderModel.ALL_FOLDER_ID);
        allFolder.setName("All");

        int totalCount = Stream.of(result)
                .mapToInt(MediaFolderModel::getCount)
                .sum();
        allFolder.setCount(totalCount);

        MediaFolderModel coverFolder = Stream.of(folders)
                .max((_folder1, _folder2) -> (int) (_folder1.getCoverMediaId() - _folder2.getCoverMediaId()))
                .get();
        allFolder.setCoverMediaId(coverFolder.getCoverMediaId());
        allFolder.setCoverThumbPath(coverFolder.getCoverThumbPath());
        allFolder.setCoverMediaType(coverFolder.getCoverMediaType());

        result.add(0, allFolder);
        return result;
    }
}
