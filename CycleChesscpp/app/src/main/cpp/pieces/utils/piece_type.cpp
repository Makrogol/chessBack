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

PieceType getPieceTypeFromFen(char fenElement) {
    if (fenElement == 'b' || fenElement == 'B') {
        return PieceType::BISHOP;
    }
    if (fenElement == 'k' || fenElement == 'K') {
        return PieceType::KING;
    }
    if (fenElement == 'q' || fenElement == 'Q') {
        return PieceType::QUEEN;
    }
    if (fenElement == 'r' || fenElement == 'R') {
        return PieceType::ROOK;
    }
    if (fenElement == 'n' || fenElement == 'N') {
        return PieceType::KNIGHT;
    }
    if (fenElement == 'p' || fenElement == 'P') {
        return PieceType::PAWN;
    }
    // TODO error need log
    return PieceType::EMPTY;
}

PieceTypes getPieceTypesForPromotion() {
    return {
        PieceType::BISHOP,
        PieceType::KNIGHT,
        PieceType::ROOK,
        PieceType::QUEEN,
    };
}