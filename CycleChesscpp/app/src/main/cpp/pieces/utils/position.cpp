#include "position.h"

#include <cmath>

#include "string_utils.h"


Position::Position(int i, int j): i(i), j(j) {}

Position MakeOffset(const Position& position, const Offset& offset) {
    // Делаем так, будто разрешен выход за границы
    int newI = (position.getI() + offset.getI() + 8) % 8;
    int newJ = (position.getJ() + offset.getJ() + 8) % 8;
    return {newI, newJ};
}

bool isStepOverBoard(const Position& position, const Offset& offset) {
    const bool iOverBoard = (position.getI() + offset.getI() < 0) || (position.getI() + offset.getI() >= 8);
    const bool jOverBoard = (position.getJ() + offset.getJ() < 0) || (position.getJ() + offset.getJ() >= 8);
    return iOverBoard || jOverBoard;
}

bool isStepInBorder(const Position& position, const Offset& offset) {
    const bool iInBorder = (position.getI() + offset.getI() == 0) || (position.getI() + offset.getI() == 7);
    const bool jInBorder = (position.getJ() + offset.getJ() == 0) || (position.getJ() + offset.getJ() == 7);
    return iInBorder || jInBorder;
}

bool isPositionsNear(const Position& firstPosition, const Position& secondPosition) {
    const bool iIsNear = std::abs(firstPosition.getI() - secondPosition.getI()) <= 1;
    const bool jIsNear = std::abs(firstPosition.getJ() - secondPosition.getJ()) <= 1;
    return iIsNear && jIsNear;
}

bool isPositionInBoard(const Position& position) {
    const bool iInBoard = (position.getI() >= 0) && (position.getI() < 8);
    const bool jInBoard = (position.getJ() >= 0) && (position.getJ() < 8);
    return iInBoard && jInBoard;
}

float distBetweenPosition(const Position& firstPosition, const Position& secondPosition) {
    const float deltaI = std::pow(firstPosition.getI() - secondPosition.getI(), 2);
    const float deltaJ = std::pow(firstPosition.getJ() - secondPosition.getJ(), 2);
    return std::sqrt(deltaI + deltaJ);
}

Position::Route getHorizontalRouteBetweenPositions(const Position& firstPosition, const Position& secondPosition) {
    // Из-за специфики board j - столбцы, i - строки
    Position::Route route;
    // const int iIteration = firstPosition.getJ() > secondPosition.getI() ? -1 : 1;
    // for (int i = 1; i <= std::abs(firstPosition.getJ() - secondPosition.getJ()) - 1; ++i) {
    //     route.push_back(Position(firstPosition.getI(), firstPosition.getJ() + i * iIteration));
    // }

    // Ожидается, что i одинаковое
    const int i = firstPosition.getI();
    const int minJ = std::min(firstPosition.getJ(), secondPosition.getJ());
    const int maxJ = std::max(firstPosition.getJ(), secondPosition.getJ());
    for (int j = minJ + 1; j < maxJ; ++j) {
        route.emplace_back(i, j);
    }
    return route;
}

bool isPositionsGoodForPassant(const Position& firstPosition, const Position& secondPosition, const Position& attackPosition) {
    // Изначально пешки стоят на одной строке и на соседних столбцах
    bool iFirstGoodForPassant = firstPosition.getI() == attackPosition.getI();
    bool jFirstGoodForPassant = std::abs(firstPosition.getJ() - attackPosition.getJ()) <= 1 && (firstPosition.getJ() != attackPosition.getJ());

    // Поле, которая пешка атакует должно быть на том же столбце, что и поле атакованной пешке и на соседней строке
    const bool iSecondGoodForPassant = std::abs(secondPosition.getI() - attackPosition.getI()) <= 1 && (secondPosition.getI() != attackPosition.getI());
    const bool jSecondGoodForPassant = secondPosition.getJ() == attackPosition.getJ();

    return iFirstGoodForPassant && jFirstGoodForPassant && iSecondGoodForPassant && jSecondGoodForPassant;
}

bool isPositionGoodForMagicTransformation(const Position& position) {
    bool iGoodForMagicTransformation = (position.getI() == 0) || (position.getI() == 7);
    return iGoodForMagicTransformation;
}

// Если мы к firstPosition применим полученный оффсет, то должна получится secondPosition
Offset getOffsetBetweenPositions(const Position& firstPosition, const Position& secondPosition) {
    // TODO без учета, что мы можем выйти за границы, мне это не нравится, мб учитывать, что можем выйти за границы и не допускать этого
    // А вообще думать о том, выходим мы за границы или нет по идеи этот класс не должен
    const int iOffset = firstPosition.getI() - secondPosition.getI();
    const int jOffset = firstPosition.getJ() - secondPosition.getJ();
    return {iOffset, jOffset};
}

bool isStepInBorderByMove(const Position& position, const Offset& offset, const int i, const int j) {
    if (i == 0) {
        return (position.getJ() + offset.getJ() == 0) || (position.getJ() + offset.getJ() == 7);
    }
    if (j == 0) {
        return (position.getI() + offset.getI() == 0) || (position.getI() + offset.getI() == 7);
    }
    return isStepInBorder(position, offset);
}

int getIDeltaBetweenPositions(const Position& firstPosition, const Position& secondPosition) {
    if (firstPosition.getJ() != secondPosition.getJ()) {
        // TODO error need log
        return 0;
    }
    return secondPosition.getI() - firstPosition.getI();
}

int getJDeltaBetweenPositions(const Position& firstPosition, const Position& secondPosition) {
    if (firstPosition.getI() != secondPosition.getI()) {
        // TODO error need log
        return 0;
    }
    return secondPosition.getJ() - firstPosition.getJ();
}

Position fromString(const String& positionString) {
    if (positionString.size() != 3) {
        // TODO error need log
        return Position(0, 0);
    }
    Strings positionsElements = split(positionString, ",");
    if (positionsElements.size() != 2) {
        // TODO error need log
        return Position(0, 0);
    }
    return Position(stringToInt(positionsElements[0]), stringToInt(positionsElements[1]));
}

std::ostream& operator<<(std::ostream& os, const Position& position) {
    os << "position:" << position.i << "," << position.j;
    return os;
}
