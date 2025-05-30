package com.serebryakov.cyclechesscpp.foundation.socket.utils

import com.serebryakov.cyclechesscpp.foundation.socket.message.SocketMessage

interface SocketMessageUtils {

    fun <T : SocketMessage> tryFromString(message: String, socketMessage: T)

    fun <T : SocketMessage> isMessageAllFieldFill(message: T): Boolean

    fun <T : SocketMessage> toString(message: T): String

    fun <T : SocketMessage> clearMessage(message: T)
}
