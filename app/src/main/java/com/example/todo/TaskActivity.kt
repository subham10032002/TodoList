package com.example.todo

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

const val DB_NAME = "todo.db"
class TaskActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var myCalendar : Calendar

    lateinit var dateSetListener:DatePickerDialog.OnDateSetListener
    lateinit var timeSetListener: TimePickerDialog.OnTimeSetListener

    var fDate = 0L
    var fTime = 0L
    private val labels = arrayListOf("Personal","Business","Insurance","Banking","Shopping")
    private val labels2 = arrayListOf("10 min","15 min","30 min","60 min","90 min")
    val db by lazy{
        AppDatabase.getDatabase(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

       dateEdit.setOnClickListener(this)
        timeEdit.setOnClickListener(this)
        saveBtn.setOnClickListener(this)
        imgAddCategory.setOnClickListener(this)
        imgAddCategory1.setOnClickListener(this)

      setUpSpinner()
        setUpSpinner2()

    }


    private fun setUpSpinner() {
        val adapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,labels)
        labels.sort()

        spinnerCategory.adapter = adapter
    }
    private fun setUpSpinner2() {
        val adapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,labels2)
        labels2.sort()

        spinnerCategory1.adapter = adapter
    }


    override fun onClick(v: View?) {
       when(v?.id){
           R.id.dateEdit -> {
               setListener()
           }
           R.id.timeEdit -> {
               setTimeListener()
           }
           R.id.saveBtn -> {
               saveTask()
           }
           R.id.imgAddCategory -> {
               addCategory("Enter new category!" , labels)
           }
           R.id.imgAddCategory1 -> {
               addCategory("Set your own time!" , labels2)
           }
       }

    }

    private fun addCategory(xyz : String , listt : ArrayList<String>) {

            val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.edit_text_layout,null)
        val editText = dialogLayout.findViewById<EditText>(R.id.editTextCategory)
        with(builder){
            setTitle(xyz)
            setPositiveButton("Ok"){
                dialog, which ->
                listt.add(editText.text.toString())

            }
            setNegativeButton("Cancel"){
                dialog, which ->
                Log.d("Main","Negative Button clicked")

            }
            setView(dialogLayout)
            show()
        }


    }

    private fun saveTask() {
       val category = spinnerCategory.selectedItem.toString()
        val category1 = spinnerCategory1.selectedItem.toString()
        val title = taskTitle.editText?.text.toString()
        val description = taskDesciption.editText?.text.toString()

        GlobalScope.launch(Dispatchers.Main){
            val id = withContext(Dispatchers.IO){
                return@withContext db.todoDao().insertTask(
                    TodoModel(
                        title,
                        description,
                        category,
                        category1,
                        fDate,
                        fTime
                    )
                )
            }
            finish()
        }
    }

    private fun setTimeListener() {
        myCalendar = Calendar.getInstance()

        timeSetListener = TimePickerDialog.OnTimeSetListener{ _: TimePicker, hourOfDay: Int, min: Int ->
            myCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay)
            myCalendar.set(Calendar.MINUTE,min)
            updateTime()
        }

        val timePickerDialog = TimePickerDialog(
            this,timeSetListener,
            myCalendar.get(Calendar.HOUR_OF_DAY),
            myCalendar.get(Calendar.MINUTE),false
        )
//

        timePickerDialog.show()
    }

    private fun updateTime() {
        val myFormat = "h:mm a"  //2:23 AM
        val sdf = SimpleDateFormat(myFormat)
        fTime = myCalendar.time.time
        timeEdit.setText(sdf.format(myCalendar.time))


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
        fDate = myCalendar.time.time
        dateEdit.setText(sdf.format(myCalendar.time))

        taskReminderTime.visibility = View.VISIBLE
    }
}