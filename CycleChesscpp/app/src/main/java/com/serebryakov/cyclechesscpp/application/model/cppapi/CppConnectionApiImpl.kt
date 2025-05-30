package com.serebryakov.cyclechesscpp.application.model.cppapi

class CppConnectionApiImpl: CppConnectionApi {
    override fun getKingPositionByColor(color: String): String {
        return getKingPositionByColorCpp(color)
    }

    override fun startGame(mainColorString: String) {
        startGameCpp(mainColorString)
    }

    override fun startGameWithFen(mainColorString: String, fenString: String) {
        startGameWithFenCpp(mainColorString, fenString)
    }

    override fun startGameWithReversedFen(mainColorString: String, fenString: String) {
        startGameWithReversedFenCpp(mainColorString, fenString)
    }

    override fun getPossibleMovesForPosition(positionString: String): String {
        return getPossibleMovesForPositionCpp(positionString)
    }

    override fun tryDoMove(positionsString: String): String {
        return tryDoMoveCpp(positionsString)
    }

    override fun tryDoMoveV2(moveString: String): String {
        return tryDoMoveV2Cpp(moveString)
    }

    override fun getGameState(): String {
        return getGameStateCpp()
    }

    override fun getFen(): String {
        return getFenCpp()
    }

    override fun getCurrentTurnColor(): String {
        return getCurrentTurnColorCpp()
    }

    override fun tryDoMagicPawnTransformation(positionAndPieceTypeString: String): String {
        return tryDoMagicPawnTransformationCpp(positionAndPieceTypeString)
    }

    override fun endGame() {
        return endGameCpp()
    }
}


