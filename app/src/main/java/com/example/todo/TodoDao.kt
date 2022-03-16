package com.example.todo

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TodoDao{

    @Insert()
    suspend fun insertTask(todoModel: TodoModel) :Long

    @Query("Select * From TodoModel where isFinished == 0")
    fun getTask(): LiveData<List<TodoModel>>

    @Query("Update TodoModel Set isFinished = 1 Where id=:uid")
    fun finishTask(uid:Long)

    @Query("Delete From TodoModel  Where id=:uid")
    fun deleteTask(uid:Long)
}