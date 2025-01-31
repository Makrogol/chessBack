#include "board.h"

// #include <android/log.h>
#include <algorithm>
#include <memory>
#include <utility>

#include "piece.h"
#include "bishop.h"
#include "king.h"
#include "knight.h"
#include "pawn.h"
#include "queen.h"
#include "rook.h"

namespace {
// TODO перенести куда-то в другое место
    Cell::PPiece createPieceByType(const Position& position, const Color& color, const PieceType& type) {
        // TODO придумать что-то по лучше, чем свитч
        switch (type)
        {
            case PieceType::BISHOP:
                return std::make_shared<Bishop>(position, color);
            case PieceType::KNIGHT:
                return std::make_shared<Knight>(position, color);
            case PieceType::QUEEN:
                return std::make_shared<Queen>(position, color);
            case PieceType::ROOK:
                return std::make_shared<Rook>(position, color);
            case PieceType::KING:
                return std::make_shared<King>(position, color);
            case PieceType::PAWN:
                return std::make_shared<Pawn>(position, color);
            default:
                break;
        }
        // TODO писать, что какой-то еррор произошел
        return nullptr;
    }
}


// TODO разбить на утили и прочее

Board::Board(const Color& mainColor, PHistory  history, PHistoryRecordManager  historyRecordManager):
    mainColor(mainColor),
    anotherColor(getAnotherColor(mainColor)),
    history(std::move(history)),
    historyRecordManager(std::move(historyRecordManager))
    {
        // TODO возможно потом от этого будут проблемы (когда мы сможет создавать поле из сеттингов)
        turnColor = Color::WHITE;
    }

void Board::createEmptyField() {
    field.clear();
    field.resize(8);
    for (Board::Line& line : field) {
        line.resize(8);
    }
}

void Board::createEmptyColoredField() {
    field.clear();
    field.resize(8);
    // 0, 0 - Левый верхний угол
    // Поле в левом верхнем углу всегда белое
    field[0].emplace_back(std::make_shared<Cell>(Position(0, 0), Color::WHITE));
    // Заполняем верхнюю строку
    for (int i = 1; i < 8; ++i) {
        Color color = getAnotherColor(field[0][i - 1]->getColor());
        field[0].emplace_back(std::make_shared<Cell>(Position(0, i), color));
    }
    // Заполняем все остальное поле
    for (int i = 1; i < 8; ++i) {
        Color color = getAnotherColor(field[i - 1][0]->getColor());
        for (int j = 0; j < 8; ++j) {
            field[i].emplace_back(std::make_shared<Cell>(Position(i, j), color));
            color = getAnotherColor(color);
        }
    }
}

void Board::setPawnsDefault() {
    createEmptyColoredField();
    const int mainLine = 6;
    const int anotherLine = 1;

    for (int j = 0; j < 8; ++j) {
        field[mainLine][j]->changePieceTo(std::make_shared<Pawn>(Position(mainLine, j), mainColor));
        field[anotherLine][j]->changePieceTo(std::make_shared<Pawn>(Position(anotherLine, j), anotherColor));
    }
}

void Board::createDefaultField() {
    // Пешки
    setPawnsDefault();

    const int mainLine = 7;
    const int anotherLine = 0;

    // Ладьи
    field[mainLine][0]->changePieceTo(std::make_shared<Rook>(Position(mainLine, 0), mainColor));
    field[mainLine][7]->changePieceTo(std::make_shared<Rook>(Position(mainLine, 7), mainColor));

    field[anotherLine][0]->changePieceTo(std::make_shared<Rook>(Position(anotherLine, 0), anotherColor));
    field[anotherLine][7]->changePieceTo(std::make_shared<Rook>(Position(anotherLine, 7), anotherColor));

    // Кони
    field[mainLine][1]->changePieceTo(std::make_shared<Knight>(Position(mainLine, 1), mainColor));
    field[mainLine][6]->changePieceTo(std::make_shared<Knight>(Position(mainLine, 6), mainColor));

    field[anotherLine][1]->changePieceTo(std::make_shared<Knight>(Position(anotherLine, 1), anotherColor));
    field[anotherLine][6]->changePieceTo(std::make_shared<Knight>(Position(anotherLine, 6), anotherColor));

    // Слоны
    field[mainLine][2]->changePieceTo(std::make_shared<Bishop>(Position(mainLine, 2), mainColor));
    field[mainLine][5]->changePieceTo(std::make_shared<Bishop>(Position(mainLine, 5), mainColor));

    field[anotherLine][2]->changePieceTo(std::make_shared<Bishop>(Position(anotherLine, 2), anotherColor));
    field[anotherLine][5]->changePieceTo(std::make_shared<Bishop>(Position(anotherLine, 5), anotherColor));

    // Ферзи и короли
    if (mainColor == Color::WHITE) {
        field[mainLine][3]->changePieceTo(std::make_shared<Queen>(Position(mainLine, 3), mainColor));
        field[mainLine][4]->changePieceTo(std::make_shared<King>(Position(mainLine, 4), mainColor));

        field[anotherLine][3]->changePieceTo(std::make_shared<Queen>(Position(anotherLine, 3), anotherColor));
        field[anotherLine][4]->changePieceTo(std::make_shared<King>(Position(anotherLine, 4), anotherColor));
    } else if (mainColor == Color::BLACK) {
        field[mainLine][4]->changePieceTo(std::make_shared<Queen>(Position(mainLine, 4), mainColor));
        field[mainLine][3]->changePieceTo(std::make_shared<King>(Position(mainLine, 3), mainColor));

        field[anotherLine][4]->changePieceTo(std::make_shared<Queen>(Position(anotherLine, 4), anotherColor));
        field[anotherLine][3]->changePieceTo(std::make_shared<King>(Position(anotherLine, 3), anotherColor));
    } else {
        // error
    }
}

Piece::Route Board::getAttackPositionsForColor(const Color& color) const {
    Piece::Route attackPositions;
    const Color attackColor = getAnotherColor(color);
    for (const Board::Line& line : field) {
        for (const PCell& cell : line) {
            if (!cell->hasPiece()) {
                continue;
            }

            const Cell::PPiece piece = cell->getPiece();
            if (piece->getColor() != attackColor) {
                continue;
            }
            const Piece::Route attackRoute = piece->getAttackRoute(*this);
            attackPositions.insert(attackPositions.end(), attackRoute.begin(), attackRoute.end());
        }
    }
    return attackPositions;
}

Board::PCell Board::getCellByPosition(const Position& position) const {
    if (!isPositionInBoard(position)) {
        return nullptr;
    }
    return field[position.getI()][position.getJ()];
}

Cell::PPiece Board::getPieceByPosition(const Position& position) const {
    const PCell cell = getCellByPosition(position);
    if (cell) {
        return cell->getPiece();
    }
    return nullptr;
}

bool Board::hasPieceSameColor(const Position& position, const Offset& offset, const Color& color) const {
    const Position newPosition = MakeOffset(position, offset);
    if (!hasPiece(position, offset)) {
        return false;
    }
    const Cell::PPiece piece = getPieceByPosition(newPosition);
    return piece->getColor() == color;
}

bool Board::hasPieceAnotherColor(const Position& position, const Offset& offset, const Color& color) const {
    const Position newPosition = MakeOffset(position, offset);
    if (!hasPiece(position, offset)) {
        return false;
    }
    const Cell::PPiece piece = getPieceByPosition(newPosition);
    return piece->getColor() == getAnotherColor(color);
}

bool Board::hasPiece(const Position& position, const Offset& offset) const {
    const Position newPosition = MakeOffset(position, offset);
    return hasPiece(newPosition);
}

bool Board::hasPiece(const Position& position) const {
    const PCell cell = getCellByPosition(position);
    if (cell) {
        return cell->hasPiece();
    }
    return false;
}

Board::PPieces Board::getPiecesByTypeAndColor(const PieceType& type, const Color& color, const bool logging) const {
    Board::PPieces pieces;
    for (const Board::Line& line : field) {
        for (const PCell& cell : line) {
            if (!cell->hasPiece()) {
                continue;
            }

            const Cell::PPiece piece = cell->getPiece();
            if (piece->getColor() == color && piece->getType() == type) {
                // if (logging) {
                //     // __android_log_print(ANDROID_LOG_INFO, "TRACKERS", "%s", "Board getPiecesByTypeAndColor add piece color");
                //     // __android_log_print(ANDROID_LOG_INFO, "TRACKERS", "%u", color);
                //     // __android_log_print(ANDROID_LOG_INFO, "TRACKERS", "%s", "Board getPiecesByTypeAndColor add piece type");
                //     // __android_log_print(ANDROID_LOG_INFO, "TRACKERS", "%u", type);
                //     // __android_log_print(ANDROID_LOG_INFO, "TRACKERS", "%s", "Board getPiecesByTypeAndColor add piece position");
                //     // __android_log_print(ANDROID_LOG_INFO, "TRACKERS", "%u", piece->getPosition().getI());
                //     // __android_log_print(ANDROID_LOG_INFO, "TRACKERS", "%u", piece->getPosition().getJ());
                // }
                pieces.push_back(piece);
            }
        }
    }
    return pieces;
}

Board::Route Board::getRouteForAllPiecesByColor(const Color& color) const {
    const PieceTypes allTypes = getAllPieceTypes();
    Board::Route route;
    for (const PieceType& type : allTypes) {
        Board::PPieces pieces = getPiecesByTypeAndColor(type, color);
        for (const Cell::PPiece& piece : pieces) {
            const Board::Route pieceRoute = piece->getRoute(*this);
            route.insert(route.end(), pieceRoute.begin(), pieceRoute.end());
        }
    }
    return route;
}

Cell::PPiece Board::getKingByColor(const Color& color) const {
    // TODO там где используются цвета добавить какюу-нибудь умную проверку, что цвет адекватный
    Board::PPieces kings = getPiecesByTypeAndColor(PieceType::KING, color, false);
//    __android_log_print(ANDROID_LOG_INFO, "TRACKERS", "%s", "Board getKingByColor kings size");
//    __android_log_print(ANDROID_LOG_INFO, "TRACKERS", "%d", kings.size());
    if (kings.size() != 1) {
        // error
        return nullptr;
    }
    return kings[0];
}

bool Board::hasKingAnotherColorNear(const Position& position, const Offset& offset, const Color& color) const {
    const Cell::PPiece king = getKingByColor(getAnotherColor(color));
    if (king == nullptr) {
        // error
        return false;
    }
    const Position& newPosition = MakeOffset(position, offset);
    return isPositionsNear(king->getPosition(), newPosition);
}

Piece::Route Board::getPossibleRouteForKingByColor(const Color& color) const {
    const Cell::PPiece king = getKingByColor(color);
    if (king == nullptr) {
        // error
        return {};
    }
    return king->getRoute(*this);
}

Board::PPieces Board::getAllPiecesByColor(const Color& color) const {
    Board::PPieces pieces;
    const PieceTypes types = getAllPieceTypes();
    for (const PieceType& type : types) {
        const Board::PPieces piecesByType = getPiecesByTypeAndColor(type, color);
        pieces.insert(pieces.end(), piecesByType.begin(), piecesByType.end());
    }
    return pieces;
}

bool Board::hasPossibleRouteInCheckByColor(const Color& color) const {
    // Верим, что сейчас есть шах этому цвету
    const Board::PPieces pieces = getAllPiecesByColor(color);
    for (const Cell::PPiece& piece : pieces) {
        const Board::Route route = piece->getRoute(*this);
        for (const Position& step : route) {
            if (!isStepDoCheck(piece->getPosition(), step, color)) {
                return true;
            }
        }
    }
    return false;
}

bool Board::isCheckForColor(const Color& color) const {
    const Cell::PPiece king = getKingByColor(color);
    if (king == nullptr) {
        // error
        return false;
    }
    return isCellByAttackForColor(king->getPosition(), color);
}

bool Board::isCheck() const {
    return isCheckForColor(Color::BLACK) || isCheckForColor(Color::WHITE);
}

bool Board::isMateForColor(const Color& color) const {
    if (!isCheckForColor(color)) {
        return false;
    }
    return !hasPossibleRouteInCheckByColor(color);
}

bool Board::isMate() const {
    return isMateForColor(Color::WHITE) || isMateForColor(Color::BLACK);
}

bool Board::isPateForColor(const Color& color) const {
    if (isCheckForColor(color)) {
        return false;
    }
    return getRouteForAllPiecesByColor(color).empty();
}

bool Board::isPate() const {
    return isPateForColor(Color::BLACK) || isPateForColor(Color::WHITE);
}

bool Board::isDrawForColor(const Color& color) const {
    return isDraw();
}

bool Board::isDraw() const {
    if (history && (history->getCountEqualMoves() >= 3 || history->getCountMovesWithoutEatingOrPawnsMove() >= 50)) {
        return true;
    }
    return checkDrawCondition();
}

GameState Board::getGameState() const {
// TODO вообще говоря тут надо не возвращать занчение, а записывать его в переменную и обновлять при необходимости
// тогда бы не было проблем, как тут. обязательно должна сначала идти проверка на мат, а потом проверка на шах
    if (isMateForColor(turnColor)) {
        return turnColor == Color::WHITE ? GameState::MATE_FOR_WHITE : GameState::MATE_FOR_BLACK;
    }
    if (isCheckForColor(turnColor)) {
        return turnColor == Color::WHITE ? GameState::CHECK_FOR_WHITE : GameState::CHECK_FOR_BLACK;
    }
    if (isPateForColor(turnColor)) {
        return GameState::PATE;
    }
    if (isDraw()) {
        return GameState::DRAW;
    }
    return GameState::ON_GOING;
}

bool Board::checkPiecesConfiguration(Board::PiecesByTypesAndColors& pieceConfiguration) const {
    Board::PiecesByTypesAndColors pieces = getPiecesByTypesAndColors();
    for (const auto& piecesByTypesColor : pieces) {
        for (const auto& piecesType : piecesByTypesColor.second) {
            if (pieceConfiguration[piecesByTypesColor.first][piecesType.first] != piecesType.second) {
                return false;
            }
        }
    }
    return true;
}

bool Board::setPiece(const Color color, const Position& position, const PieceType type) {
    if (hasPiece(position)) {
        return false;
    }
    PCell cell = getCellByPosition(position);
    // Эта проверка не нужна, потому что аналогичная есть уже в hasPiece
    if (!cell) {
        return false;
    }
    cell->changePieceTo(createPieceByType(position, color, type));
    return true;
}

void Board::removePiece(const Position& position) {
    PCell cell = getCellByPosition(position);
    if (cell) {
        cell->changePieceTo(nullptr);
    }
}

bool Board::checkDrawCondition() const {
    return checkOnlyKingInBoard() ||
        checkOnlyKingAndOneKnightInBoard() ||
        checkOnlyKingAndMonocolorBishopsInBoard();
}

bool Board::checkBishopsIsMonocolorForColor(const Color& color) const {
    const PPieces bishops = getPiecesByTypeAndColor(PieceType::BISHOP, color);
    if (!bishops.empty()) {
        const PCell cell = getCellByPosition(bishops[0]->getPosition());
        if (!cell) {
            return false;
        }
        const Color bishopsColor = cell->getColor();
        for (const Cell::PPiece& bishop : bishops) {
            const PCell bishopCell = getCellByPosition(bishop->getPosition());
            if (!bishopCell || bishopCell->getColor() != bishopsColor) {
                return false;
            }
        }
    }
    return true;
}

bool Board::checkBishopsIsMonocolor() const {
    return checkBishopsIsMonocolorForColor(Color::WHITE) && 
        checkBishopsIsMonocolorForColor(Color::BLACK);
}

bool Board::checkOnlyKingAndMonocolorBishopsInBoard() const {
    const Board::PiecesByTypes pieces = getPiecesByTypes();
    for (const auto& pieceType : pieces) {
        if (pieceType.first == PieceType::KING) {
            if (pieceType.second != 2) {
                return false;
            }
        } else if (pieceType.first == PieceType::BISHOP) {
            if (!checkBishopsIsMonocolor()) {
                return false;
            }
        } else {
            if (pieceType.second > 0) {
                return false;
            }
        }
    }
    return true;
}

// Как будто эти функции можно как-то разбить, и сделать функцию,
// которая будет проверять, что например есть ровно столько-то фигур такого-то типа
bool Board::checkOnlyKingAndOneKnightInBoard() const {
    const Board::PiecesByTypes pieces = getPiecesByTypes();
    for (const auto& pieceType : pieces) {
        if (pieceType.first == PieceType::KING) {
            if (pieceType.second != 2) {
                return false;
            }
        } else if (pieceType.first == PieceType::KNIGHT) {
            if (pieceType.second != 1) {
                return false;
            }
        } else {
            if (pieceType.second > 0) {
                return false;
            }
        }
    }
    return true;
}

bool Board::checkOnlyKingInBoard() const {
    const Board::PiecesByTypes pieces = getPiecesByTypes();
    for (const auto& pieceType : pieces) {
        std::string str = "Board checkOnlyKingInBoard piece type = " + std::to_string(static_cast<int>(pieceType.first)) + " , count = " +
                std::to_string(pieceType.second);
        // __android_log_print(ANDROID_LOG_INFO, "TRACKERS", "%s", str.c_str());
        if (pieceType.first == PieceType::KING) {
            if (pieceType.second != 2) {
                return false;
            }
        } else {
            if (pieceType.second > 0) {
                return false;
            }
        }
    }
    return true;
}

Board::PieceTypeAndColor Board::getPieceTypeAndColorFromCell(const PCell cell) const {
    if (cell->hasPiece()) {
        const Cell::PPiece piece = cell->getPiece();
        return PieceTypeAndColor{
            .type = piece->getType(),
            .color = piece->getColor(),
        };
    }
    return PieceTypeAndColor{};
}

Board::BoardRepresentation Board::getBoardRepresentation() const {
    BoardRepresentation boardRepresentation(8, LineRepresentation(8));
    for (size_t i = 0; i < 8; ++i) {
        for (size_t j = 0; j < 8; ++j) {
            boardRepresentation[i][j] = getPieceTypeAndColorFromCell(field[i][j]);
        }
    }
    return boardRepresentation;
}

Position Board::getKingPositionByColor(const Color& color) const {
    const Cell::PPiece king = getKingByColor(color);
    if (king == nullptr) {
        // error
        return Position();
    }
    return king->getPosition();
}

Board::PiecesByTypes Board::getPiecesByTypes() const {
    Board::PiecesByTypes piecesByTypes;
    const Colors allColors = getAllColors();
    const PieceTypes allPieceTypes = getAllPieceTypes();
    for (const Color& color : allColors) {
        for (const PieceType& type : allPieceTypes) {
            piecesByTypes[type] += getPiecesByTypeAndColor(type, color).size();
        }
    }
    return piecesByTypes;
}

Board::PiecesByTypes Board::getPiecesByTypesForColor(const Color& color) const {
    Board::PiecesByTypes piecesByTypes;
    const PieceTypes allPieceTypes = getAllPieceTypes();
    for (const PieceType& type : allPieceTypes) {
        piecesByTypes[type] = getPiecesByTypeAndColor(type, color).size();
    }
    return piecesByTypes;
}

Board::PiecesByTypesAndColors Board::getPiecesByTypesAndColors() const {
    Board::PiecesByTypesAndColors pieces;
    const Colors allColors = getAllColors();
    for (const Color& color : allColors) {
        pieces[color] = getPiecesByTypesForColor(color);
    }
    return pieces;
}

Board Board::getCopy() const {
    Board newBoard(mainColor, history->getCopy(), historyRecordManager->getCopy());
    newBoard.createEmptyColoredField();
    for (int i = 0; i < 8; ++i) {
        for (int j = 0; j < 8; ++j) {
            if (field[i][j]->hasPiece()) {
                // TODO вынести создание новой фигуры куда-нибудь еще
                const Cell::PPiece piece = field[i][j]->getPiece();
                Cell::PPiece newPiece = createPieceByType(piece->getPosition(), piece->getColor(), piece->getType());
                newPiece->setCountSteps(piece->getCountSteps());
                newBoard.field[i][j]->changePieceTo(newPiece);
            }
        }
    }
    return newBoard;
}

bool Board::isStepDoCheck(const Position& position, const Position& newPosition, const Color& color) const {
    Board copyBoard = getCopy();
    if (!hasPiece(position)) {
        return false;
    }
    const Cell::PPiece piece = getPieceByPosition(position);
    if (copyBoard.tryMovePiece(position, newPosition, false) != MoveType::NOT_MOVE) {
        return copyBoard.isCheckForColor(color);
    }
    return false;
}

bool Board::isStepDoCheck(const Position& position, const Offset& offset, const Color& color) const {
    return isStepDoCheck(position, MakeOffset(position, offset), color);
}

void Board::createFieldFromFen(const std::string& fen) {

}

std::string Board::getFen() const {
    std::string fen= "";
    for (int i = 0; i < 8; ++i) {
        int countEmptyCell = 0;
        for (int j = 0; j < 8; ++j) {
            if (!field[i][j]->hasPiece()) {
                ++countEmptyCell;
            } else {
                if (countEmptyCell > 0) {
                    fen += std::to_string(countEmptyCell);
                    countEmptyCell = 0;
                }
                fen += field[i][j]->getPiece()->getFen();
            }
        }
        if (countEmptyCell > 0) {
            fen += std::to_string(countEmptyCell);
            countEmptyCell = 0;
        }
        if (i < 7) {
            fen += "/";
        }
    }
    fen += " " + getTurnColorFen(turnColor);

    if (hasRightToDoAnyCastlingForAnyColor()) {
        fen += " ";
    }

    if (hasRightToDoKingSideCastling(Color::WHITE)) {
        fen += "K";
    }
    if (hasRightToDoQueenSideCastling(Color::WHITE)) {
        fen += "Q";
    }

    if (hasRightToDoKingSideCastling(Color::BLACK)) {
        fen += "k";
    }
    if (hasRightToDoQueenSideCastling(Color::BLACK)) {
        fen += "q";
    }

    fen += " ";
    if (history->getCountMoves() > 0 && history->getLastMove().passantPosition != Position(0, 0)) {
        // Тут уже начинаются расхождения с fen
        fen += history->getLastMove().passantPosition.toString();
    } else {
        fen += "-";
    }

    // TODO верим, что тут существует history
    fen += " " + history->getCountMovesWithoutEatingOrPawnsMove();
    fen += " " + history->getCountMoves();
}

void Board::clearField() {
    field.clear();
}

bool Board::hasRightToDoAnyCastlingForAnyColor() const {
    return hasRightToDoQueenSideCastling(Color::WHITE) ||
        hasRightToDoQueenSideCastling(Color::BLACK) ||
        hasRightToDoKingSideCastling(Color::WHITE) || 
        hasRightToDoKingSideCastling(Color::BLACK);
}

bool Board::hasRightToDoQueenSideCastling(const Color color) const {
    return false;
}

bool Board::hasRightToDoKingSideCastling(const Color color) const {
    return false;
}

MoveType Board::tryMovePiece(const Position& position, const Offset& offset) {
    const Position newPosition = MakeOffset(position, offset);
    return tryMovePiece(position, newPosition, false);
}

MoveType Board::tryMovePiece(const Position& position, const Position& newPosition, const bool logging) {
    if (!hasPiece(position)) {
        return MoveType::NOT_MOVE;
    }
    PCell cell = getCellByPosition(position);
    PCell newCell = getCellByPosition(newPosition);
    if (!cell || !newCell) {
        return MoveType::NOT_MOVE;
    }
    const Cell::PPiece piece = getPieceByPosition(position);
    const MoveType moveType = checkMoveIsSpecial(position, newPosition);
    if (moveType == MoveType::NOT_MOVE) {
        return MoveType::NOT_MOVE;
    }
    const Color color = piece->getColor();
    const PieceType type = piece->getType();
    historyRecordManager->setPieceTypeAndColor(type, color);
    historyRecordManager->onPieceMove(position, newPosition);
    if (moveType != MoveType::NOT_SPECIAL && moveType != MoveType::MAGIC_PAWN_TRANSFORMATION) {
        if (!tryDoSpecialMovePiece(position, newPosition, moveType)) {
            historyRecordManager->clearRecord();
            return MoveType::NOT_MOVE;
        }
        if (isCheckForColor(getAnotherColor(color))) {
            historyRecordManager->isPieceDoCheck();
        }
        historyRecordManager->onPieceDoSpecialMove(moveType);
        turnColor = getAnotherColor(turnColor);
        return moveType;
    }

    if (newCell->hasPiece()) {
        historyRecordManager->onPieceDoEatMove();
    }
    cell->changePieceTo(nullptr);
    newCell->changePieceTo(piece);
    piece->onPieceMove();
    piece->changePositionTo(newPosition);
    if (isCheckForColor(getAnotherColor(color))) {
        historyRecordManager->isPieceDoCheck();
    }
    if (piece->getType() == PieceType::PAWN && piece->getCountSteps() == 1) {
        int iDelta = getIDeltaBetweenPositions(position, newPosition);
        // То есть сделали длинный ход
        if (abs(iDelta) > 1) {
            // Ставим поле проходным
            historyRecordManager->setPassantPosition(MakeOffset(position, Offset(iDelta / 2, 0)));
        }

    }
    turnColor = getAnotherColor(turnColor);
    return moveType;
}

// TODO подумать над тем, стоит ли делать возможность превращать пешку в пешку (скорее всего нет)
bool Board::isMagicPawnTransformation() const {
    // TODO в целом можно удалять я думаю
    const int lineForMagicTransformMain = 0;
    const int lineForMagicTransformAnother = 7;

    for (const PCell& cell : field[lineForMagicTransformMain]) {
        if (!hasPiece(cell->getPosition())) {
            continue;
        }
        const Cell::PPiece piece = cell->getPiece();
        if (piece->getType() == PieceType::PAWN && piece->getColor() == mainColor) {
            return true;
        }
    }

    for (const PCell& cell : field[lineForMagicTransformAnother]) {
        if (!hasPiece(cell->getPosition())) {
            continue;
        }
        const Cell::PPiece piece = cell->getPiece();
        if (piece->getType() == PieceType::PAWN && piece->getColor() == anotherColor) {
            return true;
        }
    }
    return false;
}

bool Board::tryDoMagicPawnTransformation(const Position& position, const PieceType& newPieceType) {
    // TODO добавить проверки на то, что превращаться можем только в коня, слона, ладью и ферзя
    // TODO как будто глупо, что в isMagicPawnTransformation проверяются вообще все поля, а не только то, которое нам нужно
    if (!isMagicPawnTransformation()) {
        return false;
    }
    if (!hasPiece(position)) {
        return false;
    }
    if (!isPositionGoodForMagicTransformation(position)) {
        return false;
    }
    PCell magicCell = getCellByPosition(position);
    const Cell::PPiece pawn = getPieceByPosition(position);
    const Color& color = pawn->getColor();
    if (pawn->getType() != PieceType::PAWN) {
        return false;
    }
    historyRecordManager->setMagicPawnTransformationPieceType(newPieceType);
    magicCell->changePieceTo(nullptr);
    magicCell->changePieceTo(createPieceByType(position, color, newPieceType));
    return true;
}

bool Board::isCellByAttackForColor(const Position& position, const Color& color) const {
    const Piece::Route attackRoute = getAttackPositionsForColor(color);
//    __android_log_print(ANDROID_LOG_INFO, "TRACKERS", "%s", "Board isCellByAttackForColor position");
    return std::find(attackRoute.begin(), attackRoute.end(), position) != attackRoute.end();
}

Piece::Route Board::getSpecialMovesForPosition(const Position& position) const {
    if (!hasPiece(position)) {
        return {};
    }
    const Cell::PPiece piece = getPieceByPosition(position);
    Piece::Route route;

    // Рокировка
    if (piece->getType() == PieceType::KING) {
        if (piece->getCountSteps() > 0) {
            return {};
        }

        const PPieces rooks = getPiecesByTypeAndColor(PieceType::ROOK, piece->getColor());
        for (const Cell::PPiece& rook : rooks) {
            if (checkSpecialMoveCondition(position, rook->getPosition(), MoveType::CASTLING)) {
                route.push_back(rook->getPosition());
            }
        }
    }

    // Взятие на проходе
    if (piece->getType() == PieceType::PAWN) {
        const Piece::Route attackRoute = piece->getAttackRoute(*this);
        for (const Position& attackPosition : attackRoute) {
            if (checkSpecialMoveCondition(position, attackPosition, MoveType::PASSANT)) {
                route.push_back(attackPosition);
                // сюда можно еще дописать break - как выполнение инварианта, что может быть только одно взятие на проходе
            }
        }
        return route;
    }
    return route;
}

bool Board::checkSpecialMoveCondition(const Position& position, const Position& newPosition, const MoveType& moveType) const {
    // TODO заменить на свитч
    if (moveType == MoveType::CASTLING) {
        // TODO заменить подобные "верим" на ифы (возможно дефолтные через функции или классы, какой-нибудь класс чекер)
        // Верим, что нам пришел король и ладья на позициях
        if (!hasPiece(position) || !hasPiece(newPosition)) {
            return false;
        }
        const Cell::PPiece king = getPieceByPosition(position);
        const Cell::PPiece rook = getPieceByPosition(newPosition);

        if (king->getType() != PieceType::KING) {
            return false;
        }
        if (rook->getType() != PieceType::ROOK) {
            return false;
        }

        if (king->getColor() != rook->getColor()) {
            // error
            // TODO сделать броски и обработку исключений
            return false;
        }

        const Color color = king->getColor();
        // Фигуры не двигались
        if (king->getCountSteps() > 0 || rook->getCountSteps() > 0) {
            return false;
        }

        // Между ними поля свободные и не битые
        const Board::Route routeBetween = getHorizontalRouteBetweenPositions(position, newPosition);
        for (const Position& positionBetween : routeBetween) {
            if (hasPiece(positionBetween) || isCellByAttackForColor(positionBetween, color)) {
                return false;
            }
        }

        // Король не под шахом
        if (isCheckForColor(color)) {
            return false;
        }
        return true;
    }

    if (moveType == MoveType::PASSANT) {
        if (!hasPiece(position)) {
            return false;
        }
        const Cell::PPiece piece = getPieceByPosition(position);
        if (piece->getType() != PieceType::PAWN) {
            return false;
        }
        if (history->getCountMoves() == 0) {
            return false;
        }

        // Верим, что предыдущий ход был сделан другим цветом
        const HistoryRecord record = history->getLastMove();
        if (!hasPiece(record.endPosition)) {
            return false;
        }
        const Cell::PPiece pawn = getPieceByPosition(record.endPosition);
        if (pawn->getType() != PieceType::PAWN) {
            return false;
        }
        if (pawn->getCountSteps() != 1) {
            return false;
        }
        // Плохо, подумать как сделать без этого
        if (dynamic_cast<Pawn*>(pawn.get())->canDoBigStep()) {
            return false;
        }
        if (!isPositionsGoodForPassant(position, newPosition, record.endPosition)) {
            return false;
        }
        return true;
    }
    return false;
}

bool Board::checkMagicPawnTransformation(const Position& position, const Position& newPosition) const {
    if (!hasPiece(position)) {
        return false;
    }
    const Cell::PPiece pawn = getPieceByPosition(position);
    if (pawn->getType() != PieceType::PAWN) {
        return false;
    }
    return isPositionGoodForMagicTransformation(newPosition);
}

MoveType Board::checkMoveIsSpecial(const Position& position, const Position& newPosition) const {
    if (!hasPiece(position)) {
        // TODO сделать что-то вроде TMaybe, потому что возвращать тут not_special - это не хорошо
        //  тк непонятно, завершилось выполнение функции с ошибкой или ход действительно не спешал
        return MoveType::NOT_MOVE;
    }
    if (!isPositionInBoard(position) || !isPositionInBoard(newPosition)) {
        // TODO объединить условия и проверить, точно ли только тут надо
        //  возвращать NOT_MOVE (например - дублирующиеся проверки в checkMagicPawnTransformation,
        //  которые говорят об ошибке, а не о том, что это не спешал мув)
        return MoveType::NOT_MOVE;
    }

    // Рокировка
    if (checkSpecialMoveCondition(position, newPosition, MoveType::CASTLING)) {
        return MoveType::CASTLING;
    }

    // Взятие на проходе
    if (checkSpecialMoveCondition(position, newPosition, MoveType::PASSANT)) {
        return MoveType::PASSANT;
    }

    // Превращение пешки
    if (checkMagicPawnTransformation(position, newPosition)) {
        return MoveType::MAGIC_PAWN_TRANSFORMATION;
    }

    return MoveType::NOT_SPECIAL;
}

bool Board::tryDoSpecialMovePiece(const Position& position, const Position& newPosition, const MoveType& moveType) const {
    // TODO заменить на свитч
    if (moveType == MoveType::CASTLING) {
        // считаем, что выполнены проверки из checkMoveIsSpecial
        PCell kingCell = getCellByPosition(position);
        PCell rookCell = getCellByPosition(newPosition);
        if (!kingCell || !rookCell) {
            return false;
        }
        Cell::PPiece king = getPieceByPosition(position);
        Cell::PPiece rook = getPieceByPosition(newPosition);

        Offset kingOffset;
        Offset rookOffset;
        if (position.moreJ(newPosition)) {
            kingOffset = Offset(0, -2);
            rookOffset = Offset(0, 1);
        } else {
            kingOffset = Offset(0, 2);
            rookOffset = Offset(0, -1);
        }
        const Position newKingPosition = MakeOffset(position, kingOffset);
        PCell newKingCell = getCellByPosition(newKingPosition);
        if (!newKingCell) {
            return false;
        }
        newKingCell->changePieceTo(king);
        // TODO сделать юзинг noPiece = nullptr
        kingCell->changePieceTo(nullptr);
        king->changePositionTo(newKingPosition);
        king->onPieceMove();

        const Position newRookPosition = MakeOffset(newKingPosition, rookOffset);
        PCell newRookCell = getCellByPosition(newRookPosition);
        // TODO возможно эти проверки нужно убрать или хотя бы залогирвать, потому что
        // где-то они избыточны        
        if (!newRookCell) {
            return false;
        }
        newRookCell->changePieceTo(rook);
        rookCell->changePieceTo(nullptr);
        rook->changePositionTo(newRookPosition);
        rook->onPieceMove();
        return true;
    }

    if (moveType == MoveType::PASSANT) {
        PCell startCell = getCellByPosition(position);
        PCell endCell = getCellByPosition(newPosition);
        if (!startCell || !endCell) {
            return false;
        }
        const HistoryRecord lastRecord = history->getLastMove();
        PCell attackedPawnCell = getCellByPosition(lastRecord.endPosition);
        if (!attackedPawnCell) {
            return false;
        }
        Cell::PPiece pawn = getPieceByPosition(position);
        endCell->changePieceTo(pawn);
        startCell->changePieceTo(nullptr);
        pawn->changePositionTo(newPosition);
        pawn->onPieceMove();

        attackedPawnCell->changePieceTo(nullptr);

        historyRecordManager->onPieceDoEatMove();

        // TODO мб сделать запоминание какие фигуры были уничтожены
        return true;
    }
    return false;
}

Piece::Route Board::getPossiblePositionsToMove(const Position& position) const {
    // __android_log_print(ANDROID_LOG_INFO, "TRACKERS", "%s", "Board getPossiblePositionsToMove");
    if (!isPositionInBoard(position)) {
        return {};
    }
    if (!hasPiece(position)) {
        return {};
    }
    const Cell::PPiece piece = getPieceByPosition(position);
    if (piece->getColor() != turnColor) {
        return {};
    }
    Piece::Route route = piece->getRoute(*this);
    const Piece::Route specialMovesRoute = getSpecialMovesForPosition(position);
    route.insert(route.end(), specialMovesRoute.begin(), specialMovesRoute.end());
    return route;
}
