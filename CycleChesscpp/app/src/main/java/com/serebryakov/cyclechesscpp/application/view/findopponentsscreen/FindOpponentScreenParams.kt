package com.serebryakov.cyclechesscpp.application.view.findopponentsscreen

import com.serebryakov.cyclechesscpp.foundation.socket.listner.BaseWebSocketListener

data class FindOpponentScreenParams(
    val webSocketListener: BaseWebSocketListener? = null,
)
