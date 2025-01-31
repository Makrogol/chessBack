#include <iostream>
#include <gtest/gtest.h>
#include <gmock/gmock.h>

#include "game.h"
#include "piece.h"
#include "bishop.h"
#include "king.h"
#include "knight.h"
#include "pawn.h"
#include "queen.h"
#include "rook.h"

namespace {
    using PPawn = std::shared_ptr<Pawn>;
    using PBishop = std::shared_ptr<Bishop>;
    using PKing = std::shared_ptr<King>;
    using PKnight = std::shared_ptr<Knight>;
    using PQueen = std::shared_ptr<Queen>;
    using PRook = std::shared_ptr<Rook>;

    using namespace testing;
}

class TestPieces : public testing::Test {
private:
    // Вот это надо переделать на моки соответствующих классов
    Board::PHistory history = std::make_shared<History>();
    Board::PHistoryRecordManager historyRecordManager = std::make_shared<HistoryRecordManager>();
public:
    Game::PBoard board;

    void SetUp(const Color mainColor = Color::WHITE) {
        board = std::make_shared<Board>(mainColor, history, historyRecordManager);
        board->createEmptyColoredField();
    }

    void TearDown() {
        board.reset();
    }

    bool setPiece(const Color color, const Position& position, const PieceType type) {
        return board->setPiece(color, position, type);
    }

    Cell::PPiece getPiece(const Position& position) {
        return board->getPieceByPosition(position);
    }

    void removePiece(const Position& position) {
        return board->removePiece(position);
    }

    void expectPieceNotMove(const Color color, const Position& position, const PieceType type) {
        ASSERT_TRUE(board->hasPiece(position));
        Cell::PPiece piece = board->getPieceByPosition(position);
        EXPECT_EQ(piece->getType(), type);
        EXPECT_EQ(piece->getColor(), color);
        EXPECT_EQ(piece->getPosition(), position);
        EXPECT_EQ(piece->getCountSteps(), 0);
        EXPECT_FALSE(piece->canDoStepOverBoard());
    }

    void expectNoPiece(const Position& position) {
        EXPECT_FALSE(board->hasPiece(position));
    }

    void expectPawnPossibleDoBigMove(const Pawn* pawn) {
        const Position position = pawn->getPosition();
        const Board::Route expectedBigRoute = {
            MakeOffset(position, Offset(-1, 0)),
            MakeOffset(position, Offset(-2, 0)),
        };

        // Пока пешка не двигалась она могла сделать длинный ход
        EXPECT_TRUE(pawn->canDoBigStep());

        EXPECT_EQ(pawn->getRoute(board), expectedBigRoute);
    }

    void expectPawnPossibleDoSmallMove(const Pawn* pawn) {
        const Position position = pawn->getPosition();
        const Board::Route expectedSmallRoute = {
            MakeOffset(position, Offset(-1, 0)),
        };

        EXPECT_FALSE(pawn->canDoBigStep());
        EXPECT_EQ(pawn->getRoute(board), expectedSmallRoute);
    }

    void expectPawnPossibleDoSmallMoveButCanDoBigStep(const Pawn* pawn) {
        const Position position = pawn->getPosition();
        const Board::Route expectedAttackRoute = {
            MakeOffset(position, Offset(-1, 1)),
            MakeOffset(position, Offset(-1, -1)),
        };
        const Board::Route expectedSmallRoute = {
            MakeOffset(position, Offset(-1, 0)),
        };

        EXPECT_TRUE(pawn->canDoBigStep());
        EXPECT_EQ(pawn->getRoute(board), expectedSmallRoute);
    }

    void expectPieceNoMove(const Piece* piece) {
        ASSERT_TRUE(piece);
        EXPECT_EQ(piece->getRoute(board), Board::Route());
    }

    void addKnightMoves(Board::Route& expectedRoute, const Position& position, const bool canDoStepOverBoard) {
        const Piece::Offsets offsets = {
            Offset(1, 2),
            Offset(2, 1),

            Offset(2, -1),
            Offset(1, -2),

            Offset(-2, -1),
            Offset(-1, -2),

            Offset(-2, 1),
            Offset(-1, 2),
        };

        for (const Offset& offset : offsets) {
            if (!canDoStepOverBoard) {
                if (!isStepOverBoard(position, offset)) {
                    expectedRoute.emplace_back(MakeOffset(position, offset));
                }
            } else {
                expectedRoute.emplace_back(MakeOffset(position, offset));
            }
        }
    }

    // iCoeff, jCoeff должны быть +-1, 0
    void addLinearElement(Board::Route& route, const Position& position, int iCoeff, int jCoeff) {
        if ((iCoeff != 1 && iCoeff != -1 && iCoeff != 0) || (jCoeff != 1 && jCoeff != -1 && jCoeff != 0)) {
            return;
        }
        for (int i = 1; i < 8; ++i) {
            Offset offset(iCoeff * i, jCoeff * i);
            Position newPosition = MakeOffset(position, offset);
            if (isStepOverBoard(position, offset)) {
                break;
            }
            if (std::find(route.begin(), route.end(), newPosition) == route.end()) {
                route.emplace_back(newPosition);
            }
        }
    }

    void addDiagonals(Board::Route& route, const Position& position) {
        addLinearElement(route, position, 1, 1);
        addLinearElement(route, position, -1, 1);
        addLinearElement(route, position, 1, -1);
        addLinearElement(route, position, -1, -1);
    }

    void addHorizontals(Board::Route& route, const Position& position) {
        addLinearElement(route, position, 0, 1);
        addLinearElement(route, position, 0, -1);
    }

    void addVerticals(Board::Route& route, const Position& position) {
        addLinearElement(route, position, 1, 0);
        addLinearElement(route, position, -1, 0);
    }

    void addKingMoves(Board::Route& route, const Position& position, const bool canDoStepOverBoard) {
        const Piece::Offsets offsets = {
            Offset(1, 0),
            Offset(1, 1),
            Offset(0, 1),
            Offset(-1, 0),
            Offset(0, -1),
            Offset(1, -1),
            Offset(-1, 1),
            Offset(-1, -1),
        };
        for (const Offset& offset : offsets) {
            if (canDoStepOverBoard || !isStepOverBoard(position, offset)) {
                route.emplace_back(MakeOffset(position, offset));
            }
        }
    }

    void expectAllKnightMoves(const Cell::PPiece knight) {
        const Position position = knight->getPosition();
        Board::Route expectedRoute;
        expectedRoute.reserve(8); // По максимуму
        addKnightMoves(expectedRoute, position, knight->canDoStepOverBoard());
        EXPECT_EQ(knight->getRoute(board), expectedRoute);
    }

    void expectAllBishopMoves(const Cell::PPiece bishop) {
        const Position position = bishop->getPosition();
        Board::Route expectedRoute;
        expectedRoute.reserve(14); // по максимуму 8 - 1 + 8 - 1
        // Мы просто сдвигаемся относительно текущей позиции во все
        // 4 диагональных направления и добавляем их
        addDiagonals(expectedRoute, position);
        EXPECT_THAT(expectedRoute, UnorderedElementsAreArray(bishop->getRoute(board)));
    }

    void expectAllRookMoves(const Cell::PPiece rook) {
        const Position position = rook->getPosition();
        Board::Route expectedRoute;
        expectedRoute.reserve(14); // по максимуму 8 - 1 + 8 - 1
        // Добавляем горизонтали и вертикали
        addVerticals(expectedRoute, position);
        addHorizontals(expectedRoute, position);
        EXPECT_THAT(expectedRoute, UnorderedElementsAreArray(rook->getRoute(board)));
    }

    void expectAllQueenMoves(const Cell::PPiece queen) {
        const Position position = queen->getPosition();
        Board::Route expectedRoute;
        expectedRoute.reserve(28); // по максимуму 8 - 1 + 8 - 1 + 8 - 1 + 8 - 1
        // Добавляем горизонтали и вертикали
        addVerticals(expectedRoute, position);
        addHorizontals(expectedRoute, position);
        addDiagonals(expectedRoute, position);
        EXPECT_THAT(expectedRoute, UnorderedElementsAreArray(queen->getRoute(board)));
    }

    void expectAllKingMoves(const Cell::PPiece king, const bool canDoStepOverBoard) {
        const Position position = king->getPosition();
        Board::Route expectedRoute;
        expectedRoute.reserve(8); // по максимуму 8
        addKingMoves(expectedRoute, position, canDoStepOverBoard);
        EXPECT_THAT(expectedRoute, UnorderedElementsAreArray(king->getRoute(board)));
    }

    void expectAllKnightMovesExcludeSome(const Cell::PPiece knight, const Board::Route& excludesPositions) {
        const Position position = knight->getPosition();
        Board::Route expectedRoute;
        expectedRoute.reserve(8); // По максимуму
        addKnightMoves(expectedRoute, position, knight->canDoStepOverBoard());
        for (const Position& excludePosition: excludesPositions) {
            erase(expectedRoute, excludePosition);
        }
        EXPECT_EQ(knight->getRoute(board), expectedRoute);
    }

    void expectAllBishopMovesExcludeSome(const Cell::PPiece bishop, const Board::Route& excludesPositions) {
        const Position position = bishop->getPosition();
        Board::Route expectedRoute;
        expectedRoute.reserve(14); // по максимуму 8 - 1 + 8 - 1
        // Мы просто сдвигаемся относительно текущей позиции во все
        // 4 диагональных направления и добавляем их
        addDiagonals(expectedRoute, position);
        for (const Position& excludePosition: excludesPositions) {
            erase(expectedRoute, excludePosition);
        }
        EXPECT_THAT(expectedRoute, UnorderedElementsAreArray(bishop->getRoute(board)));
    }

    void expectAllRookMovesExcludeSome(const Cell::PPiece rook, const Board::Route& excludesPositions) {
        const Position position = rook->getPosition();
        Board::Route expectedRoute;
        expectedRoute.reserve(14); // по максимуму 8 - 1 + 8 - 1
        // Добавляем горизонтали и вертикали
        addVerticals(expectedRoute, position);
        addHorizontals(expectedRoute, position);
        for (const Position& excludePosition: excludesPositions) {
            erase(expectedRoute, excludePosition);
        }
        EXPECT_THAT(expectedRoute, UnorderedElementsAreArray(rook->getRoute(board)));
    }

    void expectAllQueenMovesExcludeSome(const Cell::PPiece queen, const Board::Route& excludesPositions) {
        const Position position = queen->getPosition();
        Board::Route expectedRoute;
        expectedRoute.reserve(28); // по максимуму 8 - 1 + 8 - 1 + 8 - 1 + 8 - 1
        // Добавляем горизонтали и вертикали
        addVerticals(expectedRoute, position);
        addHorizontals(expectedRoute, position);
        addDiagonals(expectedRoute, position);
        for (const Position& excludePosition: excludesPositions) {
            erase(expectedRoute, excludePosition);
        }
        EXPECT_THAT(expectedRoute, UnorderedElementsAreArray(queen->getRoute(board)));
    }

    void expectAllKingMovesExcludeSome(const Cell::PPiece king, const Board::Route& excludesPositions) {
        const Position position = king->getPosition();
        Board::Route expectedRoute;
        expectedRoute.reserve(8); // по максимуму 8
        addKingMoves(expectedRoute, position, king->canDoStepOverBoard());
        for (const Position& excludePosition: excludesPositions) {
            erase(expectedRoute, excludePosition);
        }
        EXPECT_THAT(expectedRoute, UnorderedElementsAreArray(king->getRoute(board)));
    }

    void testKnightMovesWithBlockedCellPipeline(
        const Position& position
        , const Board::Route& pawnsPositions
        , const Board::Route& blockedPositions
        , const bool canDoStepOverBoard
    ) {
        // Мы ставим пешки на некоторые поля
        // Но заблокировано может быть больше полей
        // Поэтому нужна отдельная переменная под блочущие фигуры
        // И отдельно под сами заблоченные поля
        const Color color = Color::WHITE;
        const PieceType type = PieceType::KNIGHT;
        setPiece(color, position, type);
        ASSERT_TRUE(board->hasPiece(position));

        // Блокируем некоторые поля для коня
        // Чтобы он мог попасть за пешку только с помощью
        // Умея ходить сквозь край доски
        for (const Position& pawnsPosition : pawnsPositions) {
            setPiece(color, pawnsPosition, PieceType::PAWN);
            ASSERT_TRUE(board->hasPiece(pawnsPosition));
        }

        Cell::PPiece knight = getPiece(position);

        if (canDoStepOverBoard) {
            // Накручиваем ходы
            knight->setCountSteps(3);
            EXPECT_TRUE(knight->canDoStepOverBoard());
        } else {
            EXPECT_FALSE(knight->canDoStepOverBoard());
        }

        // Тут ожидаем все позиции короля
        expectAllKnightMovesExcludeSome(knight, blockedPositions);

        removePiece(position);
        for (const Position& pawnsPosition : pawnsPositions) {
            removePiece(pawnsPosition);
        }
    }

    void testBishopMovesWithBlockedCellPipeline(
        const Position& position
        , const Board::Route& pawnsPositions
        , const Board::Route& blockedPositions
        , const bool canDoStepOverBoard
    ) {
        // TODO Эту и 2 нижеследующих функции надо объединить в одну
        // Точнее общую часть в одну объединить, возможно передавать колбеки

        // Мы ставим пешки на некоторые поля
        // Но заблокировано может быть больше полей
        // Поэтому нужна отдельная переменная под блочущие фигуры
        // И отдельно под сами заблоченные поля

        const Color color = Color::WHITE;
        const PieceType type = PieceType::BISHOP;
            setPiece(color, position, type);
            ASSERT_TRUE(board->hasPiece(position));

        // Блокируем некоторые поля для слона
        // Чтобы он мог попасть за пешку только с помощью
        // Умея ходить сквозь край доски
        for (const Position& pawnsPosition : pawnsPositions) {
            setPiece(color, pawnsPosition, PieceType::PAWN);
            ASSERT_TRUE(board->hasPiece(pawnsPosition));
        }


        Cell::PPiece bishop = getPiece(position);

        if (canDoStepOverBoard) {
            // Накручиваем ходы
            bishop->setCountSteps(3);
            EXPECT_TRUE(bishop->canDoStepOverBoard());
        } else {
            EXPECT_FALSE(bishop->canDoStepOverBoard());
        }

        // Тут ожидаем обе диагонали слона кроме заблоченных полей
        expectAllBishopMovesExcludeSome(bishop, blockedPositions);

        removePiece(position);
        for (const Position& pawnsPosition : pawnsPositions) {
            removePiece(pawnsPosition);
        }
    }

    void testRookMovesWithBlockedCellPipeline(
        const Position& position
        , const Board::Route& pawnsPositions
        , const Board::Route& blockedPositions
        , const bool canDoStepOverBoard
    ) {
        // Мы ставим пешки на некоторые поля
        // Но заблокировано может быть больше полей
        // Поэтому нужна отдельная переменная под блочущие фигуры
        // И отдельно под сами заблоченные поля

        const Color color = Color::WHITE;
        const PieceType type = PieceType::ROOK;
            setPiece(color, position, type);
            ASSERT_TRUE(board->hasPiece(position));

        // Блокируем некоторые поля для ладьи
        // Чтобы она мог попасть за пешку только с помощью
        // Умея ходить сквозь край доски
        for (const Position& pawnsPosition : pawnsPositions) {
            setPiece(color, pawnsPosition, PieceType::PAWN);
            ASSERT_TRUE(board->hasPiece(pawnsPosition));
        }


        Cell::PPiece rook = getPiece(position);

        if (canDoStepOverBoard) {
            // Накручиваем ходы
            rook->setCountSteps(3);
            EXPECT_TRUE(rook->canDoStepOverBoard());
        } else {
            EXPECT_FALSE(rook->canDoStepOverBoard());
        }

        // Тут ожидаем обе диагонали слона кроме заблоченных полей
        expectAllRookMovesExcludeSome(rook, blockedPositions);

        removePiece(position);
        for (const Position& pawnsPosition : pawnsPositions) {
            removePiece(pawnsPosition);
        }
    }

    void testQueenMovesWithBlockedCellPipeline(
        const Position& position
        , const Board::Route& pawnsPositions
        , const Board::Route& blockedPositions
        , const bool canDoStepOverBOard
    ) {
        // Мы ставим пешки на некоторые поля
        // Но заблокировано может быть больше полей
        // Поэтому нужна отдельная переменная под блочущие фигуры
        // И отдельно под сами заблоченные поля
        const Color color = Color::WHITE;
        const PieceType type = PieceType::QUEEN;
        setPiece(color, position, type);
        ASSERT_TRUE(board->hasPiece(position));

        // Блокируем некоторые поля для ферзя
        // Чтобы он мог попасть за пешку только с помощью
        // Умея ходить сквозь край доски
        for (const Position& pawnsPosition : pawnsPositions) {
            setPiece(color, pawnsPosition, PieceType::PAWN);
            ASSERT_TRUE(board->hasPiece(pawnsPosition));
        }


        Cell::PPiece queen = getPiece(position);

        if (canDoStepOverBOard) {
            // Накручиваем ходы
            queen->setCountSteps(3);
            EXPECT_TRUE(queen->canDoStepOverBoard());
        } else {
            EXPECT_FALSE(queen->canDoStepOverBoard());
        }

        // Тут ожидаем обе диагонали слона кроме заблоченных полей
        expectAllQueenMovesExcludeSome(queen, blockedPositions);

        removePiece(position);
        for (const Position& pawnsPosition : pawnsPositions) {
            removePiece(pawnsPosition);
        }
    }

    void testKingMovesWithBlockedCellPipeline(
        const Position& position
        , const Board::Route& pawnsPositions
        , const Board::Route& blockedPositions
        , const bool canDoStepOverBoard
    ) {
        // Мы ставим пешки на некоторые поля
        // Но заблокировано может быть больше полей
        // Поэтому нужна отдельная переменная под блочущие фигуры
        // И отдельно под сами заблоченные поля
        const Color color = Color::WHITE;
        const PieceType type = PieceType::KING;
        setPiece(color, position, type);
        ASSERT_TRUE(board->hasPiece(position));

        // Блокируем некоторые поля для короля
        // Чтобы он мог попасть за пешку только с помощью
        // Умея ходить сквозь край доски
        for (const Position& pawnsPosition : pawnsPositions) {
            setPiece(color, pawnsPosition, PieceType::PAWN);
            ASSERT_TRUE(board->hasPiece(pawnsPosition));
        }

        Cell::PPiece king = getPiece(position);

        if (canDoStepOverBoard) {
            // Накручиваем ходы
            king->setCountSteps(3);
            EXPECT_TRUE(king->canDoStepOverBoard());
        } else {
            EXPECT_FALSE(king->canDoStepOverBoard());
        }

        // Тут ожидаем все позиции короля
        expectAllKingMovesExcludeSome(king, blockedPositions);

        removePiece(position);
        for (const Position& pawnsPosition : pawnsPositions) {
            removePiece(pawnsPosition);
        }
    }
};



TEST_F(TestPieces, CreatePieces) {
    // Проверяем, что все фигуры нормально создаются
    // всех цветов на всех позициях

    // Цвет не важен
    SetUp();
    
    Position position;
    for (const PieceType type : getAllPieceTypes()) {
        for (const Color color : getAllColors()) {
            for (int i = 0; i < 8; ++i) {
                for (int j = 0; j < 8; ++j) {
                    position = Position(i, j);
                    ASSERT_TRUE(setPiece(color, position, type));
                    expectPieceNotMove(color, position, type);
                    removePiece(position);
                    expectNoPiece(position);
                }
            }
        }
    }
}

TEST_F(TestPieces, PawnPossibleMoves) {
    // Проверяем, что пешки могут ходить
    // как задумано из разных позиций

    // TODO зарефакторить этот тест, чтобы не было повторяющихся конструкций

    // Цвет не важен
    SetUp();

    const PieceType type = PieceType::PAWN;
    const Color color = Color::WHITE;

    // Так как поле создано с Color::WHITE
    // то надо далеко создавать фигуры
    for (int i = 2; i < 8; ++i) {
        for (int j = 0; j < 8; ++j) {
            Position position(i, j);
            setPiece(color, position, type);
            ASSERT_TRUE(board->hasPiece(position));
            Pawn* pawn = dynamic_cast<Pawn*>(getPiece(position).get());
            ASSERT_TRUE(pawn);

            // Пока пешка не двигалась она могла сделать длинный ход
            expectPawnPossibleDoBigMove(pawn);

            // Тут сымитировали, что пешка сделала ход
            pawn->onPieceMove();

            // Теперь она не должна мочь делать большой ход
            expectPawnPossibleDoSmallMove(pawn);

            removePiece(position);
        }
    }

    // Теперь поставим пешку на почти край доски
    // Там она может сделать большой ход в теории, но не может на самом деле
    for (int j = 0; j < 8; ++j) {
        Position position(1, j);
        setPiece(color, position, type);
        ASSERT_TRUE(board->hasPiece(position));
        Pawn* pawn = dynamic_cast<Pawn*>(getPiece(position).get());
        ASSERT_TRUE(pawn);

        expectPawnPossibleDoSmallMoveButCanDoBigStep(pawn);

        // Тут сымитировали, что пешка сделала ход
        pawn->onPieceMove();

        // Теперь она не должна мочь делать большой ход
        expectPawnPossibleDoSmallMove(pawn);

        removePiece(position);
    }

    // Теперь поставим пешку на край доски
    // Там она может сделать большой ход в теории, но не может на самом деле
    // делать никакой ход
    for (int j = 0; j < 8; ++j) {
        Position position(0, j);
        setPiece(color, position, type);
        ASSERT_TRUE(board->hasPiece(position));
        Pawn* pawn = dynamic_cast<Pawn*>(getPiece(position).get());
        ASSERT_TRUE(pawn);

        expectPieceNoMove(dynamic_cast<Piece*>(pawn));
        EXPECT_TRUE(pawn->canDoBigStep());
        EXPECT_EQ(pawn->getCountSteps(), 0);

        removePiece(position);
    }

    // Ставим на место большого хода пешки
    // Другую пешку и проверяем,
    // что не можем сделать большой ход
    // Но можем сделать маленький ход
    for (int i = 2; i < 8; ++i) {
        for (int j = 0; j < 8; ++j) {
            Position position(i, j);
            Position blockedPosition(i - 2, j);
            setPiece(color, position, type);
            ASSERT_TRUE(board->hasPiece(position));
            Pawn* pawn = dynamic_cast<Pawn*>(getPiece(position).get());
            ASSERT_TRUE(pawn);

            // Не можем делать ход за границу поля
            EXPECT_FALSE(pawn->canDoStepOverBoard());

            // Ставим блокирующую пешку
            setPiece(color, blockedPosition, type);

            // Должен быть только маленький ход
            expectPawnPossibleDoSmallMoveButCanDoBigStep(pawn);

            removePiece(position);
            removePiece(blockedPosition);
        }
    }

    // Ставим на место малого хода пешки
    // Другую пешку и проверяем,
    // что не можем сделать никакой ход
    for (int i = 2; i < 8; ++i) {
        for (int j = 0; j < 8; ++j) {
            Position position(i, j);
            Position blockedPosition(i - 1, j);
            setPiece(color, position, type);
            ASSERT_TRUE(board->hasPiece(position));
            Pawn* pawn = dynamic_cast<Pawn*>(getPiece(position).get());
            ASSERT_TRUE(pawn);

            // Не можем делать ход за границу поля
            EXPECT_FALSE(pawn->canDoStepOverBoard());
            // Можем в теории сделать большой ход
            EXPECT_TRUE(pawn->canDoBigStep());

            // Ставим блокирующую пешку
            setPiece(color, blockedPosition, type);

            // Никаких ходов быть не должно
            expectPieceNoMove(dynamic_cast<Piece*>(pawn));

            removePiece(position);
            removePiece(blockedPosition);
        }
    }
}

TEST_F(TestPieces, PawnPossibleMovesOverBoard) {
    // Проверяем, что пешки могут ходить
    // как задумано из разных позиций
    // Теперь еще с учетом ходов через край доски

    // TODO зарефакторить этот тест, чтобы не было повторяющихся конструкций

    // Цвет не важен
    SetUp();
    
    const PieceType type = PieceType::PAWN;
    const Color color = Color::WHITE;

    // Так как поле создано с Color::WHITE
    // то надо далеко создавать фигуры
    for (int i = 2; i < 8; ++i) {
        for (int j = 0; j < 8; ++j) {
            Position position(i, j);
            setPiece(color, position, type);
            ASSERT_TRUE(board->hasPiece(position));
            Pawn* pawn = dynamic_cast<Pawn*>(getPiece(position).get());
            ASSERT_TRUE(pawn);

            // Пока не можем делать ход за границу поля
            EXPECT_FALSE(pawn->canDoStepOverBoard());
            EXPECT_TRUE(pawn->canDoBigStep());

            // Накручиваем ходы
            pawn->setCountSteps(3);

            // Теперь можем делать ход за границу поля
            EXPECT_TRUE(pawn->canDoStepOverBoard());
            EXPECT_FALSE(pawn->canDoBigStep());

            removePiece(position);
        }
    }

    // Теперь ставим пешку на край поля и провеяем
    // что она может делать ходы через край доски
    for (int j = 0; j < 8; ++j) {
        Position position(0, j);
        setPiece(color, position, type);
        ASSERT_TRUE(board->hasPiece(position));
        Pawn* pawn = dynamic_cast<Pawn*>(getPiece(position).get());
        ASSERT_TRUE(pawn);

        // Накручиваем ходы
        pawn->setCountSteps(3);

        // Теперь можем делать ход за границу поля
        EXPECT_TRUE(pawn->canDoStepOverBoard());
        // В MakeOffset уже учтены походы за границу поля
        expectPawnPossibleDoSmallMove(pawn);

        removePiece(position);
    }

    // Теперь ставим пешку на край поля
    // И ставим туда, куда она хочет сделать ход другую пешку
    // И проверяем, что в таком случае она сделать ход не сможет
    for (int j = 0; j < 8; ++j) {
        Position position(0, j);
        Position blockedPosition(7, j);
        setPiece(color, position, type);
        ASSERT_TRUE(board->hasPiece(position));
        Pawn* pawn = dynamic_cast<Pawn*>(getPiece(position).get());
        ASSERT_TRUE(pawn);

        // Накручиваем ходы
        pawn->setCountSteps(3);

        // Теперь можем делать ход за границу поля
        EXPECT_TRUE(pawn->canDoStepOverBoard());
        // Но не можем делать большой ход
        EXPECT_FALSE(pawn->canDoBigStep());

        // Ставим блокирующую пешку
        setPiece(color, blockedPosition, type);

        // Ходов не должно быть
        expectPieceNoMove(dynamic_cast<Piece*>(pawn));

        removePiece(position);
        removePiece(blockedPosition);
    }
}

TEST_F(TestPieces, KnightPossibleMoves) {
    // Проверяем, что кони могут ходить
    // как задумано из разных позиций

    // Цвет не важен
    SetUp();

    const PieceType type = PieceType::KNIGHT;
    const Color color = Color::WHITE;

    for (int i = 0; i < 8; ++i) {
        for (int j = 0; j < 8; ++j) {
            Position position(i, j);
            setPiece(color, position, type);
            ASSERT_TRUE(board->hasPiece(position));
            Cell::PPiece knight = getPiece(position);

            // Тут ожидаем полный круг ходов коня
            // С учетом того, что если он стоит на краю
            // То не может ходить сквозь край доски
            // и поэтому будет не полный круг
            EXPECT_FALSE(knight->canDoStepOverBoard());
            expectAllKnightMoves(knight);

            removePiece(position);
        }
    }

    // Блокируем некоторые поля коня и смотрим
    // Чтобы он на них не мог попасть
    // Блокируем одно поле
    Board::Route blockedPositions = {Position(3, 6)};
    testKnightMovesWithBlockedCellPipeline(Position(1, 5), blockedPositions, blockedPositions, false);
    blockedPositions.clear();

    // Блокируем несколько полей
    blockedPositions = {Position(3, 6), Position(3, 4)};
    testKnightMovesWithBlockedCellPipeline(Position(1, 5), blockedPositions, blockedPositions, false);
    blockedPositions.clear();

    // Блокируем несколько полей
    blockedPositions = {Position(3, 6), Position(3, 4), Position(7, 0), Position(7, 3)};
    testKnightMovesWithBlockedCellPipeline(Position(1, 5), blockedPositions, blockedPositions, false);
    blockedPositions.clear();

    // Блокируем все поля
    // Так как конь стоит +- на краю доски, то два поля у него и так недоступны
    blockedPositions = {Position(3, 6), Position(3, 4), Position(7, 0), Position(7, 3), Position(3, 0), Position(3, 3)};
    testKnightMovesWithBlockedCellPipeline(Position(1, 5), blockedPositions, blockedPositions, false);
    blockedPositions.clear();
}

TEST_F(TestPieces, KnightPossibleMovesOverBoard) {
    // Проверяем, что кони могут ходить
    // как задумано из разных позиций
    // сквозь границы поля

    // Цвет не важен
    SetUp();

    const PieceType type = PieceType::KNIGHT;
    const Color color = Color::WHITE;

    for (int i = 0; i < 8; ++i) {
        for (int j = 0; j < 8; ++j) {
            Position position(i, j);
            setPiece(color, position, type);
            ASSERT_TRUE(board->hasPiece(position));
            Cell::PPiece knight = getPiece(position);

            // Накручиваем ходы
            knight->setCountSteps(3);

            // Тут ожидаем полный круг ходов коня
            EXPECT_TRUE(knight->canDoStepOverBoard());
            expectAllKnightMoves(knight);

            removePiece(position);
        }
    }

    // Блокируем некоторые поля коня и смотрим
    // Чтобы он на них не мог попасть
    // Блокируем одно поле
    Board::Route blockedPositions = {Position(3, 6)};
    testKnightMovesWithBlockedCellPipeline(Position(1, 5), blockedPositions, blockedPositions, true);
    blockedPositions.clear();

    // Блокируем несколько полей
    blockedPositions = {Position(3, 6), Position(3, 4)};
    testKnightMovesWithBlockedCellPipeline(Position(1, 5), blockedPositions, blockedPositions, true);
    blockedPositions.clear();

    // Блокируем несколько полей
    blockedPositions = {Position(3, 6), Position(3, 4), Position(7, 0), Position(7, 3)};
    testKnightMovesWithBlockedCellPipeline(Position(1, 5), blockedPositions, blockedPositions, true);
    blockedPositions.clear();

    // Блокируем все поля, на которые можно попасть не деля ходы за край доски
    blockedPositions = {Position(3, 6), Position(3, 4), Position(7, 0), Position(7, 3), Position(3, 0), Position(3, 3)};
    testKnightMovesWithBlockedCellPipeline(Position(1, 5), blockedPositions, blockedPositions, true);
    blockedPositions.clear();

    // Блокируем все поля
    blockedPositions = {Position(3, 6), Position(3, 4), Position(7, 0), Position(7, 3), Position(3, 0), Position(3, 3), Position(7, 4), Position(7, 6)};
    testKnightMovesWithBlockedCellPipeline(Position(1, 5), blockedPositions, blockedPositions, true);
    blockedPositions.clear();
}

TEST_F(TestPieces, BishopPossibleMoves) {
    // Проверяем, что слоны могут ходить
    // как задумано из разных позиций

    // Цвет не важен
    SetUp();

    const PieceType type = PieceType::BISHOP;
    const Color color = Color::WHITE;

    for (int i = 0; i < 8; ++i) {
        for (int j = 0; j < 8; ++j) {
            Position position(i, j);
            setPiece(color, position, type);
            ASSERT_TRUE(board->hasPiece(position));
            Cell::PPiece bishop = getPiece(position);

            // Тут ожидаем обе диагонали слона
            EXPECT_FALSE(bishop->canDoStepOverBoard());
            expectAllBishopMoves(bishop);

            removePiece(position);
        }
    }

    // Теперь проверяем, что если на пути слона
    // Будут стоять свои фигуры, то он заблочится об них
    // И не сможет дальше идти

    // Блокируем одну диагональ
    Board::Route blockedPositions = {Position(7, 1)};
    Board::Route pawnsPositions = {Position(7, 1)};
    testBishopMovesWithBlockedCellPipeline(Position(5, 3), pawnsPositions, blockedPositions, false);
    blockedPositions.clear();
    pawnsPositions.clear();

    // Блокируем одну диагональ
    blockedPositions = {Position(3, 1), Position(2, 0)};
    pawnsPositions = {Position(3, 1)};
    testBishopMovesWithBlockedCellPipeline(Position(5, 3), pawnsPositions, blockedPositions, false);
    blockedPositions.clear();
    pawnsPositions.clear();

    // Блокируем две диагонали
    blockedPositions = {Position(3, 5), Position(2, 4), Position(1, 3), Position(0, 2), Position(5, 5), Position(6, 4), Position(7, 3)};
    pawnsPositions = {Position(3, 5), Position(5, 5)};
    testBishopMovesWithBlockedCellPipeline(Position(4, 6), pawnsPositions, blockedPositions, false);
    blockedPositions.clear();
    pawnsPositions.clear();

    // Блокируем три диагонали
    blockedPositions = {Position(7, 2), Position(6, 3), Position(6, 5), Position(7, 6), Position(4, 5), Position(3, 6), Position(2, 7)};
    pawnsPositions = {Position(6, 3), Position(6, 5), Position(4, 5)};
    testBishopMovesWithBlockedCellPipeline(Position(5, 4), pawnsPositions, blockedPositions, false);
    blockedPositions.clear();
    pawnsPositions.clear();

    // Блокируем все 4 диагонали
    blockedPositions = {Position(6, 0), Position(6, 4), Position(1, 5), Position(2, 0), Position(7, 5), Position(0, 6)};
    pawnsPositions = {Position(6, 0), Position(6, 4), Position(1, 5), Position(2, 0)};
    testBishopMovesWithBlockedCellPipeline(Position(4, 2), pawnsPositions, blockedPositions, false);
    blockedPositions.clear();
    pawnsPositions.clear();

    // Блокируем вообще все ходы
    blockedPositions = {Position(7, 0), Position(7, 2), Position(5, 0), Position(5, 2), Position(4, 3), Position(3, 4), Position(2, 5), Position(1, 6), Position(0, 7)};
    pawnsPositions = {Position(7, 0), Position(7, 2), Position(5, 0), Position(5, 2)};
    testBishopMovesWithBlockedCellPipeline(Position(6, 1), pawnsPositions, blockedPositions, false);
    blockedPositions.clear();
    pawnsPositions.clear();
}

TEST_F(TestPieces, BishopPossibleMovesOverBoard) {
    // Проверяем, что слоны могут ходить
    // как задумано из разных позиций
    // При этом сквозь край поля

    // Цвет не важен
    SetUp();

    // TODO сделать подписи для названий параметров функций,
    // если они передаются не как именованные переменные

    // Проверяем, что если у нас будут дефолтные позиции
    // И при этом будут разрешены ходы через край доски
    const PieceType type = PieceType::BISHOP;
    const Color color = Color::WHITE;
    for (int i = 0; i < 8; ++i) {
        for (int j = 0; j < 8; ++j) {
            Position position(i, j);
            setPiece(color, position, type);
            ASSERT_TRUE(board->hasPiece(position));
            Cell::PPiece bishop = getPiece(position);

            // Накручиваем ходы
            bishop->setCountSteps(3);

            // Тут ожидаем обе диагонали слона
            EXPECT_TRUE(bishop->canDoStepOverBoard());
            expectAllBishopMoves(bishop);

            removePiece(position);
        }
    }

    // Блокируем одну диагональ
    Board::Route blockedPositions = {Position(6, 6)};
    testBishopMovesWithBlockedCellPipeline(Position(4, 4), blockedPositions, blockedPositions, true);
    blockedPositions.clear();

    // Теперь ставим слона на край поля
    // и ставим на его пути уже 2 фигуры подряд
    blockedPositions = {Position(6, 6), Position(5, 5)};
    testBishopMovesWithBlockedCellPipeline(Position(0, 0), blockedPositions, blockedPositions, true);
    blockedPositions.clear();

    // Теперь ставим слона на край поля
    // и ставим на его пути уже 2 фигуры но с разрывом
    blockedPositions = {Position(6, 6), Position(5, 5), Position(4, 4)};
    Board::Route pawnsPositions = {Position(6, 6), Position(4, 4)};
    testBishopMovesWithBlockedCellPipeline(Position(0, 0), pawnsPositions, blockedPositions, true);
    blockedPositions.clear();
    pawnsPositions.clear();

    // Теперь ставим слона в центр и блочим ему 2 диагонали
    blockedPositions = {Position(1, 1), Position(6, 2)};
    testBishopMovesWithBlockedCellPipeline(Position(4, 4), blockedPositions, blockedPositions, true);
    blockedPositions.clear();

    // Теперь ставим слона в центр и блочим ему целиком
    // Диагональ, чтобы он не мог ходить сквозь края доски
    pawnsPositions = {Position(6, 6), Position(1, 1)};
    blockedPositions = {Position(6, 6), Position(7, 7), Position(1, 1), Position(0, 0)};
    testBishopMovesWithBlockedCellPipeline(Position(4, 4), pawnsPositions, blockedPositions, true);
    blockedPositions.clear();
    pawnsPositions.clear();
}

TEST_F(TestPieces, RookPossibleMoves) {
    // Проверяем, что ладьи могут ходить
    // как задумано из разных позиций

    // Цвет не важен
    SetUp();

    const PieceType type = PieceType::ROOK;
    const Color color = Color::WHITE;

    for (int i = 0; i < 8; ++i) {
        for (int j = 0; j < 8; ++j) {
            Position position(i, j);
            setPiece(color, position, type);
            ASSERT_TRUE(board->hasPiece(position));
            Cell::PPiece rook = getPiece(position);

            // Тут ожидаем все горизонтали и вертикали ладьи
            EXPECT_FALSE(rook->canDoStepOverBoard());
            expectAllRookMoves(rook);

            removePiece(position);
        }
    }

    // Теперь проверяем, что если на пути ладьи
    // Будут стоять свои фигуры, то она заблочится об них
    // И не сможет дальше идти

    // Блокируем одну горизонталь
    Board::Route blockedPositions = {Position(5, 6), Position(5, 7)};
    Board::Route pawnsPositions = {Position(5, 6)};
    testRookMovesWithBlockedCellPipeline(Position(5, 3), pawnsPositions, blockedPositions, false);
    blockedPositions.clear();
    pawnsPositions.clear();

    // Блокируем одну вертикаль
    blockedPositions = {Position(5, 3), Position(6, 3), Position(7, 3)};
    pawnsPositions = {Position(5, 3)};
    testRookMovesWithBlockedCellPipeline(Position(2, 3), pawnsPositions, blockedPositions, false);
    blockedPositions.clear();
    pawnsPositions.clear();

    // Блокируем горизонталь и вертикаль
    blockedPositions = {Position(3, 5), Position(1, 7), Position(4, 5), Position(5, 5), Position(6, 5), Position(7, 5)};
    pawnsPositions = {Position(3, 5), Position(1, 7)};
    testRookMovesWithBlockedCellPipeline(Position(1, 5), pawnsPositions, blockedPositions, false);
    blockedPositions.clear();
    pawnsPositions.clear();

    // Блокируем 2 горизонтали и вертикаль
    blockedPositions = {Position(7, 2), Position(3, 2), Position(6, 1), Position(6, 0), Position(2, 2), Position(1, 2), Position(0, 2)};
    pawnsPositions = {Position(7, 2), Position(3, 2), Position(6, 1)};
    testRookMovesWithBlockedCellPipeline(Position(6, 2), pawnsPositions, blockedPositions, false);
    blockedPositions.clear();
    pawnsPositions.clear();

    // Блокируем 2 горизонтали и 2 вертикали
    blockedPositions = {Position(7, 2), Position(0, 2), Position(2, 1), Position(2, 5), Position(2, 0), Position(2, 6), Position(2, 7)};
    pawnsPositions = {Position(7, 2), Position(0, 2), Position(2, 1), Position(2, 5)};
    testRookMovesWithBlockedCellPipeline(Position(2, 2), pawnsPositions, blockedPositions, false);
    blockedPositions.clear();
    pawnsPositions.clear();

    // Блокируем все ходы
    blockedPositions = {Position(1, 7), Position(0, 6), Position(1, 2), Position(5, 6), Position(1, 1), Position(1, 0), Position(6, 6), Position(7, 6)};
    pawnsPositions = {Position(1, 7), Position(0, 6), Position(1, 2), Position(5, 6)};
    testRookMovesWithBlockedCellPipeline(Position(1, 6), pawnsPositions, blockedPositions, false);
    blockedPositions.clear();
    pawnsPositions.clear();
}

TEST_F(TestPieces, RookPossibleMovesOverBoard) {
    // Проверяем, что ладьи могут ходить
    // как задумано из разных позиций
    // При этом сквозь край поля

    // Цвет не важен
    SetUp();

    // Проверяем, что если у нас будут дефолтные позиции
    // И при этом будут разрешены ходы через край доски
    const PieceType type = PieceType::ROOK;
    const Color color = Color::WHITE;
    for (int i = 0; i < 8; ++i) {
        for (int j = 0; j < 8; ++j) {
            Position position(i, j);
            setPiece(color, position, type);
            ASSERT_TRUE(board->hasPiece(position));
            Cell::PPiece rook = getPiece(position);

            // Накручиваем ходы
            rook->setCountSteps(3);

            // Тут ожидаем все горизонтали и вертикали ладьи
            EXPECT_TRUE(rook->canDoStepOverBoard());
            expectAllRookMoves(rook);

            removePiece(position);
        }
    }


    // Блокируем одну вертикаль
    Board::Route blockedPositions = {Position(4, 6)};
    testRookMovesWithBlockedCellPipeline(Position(4, 4), blockedPositions, blockedPositions, true);
    blockedPositions.clear();

    // Теперь ставим ладью на край поля
    // и ставим на ee пути уже 2 фигуры подряд
    blockedPositions = {Position(0, 6), Position(0, 5)};
    testRookMovesWithBlockedCellPipeline(Position(0, 0), blockedPositions, blockedPositions, true);
    blockedPositions.clear();

    // Теперь ставим ладью на край поля
    // и ставим на ее пути уже 2 фигуры но с разрывом
    // TODO сделать так, чтобы blockedPositions формировалось из pawnsPositions и чего-то еще
    // Потому что pawnsPositions входит в blockedPositions
    blockedPositions = {Position(0, 6), Position(0, 5), Position(0, 4)};
    Board::Route pawnsPositions = {Position(0, 6), Position(0, 4)};
    testRookMovesWithBlockedCellPipeline(Position(0, 0), pawnsPositions, blockedPositions, true);
    blockedPositions.clear();
    pawnsPositions.clear();

    // Теперь ставим ладью в центр и блочим ей вертикаль и горизонталь
    blockedPositions = {Position(0, 4), Position(4, 0)};
    testRookMovesWithBlockedCellPipeline(Position(4, 4), blockedPositions, blockedPositions, true);
    blockedPositions.clear();

    // Теперь ставим ладью в центр и блочим ей целиком
    // Вертикаль, чтобы она не могла ходить сквозь края доски
    pawnsPositions = {Position(4, 6), Position(4, 1)};
    blockedPositions = {Position(4, 6), Position(4, 7), Position(4, 1), Position(4, 0)};
    testRookMovesWithBlockedCellPipeline(Position(4, 4), pawnsPositions, blockedPositions, true);
    blockedPositions.clear();
    pawnsPositions.clear();

    // Теперь ставим ладью в центр и блочим ей целиком
    // Горизонталь, чтобы она не могла ходить сквозь края доски
    pawnsPositions = {Position(6, 4), Position(1, 4)};
    blockedPositions = {Position(6, 4), Position(7, 4), Position(1, 4), Position(0, 4)};
    testRookMovesWithBlockedCellPipeline(Position(4, 4), pawnsPositions, blockedPositions, true);
    blockedPositions.clear();
    pawnsPositions.clear();
}

TEST_F(TestPieces, QueenPossibleMoves) {
    // Проверяем, что ферзи могут ходить
    // как задумано из разных позиций

    // Цвет не важен
    SetUp();

    const PieceType type = PieceType::QUEEN;
    const Color color = Color::WHITE;

    for (int i = 0; i < 8; ++i) {
        for (int j = 0; j < 8; ++j) {
            Position position(i, j);
            setPiece(color, position, type);
            ASSERT_TRUE(board->hasPiece(position));
            Cell::PPiece queen = getPiece(position);

            // Тут ожидаем все ходы ферзя
            EXPECT_FALSE(queen->canDoStepOverBoard());
            expectAllQueenMoves(queen);

            removePiece(position);
        }
    }

    // Теперь проверяем, что если на пути ферзя
    // Будут стоять свои фигуры, то он заблочится об них
    // И не сможет дальше идти

    // Блокируем одну диагональ
    Board::Route blockedPositions = {Position(7, 1)};
    Board::Route pawnsPositions = {Position(7, 1)};
    testQueenMovesWithBlockedCellPipeline(Position(5, 3), pawnsPositions, blockedPositions, false);
    blockedPositions.clear();
    pawnsPositions.clear();

    // Блокируем одну диагональ
    blockedPositions = {Position(3, 1), Position(2, 0)};
    pawnsPositions = {Position(3, 1)};
    testQueenMovesWithBlockedCellPipeline(Position(5, 3), pawnsPositions, blockedPositions, false);
    blockedPositions.clear();
    pawnsPositions.clear();

    // Блокируем две диагонали
    blockedPositions = {Position(3, 5), Position(2, 4), Position(1, 3), Position(0, 2), Position(5, 5), Position(6, 4), Position(7, 3)};
    pawnsPositions = {Position(3, 5), Position(5, 5)};
    testQueenMovesWithBlockedCellPipeline(Position(4, 6), pawnsPositions, blockedPositions, false);
    blockedPositions.clear();
    pawnsPositions.clear();

    // Блокируем три диагонали
    blockedPositions = {Position(7, 2), Position(6, 3), Position(6, 5), Position(7, 6), Position(4, 5), Position(3, 6), Position(2, 7)};
    pawnsPositions = {Position(6, 3), Position(6, 5), Position(4, 5)};
    testQueenMovesWithBlockedCellPipeline(Position(5, 4), pawnsPositions, blockedPositions, false);
    blockedPositions.clear();
    pawnsPositions.clear();

    // Блокируем все 4 диагонали
    blockedPositions = {Position(6, 0), Position(6, 4), Position(1, 5), Position(2, 0), Position(7, 5), Position(0, 6)};
    pawnsPositions = {Position(6, 0), Position(6, 4), Position(1, 5), Position(2, 0)};
    testQueenMovesWithBlockedCellPipeline(Position(4, 2), pawnsPositions, blockedPositions, false);
    blockedPositions.clear();
    pawnsPositions.clear();

    // Блокируем все ходы  диагональные
    blockedPositions = {Position(7, 0), Position(7, 2), Position(5, 0), Position(5, 2), Position(4, 3), Position(3, 4), Position(2, 5), Position(1, 6), Position(0, 7)};
    pawnsPositions = {Position(7, 0), Position(7, 2), Position(5, 0), Position(5, 2)};
    testQueenMovesWithBlockedCellPipeline(Position(6, 1), pawnsPositions, blockedPositions, false);
    blockedPositions.clear();
    pawnsPositions.clear();

    // Блокируем одну горизонталь
    blockedPositions = {Position(5, 6), Position(5, 7)};
    pawnsPositions = {Position(5, 6)};
    testQueenMovesWithBlockedCellPipeline(Position(5, 3), pawnsPositions, blockedPositions, false);
    blockedPositions.clear();
    pawnsPositions.clear();

    // Блокируем одну вертикаль
    blockedPositions = {Position(5, 3), Position(6, 3), Position(7, 3)};
    pawnsPositions = {Position(5, 3)};
    testQueenMovesWithBlockedCellPipeline(Position(2, 3), pawnsPositions, blockedPositions, false);
    blockedPositions.clear();
    pawnsPositions.clear();

    // Блокируем горизонталь и вертикаль
    blockedPositions = {Position(3, 5), Position(1, 7), Position(4, 5), Position(5, 5), Position(6, 5), Position(7, 5)};
    pawnsPositions = {Position(3, 5), Position(1, 7)};
    testQueenMovesWithBlockedCellPipeline(Position(1, 5), pawnsPositions, blockedPositions, false);
    blockedPositions.clear();
    pawnsPositions.clear();

    // Блокируем 2 горизонтали и вертикаль
    blockedPositions = {Position(7, 2), Position(3, 2), Position(6, 1), Position(6, 0), Position(2, 2), Position(1, 2), Position(0, 2)};
    pawnsPositions = {Position(7, 2), Position(3, 2), Position(6, 1)};
    testQueenMovesWithBlockedCellPipeline(Position(6, 2), pawnsPositions, blockedPositions, false);
    blockedPositions.clear();
    pawnsPositions.clear();

    // Блокируем 2 горизонтали и 2 вертикали
    blockedPositions = {Position(7, 2), Position(0, 2), Position(2, 1), Position(2, 5), Position(2, 0), Position(2, 6), Position(2, 7)};
    pawnsPositions = {Position(7, 2), Position(0, 2), Position(2, 1), Position(2, 5)};
    testQueenMovesWithBlockedCellPipeline(Position(2, 2), pawnsPositions, blockedPositions, false);
    blockedPositions.clear();
    pawnsPositions.clear();

    // Блокируем все ходы горизонтально-вертикальные
    blockedPositions = {Position(1, 7), Position(0, 6), Position(1, 2), Position(5, 6), Position(1, 1), Position(1, 0), Position(6, 6), Position(7, 6)};
    pawnsPositions = {Position(1, 7), Position(0, 6), Position(1, 2), Position(5, 6)};
    testQueenMovesWithBlockedCellPipeline(Position(1, 6), pawnsPositions, blockedPositions, false);
    blockedPositions.clear();
    pawnsPositions.clear();

    // Блокируем вертикаль и диагональ
    blockedPositions = {Position(2, 6), Position(3, 5), Position(1, 6), Position(0, 6), Position(2, 4), Position(1, 3), Position(0, 2)};
    pawnsPositions = {Position(2, 6), Position(3, 5)};
    testQueenMovesWithBlockedCellPipeline(Position(4, 6), pawnsPositions, blockedPositions, false);
    blockedPositions.clear();
    pawnsPositions.clear();

    // Блокируем горизонталь и диагональ
    blockedPositions = {Position(4, 1), Position(5, 7), Position(4, 0)};
    pawnsPositions = {Position(4, 1), Position(5, 7)};
    testQueenMovesWithBlockedCellPipeline(Position(4, 6), pawnsPositions, blockedPositions, false);
    blockedPositions.clear();
    pawnsPositions.clear();

    // Блокируем горизонталь, вертикаль и 2 диагонали
    blockedPositions = {Position(4, 1), Position(5, 7), Position(4, 0), Position(1, 6), Position(0, 6), Position(3, 7)};
    pawnsPositions = {Position(4, 1), Position(5, 7), Position(1, 6), Position(3, 7)};
    testQueenMovesWithBlockedCellPipeline(Position(4, 6), pawnsPositions, blockedPositions, false);
    blockedPositions.clear();
    pawnsPositions.clear();

    // Блокируем все ходы
    blockedPositions = {Position(4, 1), Position(5, 7), Position(4, 0), Position(1, 6), Position(0, 6), Position(3, 7), Position(4, 7), Position(6, 6), Position(7, 6), Position(3, 5), Position(2, 4), Position(1, 3), Position(0, 2), Position(6, 4), Position(7, 3)};
    pawnsPositions = {Position(4, 1), Position(5, 7), Position(1, 6), Position(3, 7), Position(4, 7), Position(6, 6), Position(3, 5), Position(6, 4)};
    testQueenMovesWithBlockedCellPipeline(Position(4, 6), pawnsPositions, blockedPositions, false);
    blockedPositions.clear();
    pawnsPositions.clear();
}

TEST_F(TestPieces, QueenPossibleMovesOverBoard) {
    // Проверяем, что ладьи могут ходить
    // как задумано из разных позиций
    // При этом сквозь край поля

    // Цвет не важен
    SetUp();

    // Проверяем, что если у нас будут дефолтные позиции
    // И при этом будут разрешены ходы через край доски
    const PieceType type = PieceType::QUEEN;
    const Color color = Color::WHITE;
    for (int i = 0; i < 8; ++i) {
        for (int j = 0; j < 8; ++j) {
            Position position(i, j);
            setPiece(color, position, type);
            ASSERT_TRUE(board->hasPiece(position));
            Cell::PPiece queen = getPiece(position);

            // Накручиваем ходы
            queen->setCountSteps(3);

            // Тут ожидаем все ходы ферзя
            EXPECT_TRUE(queen->canDoStepOverBoard());
            expectAllQueenMoves(queen);

            removePiece(position);
        }
    }

    // Блокируем одну вертикаль
    Board::Route blockedPositions = {Position(4, 6)};
    testQueenMovesWithBlockedCellPipeline(Position(4, 4), blockedPositions, blockedPositions, true);
    blockedPositions.clear();

    // Теперь ставим ферзя на край поля
    // и ставим на eго пути уже 2 фигуры подряд
    blockedPositions = {Position(0, 6), Position(0, 5)};
    testQueenMovesWithBlockedCellPipeline(Position(0, 0), blockedPositions, blockedPositions, true);
    blockedPositions.clear();

    // Теперь ставим ферзя на край поля
    // и ставим на его пути уже 2 фигуры но с разрывом
    // TODO сделать так, чтобы blockedPositions формировалось из pawnsPositions и чего-то еще
    // Потому что pawnsPositions входит в blockedPositions
    blockedPositions = {Position(0, 6), Position(0, 5), Position(0, 4)};
    Board::Route pawnsPositions = {Position(0, 6), Position(0, 4)};
    testQueenMovesWithBlockedCellPipeline(Position(0, 0), pawnsPositions, blockedPositions, true);
    blockedPositions.clear();
    pawnsPositions.clear();

    // Теперь ставим ферзя в центр и блочим ему вертикаль и горизонталь
    blockedPositions = {Position(0, 4), Position(4, 0)};
    testQueenMovesWithBlockedCellPipeline(Position(4, 4), blockedPositions, blockedPositions, true);
    blockedPositions.clear();

    // Теперь ставим ферзя в центр и блочим ему целиком
    // Вертикаль, чтобы он не мог ходить сквозь края доски
    pawnsPositions = {Position(4, 6), Position(4, 1)};
    blockedPositions = {Position(4, 6), Position(4, 7), Position(4, 1), Position(4, 0)};
    testQueenMovesWithBlockedCellPipeline(Position(4, 4), pawnsPositions, blockedPositions, true);
    blockedPositions.clear();
    pawnsPositions.clear();

    // Теперь ставим ферзя в центр и блочим ему целиком
    // Горизонталь, чтобы он не мог ходить сквозь края доски
    pawnsPositions = {Position(6, 4), Position(1, 4)};
    blockedPositions = {Position(6, 4), Position(7, 4), Position(1, 4), Position(0, 4)};
    testQueenMovesWithBlockedCellPipeline(Position(4, 4), pawnsPositions, blockedPositions, true);
    blockedPositions.clear();
    pawnsPositions.clear();

    // Блокируем одну диагональ
    blockedPositions = {Position(6, 6)};
    testQueenMovesWithBlockedCellPipeline(Position(4, 4), blockedPositions, blockedPositions, true);
    blockedPositions.clear();

    // Теперь ставим ферзя на край поля
    // и ставим на его пути уже 2 фигуры подряд
    blockedPositions = {Position(6, 6), Position(5, 5)};
    testQueenMovesWithBlockedCellPipeline(Position(0, 0), blockedPositions, blockedPositions, true);
    blockedPositions.clear();

    // Теперь ставим ферзя на край поля
    // и ставим на его пути уже 2 фигуры но с разрывом
    blockedPositions = {Position(6, 6), Position(5, 5), Position(4, 4)};
    pawnsPositions = {Position(6, 6), Position(4, 4)};
    testQueenMovesWithBlockedCellPipeline(Position(0, 0), pawnsPositions, blockedPositions, true);
    blockedPositions.clear();
    pawnsPositions.clear();

    // Теперь ставим ферзя в центр и блочим ему 2 диагонали
    blockedPositions = {Position(1, 1), Position(6, 2)};
    testQueenMovesWithBlockedCellPipeline(Position(4, 4), blockedPositions, blockedPositions, true);
    blockedPositions.clear();

    // Теперь ставим ферзя в центр и блочим ему целиком
    // Диагональ, чтобы он не мог ходить сквозь края доски
    pawnsPositions = {Position(6, 6), Position(1, 1)};
    blockedPositions = {Position(6, 6), Position(7, 7), Position(1, 1), Position(0, 0)};
    testQueenMovesWithBlockedCellPipeline(Position(4, 4), pawnsPositions, blockedPositions, true);
    blockedPositions.clear();
    pawnsPositions.clear();
}

TEST_F(TestPieces, KingPossibleMoves) {
    // Проверяем, что корли могут ходить
    // как задумано из разных позиций

    // Цвет не важен
    SetUp();

    const PieceType type = PieceType::KING;
    const Color color = Color::WHITE;

    for (int i = 0; i < 8; ++i) {
        for (int j = 0; j < 8; ++j) {
            Position position(i, j);
            setPiece(color, position, type);
            ASSERT_TRUE(board->hasPiece(position));
            Cell::PPiece king = getPiece(position);

            // Тут ожидаем все позиции короля, кроме тех
            // Что за краем поля
            EXPECT_FALSE(king->canDoStepOverBoard());
            expectAllKingMoves(king, false);

            removePiece(position);
        }
    }

    // Блокируем некоторые ходы короля и проверяем
    // Что он не может туда сходить
    // Блокируем одну клетку
    Board::Route blockedPositions = {Position(5, 0)};
    testKingMovesWithBlockedCellPipeline(Position(4, 0), blockedPositions, blockedPositions, false);
    blockedPositions.clear();

    // Блокируем несколько клеток
    blockedPositions = {Position(5, 4), Position(6, 4)};
    testKingMovesWithBlockedCellPipeline(Position(6, 3), blockedPositions, blockedPositions, false);
    blockedPositions.clear();

    // Блокируем несколько клеток
    blockedPositions = {Position(5, 4), Position(6, 4), Position(7, 3), Position(7, 4)};
    testKingMovesWithBlockedCellPipeline(Position(6, 3), blockedPositions, blockedPositions, false);
    blockedPositions.clear();

    // Блокируем несколько клеток
    blockedPositions = {Position(5, 4), Position(6, 4), Position(7, 3), Position(7, 4), Position(5, 3), Position(6, 2)};
    testKingMovesWithBlockedCellPipeline(Position(6, 3), blockedPositions, blockedPositions, false);
    blockedPositions.clear();

    // Блокируем все ходы
    blockedPositions = {Position(5, 4), Position(6, 4), Position(7, 3), Position(7, 4), Position(5, 3), Position(6, 2), Position(5, 2), Position(7, 2)};
    testKingMovesWithBlockedCellPipeline(Position(6, 3), blockedPositions, blockedPositions, false);
    blockedPositions.clear();
}

TEST_F(TestPieces, KingPossibleMovesOverBoard) {
    // Проверяем, что короли могут ходить
    // как задумано из разных позиций
    // При этом сквозь край поля

    // Цвет не важен
    SetUp();

    // Проверяем, что все ходы короля ожидаемые
    // Теперь уже вместе с ходами за край доски
    const Color color = Color::WHITE;
    const PieceType type = PieceType::KING;
    for (int i = 0; i < 8; ++i) {
        for (int j = 0; j < 8; ++j) {
            Position position(i, j);
            setPiece(color, position, type);
            ASSERT_TRUE(board->hasPiece(position));
            Cell::PPiece king = getPiece(position);

            // Накручиваем ходы
            king->setCountSteps(3);

            // Тут ожидаем все позиции короля
            EXPECT_TRUE(king->canDoStepOverBoard());
            expectAllKingMoves(king, true);

            removePiece(position);
        }
    }

    // Блокируем один из возможных путей, куда может
    // Сходить король с учетом хода сквозь край доски
    Board::Route blockedPositions = {Position(4, 7)};
    testKingMovesWithBlockedCellPipeline(Position(4, 0), blockedPositions, blockedPositions, true);
    blockedPositions.clear();

    // Блокируем несколько возможных путей, куда может
    // Сходить король с учетом хода сквозь край доски
    blockedPositions = {Position(4, 7), Position(5, 7)};
    testKingMovesWithBlockedCellPipeline(Position(4, 0), blockedPositions, blockedPositions, true);
    blockedPositions.clear();

    // Блокируем несколько возможных путей, куда может
    // Сходить король с учетом хода сквозь край доски
    blockedPositions = {Position(4, 7), Position(5, 7), Position(6, 7)};
    testKingMovesWithBlockedCellPipeline(Position(5, 0), blockedPositions, blockedPositions, true);
    blockedPositions.clear();

    // Блокируем все ходы за край доски
    blockedPositions = {Position(0, 7), Position(7, 0), Position(7, 7)};
    testKingMovesWithBlockedCellPipeline(Position(0, 0), blockedPositions, blockedPositions, true);
    blockedPositions.clear();

    // Блокируем несколько возможных путей, куда может
    // Сходить король с учетом хода сквозь край доски
    blockedPositions = {Position(2, 0), Position(0, 0)};
    testKingMovesWithBlockedCellPipeline(Position(0, 1), blockedPositions, blockedPositions, true);
    blockedPositions.clear();
}
