#pragma once

#include <memory>

#include "history_record.h"

// TODO мб сделать базовый класс для манагеров
class HistoryRecordManager {
public:
    using PHistoryRecordManager = std::shared_ptr<HistoryRecordManager>;

    HistoryRecordManager();

    HistoryRecord getRecord();

    void clearRecord();

    void onPieceDoSpecialMove(const MoveType& specialMovesType);

    void onPieceDoEatMove();

    void onPieceMove(const Position& position, const Position& newPosition);

    void isPieceDoCheck();

    void setPieceTypeAndColor(const PieceType& type, const Color& color);

    // TODO убрать ссылки на енамы
    void setMagicPawnTransformationPieceType(const PieceType& type);

    void setPassantPosition(const Position& position);

    PHistoryRecordManager getCopy() const;

private:
    HistoryRecord record;
};