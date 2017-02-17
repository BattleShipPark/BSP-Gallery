package com.battleshippark.bsp_gallery.domain.folders;

import com.battleshippark.bsp_gallery.data.cache.CacheController;
import com.battleshippark.bsp_gallery.data.cache.CacheControllerFactory;
import com.battleshippark.bsp_gallery.data.media.MediaFolderRepository;
import com.battleshippark.bsp_gallery.domain.MediaControllerFactory;
import com.battleshippark.bsp_gallery.domain.UseCase;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import rx.internal.schedulers.ImmediateScheduler;
import rx.observers.TestSubscriber;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class FoldersLoaderTest {
    @Mock
    MediaFolderRepository folderRepository;

    @Mock
    MediaControllerFactory mediaControllerFactory;

    @Mock
    MediaFolderController folderController;

    @Mock
    CacheController cacheController;

    @Mock
    CacheControllerFactory cacheControllerFactory;

    @Test
    public void execute() throws Exception {
        UseCase<MediaFilterMode, List<MediaFolderModel>> loader = new FoldersLoader(mediaControllerFactory,
                cacheControllerFactory, ImmediateScheduler.INSTANCE, ImmediateScheduler.INSTANCE);

        when(folderController.addList(any())).thenReturn(Collections.emptyList());
        when(folderController.addFileCount(any())).thenReturn(Collections.emptyList());
        when(folderController.addCoverFile(any())).thenReturn(Collections.emptyList());
        when(folderController.addAllFolder(any())).thenReturn(Collections.emptyList());
        when(mediaControllerFactory.createFolderController(any())).thenReturn(folderController);
        when(cacheController.readCache(any())).thenReturn(Collections.emptyList());
        when(cacheControllerFactory.create()).thenReturn(cacheController);

        TestSubscriber subscriber = new TestSubscriber();
        loader.execute(MediaFilterMode.ALL, subscriber);

        subscriber.assertCompleted();
    }

}