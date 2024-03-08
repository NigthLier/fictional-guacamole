package com.example.todoapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.MainApplication
import com.example.todoapp.data.repository.TodoRepository
import com.example.todoapp.ui.viewmodel.AuthViewModel
import com.example.todoapp.ui.viewmodel.TodoViewModel
import com.example.todoapp.ui.viewmodel.TodoViewModelFactory

@Composable
fun LoginScreen(navController: NavController, authViewModel : AuthViewModel = viewModel()) {
    val app = LocalContext.current.applicationContext as MainApplication
    val todoViewModel: TodoViewModel =
        viewModel(factory = TodoViewModelFactory(TodoRepository(app.database.todoDao())))

    if(authViewModel.getUser() != null) {
        todoViewModel.loadTodos()
        navController.navigate("todoList")
    }

    val context = LocalContext.current
    val authState by authViewModel.authStateFlow.collectAsState(initial = null)

    LaunchedEffect(authState) {
        when (authState) {
            is AuthViewModel.AuthState.SUCCESS -> {
                todoViewModel.loadTodos()
                navController.navigate("todoList")
            }
            is AuthViewModel.AuthState.ERROR -> {
                val errorMessage = (authState as AuthViewModel.AuthState.ERROR).message
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
            null -> {}
        }
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { authViewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth(0.75F)
        ) {
            Text("Login")
        }
        TextButton(
            onClick = { navController.navigate("signup") },
            modifier = Modifier.fillMaxWidth(0.75F)
        ) {
            Text("Don't have an account? Sign up")
        }
    }
}