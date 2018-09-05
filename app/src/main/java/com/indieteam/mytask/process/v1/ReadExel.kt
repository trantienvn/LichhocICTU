package com.indieteam.mytask.process.v1

import android.widget.Toast
import com.indieteam.mytask.modeldata.CalendarData
import com.indieteam.mytask.ui.MainActivity
import jxl.Workbook

class ReadExel(val activity: MainActivity){

    fun readTkbExel() {
        object : Thread() {
            override fun run() {
                activity.apply {
                    if (file.exists()) {
                        val workbook = Workbook.getWorkbook(file)
                        val sheet = workbook.getSheet(0)
                        for (i in 10 until 22) {
                            //Hàm CELL để tra cứu thông tin của một ô trong Excel
                            // Code duoi day tra cuu tung hang (trong moi hang tra cuu tung cot)
                            // getCell(collum, row)
                            //Log.d("row", i.toString())
                            nameSubject = ""
                            tc = ""
                            time = ""
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
                                            time += cell
                                        }
                                    }
                                }
                            }
                            calendarDataTemp.add(CalendarData(nameSubject, tc, time))
                        }
                        readExelCallback = 1
                    } else {
                        readExelCallback = -1
                        activity.runOnUiThread {
                            Toast.makeText(this, "file is not exists", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                this.join()
            }
        }.start()
    }

}