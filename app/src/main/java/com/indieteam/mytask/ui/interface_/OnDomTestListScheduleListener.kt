package com.indieteam.mytask.ui.interface_

import com.indieteam.mytask.collection.TestScheduleSemesterCollection
import com.indieteam.mytask.collection.TestScheduleTypeCollection

interface OnDomTestListScheduleListener {
    fun onDone(testScheduleSemesterCollection: ArrayList<TestScheduleSemesterCollection>, testScheduleTypeCollection: ArrayList<TestScheduleTypeCollection>)
    fun onFail(t: String)
}