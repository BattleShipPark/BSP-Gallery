package com.battleshippark.bsp_gallery.media;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.battleshippark.bsp_gallery.Events;
import com.battleshippark.bsp_gallery.media.folder.MediaFolderController;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CountDownLatch;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 */
@RunWith(AndroidJUnit4.class)
public class MediaControllerTest {
    @Mock
    MediaFolderController folderController;

    @Captor
    private ArgumentCaptor<Events.OnMediaFolderListUpdated> captor;

    private Bus eventBus;

    private CountDownLatch latch;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        eventBus = new Bus();
        eventBus.register(this);

        latch = new CountDownLatch(1);
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
    public void testRefreshFolderList() throws Exception {
        Bus spyBus = spy(eventBus);

        MediaController controller = new MediaController(null, eventBus);
        controller.refreshFolderList(null, folderController);

        latch.await();

        verify(spyBus).post(Events.OnMediaFolderListUpdated.FINISHED);
//        verify(eventBus).post(captor.capture());
//        Log.w("TEST", captor.getValue().name());
//        System.out.println(captor.getValue().name());
    }
}