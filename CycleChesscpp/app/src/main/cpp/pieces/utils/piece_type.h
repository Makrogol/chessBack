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

char toFen(const PieceType type);
