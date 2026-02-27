package com.ka.gastos.features.gastos.data.repository

import com.ka.gastos.features.data.remote.ApiService
import com.ka.gastos.features.data.remote.GastoSocketEvent
import com.ka.gastos.features.data.remote.WebSocketManager
import com.ka.gastos.features.gastos.data.mapper.toExpense
import com.ka.gastos.features.gastos.data.remote.dto.CreateGastoRequest
import com.ka.gastos.features.gastos.domain.model.Expense
import com.ka.gastos.features.gastos.domain.repository.GastoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject

class GastoRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val webSocketManager: WebSocketManager
) : GastoRepository {

    override fun getGastos(grupoId: Int): Flow<List<Expense>> = flow {
        // 1. Conectar y cargar datos iniciales
        webSocketManager.connect(grupoId)
        val initialGastos = apiService.getGastos(grupoId)?.gastos?.map { it.toExpense() } ?: emptyList()
        emit(initialGastos)

        // 2. Escuchar eventos en tiempo real
        var currentGastos = initialGastos
        webSocketManager.events.collect { event ->
            val updatedList = when (event) {
                is GastoSocketEvent.OnGastoCreated -> {
                    val newExpense = event.gasto.toExpense()
                    if (currentGastos.any { it.id == newExpense.id }) currentGastos
                    else listOf(newExpense) + currentGastos
                }
                is GastoSocketEvent.OnGastoUpdated -> {
                    val updatedExpense = event.gasto.toExpense()
                    currentGastos.map { if (it.id == updatedExpense.id) updatedExpense else it }
                }
                is GastoSocketEvent.OnGastoDeleted -> {
                    currentGastos.filter { it.id != event.gastoId }
                }
                else -> currentGastos
            }
            currentGastos = updatedList
            emit(currentGastos)
        }
    }.onCompletion {
        // 3. Desconectar cuando el flujo se cancele
        webSocketManager.disconnect()
    }

    override suspend fun createGasto(grupoId: Int, descripcion: String, monto: Double, pagadorId: Int) {
        val request = CreateGastoRequest(descripcion, monto, pagadorId, grupoId)
        apiService.createGasto(request)
    }
}