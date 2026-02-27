package com.ka.gastos.features.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ka.gastos.core.UserPreferences
import com.ka.gastos.features.gastos.domain.model.Expense
import com.ka.gastos.features.data.remote.ApiService
import com.ka.gastos.features.data.remote.GastoSocketEvent
import com.ka.gastos.features.data.remote.WebSocketManager
import com.ka.gastos.features.gastos.data.mapper.toExpense
import com.ka.gastos.features.gastos.data.remote.dto.CreateGastoRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiService: ApiService,
    private val webSocketManager: WebSocketManager,
    private val userPreferences: UserPreferences
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
                        _expenses.update { currentList -> listOf(newExpense) + currentList }
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

    fun addExpense(descripcion: String, monto: Double, grupoId: Int) {
        viewModelScope.launch {
            val user = userPreferences.user.first()
            if (user != null) {
                val createGastoRequest = CreateGastoRequest(descripcion, monto, user.id, grupoId)
                val success = apiService.createGasto(createGastoRequest)
                if (success) {
                    // Si el gasto se crea con éxito, recargamos la lista de gastos.
                    loadData(grupoId)
                } else {
                    _error.value = "Error al crear el gasto. Inténtalo de nuevo."
                }
            } else {
                _error.value = "Error: No se pudo obtener la información del usuario. Por favor, inicie sesión de nuevo."
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        webSocketManager.disconnect()
    }
}
