package com.example.todoapp

import android.app.Application
import androidx.room.Room
import com.example.todoapp.data.database.AppDatabase

class MainApplication : Application() {
    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this, AppDatabase::class.java, "todo_database").build()
    }
}