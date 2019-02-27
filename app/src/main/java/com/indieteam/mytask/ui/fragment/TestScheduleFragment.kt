package com.indieteam.mytask.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.indieteam.mytask.R
import com.indieteam.mytask.ui.WeekActivity

class TestScheduleFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as WeekActivity).apply {
            runOnUiThread {
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
                }
            }
        }
    }
}
