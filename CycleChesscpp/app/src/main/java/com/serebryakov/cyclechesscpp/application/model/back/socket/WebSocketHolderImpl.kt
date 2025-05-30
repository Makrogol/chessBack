package com.serebryakov.cyclechesscpp.application.model.back.socket

import com.serebryakov.cyclechesscpp.foundation.socket.listner.BaseWebSocketListener
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

class WebSocketHolderImpl(
    private var webSocket: WebSocket? = null,
    private val okHttpClient: OkHttpClient = OkHttpClient(),
    private val webSocketListener: BaseWebSocketListener,
) : WebSocketHolder {
    override suspend fun openSocket(username: String) {
        if (webSocket == null) {
            webSocket = okHttpClient.newWebSocket(createRequest(username), webSocketListener)
        }
    }

    override suspend fun sendMessage(message: String) {
        println("socket enable ${webSocket != null}")
        println("socket message send $message")
        webSocket?.send(message)
    }

    override suspend fun closeSocket() {
        webSocket?.close(1000, "Closed manually")
        println("closing websocket")
        webSocket = null
    }

    override fun isSocketExist(): Boolean = webSocket != null

    private fun createRequest(username: String): Request {
        val url = SOCKET_URL_BASE + username
        println("url = $url")
        return Request.Builder()
            .url(url)
            .build()
    }

    companion object {
        const val SOCKET_URL_BASE = "ws://130.193.53.45:80/game/"
    }
}
