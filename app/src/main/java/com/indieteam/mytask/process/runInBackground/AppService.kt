package com.indieteam.mytask.process.runInBackground

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.indieteam.mytask.R
import com.indieteam.mytask.process.notification.AppNotification
import com.indieteam.mytask.process.parseJson.ParseCalendarJson
import com.indieteam.mytask.sqlite.SqlLite
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class AppService: Service(){

    private lateinit var sqlLite: SqlLite
    private var valueDb = ""
    private lateinit var calendarJson: JSONObject
    private lateinit var parseCalendarJson: ParseCalendarJson
    private val calendarForTomorrow = Calendar.getInstance()!!
    private  lateinit var calendarForNow: Calendar
    private var result = ""
    private lateinit var appNotification: AppNotification
    private var numberSubjects = 0
    private var coutNotification = 0
    private var notificationThread = NotificationThread()
    init {
        calendarForTomorrow.set(calendarForTomorrow.get(Calendar.YEAR), calendarForTomorrow.get(Calendar.MONTH), calendarForTomorrow.get(Calendar.DAY_OF_MONTH))
        calendarForTomorrow.add(Calendar.DAY_OF_MONTH, 1)
    }

    inner class NotificationThread: Thread(){
        override fun run() {
            Timer().scheduleAtFixedRate(0, 50000) {
                calendarForNow = Calendar.getInstance()!!
                Log.d("hour", calendarForNow.get(Calendar.HOUR_OF_DAY).toString() + " " + calendarForNow.get(Calendar.MINUTE))
                if (calendarForNow.get(Calendar.HOUR_OF_DAY) == 20 && calendarForNow.get(Calendar.MINUTE) == 0) {
                    if (coutNotification == 0)
                        runTask()
                    coutNotification++
                }else
                    coutNotification = 0
            }
        }
    }


    private fun runTask(){
        result = ""
        val date = "${calendarForTomorrow.get(Calendar.DAY_OF_MONTH)}/${calendarForTomorrow.get(Calendar.MONTH)+1}/${calendarForTomorrow.get(Calendar.YEAR)}"
        sqlLite = SqlLite(this)
        try { valueDb = sqlLite.readCalendar()
        }catch (e: Exception){ Log.d("Err", e.toString())}
        if (valueDb.isNotBlank()){
            calendarJson = JSONObject(valueDb)
            parseCalendarJson = ParseCalendarJson(calendarJson)
            parseCalendarJson.getSubject(date)
            if (parseCalendarJson.subjectName.isNotEmpty() && parseCalendarJson.subjectPlace.isNotEmpty() &&
                    parseCalendarJson.subjectTime.isNotEmpty() && parseCalendarJson.teacher.isNotEmpty()) {
                if (parseCalendarJson.subjectName.size == parseCalendarJson.subjectPlace.size &&
                        parseCalendarJson.subjectName.size == parseCalendarJson.subjectTime.size &&
                        parseCalendarJson.subjectName.size == parseCalendarJson.teacher.size) {
                    for (i in 0 until parseCalendarJson.subjectName.size) {
                        numberSubjects ++
                        result += "$numberSubjects. " + "${parseCalendarJson.subjectName[i]} (${parseCalendarJson.subjectTime[i]})\n"
                    }
                }
            }else{
                result += "Nghỉ \n"
            }
            result = result.substring(0, result.lastIndexOf("\n"))
            Log.d("AppService_Log", "Date $date: $result")
            appNotification = AppNotification(this, numberSubjects.toString())
            appNotification.build(result)

        }else{
            Log.d("AppService_Log", "null")
        }
    }

    override fun onCreate() {
        super.onCreate()
        if(Build.VERSION.SDK_INT >= 26){
            val channelName  = "calendarNotification"
            val channelId = "calendar_notification"
            val description = ""
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val chanel = NotificationChannel(channelId, channelName, importance)
            chanel.description = description
            val notificationManager = this.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(chanel)

            val mBuilder = NotificationCompat.Builder(this, "calendar_notification")
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle("Tác vụ")
                    .setContentText("Ứng dụng đang chạy nền")
                    .setColor(Color.parseColor("#2c73b3"))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setAutoCancel(true) // remove notification after touch
            startForeground(1, mBuilder.build())
        }
    }

    override fun onBind(intent: Intent?): IBinder? { return null }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notificationThread.start()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSelf()
    }
}