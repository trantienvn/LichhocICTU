package com.indieteam.mytask.models

class UrlAddress{

    companion object {
        val loginClean = "http://dangkytinchi.ictu.edu.vn/kcntt/login.aspx"
        fun loginSession(sessionUrl: String) = "http://dangkytinchi.ictu.edu.vn/kcntt/(S(${sessionUrl}))/login.aspx"
        fun downloadExel(sessionUrl: String) = "http://dangkytinchi.ictu.edu.vn/kcntt/(S($sessionUrl))/Reports/Form/StudentTimeTable.aspx"
        fun semester(sessionUrl: String) = "http://dangkytinchi.ictu.edu.vn/kcntt/(S($sessionUrl))/Reports/Form/StudentTimeTable.aspx"
        fun testSchedule(sessionUrl: String) = "http://dangkytinchi.ictu.edu.vn/kcntt/(S($sessionUrl))/StudentViewExamList.aspx"
    }
}