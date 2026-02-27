package com.ka.gastos.features.gastos.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GastoRequestDto(
    val descripcion: String,
    val monto: Double,
    @SerializedName("pagador_id") val pagadorId: Int,
    @SerializedName("grupo_id") val grupoId: Int
)
