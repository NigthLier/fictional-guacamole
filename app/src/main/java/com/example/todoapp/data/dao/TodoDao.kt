package com.example.todoapp.data.dao

import androidx.room.*
import com.example.todoapp.data.model.TodoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo_items")
    fun getAllTodos(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE id = :id")
    fun getTodoById(id: Int): Flow<TodoItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: TodoItem)

    @Delete
    suspend fun deleteTodo(todo: TodoItem)

    @Update
    suspend fun updateTodo(todo: TodoItem)
}