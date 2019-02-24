package com.indieteam.mytask.ui

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteConstraintException
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.style.LineBackgroundSpan
import android.util.Log
import android.view.MotionEvent
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewTreeObserver
import android.widget.Toast
import com.github.pwittchen.swipe.library.rx2.Swipe
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.indieteam.mytask.R
import com.indieteam.mytask.ui.adapter.ScheduleAdapter
import com.indieteam.mytask.model.ads.Ads
import com.indieteam.mytask.collection.StudentCalendarCollection
import com.indieteam.mytask.collection.TimeScheduleDetails
import com.indieteam.mytask.model.InternetState
import com.indieteam.mytask.model.schedule.domHTML.DomUpdateSchedule
import com.indieteam.mytask.model.SyncToGoogleCalendar
import com.indieteam.mytask.model.schedule.parseData.ParseScheduleJson
import com.indieteam.mytask.model.notification.AppNotification
import com.indieteam.mytask.model.schedule.domHTML.DomSemesterSchedule
import com.indieteam.mytask.model.service.AppService
import com.indieteam.mytask.model.SqLite
import com.indieteam.mytask.ui.fragment.*
import com.indieteam.mytask.ui.interface_.OnLoginListener
import com.indieteam.mytask.ui.interface_.OnSemesterScheduleListener
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView.SELECTION_MODE_SINGLE
import kotlinx.android.synthetic.main.activity_week.*
import kotlinx.android.synthetic.main.fragment_process_bar.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@Suppress("DEPRECATION")
class WeekActivity : AppCompatActivity() {

    private val REQUEST_ACCOUNT = 1
    private lateinit var sqLite: SqLite
    private var scheduleJson: JSONObject? = null
    var parseScheduleJson: ParseScheduleJson? = null
    var dots = mutableMapOf<CalendarDay, String>()
    var studentScheduleObjArr = ArrayList<StudentCalendarCollection>()
    private val dateStart = CalendarDay.from(Calendar.getInstance().get(Calendar.YEAR) - 1, 0, 1)
    private val dateEnd = CalendarDay.from(Calendar.getInstance().get(Calendar.YEAR) + 1, 11, 31)
    private lateinit var scheduleAdapter: ScheduleAdapter
    private var isAccountPermission = 0
    private val swipe = Swipe()
    private lateinit var customSwipe: CustomSwipe
    private var screenHeight = 0
    private var statusBarHeight = 0
    private var navigationBarHeight = 0
    private var layoutCalendarMode = 0
    lateinit var sharedPref: SharedPreferences
    //private val background = listOf(R.drawable.bg_a, R.drawable.bg_b, R.drawable.bg_c, R.drawable.bg_i)
    private val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
    private val timeDetails = TimeScheduleDetails()
    private lateinit var internetState: InternetState
    private lateinit var appNotification: AppNotification
    private lateinit var addScheduleFragment: AddScheduleFragment

    // Google oauth2
    lateinit var credential: GoogleAccountCredential
    var scope = Scope("https://www.googleapis.com/auth/calendar")
    var scope2 = Scope("https://www.googleapis.com/auth/calendar.events")
    var RC_SIGN_IN = 2
    val httpTransport = AndroidHttp.newCompatibleTransport()
    var jsonFactory = GsonFactory.getDefaultInstance()
    lateinit var service: com.google.api.services.calendar.Calendar
    var appName = "mystask-calendar"
    lateinit var gso: GoogleSignInOptions
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var signInIntent: Intent

    lateinit var modifyDialog: ModifyDialog

    //Ads
    private lateinit var ads: Ads

    private val onLoginListener = object : OnLoginListener {
        override fun onLogin() {
        }

        override fun onSuccess(username: String, password: String, cookie: String, sessionUrl: String) {
            try {
                sqLite.updateInfo(username, password, cookie)
            } catch (e: SQLiteConstraintException) {
                e.printStackTrace()
            }
            DomSemesterSchedule(this@WeekActivity, sessionUrl, cookie, onSemesterScheduleListener).start()
        }


        override fun onFail() {
        }

        override fun onThrow(t: String) {
            runOnUiThread {
                supportFragmentManager.findFragmentByTag("processBarUpdate")?.let {
                    supportFragmentManager.beginTransaction().remove(it)
                            .commit()
                }
                visible()
                Toast.makeText(this@WeekActivity, t, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val onSemesterScheduleListener = object : OnSemesterScheduleListener {
        override fun onSuccess(semester: String, sessionUrl: String, signIn: String) {
            val bundle = Bundle()
            bundle.putString("semester", semester)
            bundle.putString("sessionUrl", sessionUrl)
            bundle.putString("signIn", signIn)

            val selectSemesterFragment = SelectSemesterFragment()
            selectSemesterFragment.arguments = bundle

            supportFragmentManager.findFragmentByTag("processBarUpdate")?.let{
                supportFragmentManager.beginTransaction().remove(it)
                        .commit()
            }
            supportFragmentManager.beginTransaction().add(R.id.calendar_root_view, selectSemesterFragment, "selectSemesterFragment")
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
                supportFragmentManager.findFragmentByTag("processBarUpdate")?.let{
                    supportFragmentManager.beginTransaction().remove(it)
                            .commit()
                }
                visible()
                Toast.makeText(this@WeekActivity, t, Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class OnSwipeListener : com.github.pwittchen.swipe.library.rx2.SwipeListener {
        private var startTouchY = 0f

        override fun onSwipedUp(event: MotionEvent?): Boolean {
            //Toast.makeText(this@WeekActivity, "Swiped up", Toast.LENGTH_SHORT).show()
            return true
        }

        override fun onSwipedDown(event: MotionEvent?): Boolean {
            //Toast.makeText(this@WeekActivity, "Swiped down", Toast.LENGTH_SHORT).show()
            return true
        }

        override fun onSwipingUp(event: MotionEvent?) {
            //Toast.makeText(this@WeekActivity, "Swiping right", Toast.LENGTH_SHORT).show()
        }

        override fun onSwipedRight(event: MotionEvent?): Boolean {
            //Toast.makeText(this@WeekActivity, "Swiped right", Toast.LENGTH_SHORT).show()
            if (startTouchY > content_layout.y && event!!.y > content_layout.y) {
                customSwipe.right()
            }
            countSwipeRight = 0
            return true
        }

        private var countSwipeLeft = 0
        override fun onSwipingLeft(event: MotionEvent?) {
            if (countSwipeLeft == 0) {
                event?.let {
                    startTouchY = event.y
                }
            }
            countSwipeLeft++
            //Toast.makeText(this@WeekActivity, "Swiping left", Toast.LENGTH_SHORT).show()
        }

        private var countSwipeRight = 0
        override fun onSwipingRight(event: MotionEvent?) {
            if (countSwipeRight == 0) {
                event?.let {
                    startTouchY = event.y
                }
            }
            countSwipeRight++
            //Toast.makeText(this@WeekActivity, "Swiping right", Toast.LENGTH_SHORT).show()
        }

        override fun onSwipingDown(event: MotionEvent?) {
            //Toast.makeText(this@WeekActivity, "Swiping down", Toast.LENGTH_SHORT).show()
        }

        override fun onSwipedLeft(event: MotionEvent?): Boolean {
            //Toast.makeText(this@WeekActivity, "Swipe left", Toast.LENGTH_SHORT).show()
            if (startTouchY > content_layout.y && event!!.y > content_layout.y) {
                customSwipe.left()
            }
            countSwipeLeft = 0
            return true
        }

    }

    inner class DrawDots(private val colors: List<Int>, private val text: String) : LineBackgroundSpan {

        override fun drawBackground(c: Canvas, p: Paint,
                                    left: Int, right: Int, top: Int,
                                    baseline: Int, bottom: Int,
                                    charSequence: CharSequence,
                                    start: Int, end: Int, lineNum: Int) {
            val lastColor = p.color
            var length = 0
            if (text.length == 1) {
                p.color = colors[0]
                length = text.length
            }
            if (text.length == 2) {
                p.color = colors[1]
                length = text.length
            }
            if (text.length in 3..5) {
                p.color = colors[2]
                length = text.length
            }
            if (text.length > 5) {
                p.color = colors[2]
                length = 5
            }
            var totalWidth = 0f
            for (i in 0 until length)
                if (i != 0)
                    totalWidth += (right.toFloat() / 100f) * 7.5f // 7.5 is space (percent) margin left of dots
            var cX = right / 2f - totalWidth / 2
            for (i in 0 until length) {
                c.drawCircle(cX, bottom.toFloat() + (bottom.toFloat() / 100f) * 10f, (right.toFloat() / 100f) * 2.1f, p)
                cX += (right.toFloat() / 100f) * 7.5f
            }
            p.color = lastColor
        }
    }

    inner class EventDecorator(private val mode: String, private val colors: List<Int>, val date: CalendarDay, private val dot: String) : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return date == day
        }

        override fun decorate(view: DayViewFacade) {
            if (mode == "Dots")
                view.addSpan(DrawDots(colors, dot))
            if (mode == "ToDay")
                view.setBackgroundDrawable(resources.getDrawable(R.drawable.shape_bg_cal_today))
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        try {
            swipe.dispatchTouchEvent(event)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_ACCOUNT) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isAccountPermission = 1
                val syncGoogle = SyncToGoogleCalendar(this)
                syncGoogle.start()
            } else {
                Toast.makeText(this@WeekActivity, "Permissions is not granted", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onBackPressed() {
        var quit = true
        if (supportFragmentManager.findFragmentByTag("processBarUpdate") == null) {
            if (supportFragmentManager.findFragmentByTag("addScheduleFragment") != null) {
                supportFragmentManager.beginTransaction().remove(addScheduleFragment)
                        .commit()
                visible()
                quit = false
            }
            if (supportFragmentManager.findFragmentByTag("selectSemesterFragment") != null) {
                supportFragmentManager.beginTransaction().remove(supportFragmentManager.findFragmentByTag("selectSemesterFragment")!!)
                        .commit()
                visible()
                quit = false
            }
            if (supportFragmentManager.findFragmentByTag("updateCalendarFragment") != null) {
                supportFragmentManager.beginTransaction().remove(supportFragmentManager.findFragmentByTag("updateCalendarFragment")!!)
                        .commit()
                visible()
                quit = false
            }
            if (supportFragmentManager.findFragmentByTag("selectTestScheduleFragment") != null) {
                supportFragmentManager.beginTransaction().remove(supportFragmentManager.findFragmentByTag("selectTestScheduleFragment")!!)
                        .commit()
                visible()
                quit = false
            }
            if (quit) {
                if (calendarView.selectedDate == CalendarDay.today())
                    super.onBackPressed()
                else
                    toDay()
            }
        }
    }

    private fun checkAccountPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.GET_ACCOUNTS), REQUEST_ACCOUNT)
            } else {
                isAccountPermission = 1
            }
        } else {
            isAccountPermission = 1
        }
    }

    private fun init() {
        sqLite = SqLite(this)
        customSwipe = CustomSwipe(this)
        calenderEvents()
        title = ""
        val point = Point()
        windowManager.defaultDisplay.getSize(point)
        screenHeight = point.y
        val resourcesId = resources.getIdentifier("status_bar_height", "dimen", "android")
        statusBarHeight = resources.getDimensionPixelSize(resourcesId)
        val resourcesId2 = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        navigationBarHeight = resources.getDimensionPixelSize(resourcesId2)
        sharedPref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        layoutCalendarMode = sharedPref.getInt("CalendarMode", 0)
        scheduleAdapter = ScheduleAdapter(this@WeekActivity, studentScheduleObjArr)
        calender_list_view.adapter = scheduleAdapter
        internetState = InternetState(this)
        ads = Ads(this)
        appNotification = AppNotification(this)
        modifyDialog = ModifyDialog(this)
    }

    fun preDate() {
        val calendarSelected = Calendar.getInstance()
        calendarSelected.set(calendarView.selectedDate.year, calendarView.selectedDate.month, calendarView.selectedDate.day)
        calendarSelected.add(Calendar.DAY_OF_MONTH, -1)
        val newCalendarDate = CalendarDay.from(calendarSelected)
        calendarView.currentDate = newCalendarDate
        calendarView.selectedDate = newCalendarDate

        val date = "${newCalendarDate.day}/${newCalendarDate.month + 1}/${newCalendarDate.year}"
        updateListView(date)
    }

    fun nextDate() {
        val calendarSelected = Calendar.getInstance()
        calendarSelected.set(calendarView.selectedDate.year, calendarView.selectedDate.month, calendarView.selectedDate.day)
        calendarSelected.add(Calendar.DAY_OF_MONTH, 1)
        val newCalendarDate = CalendarDay.from(calendarSelected)
        calendarView.currentDate = newCalendarDate
        calendarView.selectedDate = newCalendarDate

        val date = "${newCalendarDate.day}/${newCalendarDate.month + 1}/${newCalendarDate.year}"
        updateListView(date)
    }

    private fun calendarSetting() {
        calendarView.topbarVisible = true
        calendarView.state().edit().setMinimumDate(dateStart)
                .setMaximumDate(dateEnd)
                .commit()
        view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                //view.height //height is ready?
                if (layoutCalendarMode == 0) {
                    calendarView.state().edit()
                            .setCalendarDisplayMode(CalendarMode.WEEKS)
                            .commit()
                } else {
                    calendarView.layoutParams.height = (screenHeight / 2.3).toInt()
                    content_layout.layoutParams.height = (screenHeight - (screenHeight / 2.3f) - view.height - statusBarHeight).toInt()
                    calendarView.state().edit()
                            .setCalendarDisplayMode(CalendarMode.MONTHS)
                            .commit()
                }
                Log.d("view_height", view.height.toString())
            }
        })
        calendarView.currentDate = CalendarDay.today()
        calendarView.selectedDate = CalendarDay.today()
        calendarView.selectionMode = SELECTION_MODE_SINGLE
    }

    private fun setCalendarDots() {
        for (i in dots) {
            calendarView.addDecorator(EventDecorator("Dots", listOf(resources.getColor(R.color.colorBlue), resources.getColor(R.color.colorOrange), resources.getColor(R.color.colorRed)), i.key, i.value))
        }
    }

    private fun drawBackgroundToday() {
        calendarView.addDecorator(EventDecorator("ToDay", listOf(resources.getColor(R.color.colorGrayDark)), CalendarDay.today(), "null"))
    }

    private fun calenderEvents() {
        calendarView.setOnDateChangedListener { materialCalendarView, calendarDay, b ->
            val date = "${calendarDay.day}/${calendarDay.month + 1}/${calendarDay.year}"
            updateListView(date)
        }
        calendarView.setOnDateLongClickListener { materialCalendarView, calendarDay ->
            gone()
            val date = "${calendarDay.day}/${calendarDay.month + 1}/${calendarDay.year}"
            val bundle = Bundle()
            bundle.putString("date", date)
            addScheduleFragment = AddScheduleFragment()
            addScheduleFragment.arguments = bundle
            supportFragmentManager.beginTransaction().add(R.id.calendar_root_view, addScheduleFragment, "addScheduleFragment")
                    .commit()
        }
        calendarView.setOnMonthChangedListener { materialCalendarView, calendarDay ->
            /*object: CalendarView(this) {
                fun setTitleFormatter(){
                    return
                }
            }*/
            calendarView.setTitleFormatter { "Tháng ${calendarDay.month + 1} Năm ${calendarDay.year}" }
        }
    }

    private fun updateListView(date: String) {
        val day = date.substring(0, date.indexOf("/")).toInt()
        val month = date.substring(date.indexOf("/") + 1, date.lastIndexOf("/")).toInt()
        //val year = date.substring(date.lastIndexOf("/") + 1, date.length).toInt()

        if (parseScheduleJson != null) {
            studentScheduleObjArr.removeAll(studentScheduleObjArr)
            parseScheduleJson!!.apply {
                getSubject(date)
                if (!subjectName.isEmpty() &&
                        !subjectTime.isEmpty() &&
                        !subjectPlace.isEmpty() &&
                        !teacher.isEmpty()) {
                    if (subjectName.size == subjectTime.size &&
                            subjectName.size == subjectPlace.size &&
                            subjectName.size == teacher.size) {
                        for (j in 0 until subjectName.size) {
                            var firstTime: Int
                            var endTime: Int
                            if (subjectTime[j].indexOf(",") > -1) {
                                firstTime = subjectTime[j].substring(0, subjectTime[j].indexOf(",")).toInt() - 1
                                endTime = subjectTime[j].substring(subjectTime[j].lastIndexOf(",") + 1, subjectTime[j].length).toInt() - 1
                                if (CalendarDay.from(2020, month, day).date >= CalendarDay.from(2020, 3, 15).date &&
                                        CalendarDay.from(2020, month, day).date < CalendarDay.from(2020, 9, 15).date)
                                    studentScheduleObjArr.add(StudentCalendarCollection(subjectName[j], /*subjectDate[j]*/"", subjectTime[j] + " (${timeDetails.timeSummerArr[firstTime].timeIn} -> ${timeDetails.timeSummerArr[endTime].timeOut})", subjectPlace[j], teacher[j]))
                                else
                                    studentScheduleObjArr.add(StudentCalendarCollection(subjectName[j], /*subjectDate[j]*/"", subjectTime[j] + " (${timeDetails.timeWinterArr[firstTime].timeIn} -> ${timeDetails.timeWinterArr[endTime].timeOut})", subjectPlace[j], teacher[j]))
                            } else {
                                firstTime = subjectTime[j].toInt() - 1
                                if (CalendarDay.from(2020, month, day).date >= CalendarDay.from(2020, 3, 15).date &&
                                        CalendarDay.from(2020, month, day).date < CalendarDay.from(2020, 9, 15).date)
                                    studentScheduleObjArr.add(StudentCalendarCollection(subjectName[j], /*subjectDate[j]*/"", subjectTime[j] + " (${timeDetails.timeSummerArr[firstTime].timeIn} -> ${timeDetails.timeSummerArr[firstTime].timeOut})", subjectPlace[j], teacher[j]))
                                else
                                    studentScheduleObjArr.add(StudentCalendarCollection(subjectName[j], /*subjectDate[j]*/"", subjectTime[j] + " (${timeDetails.timeWinterArr[firstTime].timeIn} -> ${timeDetails.timeWinterArr[firstTime].timeOut})", subjectPlace[j], teacher[j]))
                            }
                        }
                        scheduleAdapter.notifyDataSetChanged()
                    }
                } else {
                    // Nghi
                    scheduleAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(this)
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(this, status, 2404).show()
            }
            return false
        }
        return true
    }

    private fun initFloatButton() {
        val listItem =
                listOf(SpeedDialActionItem.Builder(R.id.fab_logout, R.drawable.ic_logout)
                        .setLabel("Đ.xuất")
                        .setLabelColor(Color.BLACK)
                        .setLabelBackgroundColor(resources.getColor(R.color.colorWhite))
                        .setFabBackgroundColor(resources.getColor(R.color.colorWhite))
                        .create(),
                        SpeedDialActionItem.Builder(R.id.fab_donate, R.drawable.ic_info)
                                .setLabel("G.thiệu")
                                .setLabelColor(Color.BLACK)
                                .setLabelBackgroundColor(resources.getColor(R.color.colorWhite))
                                .setFabBackgroundColor(resources.getColor(R.color.colorWhite))
                                .create(),
                        SpeedDialActionItem.Builder(R.id.fab_setting, R.drawable.ic_switch)
                                .setLabel("Tuần/Tháng")
                                .setLabelColor(Color.BLACK)
                                .setLabelBackgroundColor(resources.getColor(R.color.colorWhite))
                                .setFabBackgroundColor(resources.getColor(R.color.colorWhite))
                                .create(),
                        SpeedDialActionItem.Builder(R.id.fab_sync_google, R.drawable.ic_cloud_upload_24dp)
                                .setLabel("Tải lên Google Calendar")
                                .setLabelColor(Color.BLACK)
                                .setLabelBackgroundColor(resources.getColor(R.color.colorWhite))
                                .setFabBackgroundColor(resources.getColor(R.color.colorWhite))
                                .create(),
                        SpeedDialActionItem.Builder(R.id.fab_update, R.drawable.ic_update)
                                .setLabel("C.nhật lịch")
                                .setLabelColor(Color.BLACK)
                                .setLabelBackgroundColor(resources.getColor(R.color.colorWhite))
                                .setFabBackgroundColor(resources.getColor(R.color.colorWhite))
                                .create(),
                        SpeedDialActionItem.Builder(R.id.fab_test, R.drawable.ic_schedule_24dp)
                                .setLabel("Xem lịch thi")
                                .setLabelColor(Color.BLACK)
                                .setLabelBackgroundColor(resources.getColor(R.color.colorWhite))
                                .setFabBackgroundColor(resources.getColor(R.color.colorWhite))
                                .create(),
                        SpeedDialActionItem.Builder(R.id.fab_info, R.drawable.ic_profile)
                                .setLabel("C.nhân/QR")
                                .setLabelColor(Color.BLACK)
                                .setLabelBackgroundColor(resources.getColor(R.color.colorWhite))
                                .setFabBackgroundColor(resources.getColor(R.color.colorWhite))
                                .create()

                )
        float_button.addAllActionItems(listItem)

        float_button.setOnActionSelectedListener { it ->
            when (it.id) {
                R.id.fab_setting -> {
                    if (layoutCalendarMode == 1) {
                        calendarView.layoutParams.height = ((screenHeight / 100f) * 21f).toInt()
                        content_layout.layoutParams.height = ((screenHeight - (screenHeight / 100f) * 21f) - view.height - statusBarHeight).toInt()
                        calendarView.state().edit()
                                .setCalendarDisplayMode(CalendarMode.WEEKS)
                                .commit()
                        sharedPref.apply {
                            with(edit()) {
                                putInt("CalendarMode", 0)
                                apply()
                            }
                        }
                        layoutCalendarMode = sharedPref.getInt("CalendarMode", 0)
                    } else {
                        calendarView.layoutParams.height = (screenHeight / 2.3f).toInt()
                        content_layout.layoutParams.height = (screenHeight - (screenHeight / 2.3f) - view.height - statusBarHeight).toInt()
                        calendarView.state().edit()
                                .setCalendarDisplayMode(CalendarMode.MONTHS)
                                .commit()
                        sharedPref.apply {
                            with(edit()) {
                                putInt("CalendarMode", 1)
                                apply()
                            }
                        }
                        layoutCalendarMode = sharedPref.getInt("CalendarMode", 0)
                    }
                }
                R.id.fab_info -> {
                    val intent = Intent(this@WeekActivity, StudentInfoActivity::class.java)
                    startActivity(intent)
                }
                R.id.fab_sync_google -> {
                    if (internetState.state()) {
                        if (isGooglePlayServicesAvailable()) {
                            checkAccountPermission()
                            if (isAccountPermission == 1) {
                                if (sharedPref.getString("accSelected", "null") != "null") {
                                    appNotification.syncing()
                                    sharedPref.edit().apply {
                                        putBoolean("isSyncing", true)
                                        apply()
                                    }
                                    val syncGoogle = SyncToGoogleCalendar(this)
                                    syncGoogle.start()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this, "Mất kết nối", Toast.LENGTH_SHORT).show()
                    }
                }
                R.id.fab_test -> {
                    gone()
                    supportFragmentManager.beginTransaction().add(R.id.calendar_root_view, SelectTestScheduleFragment(), "selectTestScheduleFragment")
                            .commit()
                    supportFragmentManager.executePendingTransactions()
                    supportFragmentManager.beginTransaction().add(R.id.calendar_root_view, ProcessBarFragment(), "processBarUpdate")
                            .commit()
                    supportFragmentManager.executePendingTransactions()
                    supportFragmentManager.findFragmentByTag("processBarUpdate")?.apply {
                        process?.text = "Tải lịch thi..."
                    }
                }
                R.id.fab_update -> {
                    if (internetState.state()) {
                        supportFragmentManager.beginTransaction().add(R.id.calendar_root_view, ProcessBarFragment(), "processBarUpdate")
                                .commit()
                        supportFragmentManager.executePendingTransactions()
                        supportFragmentManager.findFragmentByTag("processBarUpdate")?.apply {
                            process?.text = "Tải học kỳ..."
                        }
                        gone()
                        try {
                            DomUpdateSchedule(this, sqLite.readCookie(), onLoginListener).start()
                        } catch (e: Exception) {
                            visible()
                            supportFragmentManager.findFragmentByTag("processBarUpdate")?.let {
                                supportFragmentManager.beginTransaction().remove(it).commit()
                            }
                            Toast.makeText(this, "Err update", Toast.LENGTH_SHORT).show()
                            Log.d("Err", e.toString())
                        }
                    } else {
                        Toast.makeText(this, "Mất kết nối", Toast.LENGTH_SHORT).show()
                    }
                }
                R.id.fab_donate -> {
                    val intent = Intent(this, AboutActivity::class.java)
                    startActivity(intent)
                }
                R.id.fab_logout -> {
                    if (internetState.state()) {
                        try {
                            sqLite.deleteCalendar()
                            sqLite.deleteInfo()
                            if (checkServiceRunning())
                                stopService(Intent(this, AppService::class.java))
                        } catch (e: Exception) {
                            Log.d("Err", e.toString())
                        }
                        moveToLogin()
                    } else {
                        Toast.makeText(this, "Đang giữ lịch an toàn khi ngoại tuyến", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            false //false to close float button
        }
    }

    fun gone() {
        calendarView.visibility = GONE
        view.visibility = GONE
        content_layout.visibility = GONE
        float_button.visibility = GONE
    }

    fun visible() {
        calendarView.visibility = VISIBLE
        view.visibility = VISIBLE
        content_layout.visibility = VISIBLE
        float_button.visibility = VISIBLE
    }

    private fun moveToLogin() {
        val intent = Intent(this@WeekActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun toDay() {
        calendarView.currentDate = CalendarDay.today()
        calendarView.selectedDate = CalendarDay.today()
        val date = "${CalendarDay.today().day}/${CalendarDay.today().month + 1}/${CalendarDay.today().year}"
        updateListView(date)
    }

    private fun selectedDate(date: String) {
        val day = date.substring(0, date.indexOf("/")).toInt()
        val month = date.substring(date.indexOf("/") + 1, date.lastIndexOf("/")).toInt()
        val year = date.substring(date.lastIndexOf("/") + 1, date.length).toInt()
        calendarView.currentDate = CalendarDay.today()
        calendarView.selectedDate = CalendarDay.from(year, month - 1, day)
        updateListView("$day/$month/$year")
    }

    private fun startService() {
        val intent = Intent(this, AppService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            ContextCompat.startForegroundService(this, intent)
        else
            startService(intent)
    }

    private fun checkServiceRunning(): Boolean {
        try {
            val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (services in manager.getRunningServices(Integer.MAX_VALUE)) {
                if (AppService::class.java.name == services.service.className) {
                    Log.d("service", "running")
                    Log.d("service_name", services.service.className.toString())
                    return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("service", "not running")
        return false
    }

    private fun loadAds() {
        ads = Ads(this)
        ads.apply {
            loadBottomAds(ads_bottom)
        }
    }

    private fun checkCompatible(): Boolean {
        val valueDb = sqLite.readCalendar()
        val studentCalendar = JSONObject(valueDb)
        val jsonArray = studentCalendar.getJSONArray("calendar")

        for (i in 0 until jsonArray.length()) {
            try {
                jsonArray.getJSONObject(i).getString("subjectId")
                return true
            } catch (e: Exception) {
                return false
            }
        }
        return false
    }

    private fun run() {
        //changeBackground()
        var readDb: Int
        var valueDb = ""

        try {
            valueDb = sqLite.readCalendar()
            readDb = 1
            Log.d("readdb", "readCalendar db done")
        } catch (e: Exception) {
            readDb = 0
            Log.d("readdb", "db is not exits, cannot readCalendar")
            Log.d("err", e.toString())
        }

        if (readDb == 0) {
            moveToLogin()
        } else {
            if (checkCompatible()) {
                scheduleJson = JSONObject(valueDb)
                parseScheduleJson = ParseScheduleJson(scheduleJson!!)

                initFloatButton()

                if (intent.getStringExtra("date") != null)
                    selectedDate(intent.getStringExtra("date"))
                else
                    toDay()

                drawBackgroundToday()
                dots = parseScheduleJson!!.initDots()
                setCalendarDots()
                swipe.setListener(OnSwipeListener())
                Log.d("service", checkServiceRunning().toString())
                if (!checkServiceRunning())
                    startService()
//        if (Build.VERSION.SDK_INT >= 21)
//            loadAds()
            } else {
                try {
                    sqLite.deleteCalendar()
                    sqLite.deleteInfo()
                    if (checkServiceRunning())
                        stopService(Intent(this, AppService::class.java))
                } catch (e: Exception) {
                    Log.d("Err", e.toString())
                }
                moveToLogin()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_week)
        init()
        calendarSetting()
        run()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_SIGN_IN -> {
                if (resultCode != Activity.RESULT_OK && data != null) {
                    //Toast.makeText(this@WeekActivity, "Oauth false", Toast.LENGTH_LONG).show()
                    sharedPref.edit().apply {
                        putBoolean("isSyncing", false)
                        apply()
                    }
                } else {
                    credential.selectedAccountName = GoogleSignIn.getClient(this@WeekActivity, gso).silentSignIn().result?.email
                    //Toast.makeText(this, GoogleSignIn.getClient(this@WeekActivity, gso).silentSignIn().result?.email, Toast.LENGTH_LONG).show()
                    sharedPref.edit().apply {
                        putString("accSelected", GoogleSignIn.getClient(this@WeekActivity, gso).silentSignIn().result?.email)
                                .apply()
                    }
                    if (sharedPref.getString("accSelected", "null") != "null")
                        appNotification.syncing()
                    sharedPref.edit().apply {
                        putBoolean("isSyncing", true)
                        apply()
                    }
                    val syncGoogle = SyncToGoogleCalendar(this)
                    syncGoogle.start()
                }
            }
        }
    }

}
