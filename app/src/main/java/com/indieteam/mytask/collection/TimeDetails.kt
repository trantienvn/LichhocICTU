package com.indieteam.mytask.collection


class TimeDetails{

    inner class TimeObject(val timeIndex: String, val timeIn: String, val timeOut: String)

    val timeSummerArr = ArrayList<TimeObject>()
    val timeWinterArr = ArrayList<TimeObject>()

    init {
        timeSummerArr.add(TimeObject("1", "06:30", "07:20"))
        timeSummerArr.add(TimeObject("2", "07:25", "08:15"))
        timeSummerArr.add(TimeObject("3", "08:25", "09:15"))
        timeSummerArr.add(TimeObject("4", "09:25", "10:15"))
        timeSummerArr.add(TimeObject("5", "10:20", "11:10"))
        timeSummerArr.add(TimeObject("6", "13:00", "13:50"))
        timeSummerArr.add(TimeObject("7", "13:55", "14:45"))
        timeSummerArr.add(TimeObject("8", "14:55", "15:45"))
        timeSummerArr.add(TimeObject("9", "15:55", "16:45"))
        timeSummerArr.add(TimeObject("10", "16:50", "17:40"))
        timeSummerArr.add(TimeObject("11", "18:15", "19:05"))
        timeSummerArr.add(TimeObject("12", "19:10", "20:00"))
        timeSummerArr.add(TimeObject("13", "20:10", "21:00"))
        timeSummerArr.add(TimeObject("14", "21:10", "22:00"))
        timeSummerArr.add(TimeObject("15", "22:10", "23:00"))

        timeWinterArr.add(TimeObject("1", "06:45", "07:35"))
        timeWinterArr.add(TimeObject("2", "07:40", "08:30"))
        timeWinterArr.add(TimeObject("3", "08:40", "09:30"))
        timeWinterArr.add(TimeObject("4", "09:40", "10:30"))
        timeWinterArr.add(TimeObject("5", "10:35", "11:25"))
        timeWinterArr.add(TimeObject("6", "13:00", "13:50"))
        timeWinterArr.add(TimeObject("7", "13:55", "14:45"))
        timeWinterArr.add(TimeObject("8", "14:55", "15:45"))
        timeWinterArr.add(TimeObject("9", "15:55", "16:45"))
        timeWinterArr.add(TimeObject("10", "16:50", "17:40"))
        timeWinterArr.add(TimeObject("11", "18:15", "19:05"))
        timeWinterArr.add(TimeObject("12", "19:10", "20:00"))
        timeWinterArr.add(TimeObject("13", "20:10", "21:00"))
        timeWinterArr.add(TimeObject("14", "21:10", "22:00"))
        timeWinterArr.add(TimeObject("15", "22:10", "23:00"))
    }

}