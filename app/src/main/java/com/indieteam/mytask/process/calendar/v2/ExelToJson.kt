package com.indieteam.mytask.process.calendar.v2

import android.util.Log
import com.indieteam.mytask.modeldata.v2.CalendarRawV2
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class ExelToJson(val calendarRawV2Arr: ArrayList<CalendarRawV2>, val infoJson: JSONObject){

    val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
    private var dayOfWeekMap = mutableMapOf("2" to Calendar.MONDAY,
            "3" to Calendar.TUESDAY,
            "4" to Calendar.WEDNESDAY,
            "5" to Calendar.THURSDAY,
            "6" to Calendar.FRIDAY,
            "7" to Calendar.SATURDAY,
            "CN" to Calendar.SUNDAY)

    var jsonObject = JSONObject()
    var jsonArray = JSONArray()
    var size = 0

    fun parse(): JSONObject {
        for (i in calendarRawV2Arr){
            //debug use it
//            Log.d("subjectName", i.subjectName)
//            Log.d("subjectDate", i.subjectDate)
//            Log.d("subjectDayOfWeek", i.subjectDayOfWeek)
//            Log.d("subjectTime", i.subjectTime)
//            Log.d("subjectPlace", i.subjectPlace)
//            Log.d("teacher", i.teacher)

            val dateStartString = i.subjectDate.substring(0, i.subjectDate.indexOf("-")).trim()
            val dateEndString = i.subjectDate.substring(i.subjectDate.indexOf("-") + 1, i.subjectDate.length).trim()

            val dateStartDate = simpleDateFormat.parse((dateStartString))
            val dateEndDate = simpleDateFormat.parse((dateEndString))

//            Log.d("dateStartString", dateStartString)
//            Log.d("dateEndString", dateEndString)

            val calendar = Calendar.getInstance()
            val calendar2 = Calendar.getInstance()
            calendar.time = dateStartDate
            var dateStartCalendar = calendar
            calendar2.time = dateEndDate
            var dateEndCalendar = calendar2

            while (dateStartCalendar.time <= dateEndCalendar.time){
                //Log.d("date", calendar.time.toString())
                if(dateStartCalendar.get(Calendar.DAY_OF_WEEK) == dayOfWeekMap[i.subjectDayOfWeek]){
                    Log.d("size", size.toString())
                    Log.d("subjectName", i.subjectName)
                    Log.d("subjectDate", "${dateStartCalendar.get(Calendar.DAY_OF_MONTH)}/"+
                            "${dateStartCalendar.get(Calendar.MONTH) + 1}/" +
                            "${dateStartCalendar.get(Calendar.YEAR)}")
                    Log.d("subjectDayOfWeek", i.subjectDayOfWeek)
                    Log.d("subjectTime", i.subjectTime)
                    Log.d("subjectPlace", i.subjectPlace)
                    Log.d("teacher", i.teacher)

                    val jsonObjectChild = JSONObject()
                    jsonObjectChild.put("subjectName", i.subjectName)
                    jsonObjectChild.put("subjectDate", "${dateStartCalendar.get(Calendar.DAY_OF_MONTH)}/"+
                            "${dateStartCalendar.get(Calendar.MONTH) + 1}/" +
                            "${dateStartCalendar.get(Calendar.YEAR)}")
                    jsonObjectChild.put("subjectDayOfWeek", i.subjectDayOfWeek)
                    jsonObjectChild.put("subjectTime", i.subjectTime)
                    jsonObjectChild.put("subjectPlace", i.subjectPlace)
                    jsonObjectChild.put("teacher", i.teacher)

                    jsonArray.put(size, jsonObjectChild)
                    size++
                }
                dateStartCalendar.add(Calendar.DAY_OF_MONTH, 1)
            }
        }
        jsonObject.put("info", infoJson)
        jsonObject.put("calendar", jsonArray)
        Log.d("exelToJson", jsonObject.toString())
        return jsonObject
    }

}