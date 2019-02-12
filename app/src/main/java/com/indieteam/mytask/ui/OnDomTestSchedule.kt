package com.indieteam.mytask.ui

import com.indieteam.mytask.collection.TestScheduleCollection
import com.indieteam.mytask.collection.TestScheduleTypeCollection

interface OnDomTestSchedule {
    fun onDone(testScheduleCollection: ArrayList<TestScheduleCollection>, testScheduleTypeCollection: ArrayList<TestScheduleTypeCollection>)
    fun onFail(t: String)
}