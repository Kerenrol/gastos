package com.ka.gastos.features.data.mapper

import com.ka.gastos.features.data.model.Expense
import com.ka.gastos.features.data.remote.dto.GastoDto

fun GastoDto.toExpense(): Expense {
    return Expense(
        id = id,
        descripcion = descripcion,
        monto = monto,
        pagadoPor = pagadorId.toString(),
        fecha = ""
    )
}
