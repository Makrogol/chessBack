package com.serebryakov.cyclechesscpp.application.repository.socketrepository

import com.serebryakov.cyclechesscpp.foundation.model.Repository
import com.serebryakov.cyclechesscpp.foundation.socket.message.SentSocketMessage

interface SocketRepository: Repository {

    suspend fun openSocket(username: String)

    suspend fun sendMessage(message: String)

    suspend fun sendMessage(message: SentSocketMessage)

    suspend fun closeSocket()

    fun isSocketExist(): Boolean
}
