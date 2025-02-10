 #pragma once

// #include <android/log.h>
#include <iostream>
#include <algorithm>
#include <string>
#include <vector>

#include "utils/color.h"
#include "utils/offset.h"
#include "utils/position.h"
#include "utils/piece_type.h"
#include "board.h"


class Piece {
public:
    // TODO подумать на коньсьюмом пешек королем один раз за игру
    // TODO возможно надо будет переделать на list (так как часто надо будет удалять элементы)
    using Route = std::vector<Position>;
    using Offsets = std::vector<Offset>;
    using PPiece = std::shared_ptr<Piece>;

    // TODO добавить проверку на то, является фигура легкой или нет

    Piece(const Position& position, const Color& color, const PieceType& type) :
        position(position), color(color), type(type) {}

    PieceType getType() const {
        return type;
    }

    Position getPosition() const {
        return position;
    }

    Color getColor() const {
        return color;
    }

    std::string getFen() const {
        if (color == Color::WHITE) {
            return std::string{static_cast<char>(std::toupper(toFen(type)))};
        }
        return std::string{static_cast<char>(std::tolower(toFen(type)))};
    }

    // TODO вынести в утили
    static Board::PieceTypeAndColor getPieceTypeAndColorFromFen(char fenElement) {
        PieceType type = getPieceTypeFromFen(fenElement);
        if (isupper(fenElement)) {
            return {
                .type = type,
                .color = Color::WHITE,
            };
        }
        return {
            .type = type,
            .color = Color::BLACK,
        };
    }

    int getCountSteps() const {
        return countSteps;
    }

    void changePositionTo(const Position& position) {
        this->position = position;
    }

    void onPieceMove() {
        countSteps += 1;
    }

    void setCountSteps(int countSteps) {
        this->countSteps = countSteps;
    }

    bool canDoStepOverBoard() const {
        // Нужно, чтобы фигура сделала хотя бы 3 хода, чтобы могла ходить за границы поля
        return countSteps > 2;
    }

    virtual Route getRoute(const std::shared_ptr<Board> board) const {
        return getRoute(board.get());
    }

    virtual Route getRoute(const Board* board) const {
        return getRoute(*board);
    }

    virtual Route getRoute(const Board& board) const {
        Offsets offsets = getRouteImpl(board);
        Route route;
        for (const Offset& offset : offsets) {
            // Если НЕ наступаем на фигуру того же цвета и НЕ делаем шах, то добавляем такой ход
            if (!board.hasPieceSameColor(position, offset, color) && !board.isStepDoCheck(position, offset, color)) {
                Position newPosition = MakeOffset(position, offset);
                if (std::find(route.begin(), route.end(), newPosition) == route.end()) {
                    route.push_back(newPosition);
                }
            }
        }
        return route;
    }

    virtual Route getAttackRoute(const Board& board) const {
        // TODO очень странно, что это работает, по идеи не должно работать
        Offsets offsets = getAttackRouteImpl(board);
        Route route;
        for (const Offset& offset : offsets) {
            // Если НЕ наступаем на фигуру того же цвета, то добавляем такой ход
            if (!board.hasPieceSameColor(position, offset, color)) {
                Position newPosition = MakeOffset(position, offset);
                if (std::find(route.begin(), route.end(), newPosition) == route.end()) {
                    route.push_back(newPosition);
                }
            }
        }
        return route;
    }

protected:
    const PieceType type = PieceType::EMPTY;
    Position position;
    const Color color = Color::NO_COLOR;
    // TODO мб вернуть анасайнд
    int countSteps = 0;



    void safeInsertOffset(Offsets& offsets, const Offset& offset) const {
        // Добавляем сдвиг только в случае, если это обычный ход (без выхода за границу)
        // или если это позволенный выход за границу
        if (isStepOverBoard(position, offset)) {
            if (canDoStepOverBoard()) {
                offsets.push_back(offset);
            }
        } else {
            offsets.push_back(offset);
        }
    }

    void safeInsertOffset(Offsets& offsets, const Position& offsetPosition, const Offset& offset) const {
        // Добавляем сдвиг только в случае, если это обычный ход (без выхода за границу)
        // или если это позволенный выход за границу
        if (isStepOverBoard(offsetPosition, offset)) {
            if (canDoStepOverBoard()) {
                offsets.push_back(offset);
            }
        } else {
            offsets.push_back(offset);
        }
    }

    virtual Offsets getRouteImpl(const Board& board) const = 0;
    virtual Offsets getAttackRouteImpl(const Board& board) const = 0;
};
