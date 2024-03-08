package com.example.todoapp.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.todoapp.MainApplication
import com.example.todoapp.data.model.TodoItem
import com.example.todoapp.data.repository.TodoRepository
import com.example.todoapp.ui.viewmodel.TodoViewModel
import com.example.todoapp.ui.viewmodel.TodoViewModelFactory

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TodoListScreen(navController: NavController) {
    val app = LocalContext.current.applicationContext as MainApplication
    val todoViewModel: TodoViewModel =
        viewModel(factory = TodoViewModelFactory(TodoRepository(app.database.todoDao())))

    val todoList by todoViewModel.todoList.observeAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Todos") }, actions = {
                IconButton(onClick = { navController.navigate("todoDetail/0") }) {
                    Icon(Icons.Filled.Add, "Add Todo")
                }
            })
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(todoList, { todo -> todo.id }) { todo ->
                val currentItem by rememberUpdatedState(todo)
                val dismissState = rememberDismissState(
                    confirmValueChange = {
                        when (it) {
                            DismissValue.DismissedToEnd -> {
                                todoViewModel.updateTodo(currentItem.copy(isCompleted = !currentItem.isCompleted))
                                false
                            }

                            DismissValue.DismissedToStart -> {
                                todoViewModel.deleteTodo(todo)
                                true
                            }

                            else -> {
                                false
                            }
                        }
                    }, positionalThreshold = { 75.dp.toPx() }
                )
                SwipeToDismiss(
                    state = dismissState,
                    modifier = Modifier
                        .padding(vertical = 1.dp)
                        .animateItemPlacement(),
                    directions = setOf(
                        DismissDirection.EndToStart,
                        DismissDirection.StartToEnd
                    ),
                    background = {
                        SwipeBackground(dismissState)
                    },
                    dismissContent = {
                        TodoItemRow(
                            todo = todo,
                            onTodoClicked = { selectedTodo ->
                                navController.navigate("todoDetail/${selectedTodo.id}")
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun TodoItemRow(todo: TodoItem, onTodoClicked: (TodoItem) -> Unit) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clickable { onTodoClicked(todo) },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (todo.isCompleted) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.inverseSurface,
            contentColor = if (todo.isCompleted) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.inverseOnSurface
        )
    ) {
        Text(
            modifier = Modifier.padding(15.dp),
            text = todo.title,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeBackground(dismissState: DismissState) {
    val direction = dismissState.dismissDirection ?: return

    val color by animateColorAsState(
        when (dismissState.targetValue) {
            DismissValue.Default -> Color.LightGray
            DismissValue.DismissedToEnd -> Color.Green
            DismissValue.DismissedToStart -> Color.Red
        }
    )
    val alignment = when (direction) {
        DismissDirection.StartToEnd -> Alignment.CenterStart
        DismissDirection.EndToStart -> Alignment.CenterEnd
    }
    val icon = when (direction) {
        DismissDirection.StartToEnd -> Icons.Default.Done
        DismissDirection.EndToStart -> Icons.Default.Delete
    }
    val scale by animateFloatAsState(
        if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(color)
            .padding(horizontal = 20.dp),
        contentAlignment = alignment
    ) {
        Icon(
            icon,
            contentDescription = "Localized description",
            modifier = Modifier.scale(scale)
        )
    }
}
