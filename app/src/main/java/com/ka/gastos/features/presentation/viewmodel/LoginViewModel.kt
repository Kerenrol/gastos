package com.ka.gastos.features.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ka.gastos.features.data.remote.ApiService
import com.ka.gastos.features.data.remote.dto.LoginRequest
import com.ka.gastos.features.data.remote.dto.RegisterRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    var loginState by mutableStateOf(LoginState())
        private set

    var registerState by mutableStateOf(RegisterState())
        private set

    fun onLogin(user: String, pass: String) {
        viewModelScope.launch {
            loginState = loginState.copy(isLoading = true, loginError = null)
            val success = apiService.login(LoginRequest(user, pass))
            if (success) {
                loginState = loginState.copy(isLoading = false, loginSuccess = true)
            } else {
                loginState = loginState.copy(isLoading = false, loginError = "Usuario o contraseña incorrectos")
            }
        }
    }

    fun onRegister(user: String, email: String, pass: String) {
        viewModelScope.launch {
            registerState = registerState.copy(isLoading = true, registerError = null)
            val success = apiService.register(RegisterRequest(user, email, pass))
            if (success) {
                registerState = registerState.copy(isLoading = false, registerSuccess = true)
            } else {
                registerState = registerState.copy(isLoading = false, registerError = "Error en el registro")
            }
        }
    }
}

data class LoginState(
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val loginError: String? = null
)

data class RegisterState(
    val isLoading: Boolean = false,
    val registerSuccess: Boolean = false,
    val registerError: String? = null
)
