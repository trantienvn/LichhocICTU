package com.indieteam.mytask.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import com.indieteam.mytask.R
import com.indieteam.mytask.sqlite.SqlLite
import kotlinx.android.synthetic.main.activity_info_student.*
import kotlinx.android.synthetic.main.fragment_qr.*
import org.json.JSONObject

class InfoStudentActivity : AppCompatActivity() {

    private lateinit var sqlLite: SqlLite
    private lateinit var studentName: String
    private lateinit var studentId: String
    private lateinit var className: String
    private lateinit var courseName: String
    private lateinit var majorsName: String
    private val bundle = Bundle()
    private val qrFragment = QrFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_student)
        readInfo()
        genQr()
    }

    private fun gone(){
        header_profile.visibility = GONE
        content_profile.visibility = GONE
        gen_qr.visibility = GONE
    }

    private fun visible(){
        header_profile.visibility = VISIBLE
        content_profile.visibility = VISIBLE
        gen_qr.visibility = VISIBLE
    }

    private var countClick = 0
    private fun genQr(){
        gen_qr_btn.setOnClickListener {
            countClick++
            if (!studentId.isNullOrBlank()) {
                if(countClick == 1){
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
    }

    private fun readInfo(){
        var readDb: Int
        var valueDb= ""
        sqlLite = SqlLite(this)
        try{
            valueDb = sqlLite.readCalendar()
            readDb = 1
            Log.d("readdb", "readCalendar db done")
        }catch (e: Exception){
            readDb = 0
            Log.d("readdb", "db is not exits, cannot readCalendar")
            Log.d("err", e.toString())
        }

        if (readDb == 1){
            val jsonObject = JSONObject(valueDb)
            val infoObj = jsonObject.getJSONObject("info")
            studentName = infoObj.getString("studentName")
            studentId = infoObj.getString("studentId")
            className = infoObj.getString("className")
            courseName = infoObj.getString("courseName")
            majorsName = infoObj.getString("majorsName")
            student_name.text = studentName
            student_id.text = "Mã sinh viên: $studentId"
            class_name.text = "Lớp: $className"
            course_name.text = "Khóa: $courseName"
            majors_name.text = "Ngành: $majorsName"
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentByTag("qrFragment") != null){
            supportFragmentManager.beginTransaction().remove(supportFragmentManager.findFragmentByTag("qrFragment"))
                    .commit()
            visible()
            countClick = 0
        }else {
            super.onBackPressed()
            finish()
        }
    }
}
