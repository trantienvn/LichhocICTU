package com.indieteam.mytask.process.calendar.v1

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.indieteam.mytask.ui.WeekActivity
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class CalendarRawToJson(private val activity: WeekActivity){

    private var calendarJsonObj = JSONObject()
    private var calendarJsonArr = JSONArray()
    private var jsonArrSize = 0
    private var simpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
    //var simpleDateFormat2 = SimpleDateFormat("dd/MM/yyyy")
    private var arrDayOfWeek = arrayListOf("Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật")
    private var mapDayOfWeek = mutableMapOf("Thứ 2" to Calendar.MONDAY, "Thứ 3" to Calendar.TUESDAY, "Thứ 4" to Calendar.WEDNESDAY, "Thứ 5" to Calendar.THURSDAY, "Thứ 6" to Calendar.FRIDAY, "Thứ 7" to Calendar.SATURDAY, "Chủ nhật" to Calendar.SUNDAY)

    fun parse(): JSONObject{
        activity.apply {
            for (i in calendarMap) {
                Log.d("name subject", i.key)
                val subjectName = i.key
                val onlyCalendar = i.value
                if (onlyCalendar.arrDateRaw.size == onlyCalendar.arrPlaceRaw.size) {
                    for (j in 0 until onlyCalendar.arrDateRaw.size) {
                        Log.d("Date raw", onlyCalendar.arrDateRaw[j])
                        Log.d("Place raw", onlyCalendar.arrPlaceRaw[j])
                        var subjectTime = onlyCalendar.arrPlaceRaw[j].substring(onlyCalendar.arrPlaceRaw[j].indexOf("tiết"), onlyCalendar.arrPlaceRaw[j].indexOf("tại"))
                        subjectTime = subjectTime.substring(subjectTime.indexOf(" ") + 1, subjectTime.length - 1)
                        var subjectPlace = onlyCalendar.arrPlaceRaw[j].substring(onlyCalendar.arrPlaceRaw[j].indexOf("tại"), onlyCalendar.arrPlaceRaw[j].length - 1)
                        subjectPlace = subjectPlace.substring(subjectPlace.indexOf(" ") + 1, subjectPlace.length - 1)
                        val date = string2Date(onlyCalendar.arrDateRaw[j])
                        var dateStart: Any = date.substring(0, date.indexOf("\n") + 1) as String
                        var dateEnd: Any = date.substring(date.indexOf("\n"), date.length - 1) as String
                        Log.d("dateStart", dateStart.toString())
                        Log.d("dateEnd", dateEnd.toString())
                        val dayOfWeek = string2DayOfWeek(onlyCalendar.arrPlaceRaw[j])
                        Log.d("dayOfWeek", dayOfWeek)
                        var dateStartDate: Date? = null
                        var dateEndDate: Date? = null
                        try {
                            //dateStart = simpleDateFormat.format(simpleDateFormat2.parse((dateStart as String).trim()))
                            //dateEnd = simpleDateFormat.format(simpleDateFormat2.parse((dateEnd as String).trim()))
                            dateStartDate = simpleDateFormat.parse((dateStart as String).trim()) as Date
                            dateEndDate = simpleDateFormat.parse((dateEnd as String).trim()) as Date
                            //Log.d("dateStart formated", dateStart.toString())
                            //Log.d("dateEnd formated", dateEnd.toString())
                        }catch (e: Exception){
                            Log.d("Err", e.toString())
                        }
                        try {
                            if (dateStartDate != null && dateEndDate != null) {
                                val calendar = Calendar.getInstance()
                                val calendar2 = Calendar.getInstance()

                                calendar.time = dateStartDate
                                var dateStartCalendar = calendar
                                calendar2.time = dateEndDate
                                var dateEndCalendar = calendar2
                                while (dateStartCalendar.time <= dateEndCalendar.time) {
                                    if (dateStartCalendar.get(Calendar.DAY_OF_WEEK) == mapDayOfWeek[dayOfWeek]) {
                                        Log.d("Lich hoc",
                                                "$dayOfWeek ${dateStartCalendar.get(Calendar.DAY_OF_MONTH)}/" +
                                                "${dateStartCalendar.get(Calendar.MONTH) + 1}/" +
                                                "${dateStartCalendar.get(Calendar.YEAR)}," +
                                                " $subjectName, " +
                                                "$subjectTime, " +
                                                "$subjectPlace")

                                        var calendarJsonObjChild = JSONObject()
                                        calendarJsonObjChild.put(
                                                "subjectDate",
                                                "${dateStartCalendar.get(Calendar.DAY_OF_MONTH)}/" +
                                                "${dateStartCalendar.get(Calendar.MONTH) + 1}/" +
                                                "${dateStartCalendar.get(Calendar.YEAR)}")
                                        calendarJsonObjChild.put("subjectName", subjectName)
                                        calendarJsonObjChild.put("subjectTime", subjectTime)
                                        calendarJsonObjChild.put("subjectPlace", subjectPlace)

                                        calendarJsonArr.put(jsonArrSize,calendarJsonObjChild )
                                        calendarJsonObj.put("calendar", calendarJsonArr)
                                        jsonArrSize++
                                    }
                                    dateStartCalendar.add(Calendar.DAY_OF_MONTH, 1)
                                }
                            }
                        }catch (e: Exception){
                            Log.d("Err", e.toString())
                        }

                    }
                }
            }
            Log.d("calenderJsonObj", calendarJsonObj.toString())
            //log.text = calendarJsonObj.toString()
            try {
                sqlLite.insert(calendarJsonObj.toString())
            }catch (e: SQLiteConstraintException){
                Log.d("err", e.toString())
            }
            try {
                sqlLite.update(calendarJsonObj.toString())
            }catch (e: SQLiteConstraintException){
                Log.d("err", e.toString())
            }
        }
        return calendarJsonObj
    }

    private fun string2DayOfWeek(input: String): String{
        for (i in arrDayOfWeek){
            if(input.toLowerCase().contains(i.toLowerCase()))
                return i
        }
        return "null"
    }

    private fun string2Date(input: String): String {
        var result = ""
        val regex = "(\\d{1,2}[\\/]\\d{1,2}[\\/]\\d{4}|\\d{1,2}[\\/]\\d{1,2})"
        val matcher = Pattern.compile(regex).matcher(input)
        while (matcher.find()) {
            val date = matcher.group(1)
            //Log.d("Start Time", date.toString())
            result += date + "\n"
        }
        result = result.replace("/", "-")
        //Log.d("result", result)
        return result
    }
}