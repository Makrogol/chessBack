package com.serebryakov.cyclechesscpp.application.model.back.socket


interface WebSocketHolder {

    suspend fun openSocket(username: String)

    suspend fun sendMessage(message: String)

    suspend fun closeSocket()

    fun isSocketExist(): Boolean
}
