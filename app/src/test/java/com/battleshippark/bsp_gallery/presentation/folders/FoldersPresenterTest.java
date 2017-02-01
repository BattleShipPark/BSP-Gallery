package com.battleshippark.bsp_gallery.presentation.folders;

import com.battleshippark.bsp_gallery.data.cache.CacheControllerFactory;
import com.battleshippark.bsp_gallery.data.media.MediaFolderRepository;
import com.battleshippark.bsp_gallery.data.mode.MediaFilterModeRepository;
import com.battleshippark.bsp_gallery.domain.MediaControllerFactory;
import com.battleshippark.bsp_gallery.domain.folders.MediaFolderController;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import rx.schedulers.TestScheduler;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class FoldersPresenterTest {
    @Mock
    FoldersView foldersView;

    @Mock
    MediaFilterModeRepository filterModeRepository;

    @Mock
    MediaFolderRepository folderRepository;

    @Mock
    MediaControllerFactory controllerFactory;

    @Test
    public void load() throws IOException {
        FoldersPresenter presenter = new FoldersPresenter(foldersView,
                filterModeRepository,
                controllerFactory,
                new CacheControllerFactory(), new TestScheduler(), new TestScheduler());

        when(filterModeRepository.load()).thenReturn(null);
        when(folderRepository.queryList()).thenReturn(null);
        when(folderRepository.queryFileCount(0)).thenReturn(0);
        when(folderRepository.queryCoverFile(0)).thenReturn(null);
        when(controllerFactory.createFolderController(null)).thenReturn(new MediaFolderController(folderRepository));

        presenter.load(new FoldersPresenter.FoldersSubscriber(foldersView));


        verify(foldersView).hideProgress();
    }
}