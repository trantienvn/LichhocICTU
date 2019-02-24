package com.indieteam.mytask.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import com.indieteam.mytask.R
import com.indieteam.mytask.model.schedule.AddSubject
import com.indieteam.mytask.model.schedule.DeleteSubject
import com.indieteam.mytask.model.SqLite
import com.indieteam.mytask.ui.WeekActivity
import kotlinx.android.synthetic.main.fragment_update_schedule.*
import org.json.JSONObject

class UpdateScheduleFragment : Fragment() {

    private var subjectName = ""
    private var subjectPlace = ""
    private var subjectTeacher = ""
    private var subjectDate = ""
    private var subjectTimeArray = ArrayList<Int>()


    private fun removeASubjectTime(subjectTime: Int) {
        var pos = -1
        for (i in this.subjectTimeArray) {
            pos++
            if (i == subjectTime)
                break
        }
        this.subjectTimeArray.removeAt(pos)
    }

    private fun getResId(resName: String, c: Class<*>): Int {
        try {
            val idField = c.getDeclaredField(resName)
            return idField.getInt(idField)
        } catch (e: Exception) {
            e.printStackTrace()
            return -1
        }
    }

    private fun sorted() {
        for (i in 0 until subjectTimeArray.size - 1) {
            for (j in i + 1 until subjectTimeArray.size) {
                if (subjectTimeArray[i] > subjectTimeArray[j]) {
                    val temp = subjectTimeArray[i]
                    subjectTimeArray[i] = subjectTimeArray[j]
                    subjectTimeArray[j] = temp
                }
            }
        }
    }

    private fun isSubjectTimeContinuity(): Boolean {
        for (i in 0 until subjectTimeArray.size - 1) {
            if (subjectTimeArray[i] + 1 != subjectTimeArray[i + 1])
                return false
        }
        return true
    }

    private fun autoCheckBox(time: String) {
        for (i in 1..14) {
            val idName = "t_$i"
            val checkBoxId = getResId(idName, R.id::class.java)

            if (checkBoxId != -1) {
                val checkBoxView = requireActivity().findViewById<CheckBox>(checkBoxId)
                if (checkBoxView.text == time)
                    checkBoxView.isChecked = true
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val arg = arguments
        val sqLite = SqLite(requireContext())
        val studentCalendar = JSONObject(sqLite.readCalendar())
        val jsonArray = studentCalendar.getJSONArray("calendar")

        arg?.let {
            arg.getString("subjectId")?.let {
                val subjectId = it
                for (i in 0 until jsonArray.length()) {
                    val subject = jsonArray.getJSONObject(i)
                    if (subject.getString("subjectId") == subjectId) {
                        new_subject_name.setText(subject.getString("subjectName"))
                        new_subject_place.setText(subject.getString("subjectPlace"))
                        new_subject_teacher.setText(subject.getString("teacher"))
                        new_subject_date.setText(subject.getString("subjectDate"))

                        val subjectTime = subject.getString("subjectTime")

                        if (subjectTime.indexOf(",") > -1) {
                            var time = ""

                            for (char in subjectTime) {
                                if (char.toString() != ",") {
                                    time += char.toString()
                                } else {
                                    subjectTimeArray.add(time.toInt())
                                    autoCheckBox(time)
                                    time = ""
                                }
                            }

                            time = subjectTime.substring(subjectTime.lastIndexOf(",") + 1, subjectTime.length)
                            subjectTimeArray.add(time.toInt())
                            autoCheckBox(time)
                        } else {
                            subjectTimeArray.add(subjectTime.toInt())
                            autoCheckBox(subjectTime)
                        }

                    }
                }
                update_calendar.setOnClickListener {
                    subjectName = new_subject_name.text.toString()
                    subjectPlace = new_subject_place.text.toString()
                    subjectTeacher = new_subject_teacher.text.toString()
                    subjectDate = new_subject_date.text.toString()
                    if (subjectName.isNotBlank() && subjectPlace.isNotBlank() && subjectTeacher.isNotBlank()
                            && subjectTimeArray.isNotEmpty() && subjectDate.isNotBlank()) {
                        sorted()
                        if (isSubjectTimeContinuity()) {
                            saving.visibility = VISIBLE
                            var time = ""
                            subjectTimeArray.forEach {
                                time += "$it,"
                            }
                            time = time.substring(0, time.length - 1)
                            DeleteSubject(requireContext())
                                    .delete(subjectId)
                            AddSubject(requireContext())
                                    .add(subjectName, subjectPlace, subjectTeacher, time, subjectDate)

                            val intent = Intent(requireActivity(), WeekActivity::class.java)
                            intent.putExtra("date", subjectDate)
                            startActivity(intent)
                            requireActivity().finish()
                        } else {
                            Toast.makeText(requireContext(), "Tiết học phải được đánh dấu liền mạch", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Hãy nhập đủ thông tin", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        for (i in 1..14) {
            val idName = "t_$i"
            val checkBoxId = getResId(idName, R.id::class.java)

            if (checkBoxId != -1) {
                val checkBoxView = requireActivity().findViewById<CheckBox>(checkBoxId)
                checkBoxView.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked)
                        subjectTimeArray.add(buttonView.text.toString().toInt())
                    else
                        removeASubjectTime(buttonView.text.toString().toInt())
                }
            }
        }
    }
}
