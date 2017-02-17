package com.battleshippark.bsp_gallery.domain.folders;

import com.battleshippark.bsp_gallery.data.mode.MediaFilterModeRepository;
import com.battleshippark.bsp_gallery.domain.UseCase;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;

import lombok.AllArgsConstructor;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;

/**
 */

@AllArgsConstructor
public class FilterModeSaver implements UseCase<MediaFilterMode, MediaFilterMode> {
    private final MediaFilterModeRepository mediaFilterModeRepository;
    private final Scheduler scheduler;
    private final Scheduler postScheduler;

    @Override
    public void execute(MediaFilterMode filterMode, Subscriber<MediaFilterMode> subscriber) {
        Observable.create(
                (Subscriber<? super MediaFilterMode> _subscriber) -> {
                    mediaFilterModeRepository.save(filterMode);
                    _subscriber.onNext(filterMode);
                    _subscriber.onCompleted();
                }
        ).subscribeOn(scheduler).observeOn(postScheduler).subscribe(subscriber);
    }
}
