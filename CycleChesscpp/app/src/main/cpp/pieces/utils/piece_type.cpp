#include "piece_type.h"

PieceTypes getAllPieceTypes() {
    return {
        PieceType::BISHOP,
        PieceType::KING,
        PieceType::KNIGHT,
        PieceType::PAWN,
        PieceType::QUEEN,
        PieceType::ROOK
    };
}

char toFen(const PieceType type) {
    switch (type)
    {
    case PieceType::BISHOP:
        return 'b';
    case PieceType::KING:
        return 'k';
    case PieceType::QUEEN:
        return 'q';
    case PieceType::ROOK:
        return 'r';
    case PieceType::KNIGHT:
        return 'n';
    case PieceType::PAWN:
        return 'p';
    default:
        // error need log
        return ' ';
    }
}