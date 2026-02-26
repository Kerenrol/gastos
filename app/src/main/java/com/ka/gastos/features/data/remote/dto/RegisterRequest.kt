package com.ka.gastos.features.data.remote.dto

data class RegisterRequest(
    val userName: String,
    val email: String,
    val password: String
)
