package com.ka.gastos.features.presentation.grupos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ka.gastos.features.data.remote.ApiService
import com.ka.gastos.features.grupos.data.remote.dto.CreateGrupoRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GruposViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _state = MutableStateFlow(GruposState())
    val state: StateFlow<GruposState> = _state

    fun getGrupos() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val grupos = apiService.getGrupos()
                _state.value = _state.value.copy(grupos = grupos, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun createGrupo(nombre: String) {
        viewModelScope.launch {
            try {
                val success = apiService.createGrupo(CreateGrupoRequest(nombre))
                if (success) {
                    getGrupos()
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message, isLoading = false)
            }
        }
    }
}
