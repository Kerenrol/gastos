package com.ka.gastos.features.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ka.gastos.features.grupos.domain.model.Grupo
import com.ka.gastos.features.data.remote.ApiService
import com.ka.gastos.features.grupos.data.remote.dto.CreateGrupoRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GrupoViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _grupos = MutableStateFlow<List<Grupo>>(emptyList())
    val grupos = _grupos.asStateFlow()

    init {
        loadGrupos()
    }

    private fun loadGrupos() {
        viewModelScope.launch {
            try {
                _grupos.value = apiService.getGrupos()
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }

    fun createGrupo(nombre: String) {
        viewModelScope.launch {
            try {
                val success = apiService.createGrupo(CreateGrupoRequest(nombre))
                if (success) {
                    loadGrupos() // Recargamos la lista si se creó con éxito
                }
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }
}
