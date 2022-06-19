package com.example.oneseed.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase

class MyDbManager(context: Context) {

    private val myDbHelper = MyDbHelper(context)
    private var db: SQLiteDatabase? = null

    fun openDB(){
        db = myDbHelper.writableDatabase
    }
    fun insertToDB(name: String, coordinates: String, photo: String,
                   varieties: String, comment: String, locked: Int, result: Float){
        val values = ContentValues().apply {
            put(MyDbClass.COLUMN_NAME_NAME, name)
            put(MyDbClass.COLUMN_NAME_COORDINATES, coordinates)
            put(MyDbClass.COLUMN_NAME_PHOTO, photo)
            put(MyDbClass.COLUMN_NAME_VARIETIES, varieties)
            put(MyDbClass.COLUMN_NAME_COMMENT, comment)
            put(MyDbClass.COLUMN_NAME_LOADED, locked)
            put(MyDbClass.COLUMN_NAME_RESULT, result)
        }
        db?.insert(MyDbClass.TABLE_NAME, null, values)
    }
    @SuppressLint("Range")
    fun readDBData(): ArrayList<String>{
        val dataList = ArrayList<String>()
        val cursor = db?.query(MyDbClass.TABLE_NAME, null, null,null,null,null,null)
            while (cursor?.moveToNext()!!){
                val dataText = cursor.getString(cursor.getColumnIndex(MyDbClass.COLUMN_NAME_NAME))
                dataList.add(dataText)
            }

        cursor.close()
        return dataList
    }
    fun closeDB(){
        myDbHelper.close()
    }
}