package com.battleshippark.bsp_gallery;

import com.squareup.otto.Bus;

/**
 */
public class EventBusHelper {
    public static final Bus eventBus = new MainThreadBus();
}
