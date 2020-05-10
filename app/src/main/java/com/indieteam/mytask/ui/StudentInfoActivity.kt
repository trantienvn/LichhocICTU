package com.indieteam.mytask.ui

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.indieteam.mytask.R
import com.indieteam.mytask.model.GoogleSignOut
import com.indieteam.mytask.model.InternetState
import com.indieteam.mytask.model.SqLite
import com.indieteam.mytask.model.service.AppService
import com.indieteam.mytask.model.service.ServiceState
import com.indieteam.mytask.ui.fragment.QrFragment
import com.leinardi.android.speeddial.SpeedDialActionItem
import kotlinx.android.synthetic.main.activity_student_info.*
import org.json.JSONObject

@Suppress("DEPRECATION")
class StudentInfoActivity : AppCompatActivity() {

    private lateinit var sqLite: SqLite
    private lateinit var studentName: String
    private lateinit var studentId: String
    private lateinit var className: String
    private lateinit var courseName: String
    private lateinit var majorsName: String
    private val bundle = Bundle()
    private val qrFragment = QrFragment()
    private lateinit var internetState: InternetState

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentByTag("qrFragment") != null) {
            supportFragmentManager.beginTransaction().remove(supportFragmentManager.findFragmentByTag("qrFragment")!!)
                    .commit()
            visible()
            countClick = 0
        } else {
            super.onBackPressed()
            finish()
        }
    }

    private fun gone() {
        header_profile.visibility = GONE
        content_profile.visibility = GONE
    }

    private fun visible() {
        header_profile.visibility = VISIBLE
        content_profile.visibility = VISIBLE
    }

    private var countClick = 0

    private fun menu() {
        internetState = InternetState(this)

        header_profile.setOnClickListener {
            countClick++
            if (!studentId.isNullOrBlank()) {
                if (countClick == 1) {
                    gone()
                    bundle.putString("studentId", studentId)
                    qrFragment.arguments = bundle
                    supportFragmentManager.beginTransaction().add(R.id.info_root_view, qrFragment, "qrFragment")
                            .commit()
                    supportFragmentManager.executePendingTransactions()
                }
            } else {
                countClick = 0
                visible()
                Toast.makeText(this, "Err #01, Không thể tạo mã QR", Toast.LENGTH_SHORT).show()
            }
        }

        logout.setOnClickListener {
            if (internetState.state()) {
                try {
                    sqLite.deleteSchedule()
                    sqLite.deleteInfo()
                    if (ServiceState(this).isAppServiceRunning())
                        stopService(Intent(this, AppService::class.java))

                    GoogleSignOut(applicationContext).signOut()

                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    Log.d("Err", e.toString())
                }
            } else {
                Toast.makeText(this, "Đang giữ lịch an toàn khi ngoại tuyến", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun readInfo() {
        var readDb: Int
        var valueDb = ""
        sqLite = SqLite(this)
        try {
            valueDb = sqLite.readSchedule()
            readDb = 1
            Log.d("readdb", "readSchedule db done")
        } catch (e: Exception) {
            readDb = 0
            Log.d("readdb", "db is not exits, cannot readSchedule")
            Log.d("err", e.toString())
        }

        if (readDb == 1) {
            val jsonObject = JSONObject(valueDb)
            val infoObj = jsonObject.getJSONObject("info")
            studentName = infoObj.getString("studentName").trim()
            studentId = infoObj.getString("studentId").trim()
            className = infoObj.getString("className").trim()
            courseName = infoObj.getString("courseName").trim()
            majorsName = infoObj.getString("majorsName").trim()
            student_name.text = customTrim(studentName)
            student_id.text = "Mã sinh viên: ${customTrim(studentId)}"
            class_name.text = "Lớp: ${customTrim(className)}"
            course_name.text = "Khóa: ${customTrim(courseName)}"
            majors_name.text = "Ngành: ${customTrim(majorsName)}"
        }
    }

    private fun customTrim(input: String): String {
        var result = ""
        var count = 0
        for (i in input) {
            if (i.toString() != " ") {
                count = 0
                result += i.toString()
            } else {
                if (count == 0)
                    result += " "

                count++
            }
        }
        return result.trim()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_info)
        readInfo()
        menu()
//        if (Build.VERSION.SDK_INT >= 21)
//            loadAds()
    }
}
