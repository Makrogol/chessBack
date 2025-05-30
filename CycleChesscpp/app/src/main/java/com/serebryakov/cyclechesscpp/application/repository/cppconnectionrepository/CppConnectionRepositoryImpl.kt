package com.serebryakov.cyclechesscpp.application.repository.cppconnectionrepository

import com.serebryakov.cyclechesscpp.application.model.cppapi.CppConnectionApi
import com.serebryakov.cyclechesscpp.application.model.cppapi.utils.Parser
import com.serebryakov.cyclechesscpp.application.model.cppapi.utils.Unparser
import com.serebryakov.cyclechesscpp.application.model.game.GameColor
import com.serebryakov.cyclechesscpp.application.model.game.GameState
import com.serebryakov.cyclechesscpp.application.model.game.Move
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

    override fun startGameWithFen(mainColor: GameColor, fen: String) {
        val data = parser.color(mainColor)
        api.startGameWithFen(data, fen)
    }

    override fun startGameWithReversedFen(mainColor: GameColor, fen: String) {
        val data = parser.color(mainColor)
        api.startGameWithReversedFen(data, fen)
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

    override fun tryDoMoveV2(move: Move): MoveType {
        val data = parser.move(move)
        val result = api.tryDoMoveV2(data)
        return unparser.getMoveType(result)
    }

    override fun getFen(): String {
        return api.getFen()
    }

    override fun getCurrentTurnColor(): GameColor {
        return unparser.getColor(api.getCurrentTurnColor())
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
