package com.indieteam.mytask.process

import android.util.Log
import com.indieteam.mytask.ui.MainActivity
import com.prolificinteractive.materialcalendarview.CalendarDay
import org.json.JSONObject
import java.text.SimpleDateFormat

class ParseCalendarJson(val activity: MainActivity, val calendar: JSONObject){
    var subjectDate = ArrayList<String>()
    var subjectName = ArrayList<String>()
    var subjectTime = ArrayList<String>()
    var subjectPlace = ArrayList<String>()

    fun getSubject(key: String){
        subjectDate = arrayListOf()
        subjectName = arrayListOf()
        subjectTime = arrayListOf()
        subjectPlace = arrayListOf()

        val calendarValue = calendar.getJSONArray("calendar")
        Log.d("key", key)

        for (i in 0 until calendarValue.length()){
            //Log.d("date_key", calendarValue.getJSONObject(i).getString("subjectDate"))

            if (key == calendarValue.getJSONObject(i).getString("subjectDate")) {
                subjectDate.add(calendarValue.getJSONObject(i).getString("subjectDate"))
                subjectName.add(calendarValue.getJSONObject(i).getString("subjectName"))
                subjectTime.add(calendarValue.getJSONObject(i).getString("subjectTime"))
                subjectPlace.add(calendarValue.getJSONObject(i).getString("subjectPlace"))
            }
        }
    }

    fun addToArrDot(){
        val calendarValue = calendar.getJSONArray("calendar")
        for (i in 0 until calendarValue.length()){
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
                val dateFormated = simpleDateFormat.parse(calendarValue.getJSONObject(i).getString("subjectDate"))
                val calendarDay = CalendarDay.from(dateFormated)
                activity.listDate.add(calendarDay)
        }
    }

}