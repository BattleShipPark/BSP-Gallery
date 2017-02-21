package com.battleshippark.bsp_gallery.domain.files;

import com.battleshippark.bsp_gallery.domain.MediaControllerFactory;
import com.battleshippark.bsp_gallery.domain.UseCase;
import com.battleshippark.bsp_gallery.media.MediaFileModel;
import com.battleshippark.bsp_gallery.media.MediaFilterMode;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;

/**
 */

@AllArgsConstructor
public class FilesLoader implements UseCase<Void, List<MediaFileModel>> {
    private final MediaControllerFactory mediaFactory;
    private final Scheduler scheduler;
    private final Scheduler postScheduler;
    private final MediaFilterMode mediaFilterMode;
    private final int folderId;

    @Override
    public void execute(Void aVoid, Subscriber<List<MediaFileModel>> subscriber) {
        Observable.create(
                (Subscriber<? super List<MediaFileModel>> _subscriber) -> {
                    MediaFilesController filesController = mediaFactory.createFilesController(mediaFilterMode, folderId);
                    List<MediaFileModel> list = new ArrayList<>();
                    filesController.loadBufferedList(bufferedList -> {
                        list.addAll(bufferedList);
                        _subscriber.onNext(list);
                    });
                    _subscriber.onCompleted();
                }
        ).subscribeOn(scheduler).observeOn(postScheduler).subscribe(subscriber);
    }
}
