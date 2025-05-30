package com.serebryakov.cyclechesscpp.foundation.socket.utils

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.serebryakov.cyclechesscpp.foundation.socket.MessageField
import com.serebryakov.cyclechesscpp.foundation.socket.message.SocketMessage
import com.serebryakov.cyclechesscpp.foundation.socket.toTrimmedString

typealias OnMessageFieldIterate = (field: MessageField) -> Unit

class SocketMessageUtilsImpl : SocketMessageUtils {
    override fun <T : SocketMessage> tryFromString(message: String, socketMessage: T) {
        val data = JsonParser().parse(message).asJsonObject
        iterateByMessageField(socketMessage) { field ->
            if (data.has(field.name)) {
                field.value = data[field.name].toTrimmedString()
            }
        }
    }

    override fun <T : SocketMessage> isMessageAllFieldFill(message: T): Boolean {
        var isFieldsFill = true
        iterateByMessageField(message) { field ->
            if (field.value == null) {
                isFieldsFill = false
                return@iterateByMessageField
            }
        }
        return isFieldsFill
    }

    override fun <T : SocketMessage> toString(message: T): String {
        val result = JsonObject()
        iterateByMessageField(message) { field ->
            result.addProperty(field.name, field.value)
        }
        return result.toString()
    }

    override fun <T : SocketMessage> clearMessage(message: T) {
        iterateByMessageField(message) { field ->
            field.value = null
        }
    }

    private fun <T : SocketMessage> iterateByMessageField(
        message: T,
        onMessageFieldIterate: OnMessageFieldIterate
    ) {
        for (field in message::class.java.declaredFields) {
            field.isAccessible = true
//            val name = field.name
            val value = field.get(message)
            if (value is MessageField) {
                onMessageFieldIterate(value)
            } else {
                // TODO error need log
            }
        }
    }
}
