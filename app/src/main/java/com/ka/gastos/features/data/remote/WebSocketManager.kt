package com.ka.gastos.features.data.remote

import android.util.Log
import com.google.gson.Gson
import com.ka.gastos.features.gastos.data.remote.dto.GastoDto
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

                    if (!json.has("data")) {
                        Log.w("WebSocket", "El mensaje no contiene el campo 'data'.")
                        return
                    }

                    when (type) {
                        "create", "update" -> {
                            val dataString = json.getJSONObject("data").toString()
                            val gasto = gson.fromJson(dataString, GastoDto::class.java)
                            if (type == "create") {
                                _events.tryEmit(GastoSocketEvent.OnGastoCreated(gasto))
                            } else {
                                _events.tryEmit(GastoSocketEvent.OnGastoUpdated(gasto))
                            }
                        }
                        "delete" -> {
                            val dataJson = json.getJSONObject("data")
                            val deletedId = dataJson.getInt("id")
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

    fun updateGasto(gasto: GastoDto) {
        if (webSocket == null) {
            Log.e("WebSocket", "No conectado. No se puede actualizar el gasto.")
            _events.tryEmit(GastoSocketEvent.ConnectionError("Not connected, can't update expense."))
            return
        }

        val gastoJson = gson.toJson(gasto)
        val gastoData = JSONObject(gastoJson)

        val message = JSONObject()
        message.put("type", "update")
        message.put("data", gastoData)

        webSocket?.send(message.toString())
        Log.d("WebSocket", "Enviando mensaje de actualización de gasto: $message")
    }

    fun deleteGasto(gastoId: Int, grupoId: Int) {
        if (webSocket == null) {
            Log.e("WebSocket", "No conectado. No se puede eliminar el gasto.")
            _events.tryEmit(GastoSocketEvent.ConnectionError("Not connected, can't delete expense."))
            return
        }

        val gastoData = JSONObject()
        gastoData.put("id", gastoId)
        gastoData.put("grupo_id", grupoId)

        val message = JSONObject()
        message.put("type", "delete")
        message.put("data", gastoData)

        webSocket?.send(message.toString())
        Log.d("WebSocket", "Enviando mensaje de eliminación de gasto: $message")
    }

    fun disconnect() {
        webSocket?.close(1000, "Cierre manual")
        webSocket = null
    }
}