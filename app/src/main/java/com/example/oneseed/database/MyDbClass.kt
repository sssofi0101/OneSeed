package com.example.oneseed.database

import android.provider.BaseColumns

object MyDbClass {
    const val TABLE_NAME = "Request"
    const val COLUMN_NAME_NAME = "name"
    const val COLUMN_NAME_COORDINATES = "coordinates"
    const val COLUMN_NAME_PHOTO = "photo"
    const val COLUMN_NAME_VARIETIES = "varieties"
    const val COLUMN_NAME_COMMENT = "comment"
    const val COLUMN_NAME_LOADED = "loaded"
    const val COLUMN_NAME_RESULT = "result"

    const val DATABASE_VERSION = 1
    const val DATABASE_NAME = "Request.db"

    const val CreateTable = "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "$COLUMN_NAME_NAME TEXT," +
            "$COLUMN_NAME_COORDINATES TEXT,"+
            "$COLUMN_NAME_PHOTO TEXT,"+
            "$COLUMN_NAME_VARIETIES TEXT,"+
            "$COLUMN_NAME_COMMENT TEXT," +
            "$COLUMN_NAME_LOADED INTEGER," +
            "$COLUMN_NAME_RESULT REAL)"

    const val DropTable = "DROP TABLE IF EXISTS $TABLE_NAME"
}