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

fun Int.toPieceType(): PieceType {
    return when(this) {
        1 -> PieceType.PAWN
        2 -> PieceType.KING
        3 -> PieceType.ROOK
        4 -> PieceType.BISHOP
        5 -> PieceType.QUEEN
        6 -> PieceType.KNIGHT
        else -> PieceType.EMPTY
    }
}

fun Char.toPieceType(): PieceType {
    return when(this) {
        'b' -> PieceType.BISHOP
        'k' -> PieceType.KING
        'q' -> PieceType.QUEEN
        'r' -> PieceType.ROOK
        'n' -> PieceType.KNIGHT
        'p' -> PieceType.PAWN
        else -> PieceType.EMPTY
    }
}
