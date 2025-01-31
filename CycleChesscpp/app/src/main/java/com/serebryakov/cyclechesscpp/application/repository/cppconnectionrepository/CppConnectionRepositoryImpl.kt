package com.serebryakov.cyclechesscpp.application.repository.cppconnectionrepository

import com.serebryakov.cyclechesscpp.application.model.cppapi.CppConnectionApi
import com.serebryakov.cyclechesscpp.application.model.cppapi.cpptools.Parser
import com.serebryakov.cyclechesscpp.application.model.cppapi.cpptools.Unparser
import com.serebryakov.cyclechesscpp.application.model.game.GameColor
import com.serebryakov.cyclechesscpp.application.model.game.GameState
import com.serebryakov.cyclechesscpp.application.model.game.MoveType
import com.serebryakov.cyclechesscpp.application.model.game.PieceType
import com.serebryakov.cyclechesscpp.application.model.game.Position
import com.serebryakov.cyclechesscpp.application.model.game.Route

class CppConnectionRepositoryImpl(
    private val api: CppConnectionApi
): CppConnectionRepository {
    private val parser = Parser()
    private val unparser = Unparser()

    override fun getKingPositionByColor(color: GameColor): Position {
        val data = parser.color(color)
        val result = api.getKingPositionByColor(data)
        return unparser.getPosition(result)
    }

    override fun startGame(mainColor: GameColor) {
        val data = parser.color(mainColor)
        api.startGame(data)
    }

    override fun getPossibleMovesForPosition(position: Position): Route {
        val data = parser.positionToPossibleMove(position)
        val result = api.getPossibleMovesForPosition(data)
        return unparser.getPossibleMoves(result)
    }

    override fun tryDoMove(positions: Pair<Position, Position>): MoveType {
        val data = parser.positionsToMove(positions)
        val result = api.tryDoMove(data)
        return unparser.getMoveType(result)
    }

    override fun tryDoMove(positionFirst: Position, positionSecond: Position): MoveType {
        return tryDoMove(Pair(positionFirst, positionSecond))
    }

    override fun getGameState(): GameState {
        val result = api.getGameState()
        return unparser.getGameState(result)
    }

    override fun tryDoMagicPawnTransformation(position: Position, pieceType: PieceType): Boolean {
        val data = parser.positionAndPieceTypeForMagicPawnTransformation(position, pieceType)
        val result = api.tryDoMagicPawnTransformation(data)
        return unparser.getResultDoMagicTransformation(result)
    }

    override fun endGame() {
        api.endGame()
    }

}