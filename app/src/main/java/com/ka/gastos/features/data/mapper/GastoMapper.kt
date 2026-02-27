package com.ka.gastos.features.data.mapper

import com.ka.gastos.features.gastos.domain.model.Expense
import com.ka.gastos.features.gastos.data.remote.dto.GastoDto

fun GastoDto.toExpense(): Expense {
    return Expense(
        id = id,
        descripcion = descripcion,
        monto = monto,
        pagadoPor = pagadoPor ?: "",
        pagadorId = pagadorId,
        fecha = fecha
    )
}
