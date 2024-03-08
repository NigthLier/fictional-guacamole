package com.example.todoapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.model.TodoItem
import com.example.todoapp.data.repository.TodoRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

class TodoViewModel(private val repository: TodoRepository) : ViewModel() {

    val todoList = repository.getAll.asLiveData()

    fun getTodoById(id: Int): LiveData<TodoItem> {
        return repository.getById(id).asLiveData()
    }

    fun addTodo(todoItem: TodoItem) {
        viewModelScope.launch {
            repository.insert(todoItem)
        }
    }

    fun updateTodo(todoItem: TodoItem) {
        viewModelScope.launch {
            repository.update(todoItem)
        }
    }

    fun deleteTodo(todoItem: TodoItem) {
        viewModelScope.launch {
            repository.delete(todoItem)
        }
    }

    fun saveTodo(todoItem: TodoItem) {
        if (todoItem.id == 0) {
            addTodo(todoItem)
        } else {
            updateTodo(todoItem)
        }
    }

    fun uploadTodos() {
        viewModelScope.launch {
            repository.uploadAllTodos()
        }
    }

    fun loadTodos() {
        viewModelScope.launch {
            repository.loadAllTodos()
        }
    }

    fun deleteTodos() {
        viewModelScope.launch {
            repository.deleteAllTodos()
        }
    }
}

class TodoViewModelFactory(private val repository: TodoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}