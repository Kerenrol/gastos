package com.ka.gastos.features.data.repository

import com.ka.gastos.features.data.model.Balance
import com.ka.gastos.features.data.model.Expense
import com.ka.gastos.features.data.remote.GastosApi
import javax.inject.Inject

class GastosRepository @Inject constructor(
    private val api: GastosApi
) {
    suspend fun getExpenses(): List<Expense> = api.getExpenses()
    suspend fun getBalances(): List<Balance> = api.getBalances()
    suspend fun addExpense(expense: Expense): Expense = api.addExpense(expense)
}
