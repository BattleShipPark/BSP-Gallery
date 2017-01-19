package com.battleshippark.bsp_gallery.domain;

import rx.Subscriber;

/**
 */

public interface Loader {
    void execute(Subscriber subscriber);
}
