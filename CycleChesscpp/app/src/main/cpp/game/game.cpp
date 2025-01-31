#include "game.h"

Game::Game() {}

void Game::startGame(const Color& mainColor) {
    history = std::make_shared<History>();
    historyRecordManager = std::make_shared<HistoryRecordManager>();
    board = std::make_shared<Board>(mainColor, history, historyRecordManager);
    board->createDefaultField();
}

void Game::endGame() {
}

Board::Route Game::getPossibleMovesForPosition(const Position& position) const {
    return board->getPossiblePositionsToMove(position);
}

Position Game::getKingPositionByColor(const Color& color) const {
    return board->getKingPositionByColor(color);
}

Color Game::getCurrentTurn() const {
    return board->getCurrentTurn();
}

void Game::startGameWithFen(const Color mainColor, std::string fen) {
    if (board) {
        board->clearField();
    } else {
        history = std::make_shared<History>();
        historyRecordManager = std::make_shared<HistoryRecordManager>();
        board = std::make_shared<Board>(mainColor, history, historyRecordManager);
    }
    board->createFieldFromFen(fen);
}

MoveType Game::tryDoMove(const Position& position, const Position& newPosition) {
    const MoveType moveType = board->tryMovePiece(position, newPosition);
    if (moveType != MoveType::MAGIC_PAWN_TRANSFORMATION) {
        // Если мы делаем магическое превращение, то записывать в историю будем потом
        // в tryDoMagicPawnTransformation
        history->addHistoryRecord(historyRecordManager->getRecord());
    }
    return moveType;
}

Board::BoardRepresentation Game::getBoardRepresentation() {
    return board->getBoardRepresentation();
}

MoveType Game::tryDoMove(const TwoPositions& positions) {
    return tryDoMove(positions.first, positions.second);
}

bool Game::tryDoMagicPawnTransformation(const Position& position, const PieceType& type) {
    if (board->tryDoMagicPawnTransformation(position, type)) {
        history->addHistoryRecord(historyRecordManager->getRecord());
        return true;
    }
    return false;
}

bool Game::tryDoMagicPawnTransformation(const PositionAndPieceType& positionAndPieceType) {
    return tryDoMagicPawnTransformation(positionAndPieceType.first, positionAndPieceType.second);
}

GameState Game::getGameState() const {
    return board->getGameState();
}
