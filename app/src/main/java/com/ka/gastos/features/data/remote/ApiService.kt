package com.ka.gastos.features.data.remote

import android.util.Log
import com.google.gson.Gson
import com.ka.gastos.features.data.model.Grupo
import com.ka.gastos.features.data.remote.dto.CreateGastoRequest
import com.ka.gastos.features.data.remote.dto.CreateGrupoRequest
import com.ka.gastos.features.data.remote.dto.GastosResponse
import com.ka.gastos.features.data.remote.dto.LoginRequest
import com.ka.gastos.features.data.remote.dto.RegisterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiService @Inject constructor(
    private val client: OkHttpClient,
    private val gson: Gson
) {
    private val baseUrl = "http://44.197.255.1:8080"

    // --- Auth ---
    suspend fun login(request: LoginRequest): Boolean = withContext(Dispatchers.IO) {
        try {
            val requestBody = gson.toJson(request).toRequestBody("application/json".toMediaType())
            val apiRequest = Request.Builder().url("$baseUrl/login").post(requestBody).build()
            client.newCall(apiRequest).execute().isSuccessful
        } catch (e: Exception) {
            Log.e("ApiService", "Error en login: ${e.message}")
            false
        }
    }

    suspend fun register(request: RegisterRequest): Boolean = withContext(Dispatchers.IO) {
        try {
            val requestBody = gson.toJson(request).toRequestBody("application/json".toMediaType())
            val apiRequest = Request.Builder().url("$baseUrl/user").post(requestBody).build()
            client.newCall(apiRequest).execute().isSuccessful
        } catch (e: Exception) {
            Log.e("ApiService", "Error en register: ${e.message}")
            false
        }
    }

    // --- Grupos ---
    suspend fun getGrupos(): List<Grupo> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url("$baseUrl/grupos/").get().build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext emptyList()
            
            val responseBody = response.body?.string() ?: "[]"
            if (responseBody.isBlank() || responseBody == "null") return@withContext emptyList()

            val jsonArray = JSONArray(responseBody)
            val grupos = mutableListOf<Grupo>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                grupos.add(Grupo(id = obj.getInt("id"), nombre = obj.getString("nombre")))
            }
            grupos
        } catch (e: Exception) {
            Log.e("ApiService", "Error obteniendo grupos: ${e.message}")
            emptyList()
        }
    }

    suspend fun createGrupo(request: CreateGrupoRequest): Boolean = withContext(Dispatchers.IO) {
        try {
            val requestBody = gson.toJson(request).toRequestBody("application/json".toMediaType())
            val apiRequest = Request.Builder().url("$baseUrl/grupos/").post(requestBody).build()
            client.newCall(apiRequest).execute().isSuccessful
        } catch (e: Exception) {
            Log.e("ApiService", "Error creando grupo: ${e.message}")
            false
        }
    }
    
    // --- Gastos ---
    suspend fun createGasto(request: CreateGastoRequest): Boolean = withContext(Dispatchers.IO) {
        try {
            val requestBody = gson.toJson(request).toRequestBody("application/json".toMediaType())
            val apiRequest = Request.Builder().url("$baseUrl/gastos/").post(requestBody).build()
            client.newCall(apiRequest).execute().isSuccessful
        } catch (e: Exception) {
            Log.e("ApiService", "Error creando gasto: ${e.message}")
            false
        }
    }

    suspend fun getGastos(grupoId: Int): GastosResponse? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url("$baseUrl/gastos/?grupo_id=$grupoId").get().build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext null

            val responseBody = response.body?.string()
            gson.fromJson(responseBody, GastosResponse::class.java)
        } catch (e: Exception) {
            Log.e("ApiService", "Error obteniendo gastos: ${e.message}")
            null
        }
    }
}
