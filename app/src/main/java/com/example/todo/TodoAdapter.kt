package com.example.todo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.android.synthetic.main.item_todos.view.*
import java.text.SimpleDateFormat
import java.util.*

class TodoAdapter(val list:List<TodoModel>) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_todos,parent,false))
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return list[position].id
    }

    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(todoModel: TodoModel) {
            with(itemView){
                val colors = resources.getIntArray(R.array.random_color)
                val randomColor = colors[Random().nextInt(colors.size)]
                viewColorTag.setBackgroundColor(randomColor)
                txtShowTitle.text = todoModel.title
                txtShowTask.text = todoModel.description
                txtShowCategory.text = todoModel.category
                updateTime(todoModel.time)
                updateDate(todoModel.date)
            }
        }

        private fun updateTime(time: Long) {
            val myFormat = "h:mm a"  //2:23 AM
            val sdf = SimpleDateFormat(myFormat)
            itemView.txtShowTime.text = (sdf.format(Date(time)))


        }
        private fun updateDate(date: Long) {
            val myFormat = "EEE, d MMM yyyy"  // sat, 3 feb 2001
            val sdf = SimpleDateFormat(myFormat)
            itemView.txtShowDate.text = (sdf.format(Date(date)))


        }


    }


}