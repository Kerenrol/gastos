package com.ka.gastos.features.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ka.gastos.core.UserPreferences
import com.ka.gastos.features.auth.data.remote.dto.User
import com.ka.gastos.features.data.remote.ApiService
import com.ka.gastos.features.data.remote.GastoSocketEvent
import com.ka.gastos.features.data.remote.WebSocketManager
import com.ka.gastos.features.gastos.data.mapper.toExpense
import com.ka.gastos.features.gastos.data.remote.dto.CreateGastoRequest
import com.ka.gastos.features.gastos.domain.model.Expense
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val expenses: List<Expense> = emptyList(),
    val currentUser: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiService: ApiService,
    private val webSocketManager: WebSocketManager,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        listenToSocketEvents()
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            userPreferences.user.collect { user ->
                _uiState.update { it.copy(currentUser = user) }
            }
        }
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
                        _uiState.update { it.copy(expenses = listOf(newExpense) + it.expenses) }
                    }
                    is GastoSocketEvent.OnGastoUpdated -> {
                        val updatedExpense = event.gasto.toExpense()
                        _uiState.update { currentState ->
                            currentState.copy(expenses = currentState.expenses.map {
                                if (it.id == updatedExpense.id) updatedExpense else it
                            })
                        }
                    }
                    is GastoSocketEvent.OnGastoDeleted -> {
                        _uiState.update { currentState ->
                            currentState.copy(expenses = currentState.expenses.filterNot { it.id == event.gastoId })
                        }
                    }
                    else -> Unit
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadData(grupoId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = apiService.getGastos(grupoId)
                val expenses = response?.gastos?.map { it.toExpense() } ?: emptyList()
                _uiState.update { it.copy(expenses = expenses, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al cargar datos: ${e.message}", isLoading = false) }
                e.printStackTrace()
            }
        }
    }

    fun addExpense(descripcion: String, monto: Double, grupoId: Int) {
        viewModelScope.launch {
            val user = uiState.value.currentUser
            if (user != null) {
                val createGastoRequest = CreateGastoRequest(descripcion, monto, user.id, grupoId)
                val success = apiService.createGasto(createGastoRequest)
                if (success) {
                    loadData(grupoId)
                } else {
                    _uiState.update { it.copy(error = "Error al crear el gasto. Inténtalo de nuevo.") }
                }
            } else {
                _uiState.update { it.copy(error = "Error: No se pudo obtener la información del usuario. Por favor, inicie sesión de nuevo.") }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        webSocketManager.disconnect()
    }
}
