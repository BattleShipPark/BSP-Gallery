package com.battleshippark.bsp_gallery.presentation.folders;

import com.battleshippark.bsp_gallery.data.cache.CacheControllerFactory;
import com.battleshippark.bsp_gallery.data.media.MediaFolderRepository;
import com.battleshippark.bsp_gallery.data.mode.MediaFilterModeRepository;
import com.battleshippark.bsp_gallery.domain.MediaControllerFactory;
import com.battleshippark.bsp_gallery.domain.folders.MediaFolderController;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import rx.internal.schedulers.ImmediateScheduler;
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
    public void loadList() throws IOException {
        FoldersPresenter presenter = new FoldersPresenter(foldersView,
                filterModeRepository,
                controllerFactory,
                new CacheControllerFactory(), ImmediateScheduler.INSTANCE, ImmediateScheduler.INSTANCE);

        when(filterModeRepository.load()).thenReturn(null);
        when(folderRepository.queryList()).thenReturn(null);
        when(folderRepository.queryFileCount(0)).thenReturn(0);
        when(folderRepository.queryCoverFile(0)).thenReturn(null);
        when(controllerFactory.createFolderController(null)).thenReturn(new MediaFolderController(folderRepository));

        presenter.loadList(MediaFilterMode.ALL, new FoldersPresenter.FoldersSubscriber(foldersView));


        verify(foldersView).hideProgress();
    }

    @Test
    public void loadFilterMode() throws IOException {
        FoldersPresenter presenter = new FoldersPresenter(foldersView,
                filterModeRepository,
                controllerFactory,
                new CacheControllerFactory(), ImmediateScheduler.INSTANCE, ImmediateScheduler.INSTANCE);

        when(filterModeRepository.load()).thenReturn(MediaFilterMode.ALL);

        presenter.loadFilterMode();

        
        verify(foldersView).updateFilterMode(MediaFilterMode.ALL);
        verify(foldersView).refreshList();
    }
}