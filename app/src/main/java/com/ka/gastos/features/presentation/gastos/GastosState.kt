package com.ka.gastos.features.presentation.gastos

import com.ka.gastos.features.data.model.Expense

data class GastosState(
    val gastos: List<Expense> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
