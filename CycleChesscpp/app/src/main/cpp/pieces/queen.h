// #pragma once
// #ifndef QUEEN_H
// #define QUEEN_H
#include "linear_steps_piece.h"

class Queen : public LinearStepsPiece {
public:
    Queen(const Position& position, const Color& color);

private:
    // TODO Попробовать прописать везде прагмы и отнаследовать от ладьи и слона
    Lines getQueenLines() const;
    Lines getQueenLinesOverBoard() const;

    Lines getLines() const final {
        return getQueenLines();
    }

    Lines getLinesOverBoard() const final {
        return getQueenLinesOverBoard();
    }

    Positions getStartsOfAllLines() const override;
};

// #endif // QUEEN_H
