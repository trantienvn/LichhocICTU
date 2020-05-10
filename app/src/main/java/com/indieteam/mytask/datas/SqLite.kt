package com.indieteam.mytask.datas

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class SqLite(val context: Context) : SQLiteOpenHelper(context, "calendar.db", null, 1) {
    override fun onCreate(p0: SQLiteDatabase?) {
        try {
            val sql = "CREATE TABLE userCalendar(id int primary key, calendar text)"
            val sql2 = "CREATE TABLE userInfo(id int primary key, username text, password text, cookie text, email text)"
            p0?.execSQL(sql)
            p0?.execSQL(sql2)
        } catch (e: IllegalStateException) {
            Log.d("err", "Cannot create table")
        }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {}

    fun insertSchedule(data: String) {
        val dbWrite = writableDatabase
        val value = ContentValues()
        value.put("id", 1)
        value.put("calendar", data)
        dbWrite.insert("userCalendar", null, value)
        dbWrite.close()
    }

    fun insertInfo(username: String, password: String, cookie: String) {
        val dbWrite = writableDatabase
        val value = ContentValues()
        value.put("id", 1)
        value.put("username", username)
        value.put("password", password)
        value.put("cookie", cookie)
        value.put("email", "")
        dbWrite.insert("userInfo", null, value)
        dbWrite.close()
    }

    fun updateSchedule(data: String) {
        val dbWrite = writableDatabase
        val value = ContentValues()
        value.put("calendar", data)
        dbWrite.update("userCalendar", value, "id=?", arrayOf("1"))
    }

    fun updateEmail(email: String) {
        val dbWrite = writableDatabase
        val value = ContentValues()
        value.put("email", email)
        dbWrite.update("userInfo", value, null, null)
    }

    fun updateInfo(username: String, password: String, cookie: String) {
        val dbWrite = writableDatabase
        val value = ContentValues()
        value.put("username", username)
        value.put("password", password)
        value.put("cookie", cookie)
        dbWrite.update("userInfo", value, "id=?", arrayOf("1"))
        dbWrite.close()
    }

    fun readSchedule(): String {
        val dbRead = readableDatabase
        val cursor = dbRead.rawQuery("SELECT calendar FROM userCalendar", null)
        cursor.moveToFirst()
        val value = cursor.getString(0)
        cursor.close()
        dbRead.close()
        return value
    }

    fun readUsername(): String {
        val dbRead = readableDatabase
        val cursor = dbRead.rawQuery("SELECT username FROM userInfo", null)
        cursor.moveToFirst()
        val value = cursor.getString(0)
        cursor.close()
        dbRead.close()
        return value
    }

    fun readEmail(): String {
        val dbRead = readableDatabase
        val cursor = dbRead.rawQuery("SELECT email FROM userInfo", null)
        cursor.moveToFirst()
        val value = cursor.getString(0)
        cursor.close()
        dbRead.close()
        return value
    }

    fun readPassword(): String {
        val dbRead = readableDatabase
        val cursor = dbRead.rawQuery("SELECT password FROM userInfo", null)
        cursor.moveToFirst()
        val value = cursor.getString(0)
        cursor.close()
        dbRead.close()
        return value
    }

    fun readCookie(): String {
        val dbRead = readableDatabase
        val cursor = dbRead.rawQuery("SELECT cookie FROM userInfo", null)
        cursor.moveToFirst()
        val value = cursor.getString(0)
        cursor.close()
        dbRead.close()
        return value
    }

    fun deleteSchedule() {
        val dbWrite = writableDatabase
        dbWrite.delete("userCalendar", "id=1", null)
        dbWrite.close()
    }

    fun deleteInfo() {
        val dbWrite = writableDatabase
        dbWrite.delete("userInfo", "id=1", null)
        dbWrite.close()
    }

    fun dropAll() {
        context.deleteDatabase("calendar.db")
    }
}