package com.battleshippark.bsp_gallery.domain.folders;

import com.battleshippark.bsp_gallery.data.mode.MediaFilterModeRepository;
import com.battleshippark.bsp_gallery.domain.UseCase;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import rx.internal.schedulers.ImmediateScheduler;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class FilterModeLoaderTest {
    @Mock
    MediaFilterModeRepository filterModeRepository;

    @Test
    public void execute() throws Exception {
        UseCase<Void, MediaFilterMode> loader = new FilterModeLoader(filterModeRepository,
                ImmediateScheduler.INSTANCE, ImmediateScheduler.INSTANCE);

        when(filterModeRepository.load()).thenReturn(MediaFilterMode.ALL);

        TestSubscriber<MediaFilterMode> subscriber = new TestSubscriber<>();
        loader.execute(null, subscriber);

        List<MediaFilterMode> list = subscriber.getOnNextEvents();
        assertThat(list).hasSize(1);
        assertThat(list.get(0)).isEqualTo(MediaFilterMode.ALL);
        subscriber.assertCompleted();
    }
}