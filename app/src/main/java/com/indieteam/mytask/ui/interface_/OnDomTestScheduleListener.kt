package com.indieteam.mytask.ui.interface_

interface OnDomTestScheduleListener {
    fun onDone()
    fun onFail(t: String    )
    fun onThrow(t: String)
}