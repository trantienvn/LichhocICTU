package com.indieteam.mytask.process.domHTML

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.Html
import android.util.Log
import android.widget.Toast
import com.indieteam.mytask.address.UrlAddress
import com.indieteam.mytask.process.calendar.v2.ReadExel
import com.indieteam.mytask.sqlite.SqLite
import com.indieteam.mytask.ui.LoginActivity
import com.indieteam.mytask.ui.WeekActivity
import kotlinx.android.synthetic.main.fragment_process_bar.*
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.io.File
import java.io.FileOutputStream

@Suppress("DEPRECATION")
class DomDownloadExel(val context: Context, private val sessionUrl: String, private val signIn: String): Thread() {

    private var urlAddress = UrlAddress()

    var pageHeader1drpNgonNgu = "010527EFBEB84BCA8919321CFD5C3A34"
    private var drpSemester = ""
    private var drpTerm = ""
    private var drpTermArr = ArrayList<String>()
    private var characterDolla = Html.fromHtml("&#36;")
    private var err = 0
    private val sqlLite = SqLite(context)
    private var classContextName = ""
    private var readExel = ReadExel(context)

    init {
        classContextName = context.javaClass.name.substring(context.javaClass.name.lastIndexOf(".") + 1, context.javaClass.name.length)
    }

    @SuppressLint("SetTextI18n")
    override fun run() {
        try {
            if (classContextName == "LoginActivity") {
                (context as LoginActivity).supportFragmentManager.findFragmentByTag("processBarFragment")?.let {
                    context.runOnUiThread {
                        context.process.text = "Lưu Exel..."
                    }
                }
            }
            val dataMap = mutableMapOf<String, String>()
            // start get post params
            if(sessionUrl.isNotBlank()) {
                val resFirst = Jsoup.connect(urlAddress.urlDownloadExel(sessionUrl))
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
                if (classContextName == "LoginActivity") {
                    (context as LoginActivity).runOnUiThread {
                        Toast.makeText(context, "Err #04", Toast.LENGTH_SHORT).show()
                    }
                }
            }// end get post params

            if(sessionUrl.isNotBlank()) {
                val resSecond = Jsoup.connect(urlAddress.urlDownloadExel(sessionUrl))
                        .data("PageHeader1${characterDolla}drpNgonNgu", pageHeader1drpNgonNgu)
                        .data("drpSemester", /*"73FB2DDC455D410C978AB31459812122"*/ drpSemester)
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
                            Log.d("dot_hoc drpTerm", j.attr("value"))
                        }
                    }
                }

                dataMap.clear()

                for (i in resSecondParse.select("input")) {
                    dataMap[i.attr("name")] = i.`val`()
                }

            }

            if(sessionUrl.isNotBlank() && dataMap.isNotEmpty() &&
                    drpSemester.isNotBlank() && drpTermArr.isNotEmpty()) {

                //loop all dot hoc
                for (drpTerm in drpTermArr) {
                    val resDownloadExel = Jsoup.connect(urlAddress.urlDownloadExel(sessionUrl))
                            .data("PageHeader1${characterDolla}drpNgonNgu", pageHeader1drpNgonNgu)
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
                            val dir = File(context.filesDir, "exel")
                            if (!dir.exists()) {
                                dir.mkdirs()
                            }

                            val file = File(context.filesDir, "exel/tkb_v2.xls")
                            if (file.exists())
                                file.delete()

                            val fos = FileOutputStream(file)
                            fos.write(resDownloadExel.bodyAsBytes())
                            fos.close()
                            // read
                            readExel.readTkb()
                            if (readExel.readExelCallBack == -1 || readExel.readExelCallBack == 0) {
                                if (classContextName == "LoginActivity") {
                                    (context as LoginActivity).runOnUiThread {
                                        err = 1
                                        Toast.makeText(context, "Err #05", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.d("Err", e.toString())
                            if (classContextName == "LoginActivity") {
                                (context as LoginActivity).runOnUiThread {
                                    err = 1
                                    Toast.makeText(context, "Err #06", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } else {
                        if (classContextName == "LoginActivity") {
                            (context as LoginActivity).runOnUiThread {
                                err = 1
                                Toast.makeText(context, "Err #07", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                if(err == 0) {
                    save(); done()
                } else{
                    if (classContextName == "LoginActivity") {
                        (context as LoginActivity).runOnUiThread {
                            Toast.makeText(context, "Err", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }else{
                if (classContextName == "LoginActivity") {
                    (context as LoginActivity).runOnUiThread {
                        Toast.makeText(context, "Err #08", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }catch (e: Exception){
            Log.d("err", e.toString())
            if (classContextName == "LoginActivity") {
                (context as LoginActivity).runOnUiThread {
                    Toast.makeText(context, "Kiểm tra lại kết nối", Toast.LENGTH_SHORT).show()
                }
            }
        }
        this.join()
    }

    @SuppressLint("SetTextI18n")
    private fun done(){
        if (classContextName == "LoginActivity") {
            (context as LoginActivity).apply {
                supportFragmentManager.findFragmentByTag("processBarFragment")?.let {
                    runOnUiThread {
                        process.text = "Lưu Exel...Ok"
                    }
                }
                val intent = Intent(context, WeekActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        if (classContextName == "WeekActivity") {
            (context as WeekActivity).apply {
                val intent = Intent(context, WeekActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun save(){
        readExel.exelToJson.toJson(readExel.rawCalendarObjArr)
        readExel.exelToJson.jsonObject.put("info", readExel.infoObj)
        readExel.exelToJson.jsonObject.put("calendar", readExel.exelToJson.jsonArray)
        try {
            sqlLite.deleteCalendar()
            sqlLite.insertCalender(readExel.exelToJson.jsonObject.toString())
        }catch (e: Exception){
            Log.d("Err save", e.toString())
        }
    }
}
