package com.indieteam.mytask.ui.interface_

import com.indieteam.mytask.collection.TestScheduleCollection

interface OnDomTestScheduleListener {
    fun onDone(testScheduleCollection: ArrayList<TestScheduleCollection>)
    fun onFail(t: String    )
    fun onThrow(t: String)
}