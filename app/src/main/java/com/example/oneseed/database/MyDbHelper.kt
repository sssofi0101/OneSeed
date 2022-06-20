package com.example.oneseed.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDbHelper(context : Context) : SQLiteOpenHelper(context, MyDbClass.DATABASE_NAME, null, MyDbClass.DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(MyDbClass.CreateTable)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }
    fun onDrop(db: SQLiteDatabase?){
        db?.execSQL(MyDbClass.DropTable)
    }
}