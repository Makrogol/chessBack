#include "rook.h"

Rook::Rook(const Position& position, const Color& color) :
    LinearStepsPiece(position, color, PieceType::ROOK) {}

Rook::Lines Rook::getRookLines() const {
    return {
        createLine(1, 0),
        createLine(-1, 0),
        createLine(0, -1),
        createLine(0, 1),
    };
}

Rook::Lines Rook::getRookLinesOverBoard() const {
    return {
        createLineOverBoard(1, 0),
        createLineOverBoard(-1, 0),
        createLineOverBoard(0, -1),
        createLineOverBoard(0, 1),
    };
}

Rook::Positions Rook::getStartsOfAllLines() const {
    return {
        getStartOfLine(1, 0),
        getStartOfLine(-1, 0),
        getStartOfLine(0, -1),
        getStartOfLine(0, 1),
    };
}
