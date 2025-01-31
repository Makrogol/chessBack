#pragma once

#include <map>
#include <string>
#include <vector>

#include "cell.h"
#include "game_state.h"

#include "utils/color.h"
#include "utils/piece_type.h"

#include "history_record_manager.h"
#include "history.h"

class Piece;
class Position;
class Offset;

// TODO подумать, возможно некоторые методы надо вынести в утили
class Board {
public:
    struct PieceTypeAndColor{
        PieceType type = PieceType::EMPTY;
        Color color = Color::NO_COLOR;
    };

    using Route = std::vector<Position>;
    using PCell = std::shared_ptr<Cell>;
    using PPieces = std::vector<Cell::PPiece>;
    using Line = std::vector<PCell>;
    using Field = std::vector<Line>;
    using Cells = std::vector<Cell>;
    using PHistory = std::shared_ptr<History>;
    using PHistoryRecordManager = std::shared_ptr<HistoryRecordManager>;
    using PiecesByTypes = std::map<PieceType, int>;
    using PiecesByTypesAndColors = std::map<Color, PiecesByTypes>;
    using PiecesConfiguraions = std::vector<PiecesByTypesAndColors>;
    using LineRepresentation = std::vector<PieceTypeAndColor>;
    using BoardRepresentation = std::vector<LineRepresentation>;


    Board(const Color& mainColor, PHistory history, PHistoryRecordManager historyRecordManager);

    // TODO возможно мувы надо перенести все в какой-нибудь класс move_maker
    // TODO возможно надо перенести битые поля в приват (зачем это в паблик?)

    // Передается цвет фигур, которых атакуют
    Route getAttackPositionsForColor(const Color& color) const;

    Route getPossiblePositionsToMove(const Position& position) const;

    bool hasPieceSameColor(const Position& position, const Offset& offset, const Color& color) const;

    bool hasPieceAnotherColor(const Position& position, const Offset& offset, const Color& color) const;

    bool hasPiece(const Position& position, const Offset& offset) const;

    bool hasPiece(const Position& position) const;

    BoardRepresentation getBoardRepresentation() const;

    std::string getFen() const;

    Color getMainColor() const {
        return mainColor;
    }

    Color getCurrentTurn() const {
        return turnColor;
    }

    // TODO мб сделать для этого класс
    bool isMagicPawnTransformation() const;

    bool tryDoMagicPawnTransformation(const Position& position, const PieceType& newPieceType);

    // TODO сделать отдельный класс для определения игрового состояния
    bool isCheckForColor(const Color& color) const;

    bool isCheck() const;

    bool isMateForColor(const Color& color) const;

    bool isMate() const;

    bool isPateForColor(const Color& color) const;

    bool isPate() const;

    // TODO если этот метод не пригодится, его можно будет удалить
    bool isDrawForColor(const Color& color) const;

    bool isDraw() const;

    GameState getGameState() const;

    bool hasKingAnotherColorNear(const Position& position, const Offset& offset, const Color& color) const;

    // TODO мб стоит это перенести на уровень выше в Game, чтобы борда не зависила от истории
    bool isStepDoCheck(const Position& position, const Offset& offset, const Color& color) const;

    bool isStepDoCheck(const Position& position, const Position& newPosition, const Color& color) const;

    MoveType tryMovePiece(const Position& position, const Position& newPosition, const bool logging = true);

    MoveType tryMovePiece(const Position& position, const Offset& offset);

    // Передается цвет фигур, которых атакуют
    bool isCellByAttackForColor(const Position& position, const Color& color) const;

    // Может ли в теории игрок с данным цветом
    // Сделать три нуля (то есть что он не ходил ладьей)
    // Не ходил королем и все остальное
    bool hasRightToDoQueenSideCastling(const Color color) const;

    // Может ли в теории игрок с данным цветом
    // Сделать два нуля (то есть что он не ходил ладьей)
    // Не ходил королем и все остальное
    bool hasRightToDoKingSideCastling(const Color color) const;

    PiecesByTypes getPiecesByTypesForColor(const Color& color) const;

    // Суммарное количество для обоих цветов
    PiecesByTypes getPiecesByTypes() const;

    PiecesByTypesAndColors getPiecesByTypesAndColors() const;

    // Просто поле 8х8
    void createEmptyField();

    // С цветами
    void createEmptyColoredField();

    // С дефолтным расположением фигур
    void createDefaultField();

    // TODO это на будущее, надо будет потом сделать класс для настроек, которые будем парсить из строки, которая приходит в либу
    void createFieldFromFen(const std::string& fen);

    void clearField();

    Position getKingPositionByColor(const Color& color) const;

    Board getCopy() const;

    // TODO написать функцию, которая будет отдавать фен поля для определенного mainColor
    // Чтобы можно было модельку обучать только на одном mainColor и ей скармливать только один
    // Вообще говоря не хочется, чтобы было несколько mainColor
    // Точнее не хочется, чтобы board с ними работал. Хочется, чтобы решали что там за mainColor
    // Только позднее на этапе game наверное

private:
friend class TestBoard;
friend class TestPieces;

    bool hasRightToDoAnyCastlingForAnyColor() const;

    bool setPiece(const Color color, const Position& position, const PieceType type);

    void removePiece(const Position& position);

    void setPawnsDefault();

    PCell getCellByPosition(const Position& position) const;

    Cell::PPiece getPieceByPosition(const Position& Position) const;

    PPieces getPiecesByTypeAndColor(const PieceType& type, const Color& color, const bool logging = false) const;

    Cell::PPiece getKingByColor(const Color& color) const;

    PieceTypeAndColor getPieceTypeAndColorFromCell(const PCell cell) const;

    // TODO вынести специальные ходы в special_moves.h/cpp
    // Взятие на проходе и рокировка
    Route getSpecialMovesForPosition(const Position& position) const;

    MoveType checkMoveIsSpecial(const Position& position, const Position& newPosition) const;

    bool checkMagicPawnTransformation(const Position& position, const Position& newPosition) const;

    bool tryDoSpecialMovePiece(const Position& position, const Position& newPosition, const MoveType& moveType) const;

    bool checkSpecialMoveCondition(const Position& position, const Position& newPosition, const MoveType& moveType) const;

    Route getPossibleRouteForKingByColor(const Color& color) const;

    bool hasPossibleRouteInCheckByColor(const Color& color) const;

    PPieces getAllPiecesByColor(const Color& color) const;

    bool checkDrawCondition() const;

    bool checkPiecesConfiguration(PiecesByTypesAndColors& pieceConfiguration) const;

    bool checkOnlyKingInBoard() const;

    bool checkOnlyKingAndOneKnightInBoard() const;

    bool checkOnlyKingAndMonocolorBishopsInBoard() const;

    bool checkBishopsIsMonocolor() const;

    // Проверям, что только черные слоны (например) однопольные
    bool checkBishopsIsMonocolorForColor(const Color& color) const;

    Route getRouteForAllPiecesByColor(const Color& color) const;


    Field field;
    const PHistory history;
    const PHistoryRecordManager historyRecordManager;
    const Color mainColor = Color::NO_COLOR; // "Главный" цвет, то есть цвет фигур, за которые играет "основной" игрок
    const Color anotherColor = Color::NO_COLOR; // Обратный цвет к "главному"
    Color turnColor = Color::NO_COLOR; // Цвет того, кто сейчас ходит
};
