package com.battleshippark.bsp_gallery.domain.folders;

import com.battleshippark.bsp_gallery.data.media.MediaFolderRepository;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;


/**
 */
public class MediaFolderControllerTest {
    @Test
    public void addList_입력이비어있을때() throws Exception {
        MediaFolderModel queriedModel = new MediaFolderModel(1, 2, "path1", "name1", 3, 4);
        MediaFolderRepository repository = new MediaFolderRepository() {
            @Override
            public Observable<MediaFolderModel> queryList() {
                return Observable.just(queriedModel);
            }

            @Override
            public int queryFileCount(int folderId) throws IOException {
                return 0;
            }

            @Override
            public MediaFolderModel queryCoverFile(int folderId) throws IOException {
                return null;
            }
        };
        MediaFolderController controller = new MediaFolderController(repository);

        List<MediaFolderModel> result = controller.addList(Collections.emptyList());
        assertThat(result).isEqualTo(Collections.singletonList(queriedModel));
    }

    @Test
    public void addList_입력과일부겹칠때() throws Exception {
        List<MediaFolderModel> queriedModels = Arrays.asList(
                new MediaFolderModel(2, 3, "path_2", "name_2", 4, 5),
                new MediaFolderModel(3, 4, "path_3", "name_3", 5, 6));

        MediaFolderRepository repository = new MediaFolderRepository() {
            @Override
            public Observable<MediaFolderModel> queryList() {
                return Observable.from(queriedModels);
            }

            @Override
            public int queryFileCount(int folderId) throws IOException {
                return 0;
            }

            @Override
            public MediaFolderModel queryCoverFile(int folderId) throws IOException {
                return null;
            }
        };
        MediaFolderController controller = new MediaFolderController(repository);
        List<MediaFolderModel> cachedModels = Arrays.asList(
                new MediaFolderModel(1, 2, "path1", "name1", 3, 4),
                new MediaFolderModel(2, 3, "path2", "name2", 4, 5));

        List<MediaFolderModel> result = controller.addList(cachedModels);
        assertThat(result).isEqualTo(
                Arrays.asList(cachedModels.get(0), queriedModels.get(0), queriedModels.get(1)));
    }
}