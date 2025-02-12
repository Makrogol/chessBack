#pragma once

#include <vector>


enum PieceType {
    EMPTY = 0,
    PAWN = 1,
    KING = 2,
    ROOK = 3,
    BISHOP = 4,
    QUEEN = 5,
    KNIGHT = 6,
};

using PieceTypes = std::vector<PieceType>;

PieceTypes getAllPieceTypes();
PieceTypes getPieceTypesForPromotion();

char toFen(const PieceType type);
PieceType getPieceTypeFromFen(char fenElement);
