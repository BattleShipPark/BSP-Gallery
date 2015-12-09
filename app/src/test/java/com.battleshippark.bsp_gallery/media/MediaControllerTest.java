package com.battleshippark.bsp_gallery.media;

import android.util.Log;

import com.battleshippark.bsp_gallery.Events;
import com.battleshippark.bsp_gallery.cache.CacheController;
import com.battleshippark.bsp_gallery.media.folder.MediaFolderController;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
public class MediaControllerTest {
    @Mock
    CacheController cacheController;

    @Mock
    MediaFolderController folderController;

    @Captor
    private ArgumentCaptor<Events.OnMediaFolderListUpdated> captor;

    private Bus eventBus;
    private CountDownLatch latch;

    private List<MediaFolderModel> mediaFolderModels1;
    private List<MediaFolderModel> mediaFolderModels2;
    private List<MediaFolderModel> mediaFolderModels3;
    private List<MediaFolderModel> mediaFolderModels4;
    private List<MediaFolderModel> mediaFolderModels5;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        eventBus = new Bus();
        eventBus.register(this);

        latch = new CountDownLatch(1);

        mediaFolderModels1 = new ArrayList<>();
        mediaFolderModels1.add(new MediaFolderModel(1, 0, "", "4", 5, 6));
        mediaFolderModels1.add(new MediaFolderModel(2, 0, "", "5", 6, 7));

        mediaFolderModels2 = new ArrayList<>();
        mediaFolderModels2.add(new MediaFolderModel(1, 0, "", "4", 1, 6));
        mediaFolderModels2.add(new MediaFolderModel(2, 0, "", "5", 2, 7));

        mediaFolderModels3 = new ArrayList<>();
        mediaFolderModels3.add(new MediaFolderModel(1, 2, "", "4", 1, 6));
        mediaFolderModels3.add(new MediaFolderModel(2, 3, "", "5", 2, 7));

        mediaFolderModels4 = new ArrayList<>();
        mediaFolderModels4.add(new MediaFolderModel(1, 2, "3", "4", 1, 6));
        mediaFolderModels4.add(new MediaFolderModel(2, 3, "4", "5", 2, 7));

        mediaFolderModels5 = new ArrayList<>();
        mediaFolderModels5.add(new MediaFolderModel(0, 3, "4", "All", 3, 7));
        mediaFolderModels5.add(new MediaFolderModel(1, 2, "3", "4", 1, 6));
        mediaFolderModels5.add(new MediaFolderModel(2, 3, "4", "5", 2, 7));
    }

    @After
    public void cleanup() {
        eventBus.unregister(this);
    }

    @Subscribe
    public void on(Events.OnMediaFolderListUpdated event) {
        Log.w("TEST", event.name());
        latch.countDown();
    }

    @Test
    public void testRefreshFolderList_fromEmpty_verifyResult() throws Exception {
        TestSubscriber<List<MediaFolderModel>> testSubscriber = new TestSubscriber<List<MediaFolderModel>>() {
            @Override
            public void onNext(List<MediaFolderModel> mediaFolderModels) {
                super.onNext(mediaFolderModels);
            }
        };

        when(folderController.getMediaDirectoryList()).thenReturn(mediaFolderModels1);
        when(folderController.addMediaFileCount(any())).thenReturn(mediaFolderModels2);
        when(folderController.addMediaFileId(any())).thenReturn(mediaFolderModels3);
        when(folderController.addMediaThumbPath(any())).thenReturn(mediaFolderModels4);

        when(cacheController.readCache(MediaFilterMode.ALL)).thenReturn(new ArrayList<>());

        MediaController controller = new MediaController(null, eventBus);
        controller.refreshFolderList(MediaFilterMode.ALL, folderController, cacheController, testSubscriber, Schedulers.io(), Schedulers.io());

        /* */

        testSubscriber.awaitTerminalEvent();

        testSubscriber.assertReceivedOnNext(Arrays.asList(mediaFolderModels1, mediaFolderModels2, mediaFolderModels3, mediaFolderModels4, mediaFolderModels5));
    }
}