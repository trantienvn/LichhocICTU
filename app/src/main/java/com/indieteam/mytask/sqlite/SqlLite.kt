package com.indieteam.mytask.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class SqlLite(context: Context): SQLiteOpenHelper(context, "calendar.db", null, 1){
    override fun onCreate(p0: SQLiteDatabase?) {
        try {
            val sql = "CREATE TABLE userCalendar(id int primary key, calendar text)"
            p0?.execSQL(sql)
        }catch (e: IllegalStateException){
            Log.d("err", "Cannot create table")
        }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {}

    fun insert(data: String){
        val dbWrite = writableDatabase
        val value = ContentValues()
        value.put("id", 1)
        value.put("calendar", data)
        dbWrite.insert("userCalendar", null, value)
    }

    fun update(data: String){
        val dbWrite = writableDatabase
        val value = ContentValues()
        value.put("calendar", data)
        dbWrite.update("userCalendar", value, "id=?", arrayOf("1"))
    }

    fun read(): String{
        val dbRead = readableDatabase
        val cursor = dbRead.rawQuery("SELECT calendar FROM userCalendar", null)
        cursor.moveToFirst()
        return cursor.getString(0)
    }

    fun delete(){
        val dbWrite = writableDatabase
        dbWrite.delete("userCalendar", "id=1", null)
    }
}