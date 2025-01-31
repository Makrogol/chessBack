#pragma once
// #ifndef EQUAL_STEPS_PIECE_H
// #define EQUAL_STEPS_PIECE_H
#include "piece.h"

// Фигуры, у которых поля на которые они могут сходить и поля, которые они бьют одинаковые
class EqualStepsPiece : public Piece {
public:
    EqualStepsPiece(const Position& position, const Color color, const PieceType type) :
        Piece(position, color, type) {}

protected:
    virtual Offsets getSteps(const Board& board) const = 0;

private:
    Offsets getRouteImpl(const Board& board) const final {
        return getSteps(board);
    }

    Offsets getAttackRouteImpl(const Board& board) const final {
        return getSteps(board);
    }
};

// #endif // EQUAL_STEPS_PIECE_H
