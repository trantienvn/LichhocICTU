package com.indieteam.mytask.core.schedule.domHTML

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.indieteam.mytask.R
import com.indieteam.mytask.address.UrlAddress
import com.indieteam.mytask.ui.LoginActivity
import com.indieteam.mytask.ui.SelectSemesterFragment
import com.indieteam.mytask.ui.WeekActivity
import kotlinx.android.synthetic.main.fragment_process_bar.*
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Connection
import org.jsoup.Jsoup

@Suppress("DEPRECATION")
class DomSemesterSchedule(val context: Context, private val sessionUrl: String, private val signIn: String): Thread() {

    private var jsonArray = JSONArray()
    private var drpSemesterObject = JSONObject()
    private var err = 0
    private var classContextName = ""
    private var selectSemesterFragment = SelectSemesterFragment()

    init {
        classContextName = context.javaClass.name.substring(context.javaClass.name.lastIndexOf(".") + 1, context.javaClass.name.length)
    }

    @SuppressLint("SetTextI18n")
    override fun run() {
        try {
            if (classContextName == "LoginActivity") {
                (context as LoginActivity).supportFragmentManager.findFragmentByTag("processBarLogin")?.let {
                    context.runOnUiThread {
                        context.process.text = "Tải học kỳ..."
                    }
                }
            }
            // start get post params
            if(sessionUrl.isNotBlank()) {
                val resFirst = Jsoup.connect(UrlAddress.urlDownloadExel(sessionUrl))
                        .cookie("SignIn", signIn)
                        .method(Connection.Method.GET)
                        .execute()

                val resFirstParse = resFirst.parse()
                for (i in resFirstParse.select("select")) {
                    //Hoc ky
                    if (i.attr("name") == "drpSemester") {
                        for (j in i.select("option")) {
                            //Log.d("drpSemester", j.attr("value"))
                            //Log.d("drpSemester name", j.text())
                            val jsonObjectChild = JSONObject()
                            jsonObjectChild.put(j.text(), j.attr("value"))
                            jsonArray.put(jsonObjectChild)
                        }
                    }
                }
                drpSemesterObject.put("semester", jsonArray)
            }
        }catch (e: Exception){
            appException("Mất kết nối")
            err = 1
        }
        if (err == 0){
           done()
        }

        this.join()
    }

    private fun done(){
        val bundle = Bundle()
        bundle.putString("semester", drpSemesterObject.toString())
        bundle.putString("sessionUrl", sessionUrl)
        bundle.putString("signIn", signIn)
        selectSemesterFragment.arguments = bundle
        if (classContextName == "LoginActivity") {
            (context as LoginActivity).supportFragmentManager.findFragmentByTag("processBarLogin")?.let{
                context.supportFragmentManager.beginTransaction().remove(it)
                        .commit()
            }
            context.supportFragmentManager.beginTransaction().add(R.id.login_root_view, selectSemesterFragment, "selectSemesterFragment")
                    .commit()
        }
        if (classContextName == "WeekActivity") {
            (context as WeekActivity).supportFragmentManager.findFragmentByTag("processBarUpdate")?.let{
                context.supportFragmentManager.beginTransaction().remove(it)
                        .commit()
            }
            context.supportFragmentManager.beginTransaction().add(R.id.calendar_root_view, selectSemesterFragment, "selectSemesterFragment")
                    .commit()
        }

    }

    private fun appException(errorName: String){
        if (classContextName == "LoginActivity") {
            (context as LoginActivity).runOnUiThread {
                context.supportFragmentManager.findFragmentByTag("processBarLogin")?.let{
                    context.supportFragmentManager.beginTransaction().remove(it)
                            .commit()
                }
                context.visible()
                context.clickLogin = 0
                Toast.makeText(context, errorName, Toast.LENGTH_SHORT).show()
            }
        }
        if (classContextName == "WeekActivity") {
            (context as WeekActivity).runOnUiThread {
                context.supportFragmentManager.findFragmentByTag("processBarUpdate")?.let{
                    context.supportFragmentManager.beginTransaction().remove(it)
                            .commit()
                }
                context.visible()
                Toast.makeText(context, errorName, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
