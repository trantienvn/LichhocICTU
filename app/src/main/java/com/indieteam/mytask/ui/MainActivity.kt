package com.indieteam.mytask.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.text.style.LineBackgroundSpan
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import com.indieteam.mytask.R
import com.indieteam.mytask.adapter.CalendarListViewAdapter
import com.indieteam.mytask.modeldata.CalendarData
import com.indieteam.mytask.modeldata.CalendarFinal
import com.indieteam.mytask.modeldata.OnlyCalendarData
import com.indieteam.mytask.process.*
import com.prolificinteractive.materialcalendarview.MaterialCalendarView.SELECTION_MODE_SINGLE
import com.prolificinteractive.materialcalendarview.format.TitleFormatter
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import com.github.pwittchen.swipe.library.rx2.Swipe
import com.indieteam.mytask.process.v1.ExelToCalendarRaw
import com.indieteam.mytask.process.v1.ParseCalendarRaw
import com.indieteam.mytask.process.v1.ReadExel
import com.indieteam.mytask.sqlite.SqlLite
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import com.prolificinteractive.materialcalendarview.*
import kotlin.concurrent.scheduleAtFixedRate


class MainActivity : AppCompatActivity() {

    val file = File(Environment.getExternalStorageDirectory(), "tkb.xls")

    private val REQUEST_CODE = 1
    var nameSubject = ""
    lateinit var tc: String
    lateinit var time: String
    var calendarRaw = ArrayList<CalendarData>()
    var calendarMap = mutableMapOf<String, OnlyCalendarData>()
    var calendarDataTemp = ArrayList<CalendarData>()
    private lateinit var readExel: ReadExel
    private lateinit var exelToCalendarRaw: ExelToCalendarRaw
    private lateinit var parseCalendarRaw: ParseCalendarRaw
    lateinit var sqlLite: SqlLite
    private var calendarResult: JSONObject? = null
    private var parseCalendarJson: ParseCalendarJson? = null
    var mapDate = mutableMapOf<CalendarDay, String>()
    private var calendarFinalArr = ArrayList<CalendarFinal>()
    private val dateStart = CalendarDay.from(Calendar.getInstance().get(Calendar.YEAR), 1, 1)
    private val dateEnd = CalendarDay.from(Calendar.getInstance().get(Calendar.YEAR) + 1, 12, 31)
    var readExelCallback = 0
    private var allPermission = 0
    private val swipe = Swipe()
    private lateinit var animation: Animation
    private val splash = SplashFragment()
    var addDotCallBack = 0


    inner class OnSwipeListener: com.github.pwittchen.swipe.library.rx2.SwipeListener{
        override fun onSwipedUp(event: MotionEvent?): Boolean {
            //Toast.makeText(this@MainActivity, "Swiped up", Toast.LENGTH_SHORT).show()
            return true
        }

        override fun onSwipedDown(event: MotionEvent?): Boolean {
            //Toast.makeText(this@MainActivity, "Swiped down", Toast.LENGTH_SHORT).show()
            return true
        }

        override fun onSwipingUp(event: MotionEvent?) {
            //Toast.makeText(this@MainActivity, "Swiping right", Toast.LENGTH_SHORT).show()
        }

        override fun onSwipedRight(event: MotionEvent?): Boolean {
            //Toast.makeText(this@MainActivity, "Swiped right", Toast.LENGTH_SHORT).show()
            if (event!!.y > content_layout.y) {
                animation.right()
            }
            return true
        }

        override fun onSwipingLeft(event: MotionEvent?) {
            //Toast.makeText(this@MainActivity, "Swiping left", Toast.LENGTH_SHORT).show()
        }

        override fun onSwipingRight(event: MotionEvent?) {
            //Toast.makeText(this@MainActivity, "Swiping right", Toast.LENGTH_SHORT).show()
        }

        override fun onSwipingDown(event: MotionEvent?) {
            //Toast.makeText(this@MainActivity, "Swiping down", Toast.LENGTH_SHORT).show()
        }

        override fun onSwipedLeft(event: MotionEvent?): Boolean {
            //Toast.makeText(this@MainActivity, "Swipe left", Toast.LENGTH_SHORT).show()
            if (event!!.y > content_layout.y) {
                animation.left()
            }
            return true
        }

    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        swipe.dispatchTouchEvent(event)
        return super.dispatchTouchEvent(event)
    }

    override fun onBackPressed() {
        toDay()
    }

    fun preDate(){
        val calendarSelected = Calendar.getInstance()
        calendarSelected.set(calendarView.selectedDate.year, calendarView.selectedDate.month, calendarView.selectedDate.day)
        calendarSelected.add(Calendar.DAY_OF_MONTH, -1)
        val newCaldenderDate = CalendarDay.from(calendarSelected)
        calendarView.currentDate = newCaldenderDate
        calendarView.selectedDate = newCaldenderDate

        val date = "${newCaldenderDate.day}/${newCaldenderDate.month + 1}/${newCaldenderDate.year}"
        updateListView(date)
    }

    fun nextDate(){
        val calendarSelected = Calendar.getInstance()
        calendarSelected.set(calendarView.selectedDate.year, calendarView.selectedDate.month, calendarView.selectedDate.day)
        calendarSelected.add(Calendar.DAY_OF_MONTH, 1)
        val newCaldenderDate = CalendarDay.from(calendarSelected)
        calendarView.currentDate = newCaldenderDate
        calendarView.selectedDate = newCaldenderDate

        val date = "${newCaldenderDate.day}/${newCaldenderDate.month + 1}/${newCaldenderDate.year}"
        updateListView(date)
    }

    private fun init(){
        readExel = ReadExel(this)
        exelToCalendarRaw = ExelToCalendarRaw(this)
        parseCalendarRaw = ParseCalendarRaw(this)
        sqlLite = SqlLite(this)
        animation = Animation(this)
        calenderEvents()
        title = ""
    }

    private fun calendarSetting(){
        calendarView.state().edit().
                setMinimumDate(dateStart)
                .setMaximumDate(dateEnd)
                .commit()
        calendarView.state().edit()
        calendarView.currentDate = CalendarDay.today()
        calendarView.selectedDate = CalendarDay.today()
        calendarView.selectionMode = SELECTION_MODE_SINGLE

        setCalendarDots()
    }

    private fun setCalendarDots(){
        for(i in mapDate){
            calendarView.addDecorator(EventDecorator(Color.WHITE, i.key, i.value))
            Log.d("valuedot", i.value)
        }
    }

    private fun calenderEvents(){
        calendarView.setOnDateChangedListener { materialCalendarView, calendarDay, b ->
            val date = "${calendarDay.day}/${calendarDay.month+1}/${calendarDay.year}"
            //Toast.makeText(this, "$dateFormated", Toast.LENGTH_LONG).show()
            updateListView(date)
        }
        calendarView.setOnMonthChangedListener { materialCalendarView, calendarDay ->
            calendarView.setTitleFormatter(TitleFormatter { "Tháng ${calendarDay.month+1} Năm ${calendarDay.year}" })
        }
    }

    private fun updateListView(date: String){
        if (parseCalendarJson != null){
            calendarFinalArr.removeAll(calendarFinalArr)
            parseCalendarJson!!.apply { getSubject(date)

                if (!subjectDate.isEmpty() && !subjectName.isEmpty() && !subjectTime.isEmpty() && !subjectPlace.isEmpty()) {
                    var result = ""
                    if (subjectDate.size == subjectName.size && subjectDate.size == subjectTime.size
                            && subjectDate.size == subjectPlace.size) {
                        for (j in 0 until subjectDate.size) {
                            Log.d("result", "${subjectDate[j]}, ${subjectName[j]}, ${subjectTime[j]}, ${subjectPlace[j]}")
                            result += "${subjectDate[j]}, ${subjectName[j]}, ${subjectTime[j]}, ${subjectPlace[j]} \n"
                            calendarFinalArr.add(CalendarFinal(subjectDate[j], subjectName[j], subjectTime[j], subjectPlace[j]))
                        }
                        //log.text = result
                        calender_list_view.adapter = null
                        calender_list_view.adapter = CalendarListViewAdapter(this@MainActivity, calendarFinalArr)
                    }
                } else {
                    Log.d("result", "$date Nghỉ")
                    //log.text = "$date Nghỉ"
                    calender_list_view.adapter = null
                    calender_list_view.adapter = CalendarListViewAdapter(this@MainActivity, calendarFinalArr)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        calendarSetting()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission()
            if (allPermission == 1) {
                run()
            }
        }else
            run()
    }

    private fun toDay(){
            calendarView.currentDate = CalendarDay.today()
            calendarView.selectedDate = CalendarDay.today()
            val date = "${CalendarDay.today().day}/${CalendarDay.today().month+1}/${CalendarDay.today().year}"
            updateListView(date)
    }

    var floatClick = 0

    private fun initFloatButton(){
        float_button.addActionItem(
                SpeedDialActionItem.Builder(R.id.fab_setting, R.drawable.ic_switch)
                        .setLabel("Chuyển")
                        .setFabBackgroundColor(resources.getColor(R.color.colorPrimary))
                        .create()
        )

        float_button.setOnActionSelectedListener {
            floatClick++
            when(it.id){
                R.id.fab_setting ->{
                    //Toast.makeText(this@MainActivity, "Setting", Toast.LENGTH_SHORT).show()
                    if(floatClick %2 == 0)
                        calendarView.state().edit()
                                .setCalendarDisplayMode(CalendarMode.WEEKS)
                                .commit()
                    else
                        calendarView.state().edit()
                                .setCalendarDisplayMode(CalendarMode.MONTHS)
                                .commit()
                }
            }
            false //false to close float button
        }
    }

    private fun run(){
        var readDb: Int
        var valueDb= ""
        try{
            valueDb = sqlLite.read()
            readDb = 1
            Log.d("readdb", "read db done")
        }catch (e: Exception){
            readDb = 0
            Log.d("readdb", "db is not exits, cannot read")
            Log.d("err", e.toString())
        }

        if(readDb == 0){
            readExel.readTkbExel()

            while (readExelCallback == 0) {
                //wait ...
                Log.d("wait", "wait")
                if(readExelCallback == - 1)
                    break
            }

            if(readExelCallback == 1) {

                exelToCalendarRaw.apply {
                    trimTkbData()
                    addToMap()
                }
                parseCalendarRaw.parse()
                calendarResult = JSONObject(sqlLite.read())
                if (calendarResult != null) {
                    //log.text = calendarResult.toString()
                    parseCalendarJson = ParseCalendarJson(this, calendarResult!!)
                }
            }
        }else{
            calendarResult = JSONObject(valueDb)
            parseCalendarJson = ParseCalendarJson(this, calendarResult!!)
        }
        initFloatButton()
        toDay()
        parseCalendarJson!!.addToMapDot()
        setCalendarDots()
        swipe.setListener(OnSwipeListener())

        // try run in Thread
//        object : Thread() {
//            override fun run() {
//                while (addDotCallBack == 0){}
//                runOnUiThread { setCalendarDots() }
//                Log.d("addDotCallBack", addDotCallBack.toString())
//                this.join()
//            }
//        }.start()
    }

    private fun checkPermission(){
        if(Build.VERSION.SDK_INT >= 23) {
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == REQUEST_CODE){
            if(grantResults.size == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
            && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                run()
                Toast.makeText(this@MainActivity, "Permissions is granted", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this@MainActivity, "Permissions is not granted", Toast.LENGTH_LONG).show()
                finish()
            }
        }else{
            Toast.makeText(this@MainActivity, "Permissions is not granted", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    inner class DrawLableForDate(private val radius: Float, private val color: Int, private val price: String) : LineBackgroundSpan {

        override fun drawBackground(canvas: Canvas, paint: Paint,
                                    left: Int, right: Int, top: Int,
                                    baseline: Int, bottom: Int,
                                    charSequence: CharSequence,
                                    start: Int, end: Int, lineNum: Int) {
            val oldColor = paint.color
            val oldTextSize = paint.textSize
            if (color != 0) {
                paint.color = color
            }
            if (price != "") {
                paint.textSize = 60f
            }
            val text = price
            val bounds = Rect()
            paint.getTextBounds(text, 0, text.length, bounds)
            val x = right/2 - bounds.width()/2
            val y = 1.5*bottom
            canvas.drawText(text, x.toFloat(), y.toFloat(), paint)
            paint.textSize = oldTextSize
            paint.color = oldColor
        }
    }

    inner class EventDecorator(private val color: Int, val date: CalendarDay, private val dot: String): DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return date == day
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(DrawLableForDate(10f, color, dot))
        }
    }

}
