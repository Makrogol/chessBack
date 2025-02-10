#pragma once

#include <vector>


enum PieceType {
    EMPTY,
    PAWN,
    KING,
    ROOK,
    BISHOP,
    QUEEN,
    KNIGHT,
};

using PieceTypes = std::vector<PieceType>;

PieceTypes getAllPieceTypes();
PieceTypes getPieceTypesForPromotion();

char toFen(const PieceType type);
PieceType getPieceTypeFromFen(char fenElement);
