#pragma once
#include "linear_steps_piece.h"

class Rook : public LinearStepsPiece {
public:
    Rook(const Position& position, const Color& color);

protected:
    Lines getRookLines() const;
    Lines getRookLinesOverBoard() const;

    Lines getLines() const override {
        return getRookLines();
    }

    Lines getLinesOverBoard() const override {
        return getRookLinesOverBoard();
    }

    Positions getStartsOfAllLines() const override;
};

// #endif // ROOK_H
