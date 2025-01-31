#include <iostream>
#include <gtest/gtest.h>
#include "game.h"
#include "piece.h"

class TestBoard : public testing::Test {
private:
    // Вот это надо переделать на моки соответствующих классов
    Board::PHistory history = std::make_shared<History>();
    Board::PHistoryRecordManager historyRecordManager = std::make_shared<HistoryRecordManager>();
public:
    Game::PBoard board;

    void SetUp(const Color mainColor = Color::WHITE) {
        board = std::make_shared<Board>(mainColor, history, historyRecordManager);
    }

    void TearDown() {
        board.reset();
    }

    void expectPieceNotMove(const Color color, const Position position, const PieceType type) {
        ASSERT_TRUE(board->hasPiece(position));
        Cell::PPiece piece = board->getPieceByPosition(position);
        EXPECT_EQ(piece->getType(), type);
        EXPECT_EQ(piece->getColor(), color);
        EXPECT_EQ(piece->getPosition(), position);
        EXPECT_EQ(piece->getCountSteps(), 0);
        EXPECT_FALSE(piece->canDoStepOverBoard());
    }

    void expectPawnsOnDefaultCells(const Color pawnColor, const int i) {
        // Переделать все эти инты на size_t для индексов
        // записать 8 в какую-нибудь уже настройку или конфиг
        for (int j = 0; j < 8; ++j) {
            expectPieceNotMove(pawnColor, Position(i, j), PieceType::PAWN);
        }
    }

    void expectPiecesOnDefaultCells(const Color color, const int i) {
        // Без пешек
        expectPieceNotMove(color, Position(i, 0), PieceType::ROOK);
        expectPieceNotMove(color, Position(i, 7), PieceType::ROOK);

        expectPieceNotMove(color, Position(i, 1), PieceType::KNIGHT);
        expectPieceNotMove(color, Position(i, 6), PieceType::KNIGHT);

        expectPieceNotMove(color, Position(i, 2), PieceType::BISHOP);
        expectPieceNotMove(color, Position(i, 5), PieceType::BISHOP);
        
        // Проверяем правильность расстановки ферзя и короля
        const Position positionI3(i, 3), positionI4(i, 4);
        Board::PCell cell = board->getCellByPosition(positionI3);
        ASSERT_TRUE(cell);
        PieceType typeI3 = PieceType::KING, typeI4 = PieceType::QUEEN;
        // Поскольку ферзь любит свой цвет, то он стоит на клетке с таким же цветом, как у него
        // Иначе на этой клетке будет стоять король
        if (color == cell->getColor()) {
            typeI3 = PieceType::QUEEN;
            typeI4 = PieceType::KING;
        }
        expectPieceNotMove(color, positionI3, typeI3);
        expectPieceNotMove(color, positionI4, typeI4);
    }

    void expectAllPiecesOnDefaultCells(const Color mainColor) {
        const Color anotherColor = getAnotherColor(mainColor);
        expectPiecesOnDefaultCells(anotherColor, 0);
        expectPawnsOnDefaultCells(anotherColor, 1);

        expectPawnsOnDefaultCells(mainColor, 6);
        expectPiecesOnDefaultCells(mainColor, 7);
    }

    void expectFieldSize() {
        EXPECT_EQ(board->field.size(), 8);
        for (int i = 0; i < 8; ++i) {
            EXPECT_EQ(board->field[i].size(), 8);
        }
    }

    void assertFieldSize() {
        ASSERT_EQ(board->field.size(), 8);
        for (int i = 0; i < 8; ++i) {
            ASSERT_EQ(board->field[i].size(), 8);
        }
    }

    void expectCellColorAndPoistion(const Board::PCell cell, const Color color, const Position position) {
        ASSERT_TRUE(cell);
        EXPECT_EQ(cell->getColor(), color);
        EXPECT_EQ(cell->getPosition(), position);
    }

    void expectCellsOnField() {
        assertFieldSize();

        // Левый верхний угол всегда белый
        // Дальше цвета чередуются
        Position position;
        Color color = Color::WHITE;
        for (int i = 0; i < 8; ++i) {
            color = getAnotherColor(color);
            for (int j = 0; j < 8; ++j) {
                position = Position(i, j);
                color = getAnotherColor(color);
                expectCellColorAndPoistion(board->getCellByPosition(position), color, position);
            }
        }
    }
};



TEST_F(TestBoard, CreateDefaultFieldForBlack) {
    // Создаем дефолтное поле, когда главный цвет - черный
    // и проверяем, что оно правильно создалось
    const Color mainColor = Color::BLACK;
    SetUp(mainColor);

    board->createDefaultField();

    expectAllPiecesOnDefaultCells(mainColor);
}

TEST_F(TestBoard, CreateDefaultFieldForWhite) {
    // Создаем дефолтное поле, когда главный цвет - белый
    // и проверяем, что оно правильно создалось
    const Color mainColor = Color::WHITE;
    SetUp(mainColor);

    board->createDefaultField();

    expectAllPiecesOnDefaultCells(mainColor);
}

TEST_F(TestBoard, CreateField) {
    // Создадим разные виды полей и проверяем, что они всегда
    // создаются нужного размера и с ячейками

    // Цвет не важен
    SetUp();

    // Тут надо проверить только размеры, потому что ячейки не создаются
    board->createEmptyField();
    expectFieldSize();

    // Тут надо все проверять, потому что ячейки уже есть
    board->createEmptyColoredField();
    expectCellsOnField();

    // Тут тоже надо проверять все
    board->createDefaultField();
    expectCellsOnField();
}
