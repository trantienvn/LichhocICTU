package com.indieteam.mytask.ui.interface_

import android.content.Context

interface OnDownloadExcelListener {
    fun onDownload(context: Context)
    fun onSuccess(context: Context)
    fun onFail(context: Context)
    fun onThrow(t: String, context: Context)
}