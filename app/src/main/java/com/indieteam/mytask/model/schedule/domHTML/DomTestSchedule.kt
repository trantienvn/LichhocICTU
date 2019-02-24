package com.indieteam.mytask.model.schedule.domHTML

import android.content.Context
import android.text.Html
import android.util.Log
import com.indieteam.mytask.collection.UrlAddress
import com.indieteam.mytask.model.SqLite
import com.indieteam.mytask.ui.interface_.OnDomTestScheduleListener
import org.jsoup.Connection
import org.jsoup.Jsoup

class DomTestSchedule(context: Context, private val onDomTestScheduleListener: OnDomTestScheduleListener, private val semesterValue: String,
                      private val typeValue: String) : Thread() {

    private val params = mutableMapOf<String, String>()
    private lateinit var sessionUrl: String
    private val sqLite = SqLite(context)
    var pageHeader1drpNgonNgu = "010527EFBEB84BCA8919321CFD5C3A34"
    private var characterDolla = Html.fromHtml("&#36;")
    private var drpDotThi = ""
    private var __EVENTARGUMENT = ""
    private var __LASTFOCUS = ""
    private var __VIEWSTATE = ""
    private var __VIEWSTATEGENERATOR = ""
    private var __EVENTVALIDATION = ""
    private var PageHeader1_hidisNotify = "1"
    private var PageHeader1_hidValueNotify = ""
    private var hidShowShiftEndTime = ""
    private var hidExamShowNote = ""
    private var hidStudentId = ""
    private var hidEsShowRoomCode = ""
    private var hidDangKyChungChiThuocHeRieng = ""

    override fun run() {
        get()
        join()
    }

    private fun get() {
        val cookie = sqLite.readCookie()

        val response = Jsoup.connect(UrlAddress.loginClean)
                .followRedirects(false)
                .cookie("SignIn", cookie)
                .method(Connection.Method.GET)
                .execute()

        val location = response.header("Location")

        sessionUrl = location.substring(location.indexOf("S(") + 2, location.indexOf("))"))

        if (!sessionUrl.isNullOrBlank()) {
            val response2 = Jsoup.connect(UrlAddress.testSchedule(sessionUrl))
                    .cookie("SignIn", cookie)
                    .method(Connection.Method.GET)
                    .execute()

            val html = response2.parse()

            for (i in html.select("input")) {
                if (i.attr("name") != "btnList" || i.attr("name") != "btnPrint") {
                    //Log.d(i.attr("name"), i.`val`())

                    when (i.attr("name")) {
                        "__VIEWSTATE" -> {
                            Log.d(i.attr("name"), i.`val`())
                            __VIEWSTATE = i.`val`()
                        }
                        "__VIEWSTATEGENERATOR" -> {
                            Log.d(i.attr("name"), i.`val`())
                            __VIEWSTATEGENERATOR = i.`val`()
                        }
                        "__EVENTVALIDATION" -> {
                            Log.d(i.attr("name"), i.`val`())
                            __EVENTVALIDATION = i.`val`()
                        }
                        "PageHeader1${characterDolla}hidValueNotify" -> {
                            Log.d(i.attr("name"), i.`val`())
                            PageHeader1_hidValueNotify = i.`val`()
                        }
                        "hidShowShiftEndTime" -> {
                            Log.d(i.attr("name"), i.`val`())
                            hidShowShiftEndTime = i.`val`()
                        }
                        "hidStudentId" -> {
                            Log.d(i.attr("name"), i.`val`())
                            hidStudentId = i.`val`()
                        }
                        "hidEsShowRoomCode" -> {
                            Log.d(i.attr("name"), i.`val`())
                            hidEsShowRoomCode = i.`val`()
                        }
                    }
                }
            }

            val response3 = Jsoup.connect(UrlAddress.testSchedule(sessionUrl))
                    .cookie("SignIn", cookie)
                    .data("__EVENTTARGET", "drpSemester")
                    .data("__EVENTARGUMENT", __EVENTARGUMENT)
                    .data("__LASTFOCUS", __LASTFOCUS)
                    .data("__VIEWSTATE", __VIEWSTATE)
                    .data("__VIEWSTATEGENERATOR", __VIEWSTATEGENERATOR)
                    .data("__EVENTVALIDATION", __EVENTVALIDATION)
                    .data("PageHeader1${characterDolla}drpNgonNgu", pageHeader1drpNgonNgu)
                    .data("PageHeader1${PageHeader1_hidisNotify}hidisNotify", PageHeader1_hidisNotify)
                    .data("PageHeader1${characterDolla}hidValueNotify", PageHeader1_hidValueNotify)
                    .data("drpSemester", semesterValue)
                    .data("drpDotThi", "")
                    .data("drpExaminationNumber", "0")
                    .data("hidShowShiftEndTime", hidShowShiftEndTime)
                    .data("hidStudentId", hidStudentId)
                    .data("hidEsShowRoomCode", hidEsShowRoomCode)
                    .data("hidExamShowNote", hidExamShowNote)
                    .data("hidDangKyChungChiThuocHeRieng", hidDangKyChungChiThuocHeRieng)
                    .method(Connection.Method.POST)
                    .execute()

            val html2 = response3.parse()

            for (i in html2.select("select")) {
                if (i.attr("name") == "drpDotThi") {
                    for (j in i.select("option")) {
                        if (j.attr("selected") == "selected") {
                            Log.d("selected", j.`val`())
                            drpDotThi = j.`val`()
                        }
                    }
                }
            }

            val response4 = Jsoup.connect(UrlAddress.testSchedule(sessionUrl))
                    .cookie("SignIn", cookie)
                    .data("__EVENTTARGET", "drpSemester")
                    .data("__EVENTARGUMENT", __EVENTARGUMENT)
                    .data("__LASTFOCUS", __LASTFOCUS)
                    .data("__VIEWSTATE", __VIEWSTATE)
                    .data("__VIEWSTATEGENERATOR", __VIEWSTATEGENERATOR)
                    .data("__EVENTVALIDATION", __EVENTVALIDATION)
                    .data("PageHeader1${characterDolla}drpNgonNgu", pageHeader1drpNgonNgu)
                    .data("PageHeader1${PageHeader1_hidisNotify}hidisNotify", PageHeader1_hidisNotify)
                    .data("PageHeader1${characterDolla}hidValueNotify", PageHeader1_hidValueNotify)
                    .data("drpSemester", semesterValue)
                    .data("drpDotThi", "")
                    .data("drpExaminationNumber", typeValue)
                    .data("hidShowShiftEndTime", hidShowShiftEndTime)
                    .data("hidStudentId", hidStudentId)
                    .data("hidEsShowRoomCode", hidEsShowRoomCode)
                    .data("hidExamShowNote", hidExamShowNote)
                    .data("hidDangKyChungChiThuocHeRieng", hidDangKyChungChiThuocHeRieng)
                    .method(Connection.Method.POST)
                    .execute()

            val html3 = response4.parse()

            for (i in html3.select("tbody")) {
                Log.d("tbody", i.html())
            }

        } else
            onDomTestScheduleListener.onThrow("sessionUrl is null or blank")
    }

}