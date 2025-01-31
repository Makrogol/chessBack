// #pragma once
// #ifndef KNIGHT_H
// #define KNIGHT_H
#include "equal_steps_piece.h"

#include <vector>

class Knight : public EqualStepsPiece {
public:
    Knight(const Position& position, const Color& color);

private:
    Offsets getSteps(const Board& board) const final;


    std::vector<Offset> steps = {
        Offset(1, 2),
        Offset(2, 1),

        Offset(2, -1),
        Offset(1, -2),

        Offset(-2, -1),
        Offset(-1, -2),

        Offset(-2, 1),
        Offset(-1, 2)
    };
};

// #endif // KNIGHT_H
