package com.serebryakov.cyclechesscpp.application.model.game

enum class PieceType {
    EMPTY,
    PAWN,
    KING,
    ROOK,
    BISHOP,
    QUEEN,
    KNIGHT,
}

fun pieceTypeToString(pieceType: PieceType): String {
    return when(pieceType) {
        PieceType.EMPTY -> "0"
        PieceType.PAWN -> "1"
        PieceType.KING -> "2"
        PieceType.ROOK -> "3"
        PieceType.BISHOP -> "4"
        PieceType.QUEEN -> "5"
        PieceType.KNIGHT -> "6"
    }
}
