package com.indieteam.mytask.views.interfaces

interface OnLoginListener {
    fun onLogin()
    fun onSuccess(username: String, password: String, cookie: String, sessionUrl: String)
    fun onFail()
    fun onThrow(t: String)
}