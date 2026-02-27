package com.ka.gastos.features.gastos.domain.repository

import com.ka.gastos.features.gastos.domain.model.Expense
import kotlinx.coroutines.flow.Flow

interface GastoRepository {
    fun getGastos(grupoId: Int): Flow<List<Expense>>
    suspend fun createGasto(grupoId: Int, descripcion: String, monto: Double, pagadorId: Int)
}
