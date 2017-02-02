package com.battleshippark.bsp_gallery.domain;

import com.battleshippark.bsp_gallery.media.MediaFilterMode;

import rx.Subscriber;

/**
 */

public interface Loader<T> {
    void execute(Subscriber<T> subscriber);
}
