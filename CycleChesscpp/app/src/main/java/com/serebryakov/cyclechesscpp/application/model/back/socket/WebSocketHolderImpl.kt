package com.serebryakov.cyclechesscpp.application.model.back.socket

import com.google.gson.JsonObject
import com.serebryakov.cyclechesscpp.foundation.socket.BaseWebSocketListener
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

class WebSocketHolderImpl(
    private var webSocket: WebSocket? = null,
    private val okHttpClient: OkHttpClient = OkHttpClient(),
) : WebSocketHolder {
    override suspend fun createWebSocket(webSocketListener: BaseWebSocketListener, username: String) {
        webSocket = okHttpClient.newWebSocket(createRequest(username), webSocketListener)
    }

    override suspend fun sendMessage(message: String) {
        webSocket?.send(message)
    }

    override suspend fun deleteWebSocket() {
//        webSocket?.close(1000, "Closed manually")
        webSocket = null
    }

    private fun createRequest(username: String): Request {
        val url = SOCKET_URL_BASE + username
        return Request.Builder()
            .url(url)
            .build()
    }

    companion object {
        const val SOCKET_URL_BASE = "ws://78.153.139.39:80/game/"
    }
}