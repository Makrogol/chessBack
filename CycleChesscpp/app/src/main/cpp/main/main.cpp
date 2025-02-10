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

String Main::getPossibleMovesForPosition(const String& positionString) {
    const Position position = Unparser::getPositionToPossibleMove(positionString);
    Board::Route possibleMoves = game.getPossibleMovesForPosition(position);
    return Parser::possibleMoves(possibleMoves);
}

String Main::getCurrentTurn() {
    const Color turmColor = game.getCurrentTurn();
    return Parser::color(turmColor);
}

String Main::getKingPositionByColor(const String& colorString) {
    const Color color = Unparser::getColor(colorString);
    const Position kingPosition = game.getKingPositionByColor(color);
    return Parser::positionToString(kingPosition);
}

String Main::canDoOneStepAndDrawByFiftyMoves() {
    return Parser::resultCanDoOneMoveAndDrawByFiftyMoves(game.canDoOneStepAndDrawByFiftyMoves());
}

String Main::canDoPassant() {
    return Parser::resultCanDoPassant(game.canDoPassant());
}

String Main::allPossibleMoves() {
    return Parser::allPossibleMoves(game.getAllPossibleMoves());
}

String Main::getFen() {
    return game.getFen();
}

String Main::tryDoMove(const String& positionsString) {
    const Unparser::TwoPositions positions = Unparser::getPositionsToMove(positionsString);
    MoveType resultDoMove = game.tryDoMove(positions);
    return Parser::moveType(resultDoMove);
}

String Main::tryDoMoveV2(const String& moveString) {
    const Move move = Unparser::getMove(moveString);
    // TODO перенести эту логику в game и в целом перейти на мувы вместо пар позишионов
    MoveType resultDoMove = game.tryDoMove(move.positionFirst, move.positionSecond);
    if (resultDoMove == MoveType::MAGIC_PAWN_TRANSFORMATION && move.promotion != PieceType::EMPTY) {
        bool result = game.tryDoMagicPawnTransformation(move.positionSecond, move.promotion);
        if (result) {
            resultDoMove = MoveType::NOT_SPECIAL;
        } else {
            resultDoMove = MoveType::NOT_MOVE;
        }
    }
    return Parser::moveType(resultDoMove);
}

String Main::getBoardRepresentation() {
    return Parser::boardRepresentation(game.getBoardRepresentation());
}

String Main::getGameState() {
    return Parser::gameState(game.getGameState());
}

String Main::tryDoMagicPawnTransformation(const String& positionAndPieceTypeString) {
    const Unparser::PositionAndPieceType positionAndPieceType = Unparser::getPositionAndPieceTypeForMagicPawnTransformation(positionAndPieceTypeString);
    bool resultDoMagicPawnTransformation = game.tryDoMagicPawnTransformation(positionAndPieceType);
    return Parser::resultDoMagicTransformation(resultDoMagicPawnTransformation);
}
