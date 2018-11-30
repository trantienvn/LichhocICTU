package com.indieteam.mytask.process.sync

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import com.indieteam.mytask.dataStruct.TimeDetails
import com.indieteam.mytask.process.IsNet
import com.indieteam.mytask.sqlite.SqLite
import com.indieteam.mytask.ui.WeekActivity
import com.prolificinteractive.materialcalendarview.CalendarDay
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


@Suppress("DEPRECATION")
class SyncGoogleCalendar(val context: Context): Thread() {

    private var sqLite = SqLite(context)
    @SuppressLint("SimpleDateFormat")
    private val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
    val date = "${CalendarDay.today().day}/${CalendarDay.today().month+1}/${CalendarDay.today().year}"
    private val timeDetails = TimeDetails()
    private lateinit var service: com.google.api.services.calendar.Calendar
    private var weekActivity = context as WeekActivity
    private var checkNet = IsNet(context)
    private var calendarId: String? = null


    private fun init(){
        weekActivity.apply{
            credential = GoogleAccountCredential.usingOAuth2(context, Collections.singleton(CalendarScopes.CALENDAR_EVENTS))
            credential.selectedAccountName = sharedPref.getString("accSelected", "null")
            service = Calendar.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName(appName).build()
            this@SyncGoogleCalendar.service = weekActivity.service
        }
    }

    private fun checkCalendarPermission(){
        weekActivity.apply {
            gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestScopes(scope, scope2)
                    .build()
            mGoogleSignInClient = GoogleSignIn.getClient(context, gso)
            signInIntent = mGoogleSignInClient.signInIntent
            if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(context), scope, scope2)) {
                startActivityForResult(signInIntent, RC_SIGN_IN)
            } else {
                sync()
                runOnUiThread {
                    syncGoogleCallback = 0
                }
            }
        }
    }

    private fun insertCalendar(){
        val calendar = com.google.api.services.calendar.model.Calendar()
                .setSummary("Lich hoc ictu")
                .setDescription("Lich hoc ictu")

        try {
            val insert = service.Calendars().insert(calendar).execute()
            calendarId = insert.id
        }catch (e: IOException){
            e.printStackTrace()
            weekActivity.runOnUiThread {
                Toast.makeText(weekActivity, "Lỗi insert calendar", Toast.LENGTH_SHORT).show()
                weekActivity.syncGoogleCallback = 0
            }
            this@SyncGoogleCalendar.join()
        }
    }

    private fun deleteCalendar(id: String){
        try {
            service.calendars().delete(id).execute()
        }catch (e: IOException){
            e.printStackTrace()
        }
    }

    private fun findCalendarExist(){
        var pageToken: String? = null
        val calendarList = service.calendarList().list().setPageToken(pageToken).execute()
        do {
            for (calendar in calendarList.items) {
                Log.d("calendar_summary", calendar.summary)
                if (calendar.summary == "Lich hoc ictu"){
                    deleteCalendar(calendar.id)
                }
            }
            pageToken = calendarList.nextPageToken
        } while (pageToken != null)
    }

    private fun insertEvents(id: String, summaryEvent: String, location: String, date: String, timeStart: String, timeEnd: String){
        val event = Event()
                .setSummary(summaryEvent)
                .setLocation(location)
        var day = date.substring(0, date.indexOf("/"))
        var month = date.substring(date.indexOf("/") + 1, date.lastIndexOf("/"))
        val year = date.substring(date.lastIndexOf("/") + 1, date.length)

        if (day.toInt()<10)
            day = "0$day"
        if (month.toInt()<10)
            month = "0$month"

        val startDateTime = DateTime("$year-$month-${day}T$timeStart:00+07:00")
        Log.d("startDateTime", "$year-$month-${day}T$timeStart:00+07:00")
        val start = EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Asia/Ho_Chi_Minh")
        event.start = start

        val endDateTime = DateTime("$year-$month-${day}T$timeEnd:00+07:00")
        Log.d("endDateTime", "$year-$month-${day}T$timeEnd:00+07:00")

        val end = EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Asia/Ho_Chi_Minh")
        event.end = end
        try {
            service.events().insert(id, event).execute()
        }catch (e: IOException){
            e.printStackTrace()
            weekActivity.runOnUiThread {
                Toast.makeText(weekActivity, "Lỗi insert event", Toast.LENGTH_SHORT).show()
                weekActivity.syncGoogleCallback = 0
            }
            this@SyncGoogleCalendar.join()
        }
    }

    fun sync(){
        val jsonObjects = JSONObject(sqLite.readCalendar())
        val jsonArr = jsonObjects.getJSONArray("calendar")

        weekActivity.apply{
            runOnUiThread{
                Toast.makeText(this, "Đang đồng bộ trong nền", Toast.LENGTH_SHORT).show()
            }
        }

        findCalendarExist()
        insertCalendar()

        for (i in 0 until jsonArr.length()) {
            if (!checkNet.check()){
                weekActivity.runOnUiThread {
                    Toast.makeText(weekActivity, "Kiểm tra lại kết nối", Toast.LENGTH_SHORT).show()
                    weekActivity.syncGoogleCallback = 0
                }
                break
            }

            val subjectName = jsonArr.getJSONObject(i).getString("subjectName")
            val subjectDate = jsonArr.getJSONObject(i).getString("subjectDate")
            val subjectTime = jsonArr.getJSONObject(i).getString("subjectTime")
            val subjectPlace = jsonArr.getJSONObject(i).getString("subjectPlace")
            //val teacher = jsonArr.getJSONObject(i).getString("teacher")
            val firstTime = subjectTime.substring(0, subjectTime.indexOf(",")).toInt() - 1
            val endTime = subjectTime.substring(subjectTime.lastIndexOf(",") + 1, subjectTime.length).toInt() - 1
            if (simpleDateFormat.parse(date) >= CalendarDay.from(CalendarDay().year, 3, 15).date &&
                    simpleDateFormat.parse(date) < CalendarDay.from(CalendarDay().year, 9, 15).date) {
                if (calendarId != null)
                    insertEvents(calendarId!!, subjectName, subjectPlace, subjectDate, timeDetails.timeSummerArr[firstTime].timeIn, timeDetails.timeSummerArr[endTime].timeOut)
            } else {
                if (calendarId != null)
                    insertEvents(calendarId!!, subjectName, subjectPlace, subjectDate, timeDetails.timeWinterArr[firstTime].timeIn, timeDetails.timeWinterArr[endTime].timeOut)
            }
        }

        weekActivity.runOnUiThread {
            Toast.makeText(weekActivity, "Hoàn tất đồng bộ", Toast.LENGTH_SHORT).show()
            weekActivity.syncGoogleCallback = 0
        }
    }


    override fun run() {
        init()
        checkCalendarPermission()
        join()
    }
}