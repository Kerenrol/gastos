package com.ka.gastos.features.data.dto

import com.google.gson.annotations.SerializedName

data class GrupoDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String
)
