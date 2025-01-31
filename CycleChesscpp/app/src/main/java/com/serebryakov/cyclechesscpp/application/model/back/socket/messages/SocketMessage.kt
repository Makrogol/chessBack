package com.serebryakov.cyclechesscpp.application.model.back.socket.messages

typealias MutableDict = MutableMap<String, String>
typealias Dict = Map<String, String>

interface SocketMessage {

    fun tryToString(): String?

    fun tryToDict(): MutableDict?

    fun tryFillFromString(message: String)

    fun allFieldFill(): Boolean

}