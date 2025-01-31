package com.serebryakov.cyclechesscpp.foundation.socket

import com.serebryakov.cyclechesscpp.foundation.views.WebSocketViewModel
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class BaseWebSocketListener(
    private var viewModel: WebSocketViewModel? = null
): WebSocketListener() {

    fun setViewModel(newViewModel: WebSocketViewModel) {
        viewModel = newViewModel
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        viewModel?.setSocketStatus(true)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        println(text)
        viewModel?.setSocketMessage(text)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        viewModel?.setSocketStatus(false)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
    }
}