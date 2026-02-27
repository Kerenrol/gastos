package com.ka.gastos.features.gastos.data.mapper

import com.ka.gastos.features.gastos.data.remote.dto.GastoDto
import com.ka.gastos.features.gastos.domain.model.Expense

fun GastoDto.toExpense(): Expense {
    return Expense(
        id = id,
        descripcion = descripcion,
        monto = monto,
        pagadoPor = pagadoPor,
        pagadorId = pagadorId, // Agregado para copiar el ID
        fecha = fecha
    )
}
