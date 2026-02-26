package com.ka.gastos.features.presentation.gastos

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ka.gastos.features.data.mapper.toExpense
import com.ka.gastos.features.data.remote.ApiService
import com.ka.gastos.features.data.remote.GastoSocketEvent
import com.ka.gastos.features.data.remote.WebSocketManager
import com.ka.gastos.features.data.remote.dto.CreateGastoRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GastosViewModel @Inject constructor(
    private val webSocketManager: WebSocketManager,
    private val apiService: ApiService, // Inyectado
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(GastosState())
    val state = _state.asStateFlow()

    private val grupoId: Int = savedStateHandle.get<Int>("grupoId") ?: -1

    init {
        if (grupoId != -1) {
            loadInitialGastos()
            webSocketManager.connect(grupoId)
            listenToGastoEvents()
        }
    }

    private fun loadInitialGastos() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val response = apiService.getGastos(grupoId)
            if (response != null) {
                val gastos = response.gastos?.map { it.toExpense() } ?: emptyList()
                _state.update { it.copy(gastos = gastos, isLoading = false) }
            } else {
                _state.update { it.copy(error = "Error al cargar los gastos", isLoading = false) }
            }
        }
    }

    private fun listenToGastoEvents() {
        viewModelScope.launch {
            webSocketManager.events.collect { event ->
                when (event) {
                    is GastoSocketEvent.OnGastoCreated -> {
                        _state.update {
                            it.copy(gastos = it.gastos + event.gasto.toExpense())
                        }
                    }
                    is GastoSocketEvent.OnGastoUpdated -> {
                        _state.update { state ->
                            val updatedGastos = state.gastos.map {
                                if (it.id == event.gasto.id) event.gasto.toExpense() else it
                            }
                            state.copy(gastos = updatedGastos)
                        }
                    }
                    is GastoSocketEvent.OnGastoDeleted -> {
                        _state.update { state ->
                            state.copy(gastos = state.gastos.filter { it.id != event.gastoId })
                        }
                    }
                    is GastoSocketEvent.ConnectionError -> {
                        _state.update { it.copy(error = event.error) }
                    }
                    // Manejar otros eventos si es necesario
                    else -> Unit
                }
            }
        }
    }

    fun createGasto(descripcion: String, monto: Double, pagadorId: Int) {
        if (grupoId != -1) {
            viewModelScope.launch {
                val request = CreateGastoRequest(descripcion, monto, pagadorId, grupoId)
                apiService.createGasto(request)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        webSocketManager.disconnect()
    }
}