package com.indieteam.mytask.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import com.indieteam.mytask.R
import com.indieteam.mytask.collection.TestScheduleCollection
import com.indieteam.mytask.ui.WeekActivity
import kotlinx.android.synthetic.main.fragment_test_schedule.*

class TestScheduleFragment : Fragment() {

    private val testScheduleCollection = ArrayList<TestScheduleCollection>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as WeekActivity).apply {
            runOnUiThread {
                this@TestScheduleFragment.testScheduleCollection.clear()
                this@TestScheduleFragment.testScheduleCollection.addAll(testScheduleCollection)
            }
        }

        for (i in testScheduleCollection) {
            Log.d("index_", i.index_)
            Log.d("subjectsId", i.subjectsId)
            Log.d("subjectsName", i.subjectsName)
            Log.d("subjectsLevel", i.subjectsLevel)
            Log.d("contestDate", i.contestDate)
            Log.d("contestTime", i.contestTime)
            Log.d("contestType", i.contestType)
            Log.d("studentContestId", i.studentContestId)
            Log.d("contestRoom", i.contestRoom)
            Log.d("contestNote", i.contestNote)
            Log.d("_______", "_________")

            val tableRow = TableRow(requireContext())

            val subjectName = i.subjectsName.trim()
            val dateTime = "${i.contestTime.trim()},layout ${i.contestDate.trim()}"
            val place = i.contestRoom.trim()
            val studentContestId = i.studentContestId.trim()

            val subjectView = TextView(requireContext())
            val dateTimeView = TextView(requireContext())
            val placeView = TextView(requireContext())
            val studentContestIdView = TextView(requireContext())

            subjectView.text = subjectName
            subjectView.setTextColor(requireContext().resources.getColor(R.color.colorWhite))
            subjectView.gravity = Gravity.CENTER
            subjectView.textSize = 12f
            subjectView.layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2f)

            dateTimeView.text = dateTime
            dateTimeView.setTextColor(requireContext().resources.getColor(R.color.colorWhite))
            dateTimeView.gravity = Gravity.CENTER
            dateTimeView.textSize = 12f
            dateTimeView.layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.5f)

            placeView.text = place
            placeView.setTextColor(requireContext().resources.getColor(R.color.colorWhite))
            placeView.gravity = Gravity.CENTER
            placeView.textSize = 12f
            placeView.layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)

            studentContestIdView.text = studentContestId
            studentContestIdView.setTextColor(requireContext().resources.getColor(R.color.colorWhite))
            studentContestIdView.gravity = Gravity.CENTER
            studentContestIdView.textSize = 12f
            studentContestIdView.layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f)

            tableRow.addView(subjectView)
            tableRow.addView(dateTimeView)
            tableRow.addView(placeView)
            tableRow.addView(studentContestIdView)

            table_layout.addView(tableRow)
        }
    }
}
