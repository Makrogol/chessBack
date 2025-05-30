#pragma once

#include "parser.h"
#include "unparser.h"

#include "game.h"

class Main {
public:
    Main() = default;

    // TODO приписать сюда константы к методам
    // У которых это можно сделать (у всех?)

    void startGame(const String& mainColorString);

    void endGame();

    String getCurrentTurn();

    void startGameWithFen(const String& mainColorString, const String& fen);

    void startGameWithReversedFen(const String& mainColorString, const String& fen);

    String getPossibleMovesForPosition(const String& positionString);

    String getKingPositionByColor(const String& colorString);

    String canDoOneStepAndDrawByFiftyMoves();

    String canDoPassant();

    String allPossibleMoves();

    String tryDoMove(const String& positionsString);

    // TODO переименовать
    String tryDoMoveV2(const String& moveString);

    String getGameState();

    String getFen();

    String tryDoMagicPawnTransformation(const String& positionAndPieceTypeString);

    String getBoardRepresentation();

private:
    Game game;
};