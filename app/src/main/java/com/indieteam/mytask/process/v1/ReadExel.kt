package com.indieteam.mytask.process.v1

import android.widget.Toast
import com.indieteam.mytask.modeldata.v1.CalendarRaw
import com.indieteam.mytask.ui.WeekActivity
import jxl.Workbook

class ReadExel(val activity: WeekActivity){


    fun readTkbExel() {
        object : Thread() {
            override fun run() {
                activity.apply {
                    if (fileV1.exists()) {
                        val workbook = Workbook.getWorkbook(fileV1)
                        val sheet = workbook.getSheet(0)
                        for (i in 10 until 22) {
                            //Hàm CELL để tra cứu thông tin của một ô trong Excel
                            // Code duoi day tra cuu tung hang (trong moi hang tra cuu tung cot)
                            // getCell(collu//            while (dateStartCalendar.time < dateEndCalendar.time){
//
//                dateStartCalendar.add(Calendar.DAY_OF_MONTH, 1)
//            }m, row)
                            //Log.d("row", i.toString())
                            nameSubject = ""
                            tc = ""
                            info = ""
                            for (j in 0 until sheet.columns - 1) {
                                val cell = sheet.getCell(j, i).contents
                                if (!cell.isBlank()) {
                                    //Log.d("Cell", cell.toString())
                                    when (j) {
                                        3 -> {
                                            nameSubject = cell
                                        }
                                        4 -> {
                                            tc = cell
                                        }
                                        7 -> {
                                            info += cell
                                        }
                                    }
                                }
                            }
                            calendarRawArr.add(CalendarRaw(nameSubject, tc, info))
                        }
                        readExelCallback = 1
                    } else {
                        readExelCallback = -1
                        activity.runOnUiThread {
                            Toast.makeText(this, "fileV1 is not exists", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                this.join()
            }
        }.start()
    }

}