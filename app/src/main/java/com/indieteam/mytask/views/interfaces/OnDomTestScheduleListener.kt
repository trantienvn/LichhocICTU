package com.indieteam.mytask.views.interfaces

import com.indieteam.mytask.models.TestScheduleCollection

interface OnDomTestScheduleListener {
    fun onDone(testScheduleCollection: ArrayList<TestScheduleCollection>)
    fun onFail(t: String    )
    fun onThrow(t: String)
}