package com.serebryakov.cyclechesscpp.application.repository.cppconnectionrepository

import com.serebryakov.cyclechesscpp.application.model.game.GameColor
import com.serebryakov.cyclechesscpp.application.model.game.GameState
import com.serebryakov.cyclechesscpp.application.model.game.Move
import com.serebryakov.cyclechesscpp.application.model.game.MoveType
import com.serebryakov.cyclechesscpp.application.model.game.PieceType
import com.serebryakov.cyclechesscpp.application.model.game.Position
import com.serebryakov.cyclechesscpp.application.model.game.Route
import com.serebryakov.cyclechesscpp.foundation.model.Repository

interface CppConnectionRepository : Repository {
    fun getKingPositionByColor(color: GameColor): Position

    fun startGame(mainColor: GameColor)

    fun startGameWithFen(mainColor: GameColor, fen: String)

    fun startGameWithReversedFen(mainColor: GameColor, fen: String)

    fun getPossibleMovesForPosition(position: Position): Route

    fun tryDoMove(positions: Pair<Position, Position>): MoveType

    fun tryDoMove(positionFirst: Position, positionSecond: Position): MoveType

    fun tryDoMoveV2(move: Move): MoveType

    fun getFen(): String

    fun getCurrentTurnColor(): GameColor

    fun getGameState(): GameState

    fun tryDoMagicPawnTransformation(position: Position, pieceType: PieceType): Boolean

    fun endGame()
}
