package com.indieteam.mytask.process.domHTML

import android.content.Context
import android.text.Html.fromHtml
import android.util.Log
import android.widget.Toast
import com.indieteam.mytask.address.UrlAddress
import com.indieteam.mytask.process.calendar.v2.ReadExel
import com.indieteam.mytask.sqlite.SqLite
import com.indieteam.mytask.ui.WeekActivity
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.io.File
import java.io.FileOutputStream

@Suppress("DEPRECATION")
class DomUpdateCalendar(val context: Context, private val signIn: String): Thread() {

    private var sqLite = SqLite(context)

    override fun run() {
        DomLogin(context, sqLite.readUserName(), sqLite.readPassword()).start()
    }
}
