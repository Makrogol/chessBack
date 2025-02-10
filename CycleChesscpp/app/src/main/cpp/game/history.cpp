#include "history.h"

bool isMoveEatingOrPawnsMove(const HistoryMove& move) {
    return move.isEating || move.type == PieceType::PAWN;
}

void History::addHistoryRecord(const HistoryRecord& historyRecord) {
    if (isMoveEatingOrPawnsMove(historyRecord.whiteMove) || isMoveEatingOrPawnsMove(historyRecord.whiteMove)) {
        countMovesWithoutEatingOrPawnsMove = 0;
    } else {
        ++countMovesWithoutEatingOrPawnsMove;
    }

    if (history.size() > 0) {
        if (historyRecord == history[history.size() - 1]) {
            ++countEqualMoves;
        } else {
            countEqualMoves = 0;
        }
    }

    history.push_back(historyRecord);
}

History::PHistory History::getCopy() const {
    History newHistory;
    for (const HistoryRecord& record : history) {
        newHistory.addHistoryRecord(record);
    }
    return std::make_shared<History>(std::move(newHistory));
}

HistoryMove History::getLastMoveForColor(Color color) const {
    return getLastRecord().getMoveForColor(color);
}

void History::removeLastRecord() {
    if (history.size() > 0) {
        history.pop_back();
    }
}

void History::clearAll() {
    history.clear();
    countMovesWithoutEatingOrPawnsMove = 0;
    countEqualMoves = 0;
    countMoves = 0;
}

