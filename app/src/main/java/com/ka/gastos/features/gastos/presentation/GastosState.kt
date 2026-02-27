package com.ka.gastos.features.gastos.presentation

import com.ka.gastos.features.gastos.domain.model.Expense

data class GastosState(
    val gastos: List<Expense> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
