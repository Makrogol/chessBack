// #pragma once
// #ifndef PAWN_H
// #define PAWN_H
#include "piece.h"

#include <vector>

class Pawn : public Piece {
public:
    Pawn(const Position& position, const Color& color);

    bool canDoBigStep() const {
        return countSteps == 0;
    }

private:
    // Если это пешка основного игрока (который прямо на телефон смотрит)
    // то двигаем их вниз, иначе ввех. Это специфичная штука только для пешек

    Offsets getRouteImpl(const Board& board) const final;
    Offsets getAttackRouteImpl(const Board& board) const final;
};

// #endif // PAWN_H
