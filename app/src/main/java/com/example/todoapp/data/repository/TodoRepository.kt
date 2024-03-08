package com.example.todoapp.data.repository

import android.util.Log
import android.widget.Toast
import androidx.annotation.WorkerThread
import com.example.todoapp.data.dao.TodoDao
import com.example.todoapp.data.model.TodoItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class TodoRepository(private val todoDao: TodoDao) {
    private var todoRef: DatabaseReference = FirebaseDatabase.getInstance()
        .getReference().child(FirebaseAuth.getInstance().currentUser?.uid ?: "")

    val getAll: Flow<List<TodoItem>> = todoDao.getAllTodos()

    fun getById(id: Int): Flow<TodoItem> = todoDao.getTodoById(id)

    @WorkerThread
    suspend fun insert(todo: TodoItem) {
        val id = todoDao.insertTodo(todo)
        todoRef.child(id.toString()).setValue(todo)
    }

    @WorkerThread
    suspend fun delete(todo: TodoItem) {
        todoDao.deleteTodo(todo)
        todoRef.child(todo.id.toString()).removeValue()
    }

    @WorkerThread
    suspend fun update(todo: TodoItem) {
        todoDao.updateTodo(todo)
        todoRef.child(todo.id.toString()).setValue(todo)
    }

    @WorkerThread
    suspend fun uploadAllTodos() {
        val todos = todoDao.getAllTodos().firstOrNull() ?: emptyList()
        todoRef.removeValue().addOnSuccessListener {
            todoRef.updateChildren(
                todos.associate { todo -> todo.id.toString() to it }
            ).addOnSuccessListener {
            }
        }
    }

    @WorkerThread
    suspend fun loadAllTodos() {
        val todoItems = mutableListOf<TodoItem>()
        todoRef.get().addOnSuccessListener { snapshot ->
            for (child in snapshot.children) {
                Log.d("TAG", child.toString())
                child.getValue(TodoItem::class.java)?.let { todoItems.add(it) }
            }
        }
        Log.d("TAG", todoItems.toString())
        todoDao.deleteAllTodos()
        todoDao.insertAllTodos(*todoItems.toTypedArray())
    }

    @WorkerThread
    suspend fun deleteAllTodos() {
        todoDao.deleteAllTodos()
    }

    @WorkerThread
    suspend fun insertAllTodos(todos: List<TodoItem>) {
        todoDao.insertAllTodos(*todos.toTypedArray())
    }
}