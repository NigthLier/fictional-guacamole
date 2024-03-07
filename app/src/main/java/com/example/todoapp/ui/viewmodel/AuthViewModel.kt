package com.example.todoapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authStateChannel = Channel<AuthState>()
    val authStateFlow = _authStateChannel.receiveAsFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            if (email.isBlank() || password.isBlank()) {
                _authStateChannel.send(AuthState.ERROR("Please fill in both email and password."))
                return@launch
            }

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                viewModelScope.launch {
                    if (task.isSuccessful) {
                        _authStateChannel.send(AuthState.SUCCESS)
                    } else {
                        _authStateChannel.send(AuthState.ERROR(task.exception?.message ?: "Login failed"))
                    }
                }
            }
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            if (email.isBlank() || password.isBlank()) {
                _authStateChannel.send(AuthState.ERROR("Please fill in both email and password."))
                return@launch
            }

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                viewModelScope.launch {
                    if (task.isSuccessful) {
                        _authStateChannel.send(AuthState.SUCCESS)
                    } else {
                        _authStateChannel.send(AuthState.ERROR(task.exception?.message ?: "Signup failed"))
                    }
                }
            }
        }
    }

    sealed class AuthState {
        data object SUCCESS : AuthState()
        data class ERROR(val message: String) : AuthState()
    }
}