package com.indieteam.mytask.ui

import android.content.Context
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
import com.indieteam.mytask.core.calendar.domHTML.DomDownloadExcel
import kotlinx.android.synthetic.main.fragment_select_semester.*
import kotlinx.android.synthetic.main.item_semester.view.*
import org.json.JSONArray
import org.json.JSONObject

class SelectSemesterFragment : Fragment() {

    private var semesterObject = JSONObject()
    private var semesterArray = JSONArray()
    private var semesterData: String? = null
    private var sessionUrl: String? = null
    private var signIn: String? = null
    private var drpSemesterSelected = ""
    private lateinit var sharedPreferences: SharedPreferences

    inner class Adapter: BaseAdapter(){
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val layoutInflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = layoutInflater.inflate(R.layout.item_semester, null)
            val key = semesterArray.getJSONObject(position).keys().next()
            val value = semesterArray.getJSONObject(position).get(key).toString()
            view.semester_name.text = key
            if (value == sharedPreferences.getString("semesterSelected", ""))
                view.background = resources.getDrawable(R.color.colorGray)
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_semester, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                    DomDownloadExcel(requireContext(), sessionUrl!!, signIn!!, drpSemesterSelected).start()
                else
                    Toast.makeText(requireContext(), "Lost Data", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
