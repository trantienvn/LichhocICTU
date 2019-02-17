package com.indieteam.mytask.model.schedule.domHTML

import android.annotation.SuppressLint
import android.content.Context
import com.indieteam.mytask.model.address.UrlAddress
import com.indieteam.mytask.ui.interface_.OnSemesterScheduleListener
import com.indieteam.mytask.ui.fragment.SelectSemesterFragment
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Connection
import org.jsoup.Jsoup

@Suppress("DEPRECATION")
class DomSemesterSchedule(val context: Context, private val sessionUrl: String, private val signIn: String,
                          private val onSemesterScheduleListener: OnSemesterScheduleListener) : Thread() {

    private var jsonArray = JSONArray()
    private var drpSemesterObject = JSONObject()
    private var err = 0
    private var classContextName = ""
    private var selectSemesterFragment = SelectSemesterFragment()

    init {
        classContextName = context.javaClass.name.substring(context.javaClass.name.lastIndexOf(".") + 1, context.javaClass.name.length)
    }

    @SuppressLint("SetTextI18n")
    override fun run() {
        try {
            onSemesterScheduleListener.onSemesterSchedule()
            // start get post params
            if (sessionUrl.isNotBlank()) {
                val response = Jsoup.connect(UrlAddress.urlDownloadExel(sessionUrl))
                        .cookie("SignIn", signIn)
                        .method(Connection.Method.GET)
                        .execute()

                val html = response.parse()
                for (i in html.select("select")) {
                    //Hoc ky
                    if (i.attr("name") == "drpSemester") {
                        for (j in i.select("option")) {
                            //Log.d("drpSemester", j.attr("value"))
                            //Log.d("drpSemester name", j.text())
                            val jsonObjectChild = JSONObject()
                            jsonObjectChild.put(j.text(), j.attr("value"))
                            jsonArray.put(jsonObjectChild)
                        }
                    }
                }
                drpSemesterObject.put("semester", jsonArray)
            }
        } catch (e: Exception) {
            onSemesterScheduleListener.onThrow("Mất kết nối")
            err = 1
        }

        if (err == 0)
            onSemesterScheduleListener.onSuccess(drpSemesterObject.toString(), sessionUrl, signIn)

        this.join()
    }
}
