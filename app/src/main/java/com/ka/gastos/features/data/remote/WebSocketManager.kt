package com.ka.gastos.features.data.remote

import android.util.Log
import com.google.gson.Gson
import com.ka.gastos.features.data.remote.dto.GastoDto
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

sealed class GastoSocketEvent {
    data class OnGastoCreated(val gasto: GastoDto) : GastoSocketEvent()
    data class OnGastoUpdated(val gasto: GastoDto) : GastoSocketEvent()
    data class OnGastoDeleted(val gastoId: Int) : GastoSocketEvent()
    object ConnectionOpened : GastoSocketEvent()
    data class ConnectionError(val error: String) : GastoSocketEvent()
}

@Singleton
class WebSocketManager @Inject constructor(
    private val client: OkHttpClient,
    private val gson: Gson
) {
    private var webSocket: WebSocket? = null

    private val _events = MutableSharedFlow<GastoSocketEvent>()
    val events = _events.asSharedFlow()

    fun connect(grupoId: Int) {
        if (webSocket != null) return // Ya conectado

        val request = Request.Builder().url("ws://44.197.255.1:8080/ws").build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WebSocket", "Conectado!")
                val subscribeMessage = "{\"type\": \"subscribe\", \"grupo_id\": $grupoId}"
                webSocket.send(subscribeMessage)
                _events.tryEmit(GastoSocketEvent.ConnectionOpened)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "Mensaje recibido: $text")
                try {
                    val json = JSONObject(text)
                    val type = json.getString("type")
                    val data = json.getString("data")
                    
                    when (type) {
                        "create" -> {
                            val gasto = gson.fromJson(data, GastoDto::class.java)
                            _events.tryEmit(GastoSocketEvent.OnGastoCreated(gasto))
                        }
                        "update" -> {
                            val gasto = gson.fromJson(data, GastoDto::class.java)
                             _events.tryEmit(GastoSocketEvent.OnGastoUpdated(gasto))
                        }
                        "delete" -> {
                            val deletedId = JSONObject(data).getInt("id")
                            _events.tryEmit(GastoSocketEvent.OnGastoDeleted(deletedId))
                        }
                    }
                } catch (e: Exception) {
                    Log.e("WebSocket", "Error parseando mensaje: ${e.message}")
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocket", "Error: ${t.message}")
                _events.tryEmit(GastoSocketEvent.ConnectionError(t.message ?: "Error desconocido"))
                this@WebSocketManager.webSocket = null
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
                this@WebSocketManager.webSocket = null
                Log.d("WebSocket", "Cerrando conexión: $reason")
            }
        })
    }

    fun disconnect() {
        webSocket?.close(1000, "Cierre manual")
        webSocket = null
    }
}
