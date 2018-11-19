package com.indieteam.mytask.ui

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.indieteam.mytask.R
import com.indieteam.mytask.process.CheckNet
import com.indieteam.mytask.process.sync.SyncGoogle
import com.indieteam.mytask.sqlite.SqLite
import kotlinx.android.synthetic.main.activity_login.*
import java.security.NoSuchAlgorithmException


class LoginActivity : AppCompatActivity() {

    private val REQUEST_CODE = 1
    private var allPermission= 0
    lateinit var sqLite: SqLite
    private var readDb = 0
    lateinit var checkNet: CheckNet
    private lateinit var sharedPref: SharedPreferences

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == REQUEST_CODE){
            if(grantResults.size == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                run()
            }
        }else{
            Toast.makeText(this@LoginActivity, "Permissions is not granted", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE)
            }else{
                allPermission = 1
            }
        }else{
            run()
        }
    }

    private fun init(){
        sqLite = SqLite(this)
        checkNet = CheckNet(this)
        sharedPref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        text_username.setText(sharedPref.getString("username", ""))
    }

    private fun toMD5(s: String): String {
        val MD5 = "MD5"
        try {
            // Create MD5 Hash
            val digest = java.security.MessageDigest
                    .getInstance(MD5)
            digest.update(s.toByteArray())
            val messageDigest = digest.digest()

            // Create Hex String
            val hexString = StringBuilder()
            for (aMessageDigest in messageDigest) {
                var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
                while (h.length < 2)
                    h = "0$h"
                hexString.append(h)
            }
            return hexString.toString()

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        return ""
    }

    fun visible(){
        linearLayout.visibility = View.VISIBLE
        btn_login.visibility = View.VISIBLE
        developer.visibility = View.VISIBLE
    }

    private fun gone(){
        linearLayout.visibility = View.GONE
        btn_login.visibility = View.GONE
        developer.visibility = View.GONE
    }

    var clickLogin = 0

    private fun run(){
        btn_login.setOnClickListener {
            if (checkNet.check()) {
                if (text_username.text.toString().isNotBlank() && text_password.text.toString().isNotBlank() && clickLogin == 0) {
                    gone()
                    supportFragmentManager.beginTransaction().add(R.id.login_root_view, ProcessBarFragment(), "processBarLogin")
                            .commit()
                    supportFragmentManager.executePendingTransactions()
                    val md5Password = toMD5(text_password.text.toString())
                    Log.d("md5password", md5Password)
                    com.indieteam.mytask.process.domHTML.DomLogin(this, text_username.text.toString(), md5Password).start()
                    clickLogin++
                }
            } else {
                runOnUiThread {
                    Toast.makeText(this, "Kiểm tra lại kết nối", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init()
        try {
            sqLite.readCalendar()
            readDb = 1
        }catch (e: Exception){ Log.d("Err", e.toString()) }

        if(readDb == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkPermission()
                if (allPermission == 1)
                    run()
                else
                    checkPermission()
            }else
                run()
        }else{
            val intent = Intent(this@LoginActivity, WeekActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}
