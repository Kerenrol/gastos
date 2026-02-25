package com.ka.gastos.features.login.presentation.state

sealed class LoginEvent {
    data class OnLogin(val email: String, val pass: String) : LoginEvent()
}