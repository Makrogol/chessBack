#pragma once

#include "board.h"

#include "string_utils.h"

class Parser {
public:
    static String color(const Color color);

    static String possibleMoves(const Board::Route& route);

    static String allPossibleMoves(const Board::Moves& moves);

    static String gameState(const GameState& state);

    static String resultDoMagicTransformation(const bool result);

    static String resultCanDoOneMoveAndDrawByFiftyMoves(const bool result);

    static String resultCanDoPassant(const bool result);

    static String positionToString(const Position& position);

    static String moveToString(const Move& move);

    static String moveType(const MoveType& moveType);

    static String boardRepresentation(const Board::BoardRepresentation& boardRepresentation);

    static String pieceTypeAndColor(const Board::PieceTypeAndColor& pieceTypeAndColor);

};