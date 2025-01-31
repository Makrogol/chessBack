#include "bishop.h"

Bishop::Bishop(const Position& position, const Color& color) : LinearStepsPiece(position, color, PieceType::BISHOP) {
}

Bishop::Lines Bishop::getBishopLines() const {
    return {
        createLine(1, 1),
        createLine(-1, -1),
        createLine(1, -1),
        createLine(-1, 1),
    };
}

Bishop::Lines Bishop::getBishopLinesOverBoard() const {
    return {
        createLineOverBoard(1, 1),
        createLineOverBoard(-1, -1),
        createLineOverBoard(1, -1),
        createLineOverBoard(-1, 1),
    };
}

Bishop::Positions Bishop::getStartsOfAllLines() const {
    return {
        getStartOfLine(1, 1),
        getStartOfLine(-1, -1),
        getStartOfLine(1, -1),
        getStartOfLine(-1, 1),
    };
}
