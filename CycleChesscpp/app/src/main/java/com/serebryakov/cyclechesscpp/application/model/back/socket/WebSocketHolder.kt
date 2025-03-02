package com.serebryakov.cyclechesscpp.application.model.back.socket

import com.serebryakov.cyclechesscpp.foundation.socket.BaseWebSocketListener

interface WebSocketHolder {

    suspend fun openSocket(webSocketListener: BaseWebSocketListener, username: String)

    suspend fun sendMessage(message: String)

    suspend fun closeSocket()

}