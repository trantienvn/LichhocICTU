package com.indieteam.mytask.process.domHTML

import android.content.Context
import android.text.Html
import android.util.Log
import android.widget.Toast
import com.indieteam.mytask.address.UrlAddress
import com.indieteam.mytask.ui.LoginActivity
import kotlinx.android.synthetic.main.fragment_process_bar.*
import org.jsoup.Connection
import org.jsoup.Jsoup

class DomLogin(val context: Context, private val userName: String, private val passWord: String): Thread(){

    private val urlAddress = UrlAddress()
    private var sessionUrl = ""

    var pageHeader1drpNgonNgu = "010527EFBEB84BCA8919321CFD5C3A34"
    private var __EVENTTARGET = ""
    private var __EVENTARGUMENT = ""
    private var __LASTFOCUS = ""
    private var __VIEWSTATE = ""
    private var __VIEWSTATEGENERATOR = ""
    private var __EVENTVALIDATION = ""
    private var pageHeader1hidisNotify = ""
    private var pageHeader1hidValueNotify = ""
    private var btnSubmit = "Đăng nhập"
    private var hidUserId = ""
    private var hidUserFullName = ""
    private var hidTrainingSystemId = ""
    private var characterDolla = Html.fromHtml("&#36;")
    private var loginActivity = context as LoginActivity

    override fun run() {
        try{
            val res = Jsoup.connect(urlAddress.urlLoginClean)
                    .followRedirects(false)
                    .method(Connection.Method.GET)
                    .execute()
            val location = res.header("Location")
            sessionUrl = location.substring(location.indexOf("S(") + 2, location.indexOf("))"))
//            Log.d("location", res.header("Location"))
//            Log.d("sessionUrl", loginActivity.sessionUrl)

            if(sessionUrl.isNotBlank()) {
                val resFirst = Jsoup.connect(urlAddress.urlLoginSession(sessionUrl))
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
                loginActivity.runOnUiThread {
                    Toast.makeText(loginActivity, "Err #01", Toast.LENGTH_SHORT).show()
                }
                this.join()
            }

            if(sessionUrl.isNotBlank()) {
                val resLogin = Jsoup.connect(urlAddress.urlLoginSession(sessionUrl))
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
                    loginActivity.runOnUiThread {
                        //Toast.makeText(this@LoginActivity, "Đã đăng nhập", Toast.LENGTH_SHORT).show()
                        loginActivity.supportFragmentManager.findFragmentByTag("processBarFragment")?.let {
                            loginActivity.runOnUiThread {
                                loginActivity.process.text = "Đăng nhập...OK"
                            }
                        }
                        var insertCallback = 0
                        try {
                            loginActivity.sqlLite.insertInfo(userName, passWord, cookie)
                            insertCallback = 1
                        }catch (e: Exception){ Log.d("err", e.toString()) }
                        if(insertCallback == 0){
                            try {
                                loginActivity.sqlLite.updateInfo(userName, passWord, cookie)
                            }catch (e: Exception){ Log.d("err", e.toString()) }
                        }
                        DomDownloadExel(loginActivity, sessionUrl, cookie).start()
                    }
                } else {
                    loginActivity.supportFragmentManager.findFragmentByTag("processBarFragment")?.let {
                        loginActivity.supportFragmentManager.beginTransaction().remove(it)
                                .commit()
                    }
                    loginActivity.runOnUiThread {
                        loginActivity.visibly()
                        Toast.makeText(loginActivity, "Sai mã sinh viên hoặc mật khẩu", Toast.LENGTH_SHORT).show()
                    }
                    loginActivity.clickLogin = 0
                }
                Log.d("cookie", cookie)
            }else{
                loginActivity.runOnUiThread {
                    Toast.makeText(loginActivity, "Err #02", Toast.LENGTH_SHORT).show()
                }
            }

        }catch (e: Exception) {
            Log.d("Err", "$e")
            loginActivity.supportFragmentManager.findFragmentByTag("processBarFragment")?.let {
                loginActivity.supportFragmentManager.beginTransaction().remove(it)
                        .commit()
            }
            loginActivity.runOnUiThread {
                loginActivity.visibly()
                loginActivity.clickLogin = 0
                Toast.makeText(loginActivity, "Err #03 (Not Internet, ...)", Toast.LENGTH_SHORT).show()
            }
        }
        this.join()
    }
}