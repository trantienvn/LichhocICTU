package com.indieteam.mytask.process.calendar.v2

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import android.widget.Toast
import com.indieteam.mytask.modeldata.v2.CalendarRawV2
import com.indieteam.mytask.ui.WeekActivity
import jxl.Workbook
import java.util.*

class ReadExel(private val activity: WeekActivity){

    private var calendarRawV2Arr = ArrayList<CalendarRawV2>()

    fun readTkbExel(){
        object : Thread(){
            override fun run() {
                activity.apply {
                    if (fileV2.exists()) {
                        Log.d("filev2", "exits")
                        val workbook = Workbook.getWorkbook(fileV2)
                        val sheet = workbook.getSheet(0)
                        //Hàm CELL để tra cứu thông tin của một ô trong Excel
                        // Code duoi day tra cuu tung hang (trong moi hang tra cuu tung cot)
                        // getCell(collum, row)
                        for(i in 10 until sheet.rows - 9){
                            var subjectName = ""
                            var subjectDate = ""
                            var subjectDayOfWeek = ""
                            var subjectTime = ""
                            var subjectPlace = ""
                            var teacher = ""
                            for(j in 0 until 11){
                                val cell = sheet.getCell(j, i).contents
                                Log.d("cell", cell)
                                when(j){
                                    0 -> subjectDayOfWeek = cell
                                    3 -> subjectName = cell
                                    7 -> teacher = cell
                                    8 -> subjectTime = cell
                                    9 -> subjectPlace = cell
                                    10 -> subjectDate = cell
                                }
                            }
                            calendarRawV2Arr.add(CalendarRawV2(subjectName,
                                    subjectDate,
                                    subjectDayOfWeek,
                                    subjectTime,
                                    subjectPlace,
                                    teacher))
                        }
                        val exelToJson = ExelToJson(calendarRawV2Arr).parse()
                        Log.d("Json", exelToJson.toString())
                        try {
                            sqlLite.insert(exelToJson.toString())
                        }catch (e: SQLiteConstraintException){
                            Log.d("err", e.toString())
                        }
                        try {
                            sqlLite.update(exelToJson.toString())
                        }catch (e: SQLiteConstraintException){
                            Log.d("err", e.toString())
                        }
                        readExelCallback = 1
                    }  else{
                        readExelCallback = -1
                        activity.runOnUiThread {
                            Toast.makeText(this, "fileV2 is not exists", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                this.join()
            }
        }.start()
    }
}