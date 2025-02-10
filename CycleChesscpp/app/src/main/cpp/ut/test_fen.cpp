#include <iostream>
#include <gtest/gtest.h>
#include "game.h"
#include "piece.h"

class TestFen : public testing::Test {
private:
    using PGame = std::shared_ptr<Game>;
    // Вот это надо переделать на моки соответствующих классов
    Board::PHistory history = std::make_shared<History>();
    Board::PHistoryRecordManager historyRecordManager = std::make_shared<HistoryRecordManager>();
public:
    Game::PBoard board;
    PGame game;

    void SetUp(const Color mainColor = Color::WHITE) {
        game = std::make_shared<Game>();
        // board = std::make_shared<Board>(mainColor, history, historyRecordManager);
        // board->createDefaultField();
    }

    void TearDown() {
        game.reset();
        // board.reset();
    }
};


TEST_F(TestFen, DefaultFen) {
    // Создаем дефолтное поле и проверяем
    // Что у него нормально генерится фен
    SetUp(Color::WHITE);

    game->startGame(Color::WHITE);
    std::cout << "default fen" << std::endl;
    std::cout << game->getFen() << std::endl;

    game->tryDoMove(Position(1, 3), Position(3, 3));
    std::cout << "fen with passant cell" << std::endl;
    std::cout << game->getFen() << std::endl;

    game->tryDoMove(Position(6, 0), Position(5, 0));
    std::cout << "fen without all right passant" << std::endl;
    std::cout << game->getFen() << std::endl;
    game->tryDoMove(Position(0, 4), Position(1, 4));
    std::cout << game->getFen() << std::endl;

    game->tryDoMove(Position(7, 0), Position(6, 0));
    std::cout << "fen without any right passant" << std::endl;
    std::cout << game->getFen() << std::endl;
}

TEST_F(TestFen, FromFen) {
    SetUp();
    game->startGameWithFen(Color::WHITE, "r0n0b0q0k0b0n0r0/p0p0p0p0p0p0p0p0/8/8/8/8/P0P0P0P0P0P0P0P0/R0N0B0Q0K0B0N0R0 w KQkq - 0 0");
    std::cout << game->getCurrentTurn() << std::endl;
    std::cout << "default fen" << std::endl;
    std::cout << game->getFen() << std::endl;

    game->tryDoMove(Position(6, 6), Position(4, 6));

    game->tryDoMove(Position(1, 3), Position(3, 3));
    game->tryDoMove(Position(6, 1), Position(5, 1));

    game->tryDoMove(Position(3, 3), Position(4, 3));
    game->tryDoMove(Position(6, 4), Position(4, 4));
    std::cout << "possible pawns positions" << std::endl;
    for (auto pos : game->getPossibleMovesForPosition(Position(4, 3))) {
        std::cout << pos << " ";
    }
    std::cout << std::endl;
    std::cout << game->getFen() << std::endl;
}

TEST_F(TestFen, PassantFen) {
    SetUp();
    game->startGameWithFen(Color::WHITE, "r0n0b0q0k0b0n0r0/p0p0p01p0p0p0p0/8/8/3p2P11P11/1P16/P01P0P01P01P0/R0N0B0Q0K0B0N0R0 b KQkq 5,4 0 3");
    std::cout << game->getCurrentTurn() << std::endl; 
    std::cout << "possible pawns positions" << std::endl;
    for (auto pos : game->getPossibleMovesForPosition(Position(4, 3))) {
        std::cout << pos << " ";
    }
    std::cout << std::endl;
    std::cout << game->getFen() << std::endl;
}
