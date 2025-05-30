package com.serebryakov.cyclechesscpp.application.repository.socketrepository

import com.serebryakov.cyclechesscpp.application.model.back.socket.WebSocketHolder
import com.serebryakov.cyclechesscpp.foundation.socket.utils.SocketMessageUtils
import com.serebryakov.cyclechesscpp.foundation.socket.utils.SocketMessageUtilsImpl
import com.serebryakov.cyclechesscpp.foundation.model.IoDispatcher
import com.serebryakov.cyclechesscpp.foundation.socket.message.SentSocketMessage
import kotlinx.coroutines.withContext

class SocketRepositoryImpl(
    private val socketHolder: WebSocketHolder,
    private val ioDispatcher: IoDispatcher,
    private val socketMessageUtils: SocketMessageUtils = SocketMessageUtilsImpl()
) : SocketRepository {

    override suspend fun openSocket(username: String) = withContext(ioDispatcher.value) {
        socketHolder.openSocket(username)
    }

    override suspend fun sendMessage(message: String) = withContext(ioDispatcher.value) {
        socketHolder.sendMessage(message)
    }

    override suspend fun sendMessage(message: SentSocketMessage) = withContext(ioDispatcher.value) {
        if (socketMessageUtils.isMessageAllFieldFill(message)) {
            socketHolder.sendMessage(socketMessageUtils.toString(message))
        }
    }

    override suspend fun closeSocket() = withContext(ioDispatcher.value) {
        socketHolder.closeSocket()
    }

    override fun isSocketExist(): Boolean = socketHolder.isSocketExist()
}
