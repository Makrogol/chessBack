#pragma once

#include <iostream>
#include <string>
#include <vector>

#include "offset.h"

class Position {
public:
    using Route = std::vector<Position>;

    Position() = default;

    Position(int i, int j);

    int getI() const {
        return i;
    }

    int getJ() const {
        return j;
    }

    // TODO вынести в cpp
    bool operator==(const Position& position) const {
        return (i == position.i) && (j == position.j);
    }

    bool operator!=(const Position& position) const {
        return !(*this == position);
    }

    bool moreJ(const Position& position) const {
        return this->j > position.j;
    }

    std::string toString() const {
        return std::to_string(i) + "," + std::to_string(j);
    }


friend std::ostream& operator<<(std::ostream& os, const Position& position);
private:
    int i = 0;
    int j = 0;

};

// TODO это надо в какие-то утили вынести
Position MakeOffset(const Position& position, const Offset& offset);
bool isStepOverBoard(const Position& position, const Offset& offset);
bool isStepInBorder(const Position& position, const Offset& offset);
bool isPositionsNear(const Position& firstPosition, const Position& secondPosition);
// TODO мб ренейн на InsideBoard
bool isPositionInBoard(const Position& position);
// Геометрическое расстояние - корень из суммы квадратов разности координат
float distBetweenPosition(const Position& firstPosition, const Position& secondPosition);
Position::Route getHorizontalRouteBetweenPositions(const Position& firstPosition, const Position& secondPosition);
// Проверяем распложение позиций на то, пригодны они для взятия на проходе или нет
// firstPosition - позиция, на которой стоит пешка, которая атакует
// secondPosition - позиция, куда пешка пытается атаковать
// attackPosition - позиция, на которой стоит пешка, которую пытаемся взять на проходе
bool isPositionsGoodForPassant(const Position& firstPosition, const Position& secondPosition, const Position& attackPosition);
bool isPositionGoodForMagicTransformation(const Position& position);
Offset getOffsetBetweenPositions(const Position& firstPosition, const Position& secondPosition);
// TODO костыль, переделать
bool isStepInBorderByMove(const Position& position, const Offset& offset, const int i, const int j);
int getIDeltaBetweenPositions(const Position& firstPosition, const Position& secondPosition);
int getJDeltaBetweenPositions(const Position& firstPosition, const Position& secondPosition);
Position fromString(const std::string& positionString);
