package com.battleshippark.bsp_gallery.domain.folders;

import com.battleshippark.bsp_gallery.data.media.MediaFolderRepository;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;


/**
 */
public class MediaFolderControllerTest {
    @Test
    public void addList_입력이비어있을때() throws Exception {
        MediaFolderModel model = new MediaFolderModel(1, 2, "path1", "name1", 3, 4);
        MediaFolderRepository repository = new MediaFolderRepository() {
            @Override
            public Observable<MediaFolderModel> queryList() {
                return Observable.just(model);
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
        assertThat(result).isEqualTo(Collections.singletonList(model));
    }
}