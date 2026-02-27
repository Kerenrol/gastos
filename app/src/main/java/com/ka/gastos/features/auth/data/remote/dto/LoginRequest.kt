package com.ka.gastos.features.auth.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("userName") val usuario: String,
    @SerializedName("password") val contrasena: String
)
