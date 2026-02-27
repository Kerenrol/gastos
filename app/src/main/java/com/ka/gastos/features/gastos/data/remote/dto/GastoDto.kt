package com.ka.gastos.features.gastos.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GastoDto(
    val id: Int,
    val descripcion: String,
    val monto: Double,
    @SerializedName("pagador_id") val pagadorId: Int,
    @SerializedName("pagado_por") val pagadoPor: String?,
    @SerializedName("grupo_id") val grupoId: Int,
    val fecha: String
)
