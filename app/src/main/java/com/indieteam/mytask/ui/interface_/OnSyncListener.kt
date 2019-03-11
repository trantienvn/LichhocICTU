package com.indieteam.mytask.ui.interface_

interface OnSyncListener {
    fun onDone(m: String)
    fun onState(s: String)
    fun onFail(t: String, m: String)
}