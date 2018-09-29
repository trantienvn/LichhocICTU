package com.indieteam.mytask.address

class UrlAddress{

    val urlLoginClean = "http://dangkytinchi.ictu.edu.vn/kcntt/login.aspx"
    fun urlLoginSession(sessionUrl: String) = "http://dangkytinchi.ictu.edu.vn/kcntt/(S(${sessionUrl}))/login.aspx"
    fun urlDownloadExel(sessionUrl: String) = "http://dangkytinchi.ictu.edu.vn/kcntt/(S($sessionUrl))/Reports/Form/StudentTimeTable.aspx"

}