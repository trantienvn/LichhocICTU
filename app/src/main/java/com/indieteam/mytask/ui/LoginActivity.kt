package com.indieteam.mytask.ui

import android.content.Intent
import android.content.SharedPreferences
import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.indieteam.mytask.R
import com.indieteam.mytask.model.InternetState
import com.indieteam.mytask.model.schedule.domHTML.DomSemesterSchedule
import com.indieteam.mytask.model.SqLite
import com.indieteam.mytask.ui.fragment.ProcessBarFragment
import com.indieteam.mytask.ui.fragment.SelectSemesterScheduleFragment
import com.indieteam.mytask.ui.interface_.OnLoginListener
import com.indieteam.mytask.ui.interface_.OnSemesterScheduleListener
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_process_bar.*
import java.security.NoSuchAlgorithmException


class LoginActivity : AppCompatActivity() {

    private val onLoginListener = object : OnLoginListener {
        override fun onLogin() {
            supportFragmentManager.beginTransaction().add(R.id.login_root_view, ProcessBarFragment(), "processBarLogin")
                    .commit()
            supportFragmentManager.executePendingTransactions()
            gone()
            val md5Password = toMD5(text_password.text.toString().trim())
            Log.d("md5password", md5Password)
            com.indieteam.mytask.model.schedule.domHTML.DomLogin(this@LoginActivity, text_username.text.toString().trim(), md5Password, this).start()
            clickLogin++
        }

        override fun onSuccess(username: String, password: String, cookie: String, sessionUrl: String) {
            supportFragmentManager.findFragmentByTag("processBarLogin")?.let {
                runOnUiThread {
                    it.process.text = "Đăng nhập...OK"
                }
            }

            try {
                sqLite.insertInfo(username, password, cookie)
            } catch (e: SQLiteConstraintException) {
                e.printStackTrace()
            }
            DomSemesterSchedule(this@LoginActivity, sessionUrl, cookie, onSemesterScheduleListener).start()
        }

        override fun onFail() {
            runOnUiThread {
                supportFragmentManager.findFragmentByTag("processBarLogin")?.let {
                    supportFragmentManager.beginTransaction().remove(it)
                            .commit()
                }
                visible()
                clickLogin = 0
                Toast.makeText(this@LoginActivity, "Sai mã sinh viên hoặc mật khẩu", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onThrow(t: String) {
            runOnUiThread {
                supportFragmentManager.findFragmentByTag("processBarLogin")?.let {
                    supportFragmentManager.beginTransaction().remove(it)
                            .commit()
                }
                visible()
                clickLogin = 0
                Toast.makeText(this@LoginActivity, t, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val onSemesterScheduleListener = object : OnSemesterScheduleListener {
        override fun onSuccess(semester: String, sessionUrl: String, signIn: String) {
            val bundle = Bundle()
            bundle.putString("semester", semester)
            bundle.putString("sessionUrl", sessionUrl)
            bundle.putString("signIn", signIn)

            val selectSemesterFragment = SelectSemesterScheduleFragment()
            selectSemesterFragment.arguments = bundle

            supportFragmentManager.findFragmentByTag("processBarLogin")?.let {
                supportFragmentManager.beginTransaction().remove(it)
                        .commit()
            }
            supportFragmentManager.beginTransaction().add(R.id.login_root_view, selectSemesterFragment, "selectSemesterFragment")
                    .commit()
        }

        override fun onSemesterSchedule() {
            runOnUiThread {
                supportFragmentManager.findFragmentByTag("processBarLogin")?.let {
                    runOnUiThread {
                        it.process.text = "Tải học kỳ..."
                    }
                }
            }
        }

        override fun onThrow(t: String) {
            runOnUiThread {
                supportFragmentManager.findFragmentByTag("processBarLogin")?.let {
                    supportFragmentManager.beginTransaction().remove(it)
                            .commit()
                }
                visible()
                clickLogin = 0
                Toast.makeText(this@LoginActivity, t, Toast.LENGTH_SHORT).show()
            }
        }
    }

    lateinit var sqLite: SqLite
    private var readDb = 0
    lateinit var internetState: InternetState
    private lateinit var sharedPref: SharedPreferences

    private fun init() {
        sqLite = SqLite(this)
        internetState = InternetState(this)
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

    fun visible() {
        linearLayout.visibility = View.VISIBLE
        btn_login.visibility = View.VISIBLE
        developer.visibility = View.VISIBLE
    }

    private fun gone() {
        linearLayout.visibility = View.GONE
        btn_login.visibility = View.GONE
        developer.visibility = View.GONE
    }

    var clickLogin = 0

    private fun run() {
        btn_login.setOnClickListener {
            if (internetState.state()) {
                if (text_username.text.toString().isNotBlank() && text_password.text.toString().isNotBlank() && clickLogin == 0) {
                    onLoginListener.onLogin()
                }
            } else {
                onLoginListener.onThrow("Kiểm tra lại kết nối")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init()
        try {
            sqLite.readSchedule()
            readDb = 1
        } catch (e: Exception) {
            Log.d("Err", e.toString())
        }

        if (readDb == 0) {
            run()
        } else {
            val intent = Intent(this@LoginActivity, WeekActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}
