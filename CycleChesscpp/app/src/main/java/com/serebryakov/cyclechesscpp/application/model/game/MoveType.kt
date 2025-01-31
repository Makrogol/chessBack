package com.serebryakov.cyclechesscpp.application.model.game

enum class MoveType {
    CASTLING, // рокировка
    PASSANT, // взятие на проходе
    MAGIC_PAWN_TRANSFORMATION, // превращение пешки
    NOT_SPECIAL, // обычный ход
    NOT_MOVE, // ошибка
}

fun String.ToMoveType(): MoveType {
    return when(this.toInt()) {
        0 -> MoveType.CASTLING
        1 -> MoveType.PASSANT
        2 -> MoveType.MAGIC_PAWN_TRANSFORMATION
        3 -> MoveType.NOT_SPECIAL
        4 -> MoveType.NOT_MOVE
        else -> {
            MoveType.NOT_MOVE
        }
    }
}