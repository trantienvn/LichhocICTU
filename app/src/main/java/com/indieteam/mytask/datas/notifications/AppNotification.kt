package com.indieteam.mytask.datas.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.indieteam.mytask.R
import com.indieteam.mytask.models.NotificationID
import com.indieteam.mytask.views.WeekActivity

class AppNotification(val context: Context) {

    fun scheduleToday(contents: String, numberSubjects: String) {
        //touch
        val intent = Intent(context, WeekActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val channelName = "Calendar Notification"
        val channelId = "Calendar Notification"
        val description = ""

        //set channelId
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val chanel = NotificationChannel(channelId, channelName, importance)
            chanel.description = description
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(chanel)
        }

        val badgeContent: String = if (numberSubjects != "0")
            "Có $numberSubjects môn"
        else
            "Nghỉ"

        val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_schedule_24dp)
                .setContentTitle("Ngày mai")
                .setContentText(badgeContent)
                .setStyle(NotificationCompat.BigTextStyle().bigText(contents))
                .setColor(Color.parseColor("#2c73b3"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setVibrate(longArrayOf(1))
                .setAutoCancel(true) // remove notification after touch

        //show
        NotificationManagerCompat.from(context).notify(NotificationID.subject, notification.build())
    }

    fun syncStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "App Notification"
            val channelId = "App Notification"
            val description = ""
            val importance = NotificationManager.IMPORTANCE_LOW
            val chanel = NotificationChannel(channelId, channelName, importance)
            chanel.description = description

            val notification = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_cloud_upload_24dp)
                    .setContentTitle("Đang chuẩn bị...")
                    .setColor(Color.parseColor("#9C27B0"))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setAutoCancel(false) // remove notification after touch

            NotificationManagerCompat.from(context).notify(NotificationID.foreground, notification.build())
        } else {
            val channelId = "App Notification"

            val notification = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_cloud_upload_24dp)
                    .setContentTitle("Đang chuẩn bị...")
                    .setColor(Color.parseColor("#9C27B0"))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setAutoCancel(false) // remove notification after touch

            NotificationManagerCompat.from(context).notify(NotificationID.beforeSdkOREO, notification.build())
        }
    }

    fun syncing(now: Int, total: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "App Notification"
            val channelId = "App Notification"
            val description = ""
            val importance = NotificationManager.IMPORTANCE_LOW
            val chanel = NotificationChannel(channelId, channelName, importance)
            chanel.description = description

            val notification = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_cloud_upload_24dp)
                    .setContentTitle("Đã tải: $now/$total sự kiện")
                    .setColor(Color.parseColor("#9C27B0"))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setAutoCancel(false) // remove notification after touch
                    .setOnlyAlertOnce(true) // mute sound

            NotificationManagerCompat.from(context).notify(NotificationID.foreground, notification.build())
        } else {
            val channelId = "App Notification"

            val notification = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_cloud_upload_24dp)
                    .setContentTitle("Đã tải: $now/$total sự kiện")
                    .setColor(Color.parseColor("#9C27B0"))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setAutoCancel(false) // remove notification after touch
                    .setOnlyAlertOnce(true) // mute sound

            NotificationManagerCompat.from(context).notify(NotificationID.beforeSdkOREO, notification.build())
        }
    }

    fun syncDone() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "App Notification"
            val channelId = "App Notification"
            val description = ""
            val importance = NotificationManager.IMPORTANCE_LOW
            val chanel = NotificationChannel(channelId, channelName, importance)
            chanel.description = description

            val notification = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_schedule_24dp)
                    .setContentTitle("Đang theo dõi lịch học")
                    .setContentText("Đã tải lịch lên Google Calendar")
                    .setColor(Color.parseColor("#9C27B0"))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setAutoCancel(false) // remove notification after touch

            NotificationManagerCompat.from(context).notify(NotificationID.foreground, notification.build())
        } else {
            val channelId = "App Notification"

            val notification = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_schedule_24dp)
                    .setContentTitle("Đang theo dõi lịch học")
                    .setContentText("Đã tải lịch lên Google Calendar")
                    .setColor(Color.parseColor("#9C27B0"))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setAutoCancel(false) // remove notification after touch

            NotificationManagerCompat.from(context).notify(NotificationID.beforeSdkOREO, notification.build())
        }
    }

    fun syncFail() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "App Notification"
            val channelId = "App Notification"
            val description = ""
            val importance = NotificationManager.IMPORTANCE_LOW
            val chanel = NotificationChannel(channelId, channelName, importance)
            chanel.description = description

            val notification = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_schedule_24dp)
                    .setContentTitle("Đang theo dõi lịch học")
                    .setContentText("Lỗi tải lịch lên Google Calendar. Hãy thử lại")
                    .setColor(Color.parseColor("#9C27B0"))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setAutoCancel(false)

            NotificationManagerCompat.from(context).notify(NotificationID.foreground, notification.build())
        } else {
            val channelId = "App Notification"

            val notification = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_schedule_24dp)
                    .setContentTitle("Đang theo dõi lịch học")
                    .setContentText("Lỗi tải lịch lên Google Calendar")
                    .setColor(Color.parseColor("#9C27B0"))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setAutoCancel(false)

            NotificationManagerCompat.from(context).notify(NotificationID.beforeSdkOREO, notification.build())
        }
    }

    fun firebaseNotification(title: String?, body: String?) {
        val channelName = "Messenger Notification"
        val channelId = "Messenger Notification"
        val description = ""

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val chanel = NotificationChannel(channelId, channelName, importance)
            chanel.description = description
        }

        val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_message_64)
                .setContentTitle(title)
                .setContentText(body)
                .setColor(Color.parseColor("#9C27B0"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setVibrate(longArrayOf(1))

        NotificationManagerCompat.from(context).notify(NotificationID.firebase, notification.build())
    }

    fun foreground(): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, "App Notification")
                .setSmallIcon(R.drawable.ic_schedule_24dp)
                .setContentTitle("Đang theo dõi lịch học")
                .setColor(Color.parseColor("#9C27B0"))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setAutoCancel(false)
    }

}