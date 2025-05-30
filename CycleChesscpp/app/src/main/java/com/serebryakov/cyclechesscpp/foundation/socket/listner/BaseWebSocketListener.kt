package com.serebryakov.cyclechesscpp.foundation.socket.listner

import com.serebryakov.cyclechesscpp.foundation.socket.message.ReceivedSocketMessage
import com.serebryakov.cyclechesscpp.foundation.socket.utils.SocketMessageUtils
import com.serebryakov.cyclechesscpp.foundation.socket.utils.SocketMessageUtilsImpl
import com.serebryakov.cyclechesscpp.foundation.views.WebSocketViewModel
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

abstract class BaseWebSocketListener(
    private var viewModel: WebSocketViewModel? = null,
    private val socketMessageUtils: SocketMessageUtils = SocketMessageUtilsImpl()
): WebSocketListener() {
    abstract val allReceivedMessages: List<ReceivedSocketMessage>

    fun setViewModel(newViewModel: WebSocketViewModel) {
        viewModel = newViewModel
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        println("onOpen")
        viewModel?.setSocketStatus(true)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        println("onMessage message receive socket $text")
        for (message in allReceivedMessages) {
            socketMessageUtils.clearMessage(message)
            socketMessageUtils.tryFromString(text, message)
            if (socketMessageUtils.isMessageAllFieldFill(message)) {
                viewModel?.setSocketMessage(message)
                break
            }
        }
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        println("onFailure ${t.message}")
        super.onFailure(webSocket, t, response)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        println("onClosed")
        viewModel?.setSocketStatus(false)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        println("onClosing")
        super.onClosing(webSocket, code, reason)
    }
}