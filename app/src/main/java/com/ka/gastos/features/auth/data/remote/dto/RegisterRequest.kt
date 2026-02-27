package com.ka.gastos.features.auth.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("nombre") val userName: String,
    @SerializedName("correo") val email: String,
    @SerializedName("contrasena") val password: String
)
