package com.indieteam.mytask.model.address

class UrlAddress{

    companion object {
        val urlLoginClean = "http://dangkytinchi.ictu.edu.vn/kcntt/login.aspx"
        fun urlLoginSession(sessionUrl: String) = "http://dangkytinchi.ictu.edu.vn/kcntt/(S(${sessionUrl}))/login.aspx"
        fun urlDownloadExel(sessionUrl: String) = "http://dangkytinchi.ictu.edu.vn/kcntt/(S($sessionUrl))/Reports/Form/StudentTimeTable.aspx"
        fun urlSemester(sessionUrl: String) = "http://dangkytinchi.ictu.edu.vn/kcntt/(S($sessionUrl))/Reports/Form/StudentTimeTable.aspx"
        val urlTestSchedule = "http://dangkytinchi.ictu.edu.vn/kcntt/(S(jnrmtk2yvgq5lgcos3owpp5w))/StudentViewExamList.aspx"
    }
}