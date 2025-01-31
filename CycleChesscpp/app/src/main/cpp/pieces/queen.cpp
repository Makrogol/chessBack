#include "queen.h"

Queen::Queen(const Position& position, const Color& color) :
    LinearStepsPiece(position, color, PieceType::QUEEN) {}

Queen::Lines Queen::getQueenLines() const {
    return {
        // bishop
        createLine(1, 1),
        createLine(-1, -1),
        createLine(1, -1),
        createLine(-1, 1),

        // rook
        createLine(1, 0),
        createLine(-1, 0),
        createLine(0, -1),
        createLine(0, 1),
    };
}

Queen::Lines Queen::getQueenLinesOverBoard() const {
    return {
        // bishop
        createLineOverBoard(1, 1),
        createLineOverBoard(-1, -1),
        createLineOverBoard(1, -1),
        createLineOverBoard(-1, 1),

        // rook
        createLineOverBoard(1, 0),
        createLineOverBoard(-1, 0),
        createLineOverBoard(0, -1),
        createLineOverBoard(0, 1),
    };
}

Queen::Positions Queen::getStartsOfAllLines() const {
    return {
        // bishop
        getStartOfLine(1, 1),
        getStartOfLine(-1, -1),
        getStartOfLine(1, -1),
        getStartOfLine(-1, 1),

        // rook
        getStartOfLine(1, 0),
        getStartOfLine(-1, 0),
        getStartOfLine(0, -1),
        getStartOfLine(0, 1),
    };
}