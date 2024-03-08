package com.example.todoapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.todoapp.MainApplication
import com.example.todoapp.data.model.TodoItem
import com.example.todoapp.data.repository.TodoRepository
import com.example.todoapp.ui.viewmodel.TodoViewModel
import com.example.todoapp.ui.viewmodel.TodoViewModelFactory

@Composable
fun TodoDetailScreen(navController: NavController, todoItemId: Int) {
    val app = LocalContext.current.applicationContext as MainApplication
    val todoViewModel: TodoViewModel =
        viewModel(factory = TodoViewModelFactory(TodoRepository(app.database.todoDao())))

    val todo by todoViewModel.getTodoById(todoItemId).observeAsState(initial = null)
    var title by remember { mutableStateOf(todo?.title ?: "") }
    var description by remember { mutableStateOf(todo?.description ?: "") }

    LaunchedEffect(todo) {
        title = todo?.title ?: ""
        description = todo?.description ?: ""
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = if (todo == null) "Add Todo" else "Edit Todo",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.height(150.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val newItem = TodoItem(
                    id = todo?.id ?: 0,
                    title = title,
                    description = description,
                    isCompleted = todo?.isCompleted ?: false
                )
                todoViewModel.saveTodo(newItem)
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth(0.75F)
        ) {
            Text("Save")
        }
    }
}