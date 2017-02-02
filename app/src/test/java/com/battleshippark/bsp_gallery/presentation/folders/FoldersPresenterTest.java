package com.battleshippark.bsp_gallery.presentation.folders;

import com.battleshippark.bsp_gallery.data.cache.CacheControllerFactory;
import com.battleshippark.bsp_gallery.data.media.MediaFolderRepository;
import com.battleshippark.bsp_gallery.data.mode.MediaFilterModeRepository;
import com.battleshippark.bsp_gallery.domain.Loader;
import com.battleshippark.bsp_gallery.domain.MediaControllerFactory;
import com.battleshippark.bsp_gallery.domain.folders.FilterModeLoader;
import com.battleshippark.bsp_gallery.domain.folders.FoldersLoader;
import com.battleshippark.bsp_gallery.domain.folders.MediaFolderController;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import rx.Scheduler;
import rx.Subscriber;
import rx.internal.schedulers.ImmediateScheduler;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class FoldersPresenterTest {
    @Mock
    FoldersView foldersView;

    @Mock
    MediaFilterModeRepository mediaFilterModeRepository;

    @Mock
    MediaFolderRepository folderRepository;

    @Mock
    MediaControllerFactory mediaControllerFactory;

    Scheduler scheduler = ImmediateScheduler.INSTANCE, postScheduler = ImmediateScheduler.INSTANCE;

    @Test
    public void loadList() throws IOException {
        FilterModeLoader filerModeLoader = new FilterModeLoader(mediaFilterModeRepository, scheduler, postScheduler);
        FoldersLoader foldersLoader = new FoldersLoader(mediaFilterModeRepository, mediaControllerFactory,
                new CacheControllerFactory(), scheduler, postScheduler);
        FoldersPresenter presenter = new FoldersPresenter(foldersView, filerModeLoader, foldersLoader);

        when(mediaFilterModeRepository.load()).thenReturn(null);
        when(folderRepository.queryList()).thenReturn(null);
        when(folderRepository.queryFileCount(0)).thenReturn(0);
        when(folderRepository.queryCoverFile(0)).thenReturn(null);
        when(mediaControllerFactory.createFolderController(null)).thenReturn(new MediaFolderController(folderRepository));

        presenter.loadList(MediaFilterMode.ALL, new FoldersPresenter.FoldersSubscriber(foldersView));


        verify(foldersView).hideProgress();
    }

    @Test
    public void loadList_withMode() throws IOException {
        List<MediaFolderModel> folderModelList = Collections.singletonList(new MediaFolderModel(1, 2, 3, "a", "b", 4));
        Loader<List<MediaFolderModel>> foldersLoader = new FoldersLoader(null, null, null, null, null) {
            @Override
            public void execute(Subscriber<List<MediaFolderModel>> subscriber) {
                subscriber.onNext(folderModelList);
                subscriber.onCompleted();
            }
        };
        FoldersPresenter presenter = new FoldersPresenter(foldersView, null, foldersLoader);

        presenter.loadList(MediaFilterMode.ALL, new FoldersPresenter.FoldersSubscriber(foldersView));


        verify(foldersView).refreshList(folderModelList);
        verify(foldersView).hideProgress();
    }

    @Test
    public void loadFilterMode() throws IOException {
        Loader<MediaFilterMode> filerModeLoader = subscriber -> {
            subscriber.onNext(MediaFilterMode.ALL);
            subscriber.onCompleted();
        };

        FoldersPresenter presenter = new FoldersPresenter(foldersView, filerModeLoader, null);

        presenter.loadFilterMode();


        verify(foldersView).updateFilterMode(MediaFilterMode.ALL);
        verify(foldersView).refreshList();
    }
}