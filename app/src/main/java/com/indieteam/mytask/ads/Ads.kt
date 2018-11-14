package com.indieteam.mytask.ads

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

class Ads {

    fun loadBottomAds(adView: AdView){
        val request = AdRequest.Builder().build()
        adView.loadAd(request)
    }

}