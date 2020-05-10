package com.indieteam.mytask.datas.services

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.indieteam.mytask.datas.notifications.AppNotification

class FirebaseMessengerService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("Token", "Refreshed token: $token")
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        p0?.let {
            AppNotification(this).firebaseNotification(p0.notification?.title, p0.notification?.body)
        }
    }
}