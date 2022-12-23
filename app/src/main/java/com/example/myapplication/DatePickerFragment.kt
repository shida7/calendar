package com.example.myapplication

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.fragment.app.DialogFragment
import java.text.SimpleDateFormat
import java.util.*
import kotlin.IllegalStateException

class DatePickerFragment  : DialogFragment(){
    @SuppressLint("SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val cal = Calendar.getInstance()
        val dateString = arguments?.getString("defaultDate")
        val formatter = SimpleDateFormat("yyyy/MM/dd")
        val defaultDate = formatter.parse(dateString.toString())
        if (defaultDate != null) {
            cal.time = defaultDate
        }
        Log.d("test", defaultDate!!.toString())
        val dialog = activity?.let{
            DatePickerDialog(
                it,
                { _, year, monthOfYear, dayOfMonth ->
                    //選択された日付をテキストボックスに反映
                    val txtDate = it.findViewById<Button>(R.id.dateTxt)
                    txtDate.text = "${year}/${monthOfYear + 1}/${dayOfMonth}"
                },
                cal[Calendar.YEAR],
                cal[Calendar.MONTH],
                cal[Calendar.DAY_OF_MONTH]
            )
        }
        return dialog ?: throw IllegalStateException("Activity is null.")
    }
}