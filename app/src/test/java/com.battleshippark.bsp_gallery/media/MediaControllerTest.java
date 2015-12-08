package com.battleshippark.bsp_gallery.media;

import com.battleshippark.bsp_gallery.Events;
import com.battleshippark.bsp_gallery.media.folder.MediaFolderController;
import com.squareup.otto.Bus;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

/**
 */
public class MediaControllerTest {
    @Mock
    Bus eventBus;

    @Mock
    MediaFolderController folderController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRefreshFolderList() throws Exception {

        MediaController controller = new MediaController(null, eventBus);
        controller.refreshFolderList(null, folderController);

        verify(eventBus).post(Events.OnMediaFolderListUpdated.FINISHED);
    }
}