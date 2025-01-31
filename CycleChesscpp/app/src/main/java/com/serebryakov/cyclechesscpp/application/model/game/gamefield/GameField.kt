package com.serebryakov.cyclechesscpp.application.model.game.gamefield

import com.serebryakov.cyclechesscpp.application.model.game.GameColor
import com.serebryakov.cyclechesscpp.application.model.game.PieceColor
import com.serebryakov.cyclechesscpp.application.model.game.PieceType
import com.serebryakov.cyclechesscpp.application.model.game.Route
import com.serebryakov.cyclechesscpp.application.model.game.Position

// [0][0] - левый верхний угол
// фигура может ходить назад только после того, как она сделала первый ход
// (возможно надо будет добавить такую возможность после 2 или 3 ходов)
class GameField(val gameColor: GameColor) {
    private var field = Field()
    var currentActivePiecePosition = Position()

    init {
        field.generateEmptyField()
        field.setPieceToStartPosition(gameColor)
    }

    fun movePiece(newPosition: Position) {
      field.movePiece(currentActivePiecePosition, newPosition)
    }

    fun magicPawnTransformation(position: Position, pieceType: PieceType): Boolean {
        return field.magicPawnTransformation(position, pieceType)
    }

    fun doPassant(newPosition: Position) {
        field.doPassant(currentActivePiecePosition, newPosition)
    }

    fun doCastling(newPosition: Position) {
        field.doCastling(currentActivePiecePosition, newPosition)
    }

    fun movePiece(position: Position, newPosition: Position) {
        field.movePiece(position, newPosition)
    }

    fun hasPiece(position: Position) : Boolean {
        return field.hasPiece(position)
    }

    fun hasPiece(i: Int, j: Int) : Boolean {
        return hasPiece(Position(i, j))
    }

    fun getPieceColor(position: Position): PieceColor {
        // TODO сделать норм возвращаемое значение, чтобы без !!
        return field.getPiece(position)!!.color
    }

    fun getCurrentActivePosition() = currentActivePiecePosition

    fun getField() = field
}