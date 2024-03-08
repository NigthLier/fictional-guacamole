package com.example.todoapp.ui.viewmodel

import android.app.Application
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.MainApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    val app: MainApplication = getApplication()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authStateChannel = Channel<AuthState>()
    val authStateFlow = _authStateChannel.receiveAsFlow()

    init {
        if(auth.currentUser != null) {
            viewModelScope.launch {
                _authStateChannel.send(AuthState.SUCCESS)
            }
        }
    }

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
                        _authStateChannel.send(
                            AuthState.ERROR(
                                task.exception?.message ?: "Login failed"
                            )
                        )
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
                        _authStateChannel.send(
                            AuthState.ERROR(
                                task.exception?.message ?: "Signup failed"
                            )
                        )
                    }
                }
            }
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun getUser(): FirebaseUser? {
        return auth.currentUser
    }

    sealed class AuthState {
        data object SUCCESS : AuthState()
        data class ERROR(val message: String) : AuthState()
    }
}