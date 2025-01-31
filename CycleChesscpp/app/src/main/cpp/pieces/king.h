// #pragma once
// #ifndef KING_H
// #define KING_H
#include "piece.h"

#include <vector>

class King : public Piece {
public:
    King(const Position& position, const Color& color);

private:
    Offsets getRouteImpl(const Board& board) const final;
    Offsets getAttackRouteImpl(const Board& board) const final;


    const std::vector<Offset> steps = {
        Offset(-1, 1),
        Offset(-1, 0),
        Offset(-1, -1),

        Offset(1, 1),
        Offset(1, 0),
        Offset(1, -1),

        Offset(0, 1),
        Offset(0, -1),
    };
};

// #endif // KING_H
