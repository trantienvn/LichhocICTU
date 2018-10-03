package com.indieteam.mytask.process.parseJson

import android.annotation.SuppressLint
import com.prolificinteractive.materialcalendarview.CalendarDay
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ParseCalendarJson(val calendar: JSONObject){
    //var subjectDate = ArrayList<String>()
    var subjectName = ArrayList<String>()
    var subjectTime = ArrayList<String>()
    var subjectPlace = ArrayList<String>()
    var teacher = ArrayList<String>()

    fun getSubject(key: String){
        //subjectDate = arrayListOf()
        subjectName = arrayListOf()
        subjectTime = arrayListOf()
        subjectPlace = arrayListOf()
        teacher = arrayListOf()

        val calendarValue = calendar.getJSONArray("calendar")
        //Log.d("calendar", key)

        for (i in 0 until calendarValue.length()){
            //Log.d("date_key", calendarValue.getJSONObject(i).getString("subjectDate"))

            if (key == calendarValue.getJSONObject(i).getString("subjectDate")) {
                //subjectDate.add(calendarValue.getJSONObject(i).getString("subjectDate"))
                subjectName.add(calendarValue.getJSONObject(i).getString("subjectName"))
                subjectTime.add(calendarValue.getJSONObject(i).getString("subjectTime"))
                subjectPlace.add(calendarValue.getJSONObject(i).getString("subjectPlace"))
                teacher.add(calendarValue.getJSONObject(i).getString("teacher"))
            }
        }
        sort()
    }

    private fun sort(){
        if (subjectName.isNotEmpty() && subjectPlace.isNotEmpty() && subjectTime.isNotEmpty() && teacher.isNotEmpty()) {
            for (j in 0 until subjectTime.size - 1) {
                for (k in j + 1 until subjectTime.size) {
                    val firstI = subjectTime[j].substring(0, subjectTime[j].indexOf(",")).toInt()
                    val firstJ = subjectTime[k].substring(0, subjectTime[k].indexOf(",")).toInt()
                    if (firstI > firstJ) {
                        var temp = subjectName[j]
                        subjectName[j] = subjectName[k]
                        subjectName[k] = temp

                        temp = subjectTime[j]
                        subjectTime[j] = subjectTime[k]
                        subjectTime[k] = temp

                        temp = subjectPlace[j]
                        subjectPlace[j] = subjectPlace[k]
                        subjectPlace[k] = temp

                        temp = teacher[j]
                        teacher[j] = teacher[k]
                        teacher[k] = temp
                    }
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun addToMapDots(): MutableMap<CalendarDay, String>{
        val mapDateForDots = mutableMapOf<CalendarDay, String>()
        val calendarValue = calendar.getJSONArray("calendar")
        for (i in 0 until calendarValue.length()) {
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
            val dateFormated = simpleDateFormat.parse(calendarValue.getJSONObject(i).getString("subjectDate"))
            val calendar = Calendar.getInstance()
            calendar.time = dateFormated
            val calendarDay = CalendarDay.from(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH))
            val calendarDayRaw = CalendarDay.from(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            //Log.d("date_key", calendar.get(Calendar.YEAR).toString() + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH))
            //Log.d("date_key", calendarDay.toString())
            if (mapDateForDots.isEmpty()) {
                mapDateForDots[calendarDayRaw] = "."
            } else {
                var check = 0
                fun loop() {
                    for (j in mapDateForDots) {
                        check = 0
                        val calendar2 = Calendar.getInstance()
                        calendar2.set(j.key.year, j.key.month, j.key.day)
                        val calendarDayFromKey = CalendarDay.from(calendar2.get(Calendar.YEAR), calendar2.get(Calendar.MONTH), calendar2.get(Calendar.DAY_OF_MONTH))
                        if (calendarDayRaw == calendarDayFromKey && mapDateForDots[calendarDayRaw] != null) {
                            check = 1
                            var lastValue = mapDateForDots[calendarDayRaw]!!
                            lastValue += "."
                            mapDateForDots.remove(calendarDayRaw)
                            mapDateForDots[calendarDayRaw] = lastValue
                            break
                        }
                    }
                    if (check == 0)
                        mapDateForDots[calendarDayRaw] = "."
                }
                loop()
            }
        }

        //debug use it
//        for (i in activity.mapDateForDots){
//            Log.d("dots", "key: ${i.key}, value: ${i.value}")
//        }
        return mapDateForDots
    }

}