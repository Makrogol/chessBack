#pragma once

#include "move_type.h"

#include "utils/color.h"
#include "utils/position.h"
#include "utils/piece_type.h"

struct HistoryMove {
    // Цвет фигуры, которая сделала ход
    Color color;
    Position startPosition;
    Position endPosition;
    PieceType type;
    bool isCheck;
    bool isEating;
    MoveType specialMovesType;
    bool isMagicPawnTransformation;
    PieceType pawnMagicTransfomationPieceType;
    Position passantPosition; // Проходное поле (то есть пешка сделала большой ход и это ее ход, если бы она сделала малый)

    // TODO вынести в цпп
    bool operator==(const HistoryMove& secondMove) const {
        return
            (color == secondMove.color) &&
            (startPosition == secondMove.startPosition) &&
            (endPosition == secondMove.endPosition) &&
            (type == secondMove.type) &&
            (isCheck == secondMove.isCheck) &&
            (isEating == secondMove.isEating) &&
            (specialMovesType == secondMove.specialMovesType) &&
            (isMagicPawnTransformation == secondMove.isMagicPawnTransformation) &&
            (pawnMagicTransfomationPieceType == secondMove.pawnMagicTransfomationPieceType) &&
            (passantPosition == secondMove.passantPosition);
    }
};

struct HistoryRecord {
    // TODO сделать std::optional
    HistoryMove whiteMove;
    HistoryMove blackMove;

    // TODO вынести в цпп
    bool operator==(const HistoryRecord& secondRecord) const {
        return whiteMove == secondRecord.whiteMove
            && blackMove == secondRecord.blackMove;
    }

    // Тут получить ход с противоположным цветом
    HistoryMove getMoveForColor(Color color) const {
        // TODO как-то поправить элсы на то, чтобы не было тут NoColor
        return color == Color::WHITE ? blackMove : whiteMove;
    }

    // Тут получить ход с таким же цветом
    HistoryMove getMoveByColor(Color color) const {
        // TODO как-то поправить элсы на то, чтобы не было тут NoColor
        return color == Color::WHITE ? whiteMove : blackMove;
    }

    void setMoveByColor(Color color, HistoryMove&& move) {
        if (color == Color::WHITE) {
            whiteMove = std::move(move);
        } else {
            blackMove = std::move(move);
        }
    }
};
