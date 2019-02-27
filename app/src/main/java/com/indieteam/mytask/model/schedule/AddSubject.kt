package com.indieteam.mytask.model.schedule

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.indieteam.mytask.model.Random
import com.indieteam.mytask.model.SqLite
import com.indieteam.mytask.ui.WeekActivity
import org.json.JSONObject
import java.lang.Exception
import java.util.*

class AddSubject(private val context: Context) {

    private val sqLite = SqLite(context)

    private val calendarStudent = JSONObject(sqLite.readSchedule())

    fun add(subjectName: String, subjectPlace: String, subjectTeacher: String, subjectTime: String, subjectDate: String) {
        val day = subjectDate.substring(0, subjectDate.indexOf("/")).toInt()
        val month = subjectDate.substring(subjectDate.indexOf("/") + 1, subjectDate.lastIndexOf("/")).toInt() - 1
        val year = subjectDate.substring(subjectDate.lastIndexOf("/") + 1, subjectDate.length).toInt()

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK).toString()

        val newDate = "$day/${month + 1}/$year"
        val jsonArray = calendarStudent.getJSONArray("calendar")

        val elementJsonObject = JSONObject()
        elementJsonObject.put("subjectId", Random.get_string(40))
        elementJsonObject.put("subjectName", subjectName)
        elementJsonObject.put("subjectDate", newDate)
        elementJsonObject.put("subjectDayOfWeek", dayOfWeek)
        elementJsonObject.put("subjectTime", subjectTime)
        elementJsonObject.put("subjectPlace", subjectPlace)
        elementJsonObject.put("teacher", subjectTeacher)

        jsonArray.put(elementJsonObject)
        calendarStudent.remove("calendar")
        calendarStudent.put("calendar", jsonArray)
        //Log.d("New Calendar Student", calendarStudent.toString())

        for (i in 0 until jsonArray.length()) {
            Log.d("SubjectName", jsonArray.getJSONObject(i).getString("subjectName"))
            Log.d("SubjectDate", jsonArray.getJSONObject(i).getString("subjectDate"))
        }
        try {
            sqLite.deleteSchedule()
            sqLite.insertSchedule(calendarStudent.toString())
        } catch (e: Exception) {
            context as WeekActivity
            context.runOnUiThread {
                Toast.makeText(context, "Không thể thêm lịch học", Toast.LENGTH_LONG).show()
            }
        }
    }

}