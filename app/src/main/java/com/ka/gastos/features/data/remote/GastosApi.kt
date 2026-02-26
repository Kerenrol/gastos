package com.ka.gastos.features.data.remote

import com.ka.gastos.features.data.model.Balance
import com.ka.gastos.features.data.model.Expense
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface GastosApi {
    @GET("gastos")
    suspend fun getExpenses(): List<Expense>

    @POST("gastos")
    suspend fun addExpense(@Body expense: Expense): Expense

    @GET("balances")
    suspend fun getBalances(): List<Balance>
}
