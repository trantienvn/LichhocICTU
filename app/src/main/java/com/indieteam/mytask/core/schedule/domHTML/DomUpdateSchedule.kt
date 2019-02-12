package com.indieteam.mytask.core.schedule.domHTML

import android.content.Context
import com.indieteam.mytask.core.sqlite.SqLite

@Suppress("DEPRECATION")
class DomUpdateSchedule(val context: Context, private val signIn: String): Thread() {

    private var sqLite = SqLite(context)

    override fun run() {
        DomLogin(context, sqLite.readUserName(), sqLite.readPassword()).start()
    }
}
