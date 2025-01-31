#pragma once

#include "board.h"

class Parser {
public:
    using Route = std::vector<Position>;
    using String = std::string;
    using Strings = std::vector<String>;

    static String color(const Color color);

    static String possibleMoves(const Route& route);

    static String gameState(const GameState& state);

    static String resultDoMagicTransformation(const bool result);

    static String positionToString(const Position& position);

    static String moveType(const MoveType& moveType);

    static String boardRepresentation(const Board::BoardRepresentation& boardRepresentation);

    static String pieceTypeAndColor(const Board::PieceTypeAndColor& pieceTypeAndColor);

private:
    // TODO это как будто можно вынести в утили
    static String join(const String& joinCharacter, const Strings& joinStrings);

};