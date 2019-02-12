package com.indieteam.mytask.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View.GONE
import com.indieteam.mytask.R
import com.indieteam.mytask.core.schedule.DeleteSubject
import kotlinx.android.synthetic.main.dialog_modify.view.*

class ModifyDialog(private val context: Context){

    private val alertDialog = AlertDialog.Builder(context)
    private lateinit var updateScheduleFragment: UpdateScheduleFragment

    fun show(subjectId: String){
        updateScheduleFragment = UpdateScheduleFragment()
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.dialog_modify, null)
        context as WeekActivity
        alertDialog.setView(view)
        val created = alertDialog.create()
        created.show()

        view.item_edit.setOnClickListener {
            context.runOnUiThread {
                view.item_edit.background = context.resources.getDrawable(R.color.colorGrayDark)
            }
            created.dismiss()
            context.gone()
            val bundle = Bundle()
            bundle.putString("subjectId", subjectId)
            updateScheduleFragment.arguments = bundle
            context.supportFragmentManager.beginTransaction().add(R.id.calendar_root_view, updateScheduleFragment, "updateScheduleFragment")
                    .commit()
        }

        view.item_delete.setOnClickListener {
            view.item_delete.text = "Đang xoá..."
            view.item_edit.visibility = GONE
            val deleteSubject = DeleteSubject(context)
            deleteSubject.delete(subjectId)
            val intent = Intent(context, WeekActivity::class.java)
            intent.putExtra("date", deleteSubject.dateDeleted)
            context.startActivity(intent)
            context.finish()
        }
    }
}