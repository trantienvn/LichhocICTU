package com.indieteam.mytask.datas.datasources.domHTML

import android.content.Context
import com.indieteam.mytask.datas.SqLite
import com.indieteam.mytask.views.interfaces.OnLoginListener

@Suppress("DEPRECATION")
class DomUpdateSchedule(val context: Context, private val signIn: String, private val onLoginListener: OnLoginListener) : Thread() {

    private var sqLite = SqLite(context)

    override fun run() {
        update()
    }

    private fun update() {
        DomLogin(context, sqLite.readUsername(), sqLite.readPassword(), onLoginListener).start()
    }
}
