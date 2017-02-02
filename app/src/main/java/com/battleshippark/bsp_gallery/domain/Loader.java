package com.battleshippark.bsp_gallery.domain;

import rx.Subscriber;

/**
 */

public interface Loader<T> {
    void execute(Subscriber<T> subscriber);
}
