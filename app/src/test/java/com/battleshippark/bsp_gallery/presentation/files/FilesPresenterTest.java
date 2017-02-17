package com.battleshippark.bsp_gallery.presentation.files;

import com.battleshippark.bsp_gallery.domain.UseCase;
import com.battleshippark.bsp_gallery.domain.files.FilesLoader;
import com.battleshippark.bsp_gallery.media.MediaFileModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import rx.Subscriber;

import static org.mockito.Mockito.verify;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class FilesPresenterTest {
    @Mock
    FilesView filesView;

    @Test
    public void loadList() throws IOException {
        List<MediaFileModel> list = Arrays.asList(
                new MediaFileModel(1, "name1", 2, "path1", "thumb1"),
                new MediaFileModel(2, "name2", 4, "path2", "thumb2")
        );
        UseCase<Void, List<MediaFileModel>> filesLoader = new FilesLoader(null, null, null, null, 0) {
            @Override
            public void execute(Void aVoid, Subscriber<List<MediaFileModel>> subscriber) {
                subscriber.onNext(list);
                subscriber.onCompleted();
            }
        };
        FilesPresenter presenter = new FilesPresenter(filesView, filesLoader);
        presenter.loadList(new FilesPresenter.FilesSubscriber(filesView));


        verify(filesView).refreshList(list);
        verify(filesView).hideProgress();
    }
}