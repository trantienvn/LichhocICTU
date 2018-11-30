package com.indieteam.mytask.process.sync

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
import com.indieteam.mytask.dataObj.v2.TimeDetails
import com.indieteam.mytask.process.IsNet
import com.indieteam.mytask.sqlite.SqLite
import com.indieteam.mytask.ui.WeekActivity
import com.prolificinteractive.materialcalendarview.CalendarDay
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class SyncGoogle(val context: Context): Thread() {

    private var sqLite = SqLite(context)
    private val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
    val date = "${CalendarDay.today().day}/${CalendarDay.today().month+1}/${CalendarDay.today().year}"
    private val timeDetails = TimeDetails()
    private lateinit var service: com.google.api.services.calendar.Calendar
    private var weekActivity = context as WeekActivity
    private var checkNet = IsNet(context)


    private fun init(){
        weekActivity.apply{
            credential = GoogleAccountCredential.usingOAuth2(context, Collections.singleton(CalendarScopes.CALENDAR_EVENTS))
            credential.selectedAccountName = sharedPref.getString("accSelected", "null")
            service = Calendar.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName(appName).build()
            this@SyncGoogle.service = weekActivity.service
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
            weekActivity.apply {
                runOnUiThread {
                    //Toast.makeText(context, insert.id, Toast.LENGTH_SHORT).show()
                    sharedPref.edit().apply{
                        putString("google-calendar-id", insert.id)
                        apply()
                    }
                }
            }
        }catch (e: IOException){
            e.printStackTrace()
            weekActivity.runOnUiThread {
                Toast.makeText(weekActivity, "Lỗi insert calendar", Toast.LENGTH_SHORT).show()
                weekActivity.syncGoogleCallback = 0
            }
            this@SyncGoogle.join()
        }
    }

    private fun deleteCalendar(){
        try {
            if (weekActivity.sharedPref.getString("google-calendar-id", "null") != "null") {
                service.calendars().delete(weekActivity.sharedPref.getString("google-calendar-id", "null")).execute()
                weekActivity.runOnUiThread {
                    //Toast.makeText(weekActivity, "Deleted", Toast.LENGTH_SHORT).show()
                }
            }
        }catch (e: IOException){
            e.printStackTrace()
        }
    }

    private fun getCalendar(){

    }

    private fun insertEvents(summaryEvent: String, location: String, date: String, timeStart: String, timeEnd: String){
        val event = Event()
                .setSummary(summaryEvent)
                .setLocation(location)
        var day = date.substring(0, date.indexOf("/"))
        var month = date.substring(date.indexOf("/") + 1, date.lastIndexOf("/"))
        val year = date.substring(date.lastIndexOf("/") + 1, date.length)

        if (day.toInt()<10)
            day = "0${day}"
        if (month.toInt()<10)
            month = "0${month}"

        val startDateTime = DateTime("${year}-${month}-${day}T${timeStart}:00+07:00")
        Log.d("startDateTime", "${year}-${month}-${day}T${timeStart}:00+07:00")
        val start = EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Asia/Ho_Chi_Minh")
        event.start = start

        val endDateTime = DateTime("${year}-${month}-${day}T${timeEnd}:00+07:00")
        Log.d("endDateTime", "${year}-${month}-${day}T${timeEnd}:00+07:00")

        val end = EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Asia/Ho_Chi_Minh")
        event.end = end
        try {
            service.events().insert(weekActivity.sharedPref.getString("google-calendar-id", "null"), event).execute()
        }catch (e: IOException){
            e.printStackTrace()
            weekActivity.runOnUiThread {
                Toast.makeText(weekActivity, "Lỗi insert event", Toast.LENGTH_SHORT).show()
                weekActivity.syncGoogleCallback = 0
            }
            this@SyncGoogle.join()
        }
    }

    fun sync(){
        var contentCSV = "Subject,Start Date,End Date,Location, Start Time, End Time"
        val jsonObjects = JSONObject(sqLite.readCalendar())
        val jsonArr = jsonObjects.getJSONArray("calendar")

        weekActivity.apply{
            runOnUiThread{
                Toast.makeText(this, "Đang đồng bộ trong nền", Toast.LENGTH_SHORT).show()
            }
            if (sharedPref.getString("google-calendar-id", "null") != null)
                deleteCalendar()
        }

        insertCalendar()

//        val dir = File(Environment.getExternalStorageDirectory(), "TKB ICTU")
//        if (!dir.exists()) {
//            dir.mkdirs()
//        }
//
//        val file = File(Environment.getExternalStorageDirectory(), "TKB ICTU/tkb.csv")
//        if (file.exists())
//            file.delete()

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
            val teacher = jsonArr.getJSONObject(i).getString("teacher")
            val firstTime = subjectTime.substring(0, subjectTime.indexOf(",")).toInt() - 1
            val endTime = subjectTime.substring(subjectTime.lastIndexOf(",") + 1, subjectTime.length).toInt() - 1
            if (simpleDateFormat.parse(date) >= CalendarDay.from(CalendarDay().year, 3, 15).date &&
                    simpleDateFormat.parse(date) < CalendarDay.from(CalendarDay().year, 9, 15).date) {
                contentCSV += "\n$subjectName,$subjectDate,$subjectDate,$subjectPlace,${timeDetails.timeSummerArr[firstTime].timeIn},${timeDetails.timeSummerArr[endTime].timeOut},"
                insertEvents(subjectName, subjectPlace, subjectDate, timeDetails.timeSummerArr[firstTime].timeIn, timeDetails.timeSummerArr[endTime].timeOut)
            } else {
                contentCSV += "\n$subjectName,$subjectDate,$subjectDate,$subjectPlace,${timeDetails.timeWinterArr[firstTime].timeIn},${timeDetails.timeWinterArr[endTime].timeOut},"
                insertEvents(subjectName, subjectPlace, subjectDate, timeDetails.timeWinterArr[firstTime].timeIn, timeDetails.timeWinterArr[endTime].timeOut)
            }
        }

        weekActivity.runOnUiThread {
            Toast.makeText(weekActivity, "Hoàn tất đồng bộ", Toast.LENGTH_SHORT).show()
            weekActivity.syncGoogleCallback = 0
        }

//        val fos = FileOutputStream(file)
//        fos.write(contentCSV.toByteArray())
//        fos.close()
//        Log.d("contentCSV", contentCSV)
//        (context as WeekActivity).apply {
//            syncGoogleCallback = 0
//            runOnUiThread {
//                Toast.makeText(this, "Đã xuất: $file", Toast.LENGTH_LONG).show()
//            }
//        }

    }


    override fun run() {
        init()
        checkCalendarPermission()
        join()
    }
}