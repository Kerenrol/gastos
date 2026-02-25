package com.ka.gastos.features.login.presentation.state

data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null
)