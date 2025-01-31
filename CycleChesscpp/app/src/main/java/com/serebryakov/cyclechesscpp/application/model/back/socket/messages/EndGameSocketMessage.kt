package com.serebryakov.cyclechesscpp.application.model.back.socket.messages

import com.google.gson.JsonParser

class EndGameSocketMessage: DefaultSocketMessage() {
    var gameEnd: String? = null

    override fun tryToDict(): MutableDict? {
        val message =  super.tryToDict() ?: return null
        if (gameEnd == null) {
            return null
        }
        message[GAME_END_MESSAGE] = gameEnd!!
        return message
    }

    override fun allFieldFill(): Boolean {
        return super.allFieldFill() && (gameEnd != null)
    }

    override fun parseMessage(message: String) {
        val data = JsonParser().parse(message).asJsonObject
        if (data.has(GAME_END_MESSAGE)) {
            gameEnd = data[GAME_END_MESSAGE].toString().trim('\"')
        }
    }

    companion object {
        val GAME_END_MESSAGE = "game_end"
    }
}