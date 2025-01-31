package com.serebryakov.cyclechesscpp.application.model.user

import com.serebryakov.cyclechesscpp.application.model.game.GameColor
import com.serebryakov.cyclechesscpp.foundation.socket.BaseWebSocketListener

data class StartGameData(
    val username: String,
    val opponentUsername: String,
    val color: GameColor,
    val webSocketListener: BaseWebSocketListener,
    val useSocket: Boolean
)
