#pragma once

#include "board.h"

class Game {
public:
    using PBoard = std::shared_ptr<Board>;
    // TODO мб сделать пару из константных позиций
    using TwoPositions = std::pair<Position, Position>;
    using PositionAndPieceType = std::pair<Position, PieceType>;

    Game();

    void startGame(const Color& mainColor);

    void endGame();

    Color getCurrentTurn() const;

    void startGameWithFen(const Color mainColor, std::string fen);

    void startGameWithReversedFen(const Color mainColor, std::string fen);

    Board::Route getPossibleMovesForPosition(const Position& position) const;

    MoveType tryDoMove(const TwoPositions& positions);

    MoveType tryDoMove(const Position& position, const Position& newPosition);

    bool canDoOneStepAndDrawByFiftyMoves() const;

    bool canDoPassant() const;

    Board::Moves getAllPossibleMoves() const;

    // TODO тут вооще говоря может вернуться и плохая позиция (короля может не быть??)
    Position getKingPositionByColor(const Color& color) const;

    std::string getFen() const;

    GameState getGameState() const;

    bool tryDoMagicPawnTransformation(const PositionAndPieceType& positionAndPieceType);

    bool tryDoMagicPawnTransformation(const Position& position, const PieceType& type);

    Board::BoardRepresentation getBoardRepresentation();

private:
    PBoard board;
    Board::PHistory history;
    Board::PHistoryRecordManager historyRecordManager;

    void defaultCreatingOrClearingBoard(const Color mainColor);
};
