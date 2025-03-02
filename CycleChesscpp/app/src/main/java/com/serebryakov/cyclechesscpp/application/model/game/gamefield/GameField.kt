package com.serebryakov.cyclechesscpp.application.model.game.gamefield

import com.serebryakov.cyclechesscpp.application.model.cppapi.CppConnectionApiImpl
import com.serebryakov.cyclechesscpp.application.model.game.BasePiece
import com.serebryakov.cyclechesscpp.application.model.game.CellColor
import com.serebryakov.cyclechesscpp.application.model.game.GameColor
import com.serebryakov.cyclechesscpp.application.model.game.GameState
import com.serebryakov.cyclechesscpp.application.model.game.MoveType
import com.serebryakov.cyclechesscpp.application.model.game.PieceColor
import com.serebryakov.cyclechesscpp.application.model.game.PieceType
import com.serebryakov.cyclechesscpp.application.model.game.Route
import com.serebryakov.cyclechesscpp.application.model.game.Position
import com.serebryakov.cyclechesscpp.application.repository.cppconnectionrepository.CppConnectionRepositoryImpl

// [0][0] - левый верхний угол
class GameField(val gameColor: GameColor) {
    private var field = Field()
    // TODO это тоже не очень хорошо. как будто надо так, чтобы зависимости были все в одном месте
    private val cppApi = CppConnectionApiImpl()
    private val cppConnectionRepository = CppConnectionRepositoryImpl(cppApi)
    private var currentActivePiecePosition = Position()
    
    // region API connection
    fun getKingPositionByColor(color: GameColor): Position {
        return cppConnectionRepository.getKingPositionByColor(color)
    }

    fun startGame(mainColor: GameColor) {
        field.generateEmptyField()
        field.setPieceToStartPosition(gameColor)
        return cppConnectionRepository.startGame(mainColor)
    }

    fun getPossibleMovesForPosition(position: Position): Route {
        return cppConnectionRepository.getPossibleMovesForPosition(position)
    }

    fun tryDoMove(positions: Pair<Position, Position>): MoveType {
        val resultDoMove = cppConnectionRepository.tryDoMove(positions) 
        when (resultDoMove) {
            MoveType.NOT_SPECIAL -> {
                movePiece(positions.second)
            }

            MoveType.MAGIC_PAWN_TRANSFORMATION -> {
                movePiece(positions.second)
            }

            MoveType.PASSANT -> {
                doPassant(positions.second)
            }

            MoveType.CASTLING -> {
                doCastling(positions.second)
            }

            MoveType.NOT_MOVE -> {
                // TODO error need log
            }
        }
        
        return resultDoMove
    }

    fun tryDoMove(positionFirst: Position, positionSecond: Position): MoveType {
        return tryDoMove(Pair(positionFirst, positionSecond))
    }

    fun getGameState(): GameState {
        return cppConnectionRepository.getGameState()
    }

    fun tryDoMagicPawnTransformation(position: Position, pieceType: PieceType): Boolean {
        val resultApi = cppConnectionRepository.tryDoMagicPawnTransformation(position, pieceType)
        val resultField = magicPawnTransformation(position, pieceType)
        return resultApi && resultField
    }

    fun endGame() {
        return cppConnectionRepository.endGame()
    }
    //endregion
    
    // region Field connection
    private fun movePiece(newPosition: Position) {
        field.movePiece(currentActivePiecePosition, newPosition)
    }

    private fun magicPawnTransformation(position: Position, pieceType: PieceType): Boolean {
        return field.doMagicPawnTransformation(position, pieceType)
    }

    private fun doPassant(newPosition: Position) {
        field.doPassant(currentActivePiecePosition, newPosition)
    }

    private fun doCastling(newPosition: Position) {
        field.doCastling(currentActivePiecePosition, newPosition)
    }

    fun movePiece(position: Position, newPosition: Position) {
        field.movePiece(position, newPosition)
    }

    fun hasPiece(position: Position): Boolean {
        return field.hasPiece(position)
    }

    fun hasPiece(i: Int, j: Int): Boolean {
        return hasPiece(Position(i, j))
    }

    fun getPieceColor(position: Position): PieceColor {
        // TODO сделать норм возвращаемое значение, чтобы без !!
        return field.getPiece(position)!!.color
    }
    
    fun getCellColorByPosition(position: Position): CellColor {
        return field[position].color
    }
    
    fun getPieceByPosition(position: Position): BasePiece? {
        return field[position].piece
    }
    // endregion
    
    // region getter/setter
    fun getCurrentActivePosition() = currentActivePiecePosition
    
    fun setCurrentActivePosition(newPosition: Position) {
        currentActivePiecePosition = newPosition
    }
    
    // endregion
}
