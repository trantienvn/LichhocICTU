package com.indieteam.mytask.views.interfaces

import com.indieteam.mytask.models.TestScheduleSemesterCollection
import com.indieteam.mytask.models.TestScheduleTypeCollection

interface OnDomTestListScheduleListener {
    fun onDone(testScheduleSemesterCollection: ArrayList<TestScheduleSemesterCollection>, testScheduleTypeCollection: ArrayList<TestScheduleTypeCollection>)
    fun onFail(t: String)
}