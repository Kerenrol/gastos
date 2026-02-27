package com.ka.gastos.features.gastos.domain.model

data class Expense(
    val id: Int,
    val descripcion: String,
    val monto: Double,
    val pagadoPor: String?,
    val pagadorId: Int, // Agregado para almacenar el ID
    val fecha: String
)

data class Balance(
    val usuario: String,
    val monto: Double
)
