package com.indieteam.mytask.ui.interface_

import com.indieteam.mytask.collection.TestScheduleCollection
import com.indieteam.mytask.collection.TestScheduleTypeCollection

interface OnDomTestListScheduleListener {
    fun onDone(testScheduleCollection: ArrayList<TestScheduleCollection>, testScheduleTypeCollection: ArrayList<TestScheduleTypeCollection>)
    fun onFail(t: String)
}