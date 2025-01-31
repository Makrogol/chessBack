#pragma once

#include "move_type.h"

#include "utils/color.h"
#include "utils/position.h"
#include "utils/piece_type.h"

struct HistoryRecord {
    // Цвет фигуры, которая сделала ход
    Color color;
    Position startPosition;
    Position endPosition;
    PieceType typeMovedPiece;
    bool isCheck;
    bool isEating;
    MoveType specialMovesType;
    bool isMagicPawnTransformation;
    PieceType pawnMagicTransfomationPieceType;
    Position passantPosition; // Проходное поле (то есть пешка сделала большой ход и это ее ход, если бы она сделала малый)
};

bool isEqualHistoryRecord(const HistoryRecord& firstRecord, const HistoryRecord& secondRecord);