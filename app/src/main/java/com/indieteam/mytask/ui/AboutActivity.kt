package com.indieteam.mytask.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import com.indieteam.mytask.R
import kotlinx.android.synthetic.main.activity_about.*
import java.lang.Exception

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        link_donate.movementMethod = LinkMovementMethod.getInstance()

        try {
            val packageManager = packageManager.getPackageInfo(packageName, 0)
            version.text = "Version: " + packageManager.versionName
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
