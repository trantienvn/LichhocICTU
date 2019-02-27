package com.indieteam.mytask.model.schedule.domHTML

import android.content.Context
import android.util.Log
import com.indieteam.mytask.collection.UrlAddress
import com.indieteam.mytask.collection.TestScheduleSemesterCollection
import com.indieteam.mytask.collection.TestScheduleTypeCollection
import com.indieteam.mytask.model.SqLite
import com.indieteam.mytask.ui.interface_.OnDomTestListScheduleListener
import org.jsoup.Connection
import org.jsoup.Jsoup

class DomTestListSchedule(context: Context, private val onDomTestListScheduleListener: OnDomTestListScheduleListener) : Thread() {

    private val sqLite = SqLite(context)
    private val testScheduleCollection = ArrayList<TestScheduleSemesterCollection>()
    private val testScheduleTypeCollection = ArrayList<TestScheduleTypeCollection>()
    private lateinit var sessionUrl: String

    override fun run() {
        get()
        join()
    }

    private fun get() {
        try {
            val cookie = sqLite.readCookie()

            val response = Jsoup.connect(UrlAddress.loginClean)
                    .followRedirects(false)
                    .method(Connection.Method.GET)
                    .execute()

            val location = response.header("Location")

            sessionUrl = location.substring(location.indexOf("S(") + 2, location.indexOf("))"))

            if (!sessionUrl.isNullOrBlank()) {
                val response2 = Jsoup.connect(UrlAddress.testSchedule(sessionUrl))
                        .cookie("SignIn", cookie)
                        .method(Connection.Method.GET)
                        .execute()

                val html = response2.parse()

                for (i in html.select("select")) {
                    if (i.attr("id") == "drpSemester") {
                        for (j in i.select("option")) {
                            Log.d("option value", j.text() + ": " + j.`val`())
                            testScheduleCollection.add(TestScheduleSemesterCollection(j.text(), j.`val`()))
                        }
                    }

                    if (i.attr("id") == "drpExaminationNumber") {
                        for (j in i.select("option")) {
                            Log.d("option value", j.text() + ": " + j.`val`())
                            testScheduleTypeCollection.add(TestScheduleTypeCollection(j.text(), j.`val`()))
                        }
                    }
                }

                onDomTestListScheduleListener.onDone(testScheduleCollection, testScheduleTypeCollection)
            } else
                onDomTestListScheduleListener.onFail("sessionUrl is null or blank")


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}