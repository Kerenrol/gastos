package com.ka.gastos.features.data.mapper

import com.ka.gastos.features.data.model.Expense
import com.ka.gastos.features.data.remote.dto.GastoDto

fun GastoDto.toExpense(): Expense {
    return Expense(
        id = id,
        descripcion = descripcion,
        monto = monto,
        pagadoPor = pagadorId.toString(), // Convertimos a String como en el modelo original
        fecha = "" // La API no devuelve fecha, la dejamos vacía por ahora
    )
}
