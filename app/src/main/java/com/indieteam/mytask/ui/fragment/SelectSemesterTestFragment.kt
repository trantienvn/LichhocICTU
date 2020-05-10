package com.indieteam.mytask.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment

import com.indieteam.mytask.R
import com.indieteam.mytask.collection.TestScheduleSemesterCollection
import com.indieteam.mytask.collection.TestScheduleTypeCollection
import com.indieteam.mytask.model.schedule.domHTML.DomTestListSchedule
import com.indieteam.mytask.model.schedule.domHTML.DomTestSchedule
import com.indieteam.mytask.ui.WeekActivity
import com.indieteam.mytask.ui.interface_.OnDomTestListScheduleListener
import kotlinx.android.synthetic.main.fragment_select_test_schedule.*
import kotlinx.android.synthetic.main.item_semester.view.*
import kotlinx.android.synthetic.main.item_type.view.*

class SelectSemesterTestFragment : Fragment() {

    private lateinit var testScheduleCollection: ArrayList<TestScheduleSemesterCollection>
    private lateinit var testScheduleTypeCollection: ArrayList<TestScheduleTypeCollection>
    private val adapter = Adapter()
    private val adapter2 = Adapter2()
    private var pos = -1
    private var pos2 = -1

    private inner class Adapter : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.item_semester, null)
            view.semester_name.text = testScheduleCollection[position].year

            return view
        }

        override fun getItem(position: Int): Any {
            return position
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return testScheduleCollection.size
        }

    }

    private inner class Adapter2 : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.item_type, null)
            view.type_name.text = testScheduleTypeCollection[position].typeName

            return view
        }

        override fun getItem(position: Int): Any {
            return position
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return testScheduleTypeCollection.size
        }

    }

    private val onDomTestListSchedule = object : OnDomTestListScheduleListener {
        override fun onDone(testScheduleSemesterCollection: ArrayList<TestScheduleSemesterCollection>, testScheduleTypeCollection: ArrayList<TestScheduleTypeCollection>) {
            this@SelectSemesterTestFragment.testScheduleCollection = testScheduleSemesterCollection
            this@SelectSemesterTestFragment.testScheduleTypeCollection = testScheduleTypeCollection
            requireActivity().runOnUiThread {
                list_semester.adapter = adapter
                list_type.adapter = adapter2

                if (requireActivity().supportFragmentManager.findFragmentByTag("processBarUpdate") != null)
                    requireActivity().supportFragmentManager.beginTransaction().remove(requireActivity().supportFragmentManager.findFragmentByTag("processBarUpdate")!!)
                            .commit()
            }

        }

        override fun onFail(t: String) {
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_test_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DomTestListSchedule(requireContext(), onDomTestListSchedule).start()

        list_semester.setOnItemClickListener { parent, view, position, id ->
            for (i in 0 until parent.childCount) {
                val itemView = parent.getChildAt(i) as View
                itemView.setBackgroundResource(0)
            }
            view.background = resources.getDrawable(R.color.colorPurpleDark)
            pos = position
        }

        list_type.setOnItemClickListener { parent, view, position, id ->
            for (i in 0 until parent.childCount) {
                val itemView = parent.getChildAt(i) as View
                itemView.setBackgroundResource(0)
            }
            view.background = resources.getDrawable(R.color.colorPurpleDark)
            pos2 = position
        }

        submit_test_schedule.setOnClickListener {
            if (pos > -1 && pos2 > -1) {
                val semesterValue = testScheduleCollection[pos].value
                val typeValue = testScheduleTypeCollection[pos2].value
                //Toast.makeText(requireContext(), "$semesterValue $typeValue", Toast.LENGTH_SHORT).show()
                DomTestSchedule(requireContext(), (requireActivity() as WeekActivity).onDomTestSchedule, semesterValue, typeValue).start()
            } else
                Toast.makeText(requireContext(), "Hãy chọn cả 2", Toast.LENGTH_SHORT).show()
        }
    }
}
