package com.indieteam.mytask.views.interfaces

interface OnSyncListener {
    fun onDone(m: String)
    fun onState(s: String)
    fun onFail(t: String, m: String)
}