package com.serebryakov.cyclechesscpp.application.model.cppapi

interface CppConnectionApi {
    fun getKingPositionByColor(color: String): String

    fun startGame(mainColorString: String)

    fun startGameWithFen(mainColorString: String, fenString: String)

    fun startGameWithReversedFen(mainColorString: String, fenString: String)

    fun getPossibleMovesForPosition(positionString: String): String

    fun tryDoMove(positionsString: String): String

    fun tryDoMoveV2(moveString: String): String

    fun getGameState(): String

    fun getFen(): String

    fun getCurrentTurnColor(): String

    fun tryDoMagicPawnTransformation(positionAndPieceTypeString: String): String

    fun endGame()
}
