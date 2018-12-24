package com.indieteam.mytask.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.indieteam.mytask.R
import com.indieteam.mytask.collection.StudentCalendarStruct
import com.indieteam.mytask.ui.WeekActivity
import kotlinx.android.synthetic.main.item_calendar_list_view.view.*
import java.util.*

class CalendarListViewAdapter(val activity: WeekActivity, val data: ArrayList<StudentCalendarStruct>): BaseAdapter(){

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_calendar_list_view, null)

        if (data.isEmpty()){
            view.subject_name.text = "Nghỉ"
            view.subject_time_place.text = ""
            view.teacher.text = ""
            view.edit_image_button.visibility = GONE

        }else {
            view.subject_name.text = "${data[p0].subjectName}"
            view.subject_time_place.text = "${data[p0].subjectTime} tại ${data[p0].subjectPlace}"
            view.teacher.text = "${data[p0].teacher}"
            view.edit_image_button.visibility = VISIBLE
        }

        view.edit_image_button.setOnClickListener {
            if (activity.studentCalendarObjArr.isNotEmpty() && activity.parseCalendarJson!!.subjectId.isNotEmpty()) {
                val subjectId = activity.parseCalendarJson!!.subjectId[p0]
                activity.calendarDialog.show(subjectId)
            }
        }

        return view
    }

    override fun getItem(p0: Int): Any {
        return p0
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        if (data.isEmpty())
            return 1
        else
            return data.size
    }

}