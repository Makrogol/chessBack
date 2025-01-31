#pragma once

#include "parser.h"
#include "unparser.h"

#include "game.h"

class Main {
public:
    using String = std::string;

    Main() = default;

    void startGame(const String& mainColorString);

    void endGame();

    String getCurrentTurn();

    void startGameWithFen(const String& mainColorString, const String& fen);

    String getPossibleMovesForPosition(const String& positionString);

    String getKingPositionByColor(const String& colorString);

    String tryDoMove(const String& positionsString);

    String getGameState();

    String tryDoMagicPawnTransformation(const String& positionAndPieceTypeString);

    String getBoardRepresentation();

private:
    Game game;
};