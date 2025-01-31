package com.serebryakov.cyclechesscpp.application.model.cppapi

interface CppConnectionApi {
    fun getKingPositionByColor(color: String): String

    fun startGame(mainColorString: String): String

    fun getPossibleMovesForPosition(positionString: String): String

    fun tryDoMove(positionsString: String): String

    fun getGameState(): String

    fun tryDoMagicPawnTransformation(positionAndPieceTypeString: String): String

    fun endGame()
}