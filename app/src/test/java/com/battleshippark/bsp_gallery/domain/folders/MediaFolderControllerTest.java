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
    public void addList_InputIsEmpty() throws Exception {
        MediaFolderModel queriedModel = new MediaFolderModel(1, 2, 3, "path1", "name1", 4);
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
    public void addList_InputIsOverlapped() throws Exception {
        List<MediaFolderModel> queriedModels = Arrays.asList(
                new MediaFolderModel(1, 0, 0, null, "name_1", 0),
                new MediaFolderModel(2, 0, 0, null, null, 0));

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
                new MediaFolderModel(0, 1, 2, "path0", "name0", 4),
                new MediaFolderModel(1, 2, 3, "path0", "name1", 5));

        List<MediaFolderModel> result = controller.addList(cachedModels);
        assertThat(result).hasSize(3);
        assertThat(result.get(0)).isEqualTo(cachedModels.get(0));
        assertThat(result.get(1).getCoverMediaId()).isEqualTo(2);
        assertThat(result.get(1).getCoverMediaType()).isEqualTo(3);
        assertThat(result.get(1).getCoverThumbPath()).isEqualTo("path0");
        assertThat(result.get(1).getName()).isEqualTo("name_1");
        assertThat(result.get(1).getCount()).isEqualTo(5);
        assertThat(result.get(2)).isEqualTo(queriedModels.get(1));
    }

    @Test
    public void addFileCount_InputIsEmpty() throws Exception {
        MediaFolderController controller = new MediaFolderController(null);
        List<MediaFolderModel> models = Collections.emptyList();

        List<MediaFolderModel> result = controller.addFileCount(models);
        assertThat(result).isEmpty();
    }

    @Test
    public void addFileCount() throws Exception {
        MediaFolderRepository repository = new MediaFolderRepository() {
            @Override
            public Observable<MediaFolderModel> queryList() {
                return null;
            }

            @Override
            public int queryFileCount(int folderId) throws IOException {
                switch (folderId) {
                    case 1:
                        return 40;
                }
                return -1;
            }

            @Override
            public MediaFolderModel queryCoverFile(int folderId) throws IOException {
                return null;
            }
        };
        MediaFolderController controller = new MediaFolderController(repository);
        List<MediaFolderModel> models = Arrays.asList(
                new MediaFolderModel(0, 1, 2, "path0", "name0", 3),
                new MediaFolderModel(1, 2, 3, "path1", "name1", 4));

        List<MediaFolderModel> result = controller.addFileCount(models);
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(0);
        assertThat(result.get(0).getCount()).isEqualTo(3);
        assertThat(result.get(1).getId()).isEqualTo(1);
        assertThat(result.get(1).getCount()).isEqualTo(40);
    }

    @Test
    public void addCoverFile_InputIsEmpty() throws Exception {
        MediaFolderController controller = new MediaFolderController(null);
        List<MediaFolderModel> models = Collections.emptyList();

        List<MediaFolderModel> result = controller.addCoverFile(models);
        assertThat(result).isEmpty();
    }

    @Test
    public void addCoverFile() throws Exception {
        MediaFolderRepository repository = new MediaFolderRepository() {
            @Override
            public Observable<MediaFolderModel> queryList() {
                return null;
            }

            @Override
            public int queryFileCount(int folderId) throws IOException {
                return 0;
            }

            @Override
            public MediaFolderModel queryCoverFile(int folderId) throws IOException {
                switch (folderId) {
                    case 1:
                        return new MediaFolderModel(1, 20, 30, "path1", "name1", 4);
                }
                return null;
            }
        };
        MediaFolderController controller = new MediaFolderController(repository);
        List<MediaFolderModel> models = Arrays.asList(
                new MediaFolderModel(0, 1, 2, "path0", "name0", 3),
                new MediaFolderModel(1, 2, 3, "path1", "name1", 4));

        List<MediaFolderModel> result = controller.addCoverFile(models);
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(models.get(0));
        assertThat(result.get(1).getId()).isEqualTo(1);
        assertThat(result.get(1).getCoverMediaId()).isEqualTo(20);
        assertThat(result.get(1).getCoverMediaType()).isEqualTo(30);
        assertThat(result.get(1).getCoverThumbPath()).isEqualTo("path1");
    }

    @Test
    public void addAllFolder_InputIsEmpty() throws Exception {
        MediaFolderController controller = new MediaFolderController(null);
        List<MediaFolderModel> models = Collections.emptyList();

        List<MediaFolderModel> result = controller.addAllFolder(models);
        assertThat(result).isEmpty();
    }

    @Test
    public void addAllFolder() throws Exception {
        MediaFolderController controller = new MediaFolderController(null);
        List<MediaFolderModel> models = Arrays.asList(
                new MediaFolderModel(0, 1, 2, "path0", "name0", 4),
                new MediaFolderModel(1, 2, 3, "path1", "name1", 4),
                new MediaFolderModel(2, 3, 4, "path2", "name2", 5));

        List<MediaFolderModel> result = controller.addAllFolder(models);
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getId()).isEqualTo(0);
        assertThat(result.get(0).getCount()).isEqualTo(9);
        assertThat(result.get(0).getCoverMediaId()).isEqualTo(3);
        assertThat(result.get(0).getCoverMediaType()).isEqualTo(4);
        assertThat(result.get(0).getCoverThumbPath()).isEqualTo("path2");
    }
}