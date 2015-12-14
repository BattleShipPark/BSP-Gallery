package com.battleshippark.bsp_gallery.media;

import com.battleshippark.bsp_gallery.Events;
import com.battleshippark.bsp_gallery.cache.CacheController;
import com.battleshippark.bsp_gallery.media.folder.MediaFolderController;
import com.squareup.otto.Bus;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 */
public class MediaControllerTest {
    @Mock
    CacheController cacheController;

    @Mock
    MediaFolderController folderController;

    @Mock
    Bus eventBus;

    @Captor
    private ArgumentCaptor<Events.OnMediaFolderListUpdated> captor;

    private List<MediaFolderModel> mediaFolderModels1;
    private List<MediaFolderModel> mediaFolderModels2;
    private List<MediaFolderModel> mediaFolderModels3;
    private List<MediaFolderModel> mediaFolderModels4;
    private List<MediaFolderModel> mediaFolderModels5;

    private List<MediaFolderModel> mediaFolderModels_all1;
    private List<MediaFolderModel> mediaFolderModels_all2;
    private List<MediaFolderModel> mediaFolderModels_all3;
    private List<MediaFolderModel> mediaFolderModels_all4;
    private List<MediaFolderModel> mediaFolderModels_all5;
    private List<MediaFolderModel> mediaFolderModels_all6;

    <T> ArrayList<T> createList(T... array) {
        return new ArrayList<>(Arrays.asList(array));
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mediaFolderModels1 = createList(new MediaFolderModel(1, 0, "", "4", 5, 6), new MediaFolderModel(2, 0, "", "5", 6, 7));
        mediaFolderModels2 = createList(new MediaFolderModel(1, 0, "", "4", 1, 6), new MediaFolderModel(2, 0, "", "5", 2, 7));
        mediaFolderModels3 = createList(new MediaFolderModel(1, 2, "", "4", 1, 6), new MediaFolderModel(2, 3, "", "5", 2, 7));
        mediaFolderModels4 = createList(new MediaFolderModel(1, 2, "3", "4", 1, 6), new MediaFolderModel(2, 3, "4", "5", 2, 7));
        mediaFolderModels5 = createList(new MediaFolderModel(0, 3, "4", "All", 3, 7), new MediaFolderModel(1, 2, "3", "4", 1, 6), new MediaFolderModel(2, 3, "4", "5", 2, 7));

        mediaFolderModels_all1 = createList(
                new MediaFolderModel(0, 2, "/b", "All", 5, 2),
                new MediaFolderModel(1, 1, "/a", "A", 2, 1),
                new MediaFolderModel(2, 2, "/b", "B", 3, 2));
        mediaFolderModels_all2 = createList(
                new MediaFolderModel(0, 2, "/a", "All", 5, 0),
                new MediaFolderModel(1, 1, "/a", "A", 2, 1),
                new MediaFolderModel(2, 2, "/b", "B", 3, 2),
                new MediaFolderModel(3, 3, "  ", "C", 0, 3));
        mediaFolderModels_all3 = createList(
                new MediaFolderModel(0, 2, "/a", "All", 5, 0),
                new MediaFolderModel(1, 1, "/a", "A", 2, 1),
                new MediaFolderModel(2, 2, "/b", "B", 3, 2),
                new MediaFolderModel(3, 0, "  ", "C", 1, 3));
        mediaFolderModels_all4 = createList(
                new MediaFolderModel(0, 2, "/a", "All", 5, 0),
                new MediaFolderModel(1, 1, "/a", "A", 2, 1),
                new MediaFolderModel(2, 2, "/b", "B", 3, 2),
                new MediaFolderModel(3, 3, "  ", "C", 1, 3));
        mediaFolderModels_all5 = createList(
                new MediaFolderModel(0, 2, "/a", "All", 5, 0),
                new MediaFolderModel(1, 1, "/a", "A", 2, 1),
                new MediaFolderModel(2, 2, "/b", "B", 3, 2),
                new MediaFolderModel(3, 3, "/c", "C", 1, 3));
        mediaFolderModels_all6 = createList(
                new MediaFolderModel(0, 3, "/c", "All", 6, 3),
                new MediaFolderModel(1, 1, "/a", "A", 2, 1),
                new MediaFolderModel(2, 2, "/b", "B", 3, 2),
                new MediaFolderModel(3, 3, "/c", "C", 1, 3));
    }


    @Test
    public void testRefreshFolderList_fromEmpty_verifyResult() throws Exception {
        TestSubscriber<List<MediaFolderModel>> testSubscriber = new TestSubscriber<List<MediaFolderModel>>() {
            @Override
            public void onNext(List<MediaFolderModel> mediaFolderModels) {
                super.onNext(mediaFolderModels);
            }
        };

        when(cacheController.readCache(MediaFilterMode.ALL)).thenReturn(new ArrayList<>());
        when(folderController.getMediaDirectoryList(any())).thenReturn(mediaFolderModels1);
        when(folderController.addMediaFileCount(any())).thenReturn(mediaFolderModels2);
        when(folderController.addMediaFileId(any())).thenReturn(mediaFolderModels3);
        when(folderController.addMediaThumbPath(any())).thenReturn(mediaFolderModels4);


        MediaController controller = new MediaController(null, eventBus);
        controller.refreshFolderList(MediaFilterMode.ALL, folderController, cacheController, testSubscriber, Schedulers.io(), Schedulers.io());

        /* */

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Arrays.asList(mediaFolderModels1, mediaFolderModels2, mediaFolderModels3, mediaFolderModels4, mediaFolderModels5));
    }

    @Test
    public void testRefreshFolderList_fromCache_verifyResult() throws Exception {
        TestSubscriber<List<MediaFolderModel>> testSubscriber = new TestSubscriber<List<MediaFolderModel>>() {
            @Override
            public void onNext(List<MediaFolderModel> mediaFolderModels) {
                super.onNext(mediaFolderModels);
            }
        };

        when(cacheController.readCache(MediaFilterMode.ALL)).thenReturn(mediaFolderModels_all1);
        when(folderController.getMediaDirectoryList(any())).thenReturn(mediaFolderModels_all2);
        when(folderController.addMediaFileCount(any())).thenReturn(mediaFolderModels_all3);
        when(folderController.addMediaFileId(any())).thenReturn(mediaFolderModels_all4);
        when(folderController.addMediaThumbPath(any())).thenReturn(mediaFolderModels_all5);

        MediaController controller = new MediaController(null, eventBus);
        controller.refreshFolderList(MediaFilterMode.ALL, folderController, cacheController, testSubscriber, Schedulers.io(), Schedulers.io());

        /* */

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Arrays.asList(mediaFolderModels_all1, mediaFolderModels_all2, mediaFolderModels_all3, mediaFolderModels_all4, mediaFolderModels_all5, mediaFolderModels_all6));
    }
}