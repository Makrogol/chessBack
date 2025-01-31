#include "history.h"

void History::addHistoryRecord(const HistoryRecord& historyRecord) {
    history.push_back(historyRecord);

    if (!historyRecord.isEating && historyRecord.typeMovedPiece != PieceType::PAWN) {
        ++countMovesWithoutEatingOrPawnsMove;
    } else {
        countMovesWithoutEatingOrPawnsMove = 0;
    }

    if (history.size() > 2) {
        if (isEqualHistoryRecord(historyRecord, history[history.size() - 3])) {
            ++countEqualMoves;
        } else {
            countEqualMoves = 0;
        }
    }
}

History::PHistory History::getCopy() const {
    History newHistory;
    for (const HistoryRecord& record : history) {
        newHistory.addHistoryRecord(record);
    }
    return std::make_shared<History>(std::move(newHistory));
}

