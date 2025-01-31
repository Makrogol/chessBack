#include "king.h"

King::King(const Position& position, const Color& color) :
    Piece(position, color, PieceType::KING) {}

// Рокировка в классе с мувами
King::Offsets King::getRouteImpl(const Board& board) const {
    King::Offsets offsets;

    for (const Offset& step : steps) {
        if (!board.hasPieceSameColor(position, step, color) && !board.hasKingAnotherColorNear(position, step, color)) {
            safeInsertOffset(offsets, step);
        }
    }

    return offsets;
}

King::Offsets King::getAttackRouteImpl(const Board& board) const {
    King::Offsets offsets;

    for (const Offset& step : steps) {
        if (!board.hasPieceSameColor(position, step, color)) {
            safeInsertOffset(offsets, step);
        }
    }

    return offsets;
}