package com.indieteam.mytask.process.calendar.v2

import android.content.Context
import android.util.Log
import com.indieteam.mytask.modeldata.v2.CalendarRawV2
import jxl.Sheet
import jxl.Workbook
import org.json.JSONObject
import java.io.File
import java.util.*

class ReadExel(context: Context){

    val fileV2 = File(context.filesDir, "exel/tkb_v2.xls")

    var calendarRawV2Arr = ArrayList<CalendarRawV2>()
    var infoObj = JSONObject()
    var readExelCallBack = 0
    var exelToJson = ExelToJson()

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
        infoObj.put("studentName", studentName)
        infoObj.put("studentId", studentId)
        infoObj.put("className", className)
        infoObj.put("courseName", courseName)
        infoObj.put("majorsName", majorsName)
    }

    fun readTkb(){
        readExelCallBack = 0
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
            workbook.close()
            readExelCallBack = 1
        } else{
            readExelCallBack = -1
            Log.d("err", "file V2 is not exists")
        }
    }
}