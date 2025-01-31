#pragma once

#include <string>
#include <vector>

#include "utils/color.h"
#include "utils/piece_type.h"
#include "utils/position.h"

class Unparser {
public:
    using String = std::string;
    using Strings = std::vector<String>;
    using TwoPositions = std::pair<Position, Position>;
    using PositionAndPieceType = std::pair<Position, PieceType>;

    // под вопросом делать ли что-то для старта и окончания игры

    static TwoPositions getPositionsToMove(const String& positionsString);

    static Position getPositionToPossibleMove(const String& positionString);

    static PositionAndPieceType getPositionAndPieceTypeForMagicPawnTransformation(const String& positionAndPieceTypeString);

    static Color getColor(const String& color);

    static Position getPosition(const String& positionString);


private:
    static Strings split(const String& splitString, const String& splitCharacter);

};