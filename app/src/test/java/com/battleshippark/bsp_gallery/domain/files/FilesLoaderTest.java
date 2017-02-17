package com.battleshippark.bsp_gallery.domain.files;

import com.annimon.stream.function.Consumer;
import com.battleshippark.bsp_gallery.data.media.MediaFolderRepository;
import com.battleshippark.bsp_gallery.domain.MediaControllerFactory;
import com.battleshippark.bsp_gallery.domain.UseCase;
import com.battleshippark.bsp_gallery.media.MediaFileModel;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import rx.internal.schedulers.ImmediateScheduler;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class FilesLoaderTest {
    @Mock
    MediaFolderRepository folderRepository;

    @Mock
    MediaControllerFactory mediaControllerFactory;

    MediaFilesController filesController;

    @Test
    public void execute() throws Exception {
        MediaFileModel mediaFileModel1 = new MediaFileModel(1, "name1", 2, "path1", "thumb1");
        MediaFileModel mediaFileModel2 = new MediaFileModel(2, "name2", 3, "path2", "thumb2");

        UseCase<Void, List<MediaFileModel>> loader = new FilesLoader(mediaControllerFactory,
                ImmediateScheduler.INSTANCE, ImmediateScheduler.INSTANCE,
                MediaFilterMode.ALL, 1234);

        filesController = new MediaFilesController(null) {
            @Override
            public void loadBufferedList(Consumer<List<MediaFileModel>> consumer) {
                consumer.accept(Collections.singletonList(mediaFileModel1));
                consumer.accept(Collections.singletonList(mediaFileModel2));
            }
        };
        when(mediaControllerFactory.createFilesController(any(), anyInt())).thenReturn(filesController);

        TestSubscriber subscriber = new TestSubscriber();
        loader.execute(null, subscriber);

        List<List<MediaFileModel>> list = subscriber.getOnNextEvents();
        assertThat(list).hasSize(2);
        assertThat(list.get(1)).isEqualTo(Arrays.asList(mediaFileModel1, mediaFileModel2));
        subscriber.assertCompleted();
    }

}