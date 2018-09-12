package com.indieteam.mytask.process.calendar.v2

import android.database.sqlite.SQLiteConstraintException
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.indieteam.mytask.modeldata.v2.CalendarRawV2
import com.indieteam.mytask.ui.WeekActivity
import jxl.Sheet
import jxl.Workbook
import org.json.JSONObject
import java.io.File
import java.util.*

class ReadExel(private val activity: WeekActivity){

    val fileV2 = File(activity.filesDir, "exel/tkb_v2.xls")

    private var calendarRawV2Arr = ArrayList<CalendarRawV2>()
    private var infoJson = JSONObject()

    fun readInfo(sheet: Sheet){
        val studentName = sheet.getCell(2, 5).contents
        val studentId = sheet.getCell(5, 5).contents
        val className = sheet.getCell(2, 6).contents
        val courseName = sheet.getCell(2, 7).contents
        val majorsName = sheet.getCell(5, 7).contents
        Log.d("infoStudent", studentName + "\n" +
                studentId + "\n" +
                className  + "\n" +
                courseName + "\n" +
                majorsName + "\n")
        infoJson.put("studentName", studentName)
        infoJson.put("studentId", studentId)
        infoJson.put("className", className)
        infoJson.put("courseName", courseName)
        infoJson.put("majorsName", majorsName)
    }

    fun readTkb(){
        object : Thread(){
            override fun run() {
                activity.apply {
                    if (fileV2.exists()) {
                        Log.d("filev2", "exits")
                        val workbook = Workbook.getWorkbook(fileV2)
                        val sheet = workbook.getSheet(0)
                        readInfo(sheet)
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
                        val exelToJson = ExelToJson(calendarRawV2Arr, infoJson).parse()
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