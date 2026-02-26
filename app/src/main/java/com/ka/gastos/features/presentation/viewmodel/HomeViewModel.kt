package com.ka.gastos.features.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ka.gastos.features.data.model.Expense
import com.ka.gastos.features.data.remote.ApiService
import com.ka.gastos.features.data.remote.GastoSocketEvent
import com.ka.gastos.features.data.remote.WebSocketManager
import com.ka.gastos.features.data.mapper.toExpense
import com.ka.gastos.features.data.remote.dto.CreateGastoRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiService: ApiService,
    private val webSocketManager: WebSocketManager
) : ViewModel() {

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        listenToSocketEvents()
    }

    fun connectAndLoad(grupoId: Int) {
        webSocketManager.connect(grupoId)
        loadData(grupoId)
    }

    private fun listenToSocketEvents() {
        webSocketManager.events
            .onEach { event ->
                when (event) {
                    is GastoSocketEvent.OnGastoCreated -> {
                        val newExpense = event.gasto.toExpense()
                        // ---- ¡SOLUCIÓN FINAL! ----
                        // Lógica a prueba de duplicados.
                        // Se añade el nuevo gasto solo si no existe ya en la lista.
                        _expenses.update { currentList ->
                            if (currentList.any { it.id == newExpense.id }) {
                                currentList // Si ya existe, no hagas nada.
                            } else {
                                listOf(newExpense) + currentList // Si no existe, añádelo.
                            }
                        }
                    }
                    is GastoSocketEvent.OnGastoUpdated -> {
                        val updatedExpense = event.gasto.toExpense()
                        _expenses.update { currentList ->
                            currentList.map { if (it.id == updatedExpense.id) updatedExpense else it }
                        }
                    }
                    is GastoSocketEvent.OnGastoDeleted -> {
                        _expenses.update { currentList ->
                            currentList.filterNot { it.id == event.gastoId }
                        }
                    }
                    else -> Unit
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadData(grupoId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = apiService.getGastos(grupoId)
                _expenses.value = response?.gastos?.map { it.toExpense() } ?: emptyList()
            } catch (e: Exception) {
                _error.value = "Error al cargar datos: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createGasto(descripcion: String, monto: Double, pagadorId: Int, grupoId: Int) {
        viewModelScope.launch {
            val request = CreateGastoRequest(descripcion, monto, pagadorId, grupoId)
            apiService.createGasto(request)
        }
    }

    override fun onCleared() {
        super.onCleared()
        webSocketManager.disconnect()
    }
}