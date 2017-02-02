package com.battleshippark.bsp_gallery.domain.folders;

import com.battleshippark.bsp_gallery.data.mode.MediaFilterModeRepository;
import com.battleshippark.bsp_gallery.domain.Loader;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;
import com.battleshippark.bsp_gallery.media.MediaFolderModel;

import lombok.AllArgsConstructor;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;

/**
 */

@AllArgsConstructor
public class FilterModeLoader implements Loader<MediaFilterMode> {
    private final MediaFilterModeRepository mediaFilterModeRepository;
    private final Scheduler scheduler;
    private final Scheduler postScheduler;

    @Override
    public void execute(Subscriber<MediaFilterMode> subscriber) {
        Observable.create(
                (Subscriber<? super MediaFilterMode> _subscriber) -> {
                    MediaFilterMode mode = mediaFilterModeRepository.load();
                    _subscriber.onNext(mode);
                    _subscriber.onCompleted();
                }
        ).subscribeOn(scheduler).observeOn(postScheduler).subscribe(subscriber);
    }
}
