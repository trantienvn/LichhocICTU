package com.indieteam.mytask.model

import android.annotation.SuppressLint
import android.app.Activity
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
import com.indieteam.mytask.collection.TimeScheduleDetails
import com.indieteam.mytask.model.notification.AppNotification
import com.indieteam.mytask.ui.WeekActivity
import com.indieteam.mytask.ui.interface_.OnSyncListener
import com.prolificinteractive.materialcalendarview.CalendarDay
import org.json.JSONObject
import java.io.IOException
import java.util.*


@Suppress("DEPRECATION")
class GoogleCalendar(private val context: Context, activity: Activity, private val onSyncListener: OnSyncListener) : Thread() {

    private var sqLite = SqLite(context)
    @SuppressLint("SimpleDateFormat")
    private val timeDetails = TimeScheduleDetails()
    private lateinit var service: com.google.api.services.calendar.Calendar
    private var weekActivity = activity as WeekActivity
    private var checkNet = InternetState(context)
    private var calendarId: String? = null
    private val appNotification = AppNotification(context)
    private var error = false
    private var uploaded = 0


    private fun init() {
        weekActivity.apply {
            credential = GoogleAccountCredential.usingOAuth2(context, Collections.singleton(CalendarScopes.CALENDAR_EVENTS))
            val email = sqLite.readEmail()
            credential.selectedAccountName = email
            service = Calendar.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName(appName).build()
            this@GoogleCalendar.service = service

        }
        this@GoogleCalendar.checkCalendarPermission()
    }

    private fun checkCalendarPermission() {
        weekActivity.apply {
            gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestScopes(scope, scope2)
                    .build()
            mGoogleSignInClient = GoogleSignIn.getClient(context, gso)
            signInIntent = mGoogleSignInClient.signInIntent

            try {
                if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(context), scope, scope2) && sqLite.readEmail().isBlank())
                    startActivityForResult(signInIntent, RC_SIGN_IN)
                else
                    sync()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun insertCalendar() {
        val calendar = com.google.api.services.calendar.model.Calendar()
                .setSummary("Lich hoc ictu")
                .setDescription("Lich hoc ictu")

        try {
            val insert = service.Calendars().insert(calendar).execute()
            calendarId = insert.id
        } catch (e: IOException) {
            e.printStackTrace()
            appNotification.syncFail()
            error = true
        }
    }

    private fun deleteCalendar(id: String) {
        try {
            service.calendars().delete(id).execute()
        } catch (e: IOException) {
            e.printStackTrace()
            onSyncListener.onFail(e.toString(), "Lỗi xóa lịch")
            appNotification.syncFail()
            error = true
        }
    }

    private fun findCalendarExist() {
        var pageToken: String? = null
        val calendarList = service.calendarList().list().setPageToken(pageToken).execute()
        try {
            do {
                for (calendar in calendarList.items) {
                    Log.d("calendar_summary", calendar.summary)
                    if (calendar.summary == "Lich hoc ictu")
                        deleteCalendar(calendar.id)
                }
                pageToken = calendarList.nextPageToken
            } while (pageToken != null)
        } catch (e: Exception) {
            e.printStackTrace()
            onSyncListener.onFail(e.toString(), "Lỗi đọc lịch")
            appNotification.syncFail()
            error = true
        }
    }

    private fun insertEvents(id: String, summaryEvent: String, location: String, date: String, timeStart: String, timeEnd: String, teacher: String) {
        val event = Event()
                .setSummary(summaryEvent)
                .setLocation(location)
                .setDescription(teacher)

        var day = date.substring(0, date.indexOf("/"))
        var month = date.substring(date.indexOf("/") + 1, date.lastIndexOf("/"))
        val year = date.substring(date.lastIndexOf("/") + 1, date.length)

        if (day.toInt() < 10)
            day = "0$day"
        if (month.toInt() < 10)
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
        } catch (e: IOException) {
            e.printStackTrace()
            onSyncListener.onFail(e.toString(), "Lỗi chèn sự kiện")
            appNotification.syncFail()
            error = true
        }
    }

    fun sync() {
        val jsonObjects = JSONObject(sqLite.readSchedule())
        val jsonArr = jsonObjects.getJSONArray("calendar")

        onSyncListener.onState("Đang tải lịch lên Google Calendar")

        findCalendarExist()
        insertCalendar()

        for (i in 0 until jsonArr.length()) {
            if (!checkNet.state()) {
                onSyncListener.onState("Mất kết nối")
                appNotification.syncFail()
                error = true
                break
            }

            val subjectName = jsonArr.getJSONObject(i).getString("subjectName")
            val subjectDate = jsonArr.getJSONObject(i).getString("subjectDate")
            val subjectTime = jsonArr.getJSONObject(i).getString("subjectTime")
            val subjectPlace = jsonArr.getJSONObject(i).getString("subjectPlace")
            val teacher = jsonArr.getJSONObject(i).getString("teacher")

            val day = subjectDate.substring(0, subjectDate.indexOf("/")).toInt()
            val month = subjectDate.substring(subjectDate.indexOf("/") + 1, subjectDate.lastIndexOf("/")).toInt()
            val year = subjectDate.substring(subjectDate.lastIndexOf("/") + 1, subjectDate.length).toInt()

            val firstTime: Int
            val endTime: Int

            if (subjectTime.indexOf(",") > -1) {
                firstTime = subjectTime.substring(0, subjectTime.indexOf(",")).toInt() - 1
                endTime = subjectTime.substring(subjectTime.lastIndexOf(",") + 1, subjectTime.length).toInt() - 1
                if (CalendarDay.from(2020, month, day).date >= CalendarDay.from(2020, 4, 15).date &&
                        CalendarDay.from(2020, month, day).date < CalendarDay.from(2020, 10, 15).date) {
                    if (calendarId != null)
                        insertEvents(calendarId!!, subjectName, subjectPlace, subjectDate, timeDetails.timeSummerArr[firstTime].timeIn, timeDetails.timeSummerArr[endTime].timeOut, "Teacher: $teacher")
                } else {
                    if (calendarId != null)
                        insertEvents(calendarId!!, subjectName, subjectPlace, subjectDate, timeDetails.timeWinterArr[firstTime].timeIn, timeDetails.timeWinterArr[endTime].timeOut, "Teacher: $teacher")
                }
            } else {
                firstTime = subjectTime.toInt() - 1
                if (CalendarDay.from(2020, month, day).date >= CalendarDay.from(2020, 3, 15).date &&
                        CalendarDay.from(2020, month, day).date < CalendarDay.from(2020, 9, 15).date) {
                    if (calendarId != null)
                        insertEvents(calendarId!!, subjectName, subjectPlace, subjectDate, timeDetails.timeSummerArr[firstTime].timeIn, timeDetails.timeSummerArr[firstTime].timeOut, "Teacher: $teacher")
                } else {
                    if (calendarId != null)
                        insertEvents(calendarId!!, subjectName, subjectPlace, subjectDate, timeDetails.timeWinterArr[firstTime].timeIn, timeDetails.timeWinterArr[firstTime].timeOut, "Teacher: $teacher")
                }
            }

            if (!error) {
                uploaded++
                appNotification.syncing(uploaded, jsonArr.length())
            }

        }

        if (!error) {
            onSyncListener.onDone("Đã tải lịch lên Google Calendar")
            Log.d("Sync", "Done")
            appNotification.syncDone()
        }
    }


    override fun run() {
        init()
        this@GoogleCalendar.join()
    }
}