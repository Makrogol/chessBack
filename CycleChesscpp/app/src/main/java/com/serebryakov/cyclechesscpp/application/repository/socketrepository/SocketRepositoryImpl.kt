package com.serebryakov.cyclechesscpp.application.repository.socketrepository

import com.serebryakov.cyclechesscpp.application.model.back.socket.WebSocketHolder
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.SocketMessage
import com.serebryakov.cyclechesscpp.foundation.model.IoDispatcher
import com.serebryakov.cyclechesscpp.foundation.socket.BaseWebSocketListener
import kotlinx.coroutines.withContext

class SocketRepositoryImpl(
    private val socketHolder: WebSocketHolder,
    private val ioDispatcher: IoDispatcher,
): SocketRepository {

    override suspend fun createSocket(webSocketListener: BaseWebSocketListener, username: String) = withContext(ioDispatcher.value) {
        socketHolder.createWebSocket(webSocketListener, username)
    }

    override suspend fun sendMessage(message: String) = withContext(ioDispatcher.value) {
        socketHolder.sendMessage(message)
    }

    override suspend fun sendMessage(message: SocketMessage) = withContext(ioDispatcher.value) {
        val stringMessage = message.tryToString()
        if (stringMessage != null) {
            socketHolder.sendMessage(stringMessage)
        }
    }

    override suspend fun deleteWebSocket() = withContext(ioDispatcher.value) {
        socketHolder.deleteWebSocket()
    }

}