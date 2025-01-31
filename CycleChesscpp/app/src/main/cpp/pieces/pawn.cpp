#include "pawn.h"


Pawn::Pawn(const Position& position, const Color& color) : Piece(position, color, PieceType::PAWN) {
}

// Взятие на проходе в классе с мувами
Pawn::Offsets Pawn::getRouteImpl(const Board& board) const {
    Pawn::Offsets offsets;
    // TODO убрать дублирование (тут и в getAttackRouteImpl)
    int iOffset = color == board.getMainColor() ? -1 : 1;
    const std::vector<Offset> atatckSteps = {
        Offset(iOffset, -1),
        Offset(iOffset, 1),
    };

    const Offset step(iOffset, 0);
    const Offset bigStep(2 * iOffset, 0);

    if (!board.hasPiece(position, step)) {
        safeInsertOffset(offsets, step);
    }

    if (canDoBigStep() && !board.hasPiece(position, step) && !board.hasPiece(position, bigStep)) {
        safeInsertOffset(offsets, bigStep);
    }

    for (const Offset& step : atatckSteps) {
        if (!board.hasPieceSameColor(position, step, color) && board.hasPieceAnotherColor(position, step, color)) {
            safeInsertOffset(offsets, step);
        }
    }

    return offsets;
}

Pawn::Offsets Pawn::getAttackRouteImpl(const Board& board) const {
    Pawn::Offsets offsets;

    int iOffset = color == board.getMainColor() ? -1 : 1;
    const std::vector<Offset> atatckSteps = {
        Offset(iOffset, -1),
        Offset(iOffset, 1),
    };

    for (const Offset& step : atatckSteps) {
        if (!board.hasPieceSameColor(position, step, color)) {
            safeInsertOffset(offsets, step);
        }
    }

    return offsets;
}
