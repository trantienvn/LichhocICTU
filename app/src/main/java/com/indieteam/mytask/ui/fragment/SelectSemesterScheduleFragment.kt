package com.indieteam.mytask.ui.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import com.indieteam.mytask.R
import com.indieteam.mytask.model.schedule.domHTML.DomDownloadExcel
import com.indieteam.mytask.ui.LoginActivity
import com.indieteam.mytask.ui.WeekActivity
import com.indieteam.mytask.ui.interface_.OnDownloadExcelListener
import kotlinx.android.synthetic.main.fragment_process_bar.*
import kotlinx.android.synthetic.main.fragment_select_semester.*
import kotlinx.android.synthetic.main.item_semester.view.*
import org.json.JSONArray
import org.json.JSONObject

class SelectSemesterScheduleFragment : Fragment() {

    private var semesterObject = JSONObject()
    private var semesterArray = JSONArray()
    private var semesterData: String? = null
    private var sessionUrl: String? = null
    private var signIn: String? = null
    private var drpSemesterSelected = ""
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var classContextName: String

    inner class Adapter : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val layoutInflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = layoutInflater.inflate(R.layout.item_semester, null)
            val key = semesterArray.getJSONObject(position).keys().next()
            val value = semesterArray.getJSONObject(position).get(key).toString()
            view.semester_name.text = key
            if (value == sharedPreferences.getString("semesterSelected", ""))
                view.background = resources.getDrawable(R.color.colorPurpleDark)
            Log.d("value", key)
            return view
        }

        override fun getItem(position: Int): Any {
            return position
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return semesterArray.length()
        }

    }

    private val onDownloadExcelListener = object : OnDownloadExcelListener {
        override fun onDownload(context: Context) {
            if (classContextName == "LoginActivity") {
                (context as LoginActivity).apply {
                    runOnUiThread {
                        supportFragmentManager.beginTransaction().add(R.id.login_root_view, ProcessBarFragment(), "processBarLogin")
                                .commit()
                        supportFragmentManager.executePendingTransactions()
                        supportFragmentManager.findFragmentByTag("processBarLogin")?.let {
                            it.process.text = "Lưu Excel..."
                        }
                    }
                }
            }

            if (classContextName == "WeekActivity") {
                (context as WeekActivity).apply {
                    runOnUiThread {
                        supportFragmentManager.beginTransaction().add(R.id.calendar_root_view, ProcessBarFragment(), "processBarUpdate")
                                .commit()
                        supportFragmentManager.executePendingTransactions()
                        supportFragmentManager.findFragmentByTag("processBarUpdate")?.let {
                            it.process.text = "Lưu Excel mới..."
                        }
                    }
                }
            }
        }

        override fun onSuccess(context: Context) {
            if (classContextName == "LoginActivity") {
                (context as LoginActivity).apply {
                    supportFragmentManager.findFragmentByTag("processBarFragment")?.let {
                        runOnUiThread {
                            process.text = "Lưu Excel...Ok"
                        }
                    }
                    val intent = Intent(this, WeekActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            if (classContextName == "WeekActivity") {
                (context as WeekActivity).apply {
                    val intent = Intent(this, WeekActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }

        override fun onFail(context: Context) {
        }

        override fun onThrow(t: String, context: Context) {
            if (classContextName == "LoginActivity") {
                (context as LoginActivity).runOnUiThread {
                    context.supportFragmentManager.findFragmentByTag("processBarLogin")?.let {
                        context.supportFragmentManager.beginTransaction().remove(it)
                                .commit()
                    }
                    context.visible()
                    context.clickLogin = 0
                    Toast.makeText(context, t, Toast.LENGTH_SHORT).show()
                }
            }
            if (classContextName == "WeekActivity") {
                (context as WeekActivity).runOnUiThread {
                    context.supportFragmentManager.findFragmentByTag("processBarUpdate")?.let {
                        context.supportFragmentManager.beginTransaction().remove(it)
                                .commit()
                    }
                    context.visible()
                    Toast.makeText(context, t, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_semester, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        classContextName = requireContext().javaClass.name.substring(requireContext().javaClass.name.lastIndexOf(".") + 1, requireContext().javaClass.name.length)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        semesterData = arguments?.getString("semester")
        sessionUrl = arguments?.getString("sessionUrl")
        signIn = arguments?.getString("signIn")
        semesterData?.let {
            Log.d("Json", it)
            semesterObject = JSONObject(it)
            semesterArray = semesterObject.getJSONArray("semester")
            list_semester.adapter = Adapter()

            list_semester.setOnItemClickListener { parent, view, position, id ->
                val key = semesterArray.getJSONObject(position).keys().next()
                val value = semesterArray.getJSONObject(position).get(key).toString()
                drpSemesterSelected = value
                sharedPreferences.edit().apply {
                    putString("semesterSelected", value)
                            .apply()
                }
                //Toast.makeText(requireContext(), drpSemesterSelected, Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
                if (sessionUrl != null && signIn != null)
                    DomDownloadExcel(requireContext(), sessionUrl!!, signIn!!, drpSemesterSelected, onDownloadExcelListener).start()
                else
                    Toast.makeText(requireContext(), "Lost Data", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
