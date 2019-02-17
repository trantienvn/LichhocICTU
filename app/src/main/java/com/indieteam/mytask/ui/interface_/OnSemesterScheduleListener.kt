package com.indieteam.mytask.ui.interface_

interface OnSemesterScheduleListener {
    fun onSuccess(semester: String, sessionUrl: String, signIn: String)
    fun onSemesterSchedule()
    fun onThrow(t: String)
}