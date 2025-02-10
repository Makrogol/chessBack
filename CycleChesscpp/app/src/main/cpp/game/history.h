#pragma once

#include <memory>
#include <vector>

#include "history_record.h"

class History {
public:
    using PHistory = std::shared_ptr<History>;
    void addHistoryRecord(const HistoryRecord& historyRecord);

    void removeLastRecord();

    int getCountMovesWithoutEatingOrPawnsMove() const {
        return countMovesWithoutEatingOrPawnsMove;
    }

    void setCountMovesWithoutEatingOrPawnsMove(int countMovesWithoutEatingOrPawnsMove) {
        this->countMovesWithoutEatingOrPawnsMove = countMovesWithoutEatingOrPawnsMove;
    }

    int getCountEqualMoves() const {
        return countEqualMoves;
    }

    int getCountMoves() {
        if (history.size() > countMoves) {
            countMoves = history.size();
        }
        return countMoves;
    }

    void setcountEqualMoves(int countEqualMoves) {
        this->countEqualMoves = countEqualMoves;
    }

    void setCountMoves(int countMoves) {
        this->countMoves = countMoves;
    }

    HistoryRecord getLastRecord() const {
        // Верим, что перед этим была сделана провека, что есть элементы
        return history.back();
    }

    HistoryMove getLastMoveForColor(Color color) const;

    PHistory getCopy() const;

    void clearAll();

private:
    // TODO мб переделать на лист
    std::vector<HistoryRecord> history;
    // TODO переделать все инты на size_t или unsigned где это возможно
    int countMovesWithoutEatingOrPawnsMove = 0;
    int countEqualMoves = 0;
    int countMoves = 0;
};