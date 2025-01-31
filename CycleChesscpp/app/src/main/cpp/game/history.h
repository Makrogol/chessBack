#pragma once

#include <memory>
#include <vector>

#include "history_record.h"

class History {
public:
    using PHistory = std::shared_ptr<History>;
    void addHistoryRecord(const HistoryRecord& historyRecord);

    int getCountMovesWithoutEatingOrPawnsMove() const {
        return countMovesWithoutEatingOrPawnsMove;
    }

    int getCountEqualMoves() const {
        return countEqualMoves;
    }

    int getCountMoves() const {
        return history.size();
    }

    HistoryRecord getLastMove() const {
        // Верим, что перед этим была сделана провека, что есть элементы
        return history.back();
    }

    PHistory getCopy() const;

private:
    // TODO мб переделать на лист
    std::vector<HistoryRecord> history;
    // TODO переделать все инты на size_t или unsigned где это возможно
    int countMovesWithoutEatingOrPawnsMove = 0;
    int countEqualMoves = 0;
};