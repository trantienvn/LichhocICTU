package com.indieteam.mytask.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.indieteam.mytask.R
import com.indieteam.mytask.modeldata.CalendarFinal
import com.indieteam.mytask.ui.MainActivity
import kotlinx.android.synthetic.main.item_calendar_list_view.view.*
import java.util.*

class CalendarListViewAdapter(val activity: MainActivity, val data: ArrayList<CalendarFinal>): BaseAdapter(){
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.item_calendar_list_view, null)

        if (data.isEmpty()){
            layout.subject_date.text = "Nghỉ"
            layout.subject_name.text = ""
            layout.subject_time.text = ""
            layout.subject_place.text = ""

        }else {
            layout.subject_date.text = "Ngày: ${data[p0].subjectDate}"
            layout.subject_name.text = "Môn: ${data[p0].subjectName}"
            layout.subject_time.text = "Tiết: ${data[p0].subjectTime}"
            layout.subject_place.text = "Địa điểm" +
                    "" +
                    ": ${data[p0].subjectPlace}"
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