package com.indieteam.mytask.process.domHTML

import android.content.Context
import android.content.Intent
import android.text.Html
import android.util.Log
import android.widget.Toast
import com.indieteam.mytask.process.calendar.v2.ReadExel
import com.indieteam.mytask.sqlite.SqlLite
import com.indieteam.mytask.ui.LoginActivity
import com.indieteam.mytask.ui.WeekActivity
import kotlinx.android.synthetic.main.fragment_process_bar.*
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.io.File
import java.io.FileOutputStream

class DomDownloadExel(val context: Context, private val signIn: String): Thread() {

    private var drpSemester = ""
    private var drpTerm = ""
    private var drpTermArr = ArrayList<String>()
    private var loginActivity = context as LoginActivity
    private var characterDolla = Html.fromHtml("&#36;")
    private var err = 0

    private val sqlLite = SqlLite(context)

    var readExel = ReadExel(loginActivity)
    // miss drpSemester,drpTerm, drpType

    override fun run() {
        try {
            loginActivity.supportFragmentManager.findFragmentByTag("processBarFragment")?.let {
                loginActivity.runOnUiThread {
                    loginActivity.process.text = "Lưu Exel..."
                }
            }
            val dataMap = mutableMapOf<String, String>()
            if(loginActivity.sessionUrl.isNotBlank()) {
                val resFirst = Jsoup.connect("http://dangkytinchi.ictu.edu.vn/kcntt/(S(${loginActivity.sessionUrl}))/Reports/Form/StudentTimeTable.aspx")
                        .cookie("SignIn", signIn)
                        .method(Connection.Method.GET)
                        .execute()
                val resFirstParse = resFirst.parse()
                for(i in resFirstParse.select("select")){
                    //Hoc ky
                    if(i.attr("name") == "drpSemester"){
                        for (j in i.select("option")){
                            //Log.d("drpSemester", j.attr("value"))
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
                }
//                    Log.d("drpSemester", drpSemester)
//                    Log.d("drpTerm", drpTerm)
                for (i in resFirstParse.select("input")) {
//                        Log.d("name", i.attr("name"))
//                        Log.d("value", i.`val`())
                    dataMap[i.attr("name")] = i.`val`()
                    //Log.d("input", i.`val`())
                }
            }else{
                loginActivity.runOnUiThread {
                    err = 1
                    Toast.makeText(loginActivity, "Err #04", Toast.LENGTH_SHORT).show()
                }
            }

            if(loginActivity.sessionUrl.isNotBlank()) {
                val resSecond = Jsoup.connect("http://dangkytinchi.ictu.edu.vn/kcntt/(S(${loginActivity.sessionUrl}))/Reports/Form/StudentTimeTable.aspx")
                        .data("PageHeader1${characterDolla}drpNgonNgu", loginActivity.pageHeader1drpNgonNgu)
                        .data("drpSemester", /*"73FB2DDC455D410C978AB31459812122"*/drpSemester)
                        .data("drpTerm", drpTerm)
                        .data("drpType", "B")
                        .data(dataMap)
                        .cookie("SignIn", signIn)
                        .method(Connection.Method.POST)
                        .ignoreContentType(true)
                        .execute()

                val resSecondParse = resSecond.parse()
                for(i in resSecondParse.select("select")) {
                    //Dot hoc
                    if (i.attr("name") == "drpTerm") {
                        for (j in i.select("option")) {
                            drpTermArr.add(j.attr("value"))
                            Log.d("drpTerm", j.attr("value"))
                        }
                    }
                }

                dataMap.clear()

                for (i in resSecondParse.select("input")) {
                    dataMap[i.attr("name")] = i.`val`()
                }

            }

            if(loginActivity.sessionUrl.isNotBlank() && dataMap.isNotEmpty() &&
                    drpSemester.isNotBlank() && drpTermArr.isNotEmpty()) {

                //loop all dot hoc
                for (drpTerm in drpTermArr) {
                    val resDownloadExel = Jsoup.connect("http://dangkytinchi.ictu.edu.vn/kcntt/(S(${loginActivity.sessionUrl}))/Reports/Form/StudentTimeTable.aspx")
                            .data("PageHeader1${characterDolla}drpNgonNgu", loginActivity.pageHeader1drpNgonNgu)
                            .data("drpSemester", /*"73FB2DDC455D410C978AB31459812122"*/ drpSemester)
                            .data("drpTerm", drpTerm)
                            .data("drpType", "B")
                            .data("btnView", "Xuất file Excel")
                            .data(dataMap)
                            .cookie("SignIn", signIn)
                            .method(Connection.Method.POST)
                            .ignoreContentType(true)
                            .execute()
                    Log.d("contentType", resDownloadExel.contentType())
                    if (resDownloadExel.contentType() == "application/vnd.ms-excel; charset=utf-8") {
                        try {
                            val dir = File(loginActivity.filesDir, "exel")
                            if (!dir.exists()) {
                                dir.mkdirs()
                            }

                            val file = File(loginActivity.filesDir, "exel/tkb_v2.xls")
                            if (file.exists())
                                file.delete()

                            val fos = FileOutputStream(file)
                            fos.write(resDownloadExel.bodyAsBytes())
                            fos.close()
                            // read
                            readExel.readTkb()
                            if (readExel.readExelCallBack == -1 || readExel.readExelCallBack == 0) {
                                loginActivity.runOnUiThread {
                                    err = 1
                                    Toast.makeText(loginActivity, "Err #05", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            Log.d("Err", e.toString())
                            loginActivity.runOnUiThread {
                                err = 1
                                Toast.makeText(loginActivity, "Err #06", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        loginActivity.runOnUiThread {
                            err = 1
                            Toast.makeText(loginActivity, "Err #07", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                if(err == 0) {
                    save(); done()
                } else{
                    loginActivity.runOnUiThread {
                        Toast.makeText(loginActivity, "Err", Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                loginActivity.runOnUiThread {
                    Toast.makeText(loginActivity, "Err #08", Toast.LENGTH_SHORT).show()
                }
            }
        }catch (e: Exception){
            Log.d("err", e.toString())
            loginActivity.runOnUiThread {
                Toast.makeText(loginActivity, "Err #09", Toast.LENGTH_SHORT).show()
            }
        }
        this.join()
    }

    private fun done(){
        loginActivity.supportFragmentManager.findFragmentByTag("processBarFragment")?.let {
            loginActivity.runOnUiThread {
                loginActivity.process.text = "Lưu Exel...Ok"
            }
        }
        val intent = Intent(loginActivity, WeekActivity::class.java)
        loginActivity.startActivity(intent)
        loginActivity.finish()
    }

    private fun save(){
        readExel.exelToJson.toJson(readExel.calendarRawV2Arr)
        readExel.exelToJson.jsonObject.put("info", readExel.infoObj)
        readExel.exelToJson.jsonObject.put("calendar", readExel.exelToJson.jsonArray)
        try {
            sqlLite.deleteCalendar()
            sqlLite.insertCalender(readExel.exelToJson.jsonObject.toString())
        }catch (e: Exception){
            Log.d("err", e.toString())
        }

    }
}
