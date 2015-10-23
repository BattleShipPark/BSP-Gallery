package com.battleshippark.bsp_gallery;

import android.database.Cursor;

/**
 */
public class CursorUtils {
    public static String getString(Cursor c, String colName) {
        return c.getString(c.getColumnIndex(colName));
    }

    public static int getInt(Cursor c, String colName) {
        return c.getInt(c.getColumnIndex(colName));
    }

    public static Long getLong(Cursor c, String colName) {
        return c.getLong(c.getColumnIndex(colName));
    }
}
