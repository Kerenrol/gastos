package com.ka.gastos.features.gastos.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ka.gastos.features.gastos.domain.model.Expense
import com.ka.gastos.features.gastos.domain.usecase.CreateGastoUseCase
import com.ka.gastos.features.gastos.domain.usecase.GetGastosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GastosViewModel @Inject constructor(
    private val getGastosUseCase: GetGastosUseCase,
    private val createGastoUseCase: CreateGastoUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(GastosState())
    val state = _state.asStateFlow()

    private val grupoId: Int = savedStateHandle.get<Int>("grupoId") ?: -1

    init {
        if (grupoId != -1) {
            observeGastos()
        }
    }

    private fun observeGastos() {
        getGastosUseCase(grupoId)
            .onEach { gastos ->
                _state.update { it.copy(gastos = gastos) }
            }
            .launchIn(viewModelScope)
    }

    fun createGasto(descripcion: String, monto: Double, pagadorId: Int) {
        if (grupoId != -1) {
            viewModelScope.launch {
                try {
                    createGastoUseCase(grupoId, descripcion, monto, pagadorId)
                } catch (e: Exception) {
                    _state.update { it.copy(error = "Error al crear el gasto: ${e.message}") }
                }
            }
        }
    }
}
