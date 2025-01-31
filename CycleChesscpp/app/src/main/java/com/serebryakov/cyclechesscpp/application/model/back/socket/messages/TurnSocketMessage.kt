package com.serebryakov.cyclechesscpp.application.model.back.socket.messages

import com.google.gson.JsonParser

class TurnSocketMessage: DefaultSocketMessage() {
    var turn: String? = null

    override fun tryToDict(): MutableDict? {
        val message = super.tryToDict() ?: return null
        if (!allFieldFill()) {
            return null
        }
        message[TURN_MESSAGE] = turn!!
        return message
    }

    override fun allFieldFill(): Boolean {
        println(super.allFieldFill())
        return super.allFieldFill() && (turn != null)
    }

    override fun parseMessage(message: String) {
        val data = JsonParser().parse(message).asJsonObject
        if (data.has(TURN_MESSAGE)) {
            turn = data[TURN_MESSAGE].toString().trim('\"')
        }
    }

    companion object {
        val TURN_MESSAGE = "turn"
    }
}