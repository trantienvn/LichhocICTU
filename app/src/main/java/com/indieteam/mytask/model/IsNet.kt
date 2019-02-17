package com.indieteam.mytask.model

import android.content.Context
import android.net.ConnectivityManager

class IsNet(val context: Context) {

    fun check(): Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

        if (connectivityManager == null)
            return false
        else
            return connectivityManager.activeNetworkInfo != null
    }

}