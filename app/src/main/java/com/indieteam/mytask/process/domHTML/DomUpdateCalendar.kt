package com.indieteam.mytask.process.domHTML

import android.content.Context
import android.content.Intent
import android.text.Html
import android.util.Log
import android.widget.Toast
import com.indieteam.mytask.process.calendar.v2.ReadExel
import com.indieteam.mytask.ui.WeekActivity
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.io.File
import java.io.FileOutputStream

class DomUpdateCalendar(val context: Context, val signIn: String): Thread() {

    private var drpSemester = ""
    private var drpTerm = ""
    private var weekActivity = context as WeekActivity
    private var characterDolla = Html.fromHtml("&#36;")
    private var sessionUrl = ""
    var pageHeader1drpNgonNgu = "010527EFBEB84BCA8919321CFD5C3A34"
    // miss drpSemester,drpTerm, drpType

    override fun run() {
        try {
            val res = Jsoup.connect("http://dangkytinchi.ictu.edu.vn/kcntt/login.aspx")
                    .followRedirects(false)
                    .method(Connection.Method.GET)
                    .execute()
            val location = res.header("Location")
            sessionUrl = location.substring(location.indexOf("S(") + 2, location.indexOf("))"))

            val dataMap = mutableMapOf<String, String>()
            if(sessionUrl.isNotBlank()) {
                val resFirst = Jsoup.connect("http://dangkytinchi.ictu.edu.vn/kcntt/(S($sessionUrl))/Reports/Form/StudentTimeTable.aspx")
                        .cookie("SignIn", signIn)
                        .method(Connection.Method.GET)
                        .execute()
                val resFirstParse = resFirst.parse()
                for(i in resFirstParse.select("select")){
                    //Hoc ky
                    if(i.attr("name") == "drpSemester"){
                        for (j in i.select("option")){
                            if(j.attr("selected") == "selected"){
                                drpSemester = j.attr("value")
                            }
                        }
                    }
                    //Dot hoc
                    if(i.attr("name") == "drpTerm"){
                        for (j in i.select("option")){
                            if (j.attr("selected") == "selected"){
                                drpTerm = j.attr("value")
                            }
                        }
                    }
                    //Nam hoc
                    if(i.attr("name") == "drpSemester"){
                        for (j in i.select("option")){
                            if (j.attr("selected") == "selected"){
                                drpTerm = j.attr("value")
                            }
                        }
                    }
                }
//                    Log.d("drpSemester", drpSemester)
//                    Log.d("drpTerm", drpTerm)
                for (i in resFirstParse.select("input")) {
//                        Log.d("name", i.attr("name"))
//                        Log.d("value", i.`val`())
                    dataMap[i.attr("name")] = i.`val`()
                }
            }else{
                weekActivity.runOnUiThread {
                    Toast.makeText(weekActivity, "Err #04", Toast.LENGTH_SHORT).show()
                }
                this.join()
            }

            if(sessionUrl.isNotBlank() && dataMap.isNotEmpty() &&
                    drpSemester.isNotBlank() && drpTerm.isNotBlank()) {
                val resDownloadExel = Jsoup.connect("http://dangkytinchi.ictu.edu.vn/kcntt/(S($sessionUrl))/Reports/Form/StudentTimeTable.aspx")
                        .data("PageHeader1${characterDolla}drpNgonNgu", pageHeader1drpNgonNgu)
                        .data("drpSemester", drpSemester)
                        .data("drpTerm", drpTerm)
                        .data("drpType", "B")
                        .data(dataMap)
                        .cookie("SignIn", signIn)
                        .method(Connection.Method.POST)
                        .ignoreContentType(true)
                        .execute()
                Log.d("response", resDownloadExel.contentType())
                if (resDownloadExel.contentType() == "application/vnd.ms-excel; charset=utf-8") {
                    try {
                        val dir = File(weekActivity.filesDir, "exel")
                        if (!dir.exists()) {
                            dir.mkdirs()
                        }

                        val file = File(weekActivity.filesDir, "exel/tkb_v2.xls")
                        if (file.exists())
                            file.delete()

                        val fos = FileOutputStream(file)
                        fos.write(resDownloadExel.bodyAsBytes())
                        fos.close()
                        // read and save to sqlLite
                        val readExel = ReadExel(weekActivity)
                        readExel.readTkb()
                        Log.d("readExelCallBack", readExel.readExelCallBack.toString())
                        if(readExel.readExelCallBack == -1){
                            weekActivity.runOnUiThread {
                                Toast.makeText(weekActivity, "Err #05", Toast.LENGTH_SHORT).show()
                            }
                        }else {
//                            weekActivity.runOnUiThread {
//                                Toast.makeText(weekActivity, "Đã cập nhật", Toast.LENGTH_SHORT).show()
//                            }
                            weekActivity.supportFragmentManager.findFragmentByTag("processBarUpdate")?.let {
                                weekActivity.supportFragmentManager.beginTransaction().remove(it)
                            }
                            weekActivity.startActivity(weekActivity.intent)
                            weekActivity.finish()
                        }
                    } catch (e: Exception) {
                        Log.d("Err", e.toString())
                        weekActivity.runOnUiThread {
                            Toast.makeText(weekActivity, "Err #06", Toast.LENGTH_SHORT).show()
                        }
                        this.join()
                    }
                }else{
                    weekActivity.runOnUiThread {
                        Toast.makeText(weekActivity, "Err #07", Toast.LENGTH_SHORT).show()
                    }
                    this.join()
                }
            }else{
                weekActivity.runOnUiThread {
                    Toast.makeText(weekActivity, "Err #08", Toast.LENGTH_SHORT).show()
                }
                this.join()
            }
        }catch (e: Exception){
            Log.d("err", e.toString())
            weekActivity.supportFragmentManager.findFragmentByTag("processBarUpdate")?.let {
                weekActivity.supportFragmentManager.beginTransaction().remove(it).commit()
            }
            weekActivity.runOnUiThread {
                weekActivity.visible()
                Toast.makeText(weekActivity, "Err #09 (Not Internet, ...)", Toast.LENGTH_SHORT).show()
            }
            this.join()
        }
        this.join()
    }
}
