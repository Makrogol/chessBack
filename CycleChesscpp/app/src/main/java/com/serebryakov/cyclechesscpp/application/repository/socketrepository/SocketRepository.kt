package com.serebryakov.cyclechesscpp.application.repository.socketrepository

import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.Dict
import com.serebryakov.cyclechesscpp.application.model.back.socket.messages.SocketMessage
import com.serebryakov.cyclechesscpp.foundation.model.Repository
import com.serebryakov.cyclechesscpp.foundation.socket.BaseWebSocketListener

interface SocketRepository: Repository {

    suspend fun createSocket(webSocketListener: BaseWebSocketListener, username: String)

    suspend fun sendMessage(message: String)

    suspend fun sendMessage(message: SocketMessage)

    suspend fun deleteWebSocket()

}