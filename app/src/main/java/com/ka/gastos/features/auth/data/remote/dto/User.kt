package com.ka.gastos.features.auth.data.remote.dto

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    @SerializedName("userName") val userName: String,
    val email: String
)
