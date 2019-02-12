package com.indieteam.mytask.core.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.indieteam.mytask.core.notification.AppNotification

class AppFirebaseMessengerService: FirebaseMessagingService() {

    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        Log.d("Token", "Refreshed token: $token")
    }

    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)
        p0?.let {
            AppNotification(this).firebaseNotification(p0.notification?.title, p0.notification?.body)
        }
    }
}