package com.example.todoapp.data.repository

import androidx.annotation.WorkerThread
import com.example.todoapp.data.dao.TodoDao
import com.example.todoapp.data.model.TodoItem
import kotlinx.coroutines.flow.Flow

class TodoRepository(private val todoDao: TodoDao) {

    val getAll: Flow<List<TodoItem>> = todoDao.getAllTodos()

    fun getById(id: Int): Flow<TodoItem> = todoDao.getTodoById(id)

    @WorkerThread
    suspend fun insert(todo: TodoItem) {
        todoDao.insertTodo(todo)
    }

    @WorkerThread
    suspend fun delete(todo: TodoItem) {
        todoDao.deleteTodo(todo)
    }

    @WorkerThread
    suspend fun update(todo: TodoItem) {
        todoDao.updateTodo(todo)
    }
}