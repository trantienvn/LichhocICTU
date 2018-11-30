package com.indieteam.mytask.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.indieteam.mytask.R
import com.indieteam.mytask.dataStruct.StudentCalendarStruct
import com.indieteam.mytask.ui.WeekActivity
import kotlinx.android.synthetic.main.item_calendar_list_view.view.*
import java.util.*

class CalendarListViewAdapter(val activity: WeekActivity, val data: ArrayList<StudentCalendarStruct>): BaseAdapter(){

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.item_calendar_list_view, null)

        if (data.isEmpty()){
            //layout.subject_date.text = "Nghỉ"
            layout.subject_name.text = "Nghỉ"
            layout.subject_time_place.text = ""
            layout.teacher.text = ""

        }else {
            layout.subject_name.text = "${data[p0].subjectName}"
            layout.subject_time_place.text = "${data[p0].subjectTime} tại ${data[p0].subjectPlace}"
            layout.teacher.text = "${data[p0].teacher}"
        }

        return layout
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