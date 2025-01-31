// #pragma once
// #ifndef BISHOP_H
// #define BISHOP_H
#include "linear_steps_piece.h"

class Bishop : public LinearStepsPiece {
public:
    Bishop(const Position& position, const Color& color);

protected:
    Lines getBishopLines() const;
    Lines getBishopLinesOverBoard() const;

    Lines getLines() const override {
        return getBishopLines();
    }

    Lines getLinesOverBoard() const override {
        return getBishopLinesOverBoard();
    }

    Positions getStartsOfAllLines() const override;
};

// #endif // BISHOP_H
