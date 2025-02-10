#pragma once

#include <string>
#include <vector>

#include "string_utils.h"

#include "move.h"

#include "utils/color.h"
#include "utils/piece_type.h"
#include "utils/position.h"

class Unparser {
public:
    using TwoPositions = std::pair<Position, Position>;
    using PositionAndPieceType = std::pair<Position, PieceType>;

    // под вопросом делать ли что-то для старта и окончания игры

    static TwoPositions getPositionsToMove(const String& positionsString);

    static Position getPositionToPossibleMove(const String& positionString);

    static Move getMove(const String& moveString);

    static PositionAndPieceType getPositionAndPieceTypeForMagicPawnTransformation(const String& positionAndPieceTypeString);

    static Color getColor(const String& color);

    static Position getPosition(const String& positionString);

};