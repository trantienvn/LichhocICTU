package com.indieteam.mytask.core.calendar.domHTML

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.preference.PreferenceManager
import android.text.Html
import android.util.Log
import android.widget.Toast
import com.indieteam.mytask.address.UrlAddress
import com.indieteam.mytask.core.sqlite.SqLite
import com.indieteam.mytask.ui.LoginActivity
import com.indieteam.mytask.ui.WeekActivity
import kotlinx.android.synthetic.main.fragment_process_bar.*
import org.jsoup.Connection
import org.jsoup.Jsoup

@Suppress("DEPRECATION")
class DomLogin(val context: Context, private val userName: String, private val passWord: String): Thread(){

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
    private var classContextName = ""
    private val sqlLite = SqLite(context)
    private val sharedPref = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

    init {
        classContextName = context.javaClass.name.substring(context.javaClass.name.lastIndexOf(".") + 1, context.javaClass.name.length)
    }

    @SuppressLint("SetTextI18n")
    override fun run() {
        try{
            val res = Jsoup.connect(UrlAddress.urlLoginClean)
                    .followRedirects(false)
                    .method(Connection.Method.GET)
                    .execute()
            val location = res.header("Location")
            sessionUrl = location.substring(location.indexOf("S(") + 2, location.indexOf("))"))
            Log.d("sessionUrlInLogin", sessionUrl)

            if(sessionUrl.isNotBlank()) {
                val resFirst = Jsoup.connect(UrlAddress.urlLoginSession(sessionUrl))
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
                if (classContextName == "LoginActivity") {
                    (context as LoginActivity).runOnUiThread {
                        Toast.makeText(context, "Err #01", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            if(sessionUrl.isNotBlank()) {
                val resLogin = Jsoup.connect(UrlAddress.urlLoginSession(sessionUrl))
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
                    sharedPref.edit().apply {
                        putString("username", userName)
                                .apply()
                    }
                    cookie = resLogin.cookie("SignIn")
                    if (classContextName == "LoginActivity"){
                        (context as LoginActivity).runOnUiThread {
                            //Toast.makeText(this@LoginActivity, "Đã đăng nhập", Toast.LENGTH_SHORT).show()
                            context.supportFragmentManager.findFragmentByTag("processBarLogin")?.let {
                                context.runOnUiThread {
                                    context.process.text = "Đăng nhập...OK"
                                }
                            }
                        }
                    }

                    if (classContextName == "LoginActivity") {
                        try {
                            sqlLite.insertInfo(userName, passWord, cookie)
                        }catch (e: SQLiteConstraintException){ Log.d("err", e.toString()) }
                        DomGetSemester(context, sessionUrl, cookie).start()
                    }

                    if (classContextName == "WeekActivity") {
                        try {
                            sqlLite.updateInfo(userName, passWord, cookie)
                        }catch (e: SQLiteConstraintException){ Log.d("err", e.toString()) }
                        DomGetSemester(context, sessionUrl, cookie).start()
                    }
                } else {
                    appException("Sai mã sinh viên hoặc mật khẩu")
                }
                Log.d("cookie", cookie)
            }else{
                if (classContextName == "LoginActivity") {
                    (context as LoginActivity).runOnUiThread {
                        Toast.makeText(context, "Err #02", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }catch (e: Exception) {
           appException("Mất kết nối")
        }
        this.join()
    }

    private fun appException(errorName: String){
        if (classContextName == "LoginActivity") {
            (context as LoginActivity).runOnUiThread {
                context.supportFragmentManager.findFragmentByTag("processBarLogin")?.let {
                    context.supportFragmentManager.beginTransaction().remove(it)
                            .commit()
                }
                context.visible()
                context.clickLogin = 0
                Toast.makeText(context, errorName, Toast.LENGTH_SHORT).show()
            }
        }
        if (classContextName == "WeekActivity") {
            (context as WeekActivity).runOnUiThread {
                context.supportFragmentManager.findFragmentByTag("processBarUpdate")?.let {
                    context.supportFragmentManager.beginTransaction().remove(it)
                            .commit()
                }
                context.visible()
                Toast.makeText(context, errorName, Toast.LENGTH_SHORT).show()
            }
        }
    }
}