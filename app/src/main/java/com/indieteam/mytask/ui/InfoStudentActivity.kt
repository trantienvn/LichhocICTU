package com.indieteam.mytask.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import com.indieteam.mytask.R
import com.indieteam.mytask.ads.Ads
import com.indieteam.mytask.sqlite.SqLite
import com.leinardi.android.speeddial.SpeedDialActionItem
import kotlinx.android.synthetic.main.activity_info_student.*
import org.json.JSONObject

@Suppress("DEPRECATION")
class InfoStudentActivity : AppCompatActivity() {

    private lateinit var sqLite: SqLite
    private lateinit var studentName: String
    private lateinit var studentId: String
    private lateinit var className: String
    private lateinit var courseName: String
    private lateinit var majorsName: String
    private val bundle = Bundle()
    private val qrFragment = QrFragment()
    private lateinit var ads: Ads

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentByTag("qrFragment") != null){
            supportFragmentManager.beginTransaction().remove(supportFragmentManager.findFragmentByTag("qrFragment")!!)
                    .commit()
            visible()
            countClick = 0
        }else {
            super.onBackPressed()
            finish()
        }
    }

    private fun gone(){
        header_profile.visibility = GONE
        content_profile.visibility = GONE
        gen_qr_btn.visibility = GONE
    }

    private fun visible(){
        header_profile.visibility = VISIBLE
        content_profile.visibility = VISIBLE
        gen_qr_btn.visibility = VISIBLE
    }

    private var countClick = 0
    private fun genQr(){

        val listItem =
                listOf(SpeedDialActionItem.Builder(R.id.fab_gen_qr, R.drawable.ic_gen_qr_code)
                        .setLabel("Tạo QR")
                        .setFabBackgroundColor(resources.getColor(R.color.colorWhite))
                        .create()
                )
        gen_qr_btn.addAllActionItems(listItem)

        image_profile.setOnClickListener {
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

        gen_qr_btn.setOnActionSelectedListener{
            when (it.id){
                R.id.fab_gen_qr ->{
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
            false
        }
    }

    @SuppressLint("SetTextI18n")
    private fun readInfo(){
        var readDb: Int
        var valueDb= ""
        sqLite = SqLite(this)
        try{
            valueDb = sqLite.readCalendar()
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
            studentName = infoObj.getString("studentName").trim()
            studentId = infoObj.getString("studentId").trim()
            className = infoObj.getString("className").trim()
            courseName = infoObj.getString("courseName").trim()
            majorsName = infoObj.getString("majorsName").trim()
            student_name.text = studentName
            student_id.text = "Mã sinh viên: $studentId"
            class_name.text = "Lớp: $className"
            course_name.text = "Khóa: $courseName"
            majors_name.text = "Ngành: $majorsName"
        }
    }

    private fun loadAds(){
        ads = Ads(this)
        ads.apply {
            loadBottomAds(ads_bottom)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_student)
        readInfo()
        genQr()
        loadAds()
    }
}
