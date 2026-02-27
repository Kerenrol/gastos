package com.ka.gastos.features.grupos.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ka.gastos.features.grupos.domain.model.Grupo
import com.ka.gastos.features.grupos.domain.usecase.CreateGrupoUseCase
import com.ka.gastos.features.grupos.domain.usecase.GetGruposUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GrupoViewModel @Inject constructor(
    private val getGruposUseCase: GetGruposUseCase,
    private val createGrupoUseCase: CreateGrupoUseCase
) : ViewModel() {

    private val _grupos = MutableStateFlow<List<Grupo>>(emptyList())
    val grupos = _grupos.asStateFlow()

    init {
        loadGrupos()
    }

    fun loadGrupos() {
        viewModelScope.launch {
            _grupos.value = getGruposUseCase()
        }
    }

    fun createGrupo(nombre: String) {
        viewModelScope.launch {
            createGrupoUseCase(nombre)
            loadGrupos() // Recargamos la lista después de crear
        }
    }
}
