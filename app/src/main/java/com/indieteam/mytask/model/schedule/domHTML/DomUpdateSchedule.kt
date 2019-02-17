package com.indieteam.mytask.model.schedule.domHTML

import android.content.Context
import com.indieteam.mytask.model.sqlite.SqLite
import com.indieteam.mytask.ui.interface_.OnLoginListener

@Suppress("DEPRECATION")
class DomUpdateSchedule(val context: Context, private val signIn: String, private val onLoginListener: OnLoginListener): Thread() {

    private var sqLite = SqLite(context)

    override fun run() {
        DomLogin(context, sqLite.readUserName(), sqLite.readPassword(), onLoginListener).start()
    }
}
