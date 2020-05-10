package com.indieteam.mytask.datas.services

import android.app.ActivityManager
import android.content.Context
import android.util.Log

class ServiceState(val context: Context) {

    fun isAppServiceRunning(): Boolean {
        try {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (services in manager.getRunningServices(Integer.MAX_VALUE)) {
                if (AppService::class.java.name == services.service.className) {
                    Log.d("service", "running")
                    Log.d("service_name", services.service.className.toString())
                    return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("service", "not running")
        return false
    }

}