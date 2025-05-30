package com.serebryakov.cyclechesscpp.application.model.game

class Move(
    var positionFirst: Position,
    var positionSecond: Position,
    var promotion: PieceType = PieceType.EMPTY,
) {
    override fun toString(): String {
        var result = "$positionFirst.$positionSecond"
        if (promotion != PieceType.EMPTY) {
            result += ".${pieceTypeToString(promotion)}"
        }
        return result
    }
}