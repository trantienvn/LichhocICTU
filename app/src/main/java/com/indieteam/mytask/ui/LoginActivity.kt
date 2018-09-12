package com.indieteam.mytask.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.Html
import android.util.Log
import android.widget.Toast
import com.indieteam.mytask.R
import kotlinx.android.synthetic.main.activity_login.*
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.io.File
import java.io.FileOutputStream
import android.view.View
import com.indieteam.mytask.sqlite.SqlLite
import kotlinx.android.synthetic.main.fragment_process_bar.*
import java.security.NoSuchAlgorithmException


class LoginActivity : AppCompatActivity() {

    private val REQUEST_CODE = 1
    private var allPermission= 0
    private var __EVENTTARGET = ""
    private var __EVENTARGUMENT = ""
    private var __LASTFOCUS = ""
    private var __VIEWSTATE = ""
    private var __VIEWSTATEGENERATOR = ""
    private var __EVENTVALIDATION = ""
    private var pageHeader1drpNgonNgu = "010527EFBEB84BCA8919321CFD5C3A34"
    private var pageHeader1hidisNotify = ""
    private var pageHeader1hidValueNotify = ""
    private var btnSubmit = "Đăng nhập"
    private var hidUserId = ""
    private var hidUserFullName = ""
    private var hidTrainingSystemId = ""
    private var characterDolla = Html.fromHtml("&#36;")
    private var sessionUrl = ""
    private lateinit var sqlLite: SqlLite
    var readDb = 0

    inner class DomLogin(val userName: String, val passWord: String): Thread(){
        override fun run() {
            try{
                val res = Jsoup.connect("http://dangkytinchi.ictu.edu.vn/kcntt/login.aspx")
                        .userAgent("Mozilla/5.0 (X11; Linux x86_64; rv:62.0) Gecko/20100101 Firefox/62.0")
                        .followRedirects(false)
                        .method(Connection.Method.GET)
                        .execute()
                val location = res.header("Location")
                sessionUrl = location.substring(location.indexOf("S(") + 2, location.indexOf("))"))
                Log.d("location", res.header("Location"))
                Log.d("sessionUrl", sessionUrl)

                if(sessionUrl.isNotBlank()) {
                    val resFirst = Jsoup.connect("http://dangkytinchi.ictu.edu.vn/kcntt/(S($sessionUrl))/login.aspx")
                            .method(Connection.Method.GET)
                            .execute()

                    val inputs = resFirst.parse().select("input")
                    for (i in inputs) {
//                    Log.d("name", i.attr("name"))
//                    Log.d("value", i.`val`())
                        when (i.attr("name")) {
                            "__EVENTTARGET" -> __EVENTTARGET = i.`val`()
                            "__EVENTARGUMENT" -> __EVENTARGUMENT = i.`val`()
                            "__LASTFOCUS" -> __LASTFOCUS = i.`val`()
                            "__VIEWSTATE" -> __VIEWSTATE = i.`val`()
                            "__VIEWSTATEGENERATOR" -> __VIEWSTATEGENERATOR = i.`val`()
                            "__EVENTVALIDATION" -> __EVENTVALIDATION = i.`val`()
                            "PageHeader1${characterDolla}drpNgonNgu" -> pageHeader1drpNgonNgu = i.`val`()
                            "PageHeader1${characterDolla}hidisNotify" -> pageHeader1hidisNotify = i.`val`()
                            "PageHeader1${characterDolla}hidValueNotify" -> pageHeader1hidValueNotify = i.`val`()
                            "hidUserId" -> hidUserId = i.`val`()
                            "hidUserFullName" -> hidUserFullName = i.`val`()
                            "hidTrainingSystemId" -> hidTrainingSystemId = i.`val`()
                        }
                    }
                }else{
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Err #01", Toast.LENGTH_SHORT).show()
                    }
                    this.join()
                }

                if(sessionUrl.isNotBlank()) {
                    val resLogin = Jsoup.connect("http://dangkytinchi.ictu.edu.vn/kcntt/(S($sessionUrl))/login.aspx")
                            .data("__EVENTTARGET", __EVENTTARGET)
                            .data("__EVENTARGUMENT", __EVENTARGUMENT)
                            .data("__LASTFOCUS", __LASTFOCUS)
                            .data("__VIEWSTATE", __VIEWSTATE)
                            .data("__VIEWSTATEGENERATOR", __VIEWSTATEGENERATOR)
                            .data("__EVENTVALIDATION", __EVENTVALIDATION)
                            .data("PageHeader1${characterDolla}hidisNotify", pageHeader1hidisNotify)
                            .data("PageHeader1${characterDolla}hidValueNotify", pageHeader1hidValueNotify)
                            .data("txtUserName", userName)
                            .data("txtPassword", passWord)
                            .data("btnSubmit", btnSubmit)
                            .data("hidUserId", hidUserId)
                            .data("hidUserFullName", hidUserFullName)
                            .data("hidTrainingSystemId", hidTrainingSystemId)
                            .method(Connection.Method.POST)
                            .execute()

                    var cookie = ""
                    if (resLogin.cookie("SignIn") != null) {
                        cookie = resLogin.cookie("SignIn")
                        runOnUiThread {
                            //Toast.makeText(this@LoginActivity, "Đã đăng nhập", Toast.LENGTH_SHORT).show()
                            supportFragmentManager.findFragmentByTag("processBarFragment")?.let {
                                runOnUiThread {
                                    process.text = "Đăng nhập...OK"
                                }
                            }
                            DomDownloadExel(cookie).start()
                        }
                    } else {
                        supportFragmentManager.findFragmentByTag("processBarFragment")?.let {
                            supportFragmentManager.beginTransaction().remove(it)
                                    .commit()
                        }
                        runOnUiThread {
                            visibly()
                            Toast.makeText(this@LoginActivity, "Sai mã sinh viên hoặc mật khẩu", Toast.LENGTH_SHORT).show()
                        }
                        clickLogin = 0
                    }
                    Log.d("cookie", cookie)
                }else{
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Err #02", Toast.LENGTH_SHORT).show()
                    }
                    this.join()
                }

            }catch (e: Exception) {
                Log.d("Err", "$e")
                supportFragmentManager.findFragmentByTag("processBarFragment")?.let {
                    supportFragmentManager.beginTransaction().remove(it)
                            .commit()
                }
                runOnUiThread {
                    visibly()
                    clickLogin = 0
                    Toast.makeText(this@LoginActivity, "Err #03 (Not Internet, ...)", Toast.LENGTH_SHORT).show()
                }
                this.join()
            }
            this.join()
        }
    }

    inner class DomDownloadExel(val signIn: String): Thread() {

        private var drpSemester = ""
        private var drpTerm = ""
        // miss drpSemester,drpTerm, drpType

        override fun run() {
            try {
                supportFragmentManager.findFragmentByTag("processBarFragment")?.let {
                    runOnUiThread {
                        process.text = "Lưu Exel..."
                    }
                }
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
                        //if (i.attr("name") != "hidAcademicYearId")
                        dataMap[i.attr("name")] = i.`val`()
                    }
                }else{
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Err #04", Toast.LENGTH_SHORT).show()
                    }
                    this.join()
                }

                if(sessionUrl.isNotBlank() && dataMap.isNotEmpty() &&
                        drpSemester.isNotBlank() && drpTerm.isNotBlank()) {
                    val resDownloadExel = Jsoup.connect("http://dangkytinchi.ictu.edu.vn/kcntt/(S($sessionUrl))/Reports/Form/StudentTimeTable.aspx")
                            //.data("hidAcademicYearId", "C63BA53030A54AA490637046CCE5FFDC")
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
                            val dir = File(filesDir, "exel")
                            if (!dir.exists()) {
                                dir.mkdirs()
                            }

                            val file = File(filesDir, "exel/tkb_v2.xls")
                            if (file.exists())
                                file.delete()

                            val fos = FileOutputStream(file)
                            fos.write(resDownloadExel.bodyAsBytes())
                            fos.close()
                            supportFragmentManager.findFragmentByTag("processBarFragment")?.let {
                                runOnUiThread {
                                    process.text = "Lưu Exel...Ok"
                                }
                            }
                            val intent = Intent(this@LoginActivity, WeekActivity::class.java)
                            startActivity(intent)
                            finish()
                        } catch (e: Exception) {
                            Log.d("Err", e.toString())
                            runOnUiThread {
                                Toast.makeText(this@LoginActivity, "Err #05", Toast.LENGTH_SHORT).show()
                            }
                            this.join()
                        }
                    }else{
                        runOnUiThread {
                            Toast.makeText(this@LoginActivity, "Err #06", Toast.LENGTH_SHORT).show()
                        }
                        this.join()
                    }
                }else{
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Err #07", Toast.LENGTH_SHORT).show()
                    }
                    this.join()
                }
            }catch (e: Exception){
                Log.d("err", e.toString())
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Err #08", Toast.LENGTH_SHORT).show()
                }
                this.join()
            }
            this.join()
        }
    }

    private fun init(){
        sqlLite = SqlLite(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init()
        try {
            sqlLite.read()
            readDb = 1
        }catch (e: Exception){ Log.d("Err", e.toString()) }

        if(readDb == 0) {
            checkPermission()
            if (allPermission == 1)
                run()
            else
                checkPermission()
        }else{
            val intent = Intent(this@LoginActivity, WeekActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun toMD5(s: String): String {
        val MD5 = "MD5"
        try {
            // Create MD5 Hash
            val digest = java.security.MessageDigest
                    .getInstance(MD5)
            digest.update(s.toByteArray())
            val messageDigest = digest.digest()

            // Create Hex String
            val hexString = StringBuilder()
            for (aMessageDigest in messageDigest) {
                var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
                while (h.length < 2)
                    h = "0$h"
                hexString.append(h)
            }
            return hexString.toString()

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        return ""
    }

    private fun visibly(){
        linearLayout.visibility = View.VISIBLE
        btn_login.visibility = View.VISIBLE
        developer.visibility = View.VISIBLE
    }

    private fun gone(){
        linearLayout.visibility = View.GONE
        btn_login.visibility = View.GONE
        developer.visibility = View.GONE
    }

    private var clickLogin = 0

    private fun run(){
        btn_login.setOnClickListener {
            if (text_username.text.toString().isNotBlank() && text_password.text.toString().isNotBlank() && clickLogin == 0) {
                gone()
                supportFragmentManager.beginTransaction().add(R.id.login_root_view, ProcessBarFragment(), "processBarFragment")
                        .commit()
                val md5Password = toMD5(text_password.text.toString())
                Log.d("md5password", md5Password)
                DomLogin(text_username.text.toString(), md5Password).start()
                clickLogin++
            }
        }
    }

    private fun checkPermission(){
        if(Build.VERSION.SDK_INT >= 23) {
            if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE)
            }else{
                allPermission = 1
            }
        }else{
            run()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == REQUEST_CODE){
            if(grantResults.size == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                run()
            }
        }else{
            Toast.makeText(this@LoginActivity, "Permissions is not granted", Toast.LENGTH_LONG).show()
        }
    }
}
