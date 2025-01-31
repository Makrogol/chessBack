#pragma once

enum MoveType {
    CASTLING, // рокировка
    PASSANT, // взятие на проходе
    MAGIC_PAWN_TRANSFORMATION, // превращение пешки
    NOT_SPECIAL, // обычный ход
    NOT_MOVE, // ошибка
};
