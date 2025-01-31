package com.serebryakov.cyclechesscpp.application.model.back.socket

import com.serebryakov.cyclechesscpp.foundation.socket.BaseWebSocketListener

interface WebSocketHolder {

    suspend fun createWebSocket(webSocketListener: BaseWebSocketListener, username: String)

    suspend fun sendMessage(message: String)


    suspend fun deleteWebSocket()

}