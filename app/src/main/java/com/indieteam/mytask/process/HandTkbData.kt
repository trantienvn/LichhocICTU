package com.indieteam.mytask.process

import android.util.Log
import com.indieteam.mytask.modeldata.CalendarData
import com.indieteam.mytask.modeldata.OnlyCalendarData
import com.indieteam.mytask.ui.MainActivity

class HandTkbData(val activity: MainActivity){

    fun trimTkbData(){
        var index2Add = -1
        activity.apply {
            for (i in 0 until calendarDataTemp.size) {
                if(!calendarDataTemp[i].subjectName.isBlank() && !calendarDataTemp[i].tc.isBlank() && !calendarDataTemp[i].info.isBlank()){
                    calendarRaw.add(CalendarData(calendarDataTemp[i].subjectName, calendarDataTemp[i].tc, calendarDataTemp[i].info))
                    index2Add ++
                }

                if(calendarDataTemp[i].subjectName.isBlank() && calendarDataTemp[i].tc.isBlank() && !calendarDataTemp[i].info.isBlank()){
                    if(calendarRaw[index2Add].info.lastIndexOf("\n") == calendarRaw[index2Add].info.length - 3)
                        calendarRaw[index2Add].info += calendarDataTemp[i].info + "\n"
                    else
                        calendarRaw[index2Add].info += "\n" + calendarDataTemp[i].info + "\n"
                }
            }
        }
    }

    fun addToMap(){
        activity.apply {
            Log.d("size", calendarRaw.size.toString() + "\n\n")

            //Debug use it:
//        for (i in calendarRaw) {
//            Log.d("_______info________", "___start____"+i.info + "____end____\n\n")
//        }

            Log.d("------", "-----------------------------------Try parse calender-------------------------------------------------------------------------------------------------------------------------")

            var info: String
            for (i in calendarRaw) {
                //Log.d("___", "________________start item____________________")
                //Log.d("name subject ", i.subjectName)
                val arrDate = ArrayList<String>()
                val arrPlace = ArrayList<String>()
                info = i.info
                var nameSubj = ""
                var dateTemp = ""
                var placeTemp = ""
                var onlyCalendar: OnlyCalendarData
                var indexDate = -1
                var indexPlace = -1
                var indexBreakLineDate = -1
                var indexBreakLinePlace = -1
                indexDate = info.indexOf("Từ")
                indexPlace = info.indexOf("\n")
                indexBreakLineDate = info.indexOf("\n")
                indexBreakLinePlace = info.indexOf("\n", indexBreakLineDate + 1)

                Log.d("indexDate", indexDate.toString())
                Log.d("indexBreakLineDate", indexBreakLineDate.toString())

                Log.d("indexPlace", indexPlace.toString())
                Log.d("indexBreakLinePlace", indexBreakLinePlace.toString())

                dateTemp = info.substring(indexDate, indexBreakLineDate)
                if(indexBreakLinePlace == -1)
                    placeTemp = info.substring(indexPlace + 2, info.length - 1)
                else
                    placeTemp = info.substring(indexPlace + 2, indexBreakLinePlace)
                arrDate.add(dateTemp)
                arrPlace.add(placeTemp)
                //Log.d("parse date", timeTemp)
                nameSubj = i.subjectName
                while (indexDate >= 0) {
                    indexDate = info.indexOf("Từ", indexDate + 1)
                    indexBreakLinePlace = info.indexOf("\n", indexBreakLineDate + 1)
                    indexBreakLineDate = info.indexOf("\n", indexBreakLinePlace + 1)
                    if (indexDate != -1 && indexBreakLineDate != -1 && indexDate < indexBreakLineDate) {
                        //Log.d("parse date ", lineTime.toString())
                        dateTemp = info.substring(indexDate, indexBreakLineDate)
                        //Log.d("parse date", timeTemp)
                        arrDate.add(dateTemp)
                    }

                }
                indexBreakLineDate = info.indexOf("\n")
                indexBreakLinePlace = info.indexOf("\n", indexBreakLineDate + 1)

                //Log.d("parse place", placeTemp)
                while (indexPlace >= 0) {
                    indexPlace = info.indexOf(":\n", indexBreakLineDate + 1)
                    indexBreakLineDate = info.indexOf("\n", indexBreakLinePlace + 1)
                    indexBreakLinePlace = info.indexOf("\n", indexBreakLineDate + 1)
                    if (indexPlace != -1 && indexBreakLinePlace != -1 && indexPlace < indexBreakLinePlace) {
                        //Log.d("parse place ", linePlace.toString())
                        placeTemp = info.substring(indexPlace + 1, indexBreakLinePlace)
                        //Log.d("parse place", placeTemp)
                        arrPlace.add(placeTemp)
                    }
                }
                //Log.d("___", "end item____________________")
                onlyCalendar = OnlyCalendarData(arrDate, arrPlace)
                calendarMap[nameSubj] = onlyCalendar
            }
            // Debug use it:
//            for (i in calendarMap) {
//                Log.d("name subject", i.key)
//                val onlyCalendar = i.value
//                if (onlyCalendar.arrDateRaw.size == onlyCalendar.arrPlaceRaw.size) {
//                    for (j in 0 until onlyCalendar.arrDateRaw.size) {
//                        Log.d("Date raw", onlyCalendar.arrDateRaw[j])
//                        Log.d("Place raw", onlyCalendar.arrPlaceRaw[j])
//
//                    }
//                }
//            }
        }
    }

}
