package com.indieteam.mytask.ads

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class Ads(context: Context) {

    init {
        MobileAds.initialize(context, "ca-app-pub-1117482668766229~6134610505")
    }

    fun loadBottomAds(adView: AdView){
        val request = AdRequest.Builder().build()
        adView.loadAd(request)
    }

}