package com.indieteam.mytask.process.domHTML

import android.content.Context
import com.indieteam.mytask.sqlite.SqLite

@Suppress("DEPRECATION")
class DomUpdateCalendar(val context: Context, private val signIn: String): Thread() {

    private var sqLite = SqLite(context)

    override fun run() {
        DomLogin(context, sqLite.readUserName(), sqLite.readPassword()).start()
    }
}
