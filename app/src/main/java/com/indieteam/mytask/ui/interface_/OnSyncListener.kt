package com.indieteam.mytask.ui.interface_

interface OnSyncListener {
    fun onDone()
    fun onFail(t: String, m: String)
}