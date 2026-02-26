package com.ka.gastos.features.data.mapper

import com.ka.gastos.features.data.model.Expense
import com.ka.gastos.features.data.remote.dto.GastoDto

fun GastoDto.toExpense(): Expense {
    // TODO: El campo `pagadoPor` debería ser el nombre del usuario, no el ID.
    //       Necesitarás obtener el nombre del usuario a partir del `pagadorId`.
    //       Por ahora, se deja el ID como placeholder.
    return Expense(
        id = id,
        descripcion = descripcion,
        monto = monto,
        pagadoPor = pagadorId.toString(), // Placeholder, debería ser el nombre
        fecha = fecha // <-- ¡SOLUCIÓN! Usamos la fecha que viene del DTO.
    )
}
