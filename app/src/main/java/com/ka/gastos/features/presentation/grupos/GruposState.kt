package com.ka.gastos.features.presentation.grupos

import com.ka.gastos.features.grupos.domain.model.Grupo

data class GruposState(
    val grupos: List<Grupo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
