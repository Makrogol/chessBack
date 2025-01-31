package com.serebryakov.cyclechesscpp.application.model.cppapi

class CppConnectionApiImpl: CppConnectionApi {
    override fun getKingPositionByColor(color: String): String {
        return getKingPositionByColorCpp(color)
    }

    override fun startGame(mainColorString: String): String {
        return startGameCpp(mainColorString)
    }

    override fun getPossibleMovesForPosition(positionString: String): String {
        return getPossibleMovesForPositionCpp(positionString)
    }

    override fun tryDoMove(positionsString: String): String {
        return tryDoMoveCpp(positionsString)
    }

    override fun getGameState(): String {
        return getGameStateCpp()
    }

    override fun tryDoMagicPawnTransformation(positionAndPieceTypeString: String): String {
        return tryDoMagicPawnTransformationCpp(positionAndPieceTypeString)
    }

    override fun endGame() {
        return endGameCpp()
    }
}


