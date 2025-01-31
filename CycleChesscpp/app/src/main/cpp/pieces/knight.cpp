#include "knight.h"

Knight::Knight(const Position& position, const Color& color) :
    EqualStepsPiece(position, color, PieceType::KNIGHT) {}

Knight::Offsets Knight::getSteps(const Board& board) const {
    Knight::Offsets offsets;

    for (const Offset& step : steps) {
        if (!board.hasPieceSameColor(position, step, color)) {
            safeInsertOffset(offsets, step);
        }
    }

    return offsets;
}
