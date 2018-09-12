package com.indieteam.mytask.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.indieteam.mytask.R
import com.indieteam.mytask.sqlite.SqlLite
import kotlinx.android.synthetic.main.activity_info_student.*
import org.json.JSONObject

class InfoStudentActivity : AppCompatActivity() {

    private lateinit var sqlLite: SqlLite
    private lateinit var studentName: String
    private lateinit var studentId: String
    private lateinit var className: String
    private lateinit var courseName: String
    private lateinit var majorsName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_student)
        readInfo()
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
        super.onBackPressed()
        finish()
    }
}
