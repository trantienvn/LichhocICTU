package com.indieteam.mytask.model.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.IBinder
import android.preference.PreferenceManager
import android.util.Log
import com.indieteam.mytask.collection.NotificationID
import com.indieteam.mytask.model.notification.AppNotification
import com.indieteam.mytask.model.schedule.parseData.ParseScheduleJson
import com.indieteam.mytask.model.SqLite
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class AppService : Service() {

    private lateinit var sqLite: SqLite
    private lateinit var calendarJson: JSONObject
    private lateinit var parseScheduleJson: ParseScheduleJson
    private lateinit var appNotification: AppNotification
    private var pushNotification = false

    private lateinit var sharedPreferences: SharedPreferences

    private fun checkTimeBackground() {
        Timer().scheduleAtFixedRate(0, 5000) {
            val calendar = Calendar.getInstance()!!
            Log.d("Time Now", calendar.get(Calendar.HOUR_OF_DAY).toString() + ":" + calendar.get(Calendar.MINUTE))
            if (calendar.get(Calendar.HOUR_OF_DAY) == 20 && calendar.get(Calendar.MINUTE) == 0) {
                if (!pushNotification) {
                    pushSubjectTomorrowNotification()
                    pushNotification = true
                }

            } else
                pushNotification = false
        }
    }


    private fun pushSubjectTomorrowNotification() {
        val calendar = Calendar.getInstance()
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        var numberSubjects = 0
        var result = ""
        val date = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
        sqLite = SqLite(this)

        try {
            val valueDb = sqLite.readSchedule()
            if (valueDb.isNotBlank()) {
                calendarJson = JSONObject(valueDb)
                parseScheduleJson = ParseScheduleJson(calendarJson)
                parseScheduleJson.getSubject(date)
                if (parseScheduleJson.subjectName.isNotEmpty() && parseScheduleJson.subjectPlace.isNotEmpty() &&
                        parseScheduleJson.subjectTime.isNotEmpty() && parseScheduleJson.teacher.isNotEmpty()) {
                    if (parseScheduleJson.subjectName.size == parseScheduleJson.subjectPlace.size &&
                            parseScheduleJson.subjectName.size == parseScheduleJson.subjectTime.size &&
                            parseScheduleJson.subjectName.size == parseScheduleJson.teacher.size) {
                        for (i in 0 until parseScheduleJson.subjectName.size) {
                            numberSubjects++
                            result += "$numberSubjects. " + "${parseScheduleJson.subjectName[i]} (${parseScheduleJson.subjectTime[i]})\n"
                        }
                    }
                } else
                    result += "Nghỉ \n"

                result = result.substring(0, result.lastIndexOf("\n"))
                Log.d("Tomorrow", "Date $date: $result")
                appNotification = AppNotification(this)
                appNotification.scheduleToday(result, numberSubjects.toString())

            } else
                Log.d("Tomorrow", "")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreate() {
        super.onCreate()
        appNotification = AppNotification(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "App Notification"
            val channelId = "App Notification"
            val description = ""
            val importance = NotificationManager.IMPORTANCE_LOW
            val chanel = NotificationChannel(channelId, channelName, importance)
            chanel.description = description
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(chanel)

            startForeground(NotificationID.foreground, appNotification.foreground().build())
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("service", "started")
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        checkTimeBackground()
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d("service", "stopped")
        super.onDestroy()
        try {
            stopSelf()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}