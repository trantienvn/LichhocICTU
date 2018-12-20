package com.indieteam.mytask.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import com.indieteam.mytask.R
import com.indieteam.mytask.core.calendar.AddSubject
import kotlinx.android.synthetic.main.fragment_add_calendar.*

class AddCalendarFragment : Fragment() {

    private var subjectName = ""
    private var subjectPlace = ""
    private var subjectTeacher = ""
    private var subjectDate = ""
    private var subjectTime = ArrayList<Int>()


    private fun removeASubjectTime(subjectTime: Int){
        var pos = -1
        for (i in this.subjectTime){
            pos++
            if (i == subjectTime)
                break
        }
        this.subjectTime.removeAt(pos)
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

    private fun sorted(){
        for (i in 0 until subjectTime.size - 1){
            for (j in i+1 until subjectTime.size){
                if (subjectTime[i] > subjectTime[j]){
                    val temp = subjectTime[i]
                    subjectTime[i] = subjectTime[j]
                    subjectTime[j] = temp
                }
            }
        }
    }

    private fun isSubjectTimeContinuity(): Boolean{
        for (i in 0 until subjectTime.size - 1){
            if (subjectTime[i] +1 != subjectTime[i+1])
                return false
        }
        return true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val arg = arguments
        arg?.let {
            arg.getString("date")?.let {
                subjectDate = it
                new_subject_date.setText(it)

                add_calendar.setOnClickListener {
                    subjectName = new_subject_name.text.toString()
                    subjectPlace = new_subject_place.text.toString()
                    subjectTeacher = new_subject_teacher.text.toString()
                    if (subjectName.isNotBlank() && subjectPlace.isNotBlank() && subjectTeacher.isNotBlank()
                            && subjectTime.isNotEmpty()){
                        sorted()
                        if (isSubjectTimeContinuity()) {
                            var time = ""
                            subjectTime.forEach {
                                time += "$it,"
                            }
                            time = time.substring(0, time.length-1)
                            if (subjectDate.isNotBlank()) {
                                AddSubject(requireContext())
                                        .addCalendar(subjectName, subjectPlace, subjectTeacher, time, subjectDate)
                                val intent = Intent(requireActivity(), WeekActivity::class.java)
                                intent.putExtra("date", subjectDate)
                                startActivity(intent)
                                requireActivity().finish()
                            } else
                                Toast.makeText(requireContext(), "Ngày không xác định", Toast.LENGTH_LONG).show()
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
                        subjectTime.add(buttonView.text.toString().toInt())
                    else
                        removeASubjectTime(buttonView.text.toString().toInt())
                }
            }
        }
    }
}
