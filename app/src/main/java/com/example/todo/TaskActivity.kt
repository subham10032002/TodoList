package com.example.todo

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_task.*
import java.text.SimpleDateFormat
import java.util.*

const val DB_NAME = "todo.db"
class TaskActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var myCalendar : Calendar

    lateinit var dateSetListener:DatePickerDialog.OnDateSetListener
    val db by lazy{
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            DB_NAME
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

       dateEdit.setOnClickListener(this)


    }

    override fun onClick(v: View?) {
       when(v?.id){
           R.id.dateEdit -> {
               setListener()
           }
       }

    }

    private fun setListener() {
        myCalendar = Calendar.getInstance()

        dateSetListener = DatePickerDialog.OnDateSetListener{ _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            myCalendar.set(Calendar.YEAR,year)
            myCalendar.set(Calendar.MONTH,month)
            myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateDate()
        }

        val datePickerDialog = DatePickerDialog(
            this,dateSetListener,myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        )
//
        datePickerDialog.datePicker.minDate= System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun updateDate() {
      val myFormat = "EEE, d MMM yyyy"  // sat, 3 feb 2001
        val sdf = SimpleDateFormat(myFormat)
        dateEdit.setText(sdf.format(myCalendar.time))

        taskReminderTime.visibility = View.VISIBLE
    }
}