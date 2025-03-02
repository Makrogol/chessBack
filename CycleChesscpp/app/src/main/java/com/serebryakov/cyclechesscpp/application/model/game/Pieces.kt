package com.serebryakov.cyclechesscpp.application.model.game


open class BasePiece {
    open var position = Position()
    open var color = PieceColor.noColor
    open var type: PieceType = PieceType.EMPTY
    open var countSteps = 0

    fun changePosition(newPosition: Position) {
        position = newPosition
    }

    fun onPieceDoStep() {
        countSteps += 1
    }

    fun canDoStepsOverBoard() = countSteps > 2

    fun countSteps() = countSteps
}

// region Pieces
class BishopPiece(override var color: PieceColor, override var position: Position) : BasePiece() {
    override var type: PieceType = PieceType.BISHOP
}

class KingPiece(override var color: PieceColor, override var position: Position) : BasePiece() {
    override var type: PieceType = PieceType.KING
}

class KnightPiece(override var color: PieceColor, override var position: Position) : BasePiece() {
    override var type: PieceType = PieceType.KNIGHT
}

class PawnPiece(override var color: PieceColor, override var position: Position) : BasePiece() {
    override var type: PieceType = PieceType.PAWN
}

class QueenPiece(override var color: PieceColor, override var position: Position) : BasePiece() {
    override var type: PieceType = PieceType.QUEEN
}

class RookPiece(override var color: PieceColor, override var position: Position) : BasePiece() {
    override var type: PieceType = PieceType.ROOK
}

// endregion
