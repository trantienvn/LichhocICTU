package com.indieteam.mytask.datas.datasources.domHTML

import android.content.Context
import android.text.Html
import android.util.Log
import com.indieteam.mytask.models.TestScheduleCollection
import com.indieteam.mytask.models.UrlAddress
import com.indieteam.mytask.datas.SqLite
import com.indieteam.mytask.views.interfaces.OnDomTestScheduleListener
import org.jsoup.Connection
import org.jsoup.Jsoup

@Suppress("DEPRECATION")
class DomTestSchedule(context: Context, private val onDomTestScheduleListener: OnDomTestScheduleListener, private val semesterValue: String,
                      private val typeValue: String) : Thread() {

    private val params = mutableMapOf<String, String>()
    private lateinit var sessionUrl: String
    private val sqLite = SqLite(context)
    var pageHeader1drpNgonNgu = "010527EFBEB84BCA8919321CFD5C3A34"
    private var characterDollar = Html.fromHtml("&#36;")
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
                        "PageHeader1${characterDollar}hidValueNotify" -> {
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

//            val response3 = Jsoup.connect(UrlAddress.testSchedule(sessionUrl))
//                    .cookie("SignIn", cookie)
//                    .data("__EVENTTARGET", "drpSemester")
//                    .data("__EVENTARGUMENT", __EVENTARGUMENT)
//                    .data("__LASTFOCUS", __LASTFOCUS)
//                    .data("__VIEWSTATE", __VIEWSTATE)
//                    .data("__VIEWSTATEGENERATOR", __VIEWSTATEGENERATOR)
//                    .data("__EVENTVALIDATION", __EVENTVALIDATION)
//                    .data("PageHeader1${characterDolla}drpNgonNgu", pageHeader1drpNgonNgu)
//                    .data("PageHeader1${PageHeader1_hidisNotify}hidisNotify", PageHeader1_hidisNotify)
//                    .data("PageHeader1${characterDolla}hidValueNotify", PageHeader1_hidValueNotify)
//                    .data("drpSemester", semesterValue)
//                    .data("drpDotThi", "")
//                    .data("drpExaminationNumber", "0")
//                    .data("hidShowShiftEndTime", hidShowShiftEndTime)
//                    .data("hidStudentId", hidStudentId)
//                    .data("hidEsShowRoomCode", hidEsShowRoomCode)
//                    .data("hidExamShowNote", hidExamShowNote)
//                    .data("hidDangKyChungChiThuocHeRieng", hidDangKyChungChiThuocHeRieng)
//                    .method(Connection.Method.POST)
//                    .execute()
//
//            val html2 = response3.parse()
//
//            for (i in html2.select("select")) {
//                if (i.attr("name") == "drpDotThi") {
//                    for (j in i.select("option")) {
//                        if (j.attr("selected") == "selected") {
//                            Log.d("selected", j.`val`())
//                            drpDotThi = j.`val`()
//                        }
//                    }
//                }
//            }

            val response4 = Jsoup.connect(UrlAddress.testSchedule(sessionUrl))
                    .cookie("SignIn", cookie)
                    .data("__EVENTTARGET", "drpSemester")
                    .data("__EVENTARGUMENT", __EVENTARGUMENT)
                    .data("__LASTFOCUS", __LASTFOCUS)
                    .data("__VIEWSTATE", __VIEWSTATE)
                    .data("__VIEWSTATEGENERATOR", __VIEWSTATEGENERATOR)
                    .data("__EVENTVALIDATION", __EVENTVALIDATION)
                    .data("PageHeader1${characterDollar}drpNgonNgu", pageHeader1drpNgonNgu)
                    .data("PageHeader1${PageHeader1_hidisNotify}hidisNotify", PageHeader1_hidisNotify)
                    .data("PageHeader1${characterDollar}hidValueNotify", PageHeader1_hidValueNotify)
                    .data("drpSemester", semesterValue)
                    .data("drpDotThi", drpDotThi)
                    .data("drpExaminationNumber", typeValue)
                    .data("hidShowShiftEndTime", hidShowShiftEndTime)
                    .data("hidStudentId", hidStudentId)
                    .data("hidEsShowRoomCode", hidEsShowRoomCode)
                    .data("hidExamShowNote", hidExamShowNote)
                    .data("hidDangKyChungChiThuocHeRieng", hidDangKyChungChiThuocHeRieng)
                    .method(Connection.Method.POST)
                    .execute()


//            val response4 = Jsoup.connect(UrlAddress.testSchedule(sessionUrl))
//                    .cookie("SignIn", "17A1E3FE6F477311A379CD6404EF8958F7454F741619C5E53095837DF217302F3C29C60842A1B344567B5F9BB3E27A1BB3E0DB03DC647826CA15CEDDD1ECFB8D467D22A6CB0D11998BD98789DFE6B4BD6E5E72938AC8E166E9793187D9A0A883BFB215A86DAF4EB898B7EE0C5CD3E12C453AF61F9EDE554B62ADD9A771BC803327FCB634D21ECA97359E76522F65EF6C32FA8FE84CC6D9FFF5548DC24817C98296FCCBAF7A8B8B96180015C600AB3BBE61BB4A7970EFDC67AD62A348A95AC4BD075F54128F99761DE55E667ACCA970E5E6BCF8E087BBACCEAADBA0AED298E5882CF910137CAB6821DE3AC36ADF15DD647602ED766154F91D5D5DCC926B1D2A5A5F4DA168533E11E61A9297F20BDE4C9FECDE1CEA179A8508D002FA2917E4105BB08B2ED23E3F2E54DB400A756C61FECE6BE270D0F9F0DD209CA7024A1CFB304D8AE2F67A2A9516A8917C4663572D42F803ADEDB96F79B00311CD8DF1EB1F3B99179E0903963C6735D9090C2D92D4310CFD176060FDEBFD2FDEB782A2FF601790A92093A435C0700D841F2B463F43281A766CCBFCE2A30FDF0F1AAB4279961AC41F0414FD8CD3763C13100C811658C374B98D238D91ADEC09CD9E217E4CAB9505AC6920A802C1006CB84BD081C8D6F3735B5190DD7363F44321CBD2209AADE932")
//                    .data("__EVENTTARGET", "drpSemester")
//                    .data("__EVENTARGUMENT", "")
//                    .data("__LASTFOCUS", "")
//                    .data("__VIEWSTATE", "/wEPDwUJNTQ5MzM5MzA3D2QWAgIBD2QWHAIBD2QWDAIBDw8WAh4EVGV4dAVdVFLGr+G7nE5HIMSQ4bqgSSBI4buMQyBDw5RORyBOR0jhu4YgVEjDlE5HIFRJTiAmIFRSVVnhu4BOIFRIw5RORyAtIMSQ4bqgSSBI4buMQyBUSMOBSSBOR1VZw4pOZGQCAg9kFgJmDw8WBB8ABQZUaG/DoXQeEENhdXNlc1ZhbGlkYXRpb25oZGQCAw8QDxYGHg1EYXRhVGV4dEZpZWxkBQZreWhpZXUeDkRhdGFWYWx1ZUZpZWxkBQJJRB4LXyFEYXRhQm91bmRnZBAVAQJWThUBIDAxMDUyN0VGQkVCODRCQ0E4OTE5MzIxQ0ZENUMzQTM0FCsDAWcWAWZkAgQPDxYCHghJbWFnZVVybAUaL2tjbnR0L0ltYWdlcy9Vc2VySW5mby5naWZkZAIFD2QWCAIBDw8WAh8ABShOZ3V54buFbiBNaW5oICDEkOG7qWMoRFRD…iBzdHlsZT0iRkxPQVQ6bGVmdCI+PGltZyBzcmM9Ii9rY250dC9pbWFnZXMvc2VuZGVtYWlsLnBuZyIgIGJvcmRlcj0iMCI+PC9kaXY+PGRpdiBzdHlsZT0iRkxPQVQ6bGVmdDtQQURESU5HLVRPUDo2cHgiPkfhu61pIGVtYWlsIHRyYW5nIG7DoHk8L2Rpdj48L2E+PGEgaHJlZj0iIyIgb25jbGljaz0iamF2YXNjcmlwdDphZGRmYXYoKSI+PGRpdiBzdHlsZT0iRkxPQVQ6bGVmdCI+PGltZyBzcmM9Ii9rY250dC9pbWFnZXMvYWRkdG9mYXZvcml0ZXMucG5nIiAgYm9yZGVyPSIwIj48L2Rpdj48ZGl2IHN0eWxlPSJGTE9BVDpsZWZ0O1BBRERJTkctVE9QOjZweCI+VGjDqm0gdsOgbyDGsGEgdGjDrWNoPC9kaXY+PC9hPmRkZBIKw80k0wAzYFsP0rQZOBrWKFPbhfXx0V6VlNECzEle")
//                    .data("__VIEWSTATEGENERATOR", "833EB390")
//                    .data("__EVENTVALIDATION", "/wEdADSFyqUAb0ZSGi+W/DVngp2Jb8csnTIorMPSfpUKU79Fa8zr1tijm/dVbgMI0MJ/5MiejcL1QZG0LykmkeF10EWpHW/isUyw6w8trNAGHDe5T/w2lIs9E7eeV2CwsZKam8yG9tEt/TDyJa1fzAdIcnRuq940A0sVd2nflhG7GplI5+8XeUh3gRTV1fmhPau35QRJEm+/71JNhmPUTYDle8ZOvKX9VcewtexolGMgtpHF4RHvmbxY97PTF0ap69+O0aAWYN9/N0KjPR4E88EnMFo10iYpaElPtzfddSApY+Imxc02JKvi/owAHK5jNn6zwUoBLafTAfTaH2VFMgDn4MK/ilNDlOX4MBHo8xHdSpfriTRWvFkZl3dHTzs4cfBYp/SVT+bOd8C+9crGdIPEqOlEJ2iOYqW5QIpQBZAIlWdtMoU6QCNazaXZ0KEKKfcc3rhkG1RogICM9Ef+KQT+X3gA4bCNti2NVlH4Km76JOEUtuOBbbHEWXyiG2IK…yd7hNFGTP6QjjYIsojtYqXXsmdNgJ0NGG3qNJvO3W+0TXn5YkxyZjKU9VzsXq3mLjQOmGP81ZLWUI0xpJgZazcCJqovvl/nZn+9uLKuyF8tgQTiqi84Uf39MEp+fE9k/N39hrMT93oznRXWqYM9osMdpZp0BbEPK8rnYf0UuLmBdN5WrkveKpRFT8Y0o8mFByfa67DrhwhibCg8NpHMW69HfVHjxqNtBpSXdyajYQiGuPyhkmYVE7L10B+y2ClWf65pZlwSJTtA+CVVm0wi7PQysZBynTeKqZ1pA4czgHs7nD5doR9Ilon20avH5sFXr3T5Y7/s5/yEKdh2PsSxC2/XN0nAHO2olVFCV4odLXDMYv/zgs/meZjhaGPqLRsMRdvMnsEUpdIdQiYxbtjg1uzEySjeA1Pc2tLEo6eorjEttfn4WHW1+x4xjkWsEDKqXTaSpLcLZBVGEu9Q8xr+yLSx2fjZCHq+3R4FwzvUcPcBLz+fueHTsJIq8I0xNCo=")
//                    .data("PageHeader1${characterDolla}drpNgonNgu", "010527EFBEB84BCA8919321CFD5C3A34")
//                    .data("PageHeader1${PageHeader1_hidisNotify}hidisNotify", "1")
//                    .data("PageHeader1${characterDolla}hidValueNotify", "Bạn+còn+nợ+học+phí+<br>+Số+tiền+nợ:+<b>80.000</b><br>Click+<a+href=/kcntt/PaymentOnline/hocPhi.aspx>here</a>+Để+nộp+tiền<br>")
//                    .data("drpSemester", "4d57b94fd0514197ae7b2c287d76c6d0")
//                    .data("drpDotThi", "cb5d574e011b43f8a49aa808c29c1551")
//                    .data("drpExaminationNumber", "1")
//                    .data("hidShowShiftEndTime", "1")
//                    .data("hidExamShowNote", "")
//                    .data("hidStudentId", "7a7ead6d6ab54cb8ac8f215a393c1521")
//                    .data("hidEsShowRoomCode", "1")
//                    .data("hidDangKyChungChiThuocHeRieng", "")
//                    .method(Connection.Method.POST)
//                    .execute()

            val html3 = response4.parse()
            Log.d("source", html3.html())

            for (table in html3.select("table")) {
                if (table.attr("id") == "tblCourseList") {
                    //Log.d("table", table.html())
                    val testScheduleCollection = ArrayList<TestScheduleCollection>()
                    val temp = ArrayList<String>()

                    for ((trIndex, tr) in table.select("tr").withIndex()) {
                        if (trIndex != 0) {
                            temp.clear()
                            //Log.d("td", "______________________")
                            for (td in tr.select("td")) {
                                //Log.d("td", td.html())
                                temp.add(td.html())
                            }
                            //Log.d("td size", temp.size.toString())

                            if (temp.size == 10)
                                testScheduleCollection.add(
                                        TestScheduleCollection(
                                                temp[0], temp[1], temp[2], temp[3], temp[4], temp[5], temp[6], temp[7], temp[8], temp[9]
                                        )
                                )
                        }
                    }
                    onDomTestScheduleListener.onDone(testScheduleCollection)
                }
            }

        } else
            onDomTestScheduleListener.onThrow("sessionUrl is null or blank")
    }

}