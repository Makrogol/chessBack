package com.serebryakov.cyclechesscpp.application.model.back.socket.messages

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.serebryakov.cyclechesscpp.application.model.user.StartGameData

open class DefaultSocketMessage : SocketMessage {
    var username: String? = null
    var opponentUsername: String? = null

    override fun tryToString(): String? {
        val message = tryToDict() ?: return null
        val result = JsonObject()
        message.forEach { element ->
            result.addProperty(element.key, element.value)
        }
        return result.toString()
    }

    override fun tryToDict(): MutableDict? {
        val message = mutableMapOf<String, String>()
        if (!allFieldFill()) {
            return null
        }
        message[USERNAME_MESSAGE] = username!!
        message[OPPONENT_USERNAME_MESSAGE] = opponentUsername!!
        return message
    }

    override fun tryFillFromString(message: String) {
        val data = JsonParser().parse(message).asJsonObject
        if (data.has(USERNAME_MESSAGE) && data.has(OPPONENT_USERNAME_MESSAGE)) {
            username = data[USERNAME_MESSAGE].toString().trim('\"')
            opponentUsername = data[OPPONENT_USERNAME_MESSAGE].toString().trim('\"')
        }
        parseMessage(message)
    }

    override fun allFieldFill(): Boolean {
        return (username != null) && (opponentUsername != null)
    }

    fun fillFromDefaultMessage(defaultSocketMessage: DefaultSocketMessage) {
        username = defaultSocketMessage.username
        opponentUsername = defaultSocketMessage.opponentUsername
    }

    fun fillFromStartGameData(startGameData: StartGameData) {
        username = startGameData.username
        opponentUsername = startGameData.opponentUsername
    }

    fun fillFromStringAndDefaultMessage(
        message: String,
        defaultSocketMessage: DefaultSocketMessage
    ) {
        username = defaultSocketMessage.username
        opponentUsername = defaultSocketMessage.opponentUsername
        parseMessage(message)
    }

    open fun parseMessage(message: String) = Unit

    companion object {
        val USERNAME_MESSAGE = "username"
        val OPPONENT_USERNAME_MESSAGE = "opponent_username"
    }
}