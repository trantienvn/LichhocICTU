package com.indieteam.mytask.datas.datasources.domHTML

import android.annotation.SuppressLint
import android.content.Context
import android.text.Html
import android.util.Log
import com.indieteam.mytask.models.UrlAddress
import com.indieteam.mytask.datas.datasources.parseData.ParseExcel
import com.indieteam.mytask.datas.SqLite
import com.indieteam.mytask.views.interfaces.OnDownloadExcelListener
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.io.File
import java.io.FileOutputStream

@Suppress("DEPRECATION")
class DomDownloadExcel(val context: Context, private val sessionUrl: String, private val signIn: String,
                       val semesterSelected: String, val onDownloadExcelListener: OnDownloadExcelListener) : Thread() {

    var pageHeader1drpNgonNgu = "010527EFBEB84BCA8919321CFD5C3A34"
    private var drpSemester = ""
    private var drpTerm = "1"
    private var drpTermArr = ArrayList<String>()
    private var characterDolla = Html.fromHtml("&#36;")
    private var err = 0
    private val sqlLite = SqLite(context)
    private var classContextName = ""
    private var readExel = ParseExcel(context)
    private val params = mutableMapOf<String, String>()
    private var emptyCalendar = false
    private var requestTime = 0

    init {
        classContextName = context.javaClass.name.substring(context.javaClass.name.lastIndexOf(".") + 1, context.javaClass.name.length)
    }

    @SuppressLint("SetTextI18n")
    override fun run() {
        onDownloadExcelListener.onDownload(context)
        loadPageToGetParams()
        loadPageWithParams()
        download()
        join()
    }

    private fun loadPageToGetParams() {
        if (sessionUrl.isNotBlank()) {
            try {
                val response = Jsoup.connect(UrlAddress.semester(sessionUrl))
                        .cookie("SignIn", signIn)
                        .method(Connection.Method.GET)
                        .execute()

                val html = response.parse()

                for (input in html.select("input[type=hidden]")) {
                    params[input.attr("name")] = input.`val`()
                }
                val drpTermSelect = html.selectFirst("select[name=drpTerm]");
                val selectedOption = drpTermSelect.selectFirst("option[selected]");
                params["drpTerm"] = selectedOption..`val`();

                val drpTypeSelect = html.selectFirst("select[name=drpType]");
                val selectedOptionType = drpTypeSelect.selectFirst("option[selected]");
                params["drpType"] = selectedOptionType.`val`();
            } catch (e: Exception) {
                onDownloadExcelListener.onThrow("Mất kết nối", context)
                e.printStackTrace()
            }
        }
    }

    private fun loadPageWithParams() {
        if (sessionUrl.isNotBlank()) {
            try {
                requestTime++
                val response: Connection.Response
                if (!emptyCalendar) {
                    response = Jsoup.connect(UrlAddress.downloadExel(sessionUrl))
                            .data(params)
                            .data("PageHeader1${characterDolla}drpNgonNgu", pageHeader1drpNgonNgu)
                            .data("drpTerm", drpTerm)
                            .data("drpType", "K")
                            .data("drpSemester", semesterSelected)
                            .data("txtTuNgay", "")
                            .data("txtDenNgay", "")
                            .cookie("SignIn", signIn)
                            .method(Connection.Method.POST)
                            .ignoreContentType(true)
                            .execute()
                } else {
                    response = Jsoup.connect(UrlAddress.downloadExel(sessionUrl))
                            .data(params)
                            .data("PageHeader1${characterDolla}drpNgonNgu", pageHeader1drpNgonNgu)
                            .data("drpType", "K")
                            .data("drpSemester", semesterSelected)
                            .data("txtTuNgay", "")
                            .data("txtDenNgay", "")
                            .cookie("SignIn", signIn)
                            .method(Connection.Method.POST)
                            .ignoreContentType(true)
                            .execute()
                }

                val html = response.parse()

                for (i in html.select("select")) {
                    //Hoc ky
                    if (i.attr("name") == "drpSemester") {
                        for (j in i.select("option")) {
                            //Log.d("drpSemester", j.attr("value"))
                            if (j.attr("selected") == "selected") {
                                drpSemester = j.attr("value")
                                Log.d("Hoc Ky", j.text())
                            }
                        }
                    }
                    //Dot hoc
                    if (i.attr("name") == "drpTerm") {
                        for (j in i.select("option")) {
                            drpTermArr.add(j.attr("value"))
                            Log.d("Dot hoc", j.text())
                        }
                    }
                }

                params.clear()

                if (drpSemester == "") {
                    Log.d("drpSemester", "blank")
                    emptyCalendar = true
                    if (emptyCalendar && requestTime == 1)
                        loadPageWithParams()
                    else {
                        for (i in html.select("input")) {
                            params[i.attr("name")] = i.`val`()
                        }
                    }
                } else {
                    Log.d("drpSemester", "not blank")
                    for (i in html.select("input")) {
                        params[i.attr("name")] = i.`val`()
                    }
                }

            } catch (e: java.lang.Exception) {
                if (drpSemester.isBlank()) {
                    Log.d("drpSemester", "blank")
                    emptyCalendar = true
                    if (emptyCalendar && requestTime == 1)
                        loadPageWithParams()
                } else {
                    onDownloadExcelListener.onThrow("Mất kết nối", context)
                    e.printStackTrace()
                }
            }
        }
    }

    private fun download() {
        try {
            Log.d("sessionUrl", sessionUrl)
            Log.d("drpSemester", drpSemester)
            Log.d("semesterSelected", semesterSelected)
            params.forEach {
                Log.d("params", it.value)
            }
            drpTermArr.forEach {
                Log.d("drpTermArr", it)
            }
            if (sessionUrl.isNotBlank() && params.isNotEmpty() &&
                    drpSemester.isNotBlank() && drpTermArr.isNotEmpty()) {

                //loop all dot hoc
                for (drpTerm in drpTermArr) {
                    try {
                        val response = Jsoup.connect(UrlAddress.downloadExel(sessionUrl))
                                .data(params)
                                .data("PageHeader1${characterDolla}drpNgonNgu", pageHeader1drpNgonNgu)
                                .data("drpSemester", semesterSelected)
                                .data("drpTerm", drpTerm)
                                .data("drpType", "K")
                                .data("txtTuNgay", "")
                                .data("txtDenNgay", "")
                                .data("btnView", "Xuất file Excel")
                                .cookie("SignIn", signIn)
                                .method(Connection.Method.POST)
                                .ignoreContentType(true)
                                .execute()
                        Log.d("contentType", response.contentType())
                        if (response.contentType() == "application/vnd.ms-excel; charset=utf-8") {
                            try {
                                val dir = File(context.filesDir, "exel")
                                if (!dir.exists())
                                    dir.mkdirs()

                                val file = File(context.filesDir, "exel/tkb_v2.xls")

                                if (file.exists())
                                    file.delete()

                                val fos = FileOutputStream(file)
                                fos.write(response.bodyAsBytes())
                                fos.close()

                                // read
                                readExel.studentSchedule()

                                if (readExel.readExelCallBack == -1 || readExel.readExelCallBack == 0)
                                    onDownloadExcelListener.onThrow("Error read Excel", context)

                            } catch (e: Exception) {
                                onDownloadExcelListener.onThrow("Error file", context)
                                e.printStackTrace()
                            }
                        } else
                            onDownloadExcelListener.onThrow("Cannot Download Excel", context)
                    } catch (e: Exception) {
                        onDownloadExcelListener.onThrow("Mất kết nối", context)
                        e.printStackTrace()
                        break
                    }
                }
                if (err == 0) {
                    saveLocal()
                    onDownloadExcelListener.onSuccess(context)
                }
            } else {
                onDownloadExcelListener.onThrow("Lịch rỗng", context)
            }
        } catch (e: Exception) {
            onDownloadExcelListener.onThrow("Mất kết nối", context)
            e.printStackTrace()
        }
    }

    private fun saveLocal() {
        readExel.exelToJson.toJson(readExel.rawCalendarObjArr)
        readExel.exelToJson.jsonObject.put("info", readExel.infoObj)
        readExel.exelToJson.jsonObject.put("calendar", readExel.exelToJson.jsonArray)
        try {
            sqlLite.deleteSchedule()
            sqlLite.insertSchedule(readExel.exelToJson.jsonObject.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
