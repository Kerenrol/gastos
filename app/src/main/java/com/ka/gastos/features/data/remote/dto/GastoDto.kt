package com.ka.gastos.features.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GastoDto(
    val id: Int,
    val descripcion: String,
    val monto: Double,
    @SerializedName("pagador_id") val pagadorId: Int,
    @SerializedName("grupo_id") val grupoId: Int
    // Añade aquí cualquier otro campo que devuelva tu API, como la fecha.
)
