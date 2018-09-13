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
            val sql2 = "CREATE TABLE userInfo(id int primary key, username text, password text, cookie text)"
            p0?.execSQL(sql)
            p0?.execSQL(sql2)
        }catch (e: IllegalStateException){
            Log.d("err", "Cannot create table")
        }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {}

    fun insertCalender(data: String){
        val dbWrite = writableDatabase
        val value = ContentValues()
        value.put("id", 1)
        value.put("calendar", data)
        dbWrite.insert("userCalendar", null, value)
    }

    fun insertInfo(username: String, password: String, cookie: String){
        val dbWrite = writableDatabase
        val value = ContentValues()
        value.put("id", 1)
        value.put("username", username)
        value.put("password", password)
        value.put("cookie", cookie)
        dbWrite.insert("userInfo", null, value)
    }

    fun updateCalendar(data: String){
        val dbWrite = writableDatabase
        val value = ContentValues()
        value.put("calendar", data)
        dbWrite.update("userCalendar", value, "id=?", arrayOf("1"))
    }

    fun updateInfo(username: String, password: String, cookie: String){
        val dbWrite = writableDatabase
        val value = ContentValues()
        value.put("username", username)
        value.put("password", password)
        value.put("cookie", cookie)
        dbWrite.update("userInfo", value, "id=?", arrayOf("1"))
    }

    fun readCalendar(): String{
        val dbRead = readableDatabase
        val cursor = dbRead.rawQuery("SELECT calendar FROM userCalendar", null)
        cursor.moveToFirst()
        val value = cursor.getString(0)
        cursor.close()
        return value
    }

    fun readCookie(): String{
        val dbRead = readableDatabase
        val cursor = dbRead.rawQuery("SELECT cookie FROM userInfo", null)
        cursor.moveToFirst()
        val value = cursor.getString(0)
        cursor.close()
        return value
    }

    fun deleteCalendar(){
        val dbWrite = writableDatabase
        dbWrite.delete("userCalendar", "id=1", null)
    }

    fun deleteInfo(){
        val dbWrite = writableDatabase
        dbWrite.delete("userInfo", "id=1", null)
    }
}