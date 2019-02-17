package com.indieteam.mytask.model.schedule.domHTML

import android.content.Context
import android.util.Log
import com.indieteam.mytask.model.address.UrlAddress
import com.indieteam.mytask.collection.TestScheduleCollection
import com.indieteam.mytask.collection.TestScheduleTypeCollection
import com.indieteam.mytask.model.sqlite.SqLite
import com.indieteam.mytask.ui.interface_.OnDomTestScheduleListener
import org.jsoup.Connection
import org.jsoup.Jsoup

class DomTestSchedule(context: Context, private val onDomTestScheduleListener: OnDomTestScheduleListener): Thread() {

    private val sqlite = SqLite(context)
    private val testScheduleCollection = ArrayList<TestScheduleCollection>()
    private val testScheduleTypeCollection = ArrayList<TestScheduleTypeCollection>()
    private val params = mutableMapOf<String, String>()

    override fun run() {
        try {
            val cookie = sqlite.readCookie()

            val firstRequest = Jsoup.connect(UrlAddress.urlTestSchedule)
                    .cookie("SignIn", cookie)
                    .method(Connection.Method.GET)
                    .execute()

            val parseFirst = firstRequest.parse()

            for (i in parseFirst.select("select")) {
                if (i.attr("id") == "drpSemester") {
                    for (j in i.select("option")) {
                        Log.d("option value", j.text() + ": " + j.`val`())
                        testScheduleCollection.add(TestScheduleCollection(j.text(), j.`val`()))
                    }
                }

                if (i.attr("id") == "drpExaminationNumber") {
                    for (j in i.select("option")) {
                        Log.d("option value", j.text() + ": " + j.`val`())
                        testScheduleTypeCollection.add(TestScheduleTypeCollection(j.text(), j.`val`()))
                    }
                }
            }

            onDomTestScheduleListener.onDone(testScheduleCollection, testScheduleTypeCollection)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}