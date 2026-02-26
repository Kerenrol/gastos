package com.ka.gastos.features.data.model

data class Expense(
    val id: Int,
    val descripcion: String,
    val monto: Double,
    val pagadoPor: String,
    val fecha: String
)

data class Balance(
    val usuario: String,
    val monto: Double
)
