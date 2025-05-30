package com.serebryakov.cyclechesscpp.application.model.data

import com.serebryakov.cyclechesscpp.application.model.game.GameColor

data class StartGameData(
    var username: String,
    var opponentUsername: String,
    var mainColor: GameColor,
    var useSocket: Boolean,
    var fen: String?,
    var isOpponentTurn: Boolean,
    var isSwitchedColor: Boolean,
    var isPlayWithBot: Boolean,
)
