#pragma once

#include "utils/piece_type.h"
#include "utils/position.h"

class Move {
public:
    Move(Position positionFirst, Position positionSecond, PieceType promotion):
        positionFirst(positionFirst),
        positionSecond(positionSecond),
        promotion(promotion)
    {}

    Position positionFirst;
    Position positionSecond;
    // Превращение пешки (то есть если мы сходили пешкой
    // на последнюю линию и выбрали в кого будем
    // превращаться это будет вот это поле)
    PieceType promotion;

};
