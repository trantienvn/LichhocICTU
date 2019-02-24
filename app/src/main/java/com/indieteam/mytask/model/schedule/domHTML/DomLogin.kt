package com.indieteam.mytask.model.schedule.domHTML

import android.annotation.SuppressLint
import android.content.Context
import android.preference.PreferenceManager
import android.text.Html
import android.util.Log
import com.indieteam.mytask.collection.UrlAddress
import com.indieteam.mytask.ui.interface_.OnLoginListener
import org.jsoup.Connection
import org.jsoup.Jsoup

@Suppress("DEPRECATION")
class DomLogin(val context: Context, private val userName: String, private val passWord: String, private val onLoginListener: OnLoginListener) : Thread() {

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
    private val sharedPref = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

    init {
        classContextName = context.javaClass.name.substring(context.javaClass.name.lastIndexOf(".") + 1, context.javaClass.name.length)
    }

    @SuppressLint("SetTextI18n")
    override fun run() {
        login()
        join()
    }

    private fun login() {
        try {
            val response = Jsoup.connect(UrlAddress.loginClean)
                    .followRedirects(false)
                    .method(Connection.Method.GET)
                    .execute()
            val location = response.header("Location")
            sessionUrl = location.substring(location.indexOf("S(") + 2, location.indexOf("))"))
            Log.d("sessionUrlInLogin", sessionUrl)

            if (sessionUrl.isNotBlank()) {
                val response2 = Jsoup.connect(UrlAddress.loginSession(sessionUrl))
                        .method(Connection.Method.GET)
                        .execute()

                val inputTags = response2.parse().select("input")
                for (i in inputTags) {
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

                val response3 = Jsoup.connect(UrlAddress.loginSession(sessionUrl))
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
                if (response3.cookie("SignIn") != null) {
                    sharedPref.edit().apply {
                        putString("username", userName)
                                .apply()
                    }
                    cookie = response3.cookie("SignIn")

                    onLoginListener.onSuccess(userName, passWord, cookie, sessionUrl)
                } else {
                    onLoginListener.onFail()
                }
                Log.d("cookie", cookie)
            } else {
                onLoginListener.onThrow("sessionUrl is blank")
            }
        } catch (e: Exception) {
            onLoginListener.onThrow("Mất kết nối")
            e.printStackTrace()
        }
    }
}