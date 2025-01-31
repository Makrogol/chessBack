#include "main.h"

// #include <android/log.h>


void Main::startGame(const String& mainColorString) {
    const Color mainColor = Unparser::getColor(mainColorString);
    game.startGame(mainColor);
}

void Main::endGame() {
    game.endGame();
}

void Main::startGameWithFen(const String& mainColorString, const String& fen) {
    const Color mainColor = Unparser::getColor(mainColorString);
    game.startGameWithFen(mainColor, fen);
}

Main::String Main::getPossibleMovesForPosition(const String& positionString) {
    const Position position = Unparser::getPositionToPossibleMove(positionString);
    Board::Route possibleMoves = game.getPossibleMovesForPosition(position);
    return Parser::possibleMoves(possibleMoves);
}

Main::String Main::getCurrentTurn() {
    const Color turmColor = game.getCurrentTurn();
    return Parser::color(turmColor);
}

Main::String Main::getKingPositionByColor(const String& colorString) {
    const Color color = Unparser::getColor(colorString);
    const Position kingPosition = game.getKingPositionByColor(color);
    return Parser::positionToString(kingPosition);
}

Main::String Main::tryDoMove(const String& positionsString) {
    const Unparser::TwoPositions positions = Unparser::getPositionsToMove(positionsString);
    MoveType resultDoMove = game.tryDoMove(positions);
    return Parser::moveType(resultDoMove);
}

Main::String Main::getBoardRepresentation() {
    return Parser::boardRepresentation(game.getBoardRepresentation());
}

Main::String Main::getGameState() {
    return Parser::gameState(game.getGameState());
}

Main::String Main::tryDoMagicPawnTransformation(const String& positionAndPieceTypeString) {
    const Unparser::PositionAndPieceType positionAndPieceType = Unparser::getPositionAndPieceTypeForMagicPawnTransformation(positionAndPieceTypeString);
    bool resultDoMagicPawnTransformation = game.tryDoMagicPawnTransformation(positionAndPieceType);
    return Parser::resultDoMagicTransformation(resultDoMagicPawnTransformation);
}
