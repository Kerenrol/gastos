package com.ka.gastos.features.data.remote.dto

// Esta clase representa el objeto JSON que envuelve la lista de gastos
data class GastosResponse(
    val gastos: List<GastoDto>?
)
