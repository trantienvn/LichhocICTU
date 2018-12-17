package com.indieteam.mytask.core.calendar.domHTML

import android.content.Context
import com.indieteam.mytask.core.sqlite.SqLite

@Suppress("DEPRECATION")
class DomUpdateCalendar(val context: Context, private val signIn: String): Thread() {

    private var sqLite = SqLite(context)

    override fun run() {
        DomLogin(context, sqLite.readUserName(), sqLite.readPassword()).start()
    }
}
