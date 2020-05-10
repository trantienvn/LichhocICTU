package com.indieteam.mytask.views.interfaces

interface OnSemesterScheduleListener {
    fun onSuccess(semester: String, sessionUrl: String, signIn: String)
    fun onSemesterSchedule()
    fun onThrow(t: String)
}