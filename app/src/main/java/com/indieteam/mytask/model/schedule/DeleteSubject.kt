package com.indieteam.mytask.model.schedule

import android.content.Context
import com.indieteam.mytask.model.SqLite
import org.json.JSONArray
import org.json.JSONObject

class DeleteSubject(context: Context) {

    private val sqLite = SqLite(context)
    private val calendarStudent = JSONObject(sqLite.readCalendar())
    var dateDeleted = ""

    fun delete(subjectId: String) {
        val jsonArray = calendarStudent.getJSONArray("calendar")
        val newJsonArray = JSONArray()

        for (i in 0 until jsonArray.length()) {
            val subject = jsonArray.getJSONObject(i)
            if (subject.getString("subjectId") != subjectId)
                newJsonArray.put(subject)
            else
                dateDeleted = subject.getString("subjectDate")
        }

        calendarStudent.remove("calendar")
        calendarStudent.put("calendar", newJsonArray)
        sqLite.deleteCalendar()
        sqLite.insertCalender(calendarStudent.toString())
    }

}