package com.battleshippark.bsp_gallery;

import io.realm.RealmMigration;

/**
 */
public class RealmHelper {
    public static final int SCHEMA_VERSION = 1;
    public static RealmMigration migration = (realm, oldVersion, newVersion) -> {
    };
}
