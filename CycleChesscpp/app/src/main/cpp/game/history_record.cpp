#include "history_record.h"

// TODO мб надо это перенести в какой-то класс мувами??
bool isEqualHistoryRecord(const HistoryRecord& firstRecord, const HistoryRecord& secondRecord) {
    return (firstRecord.color == secondRecord.color) &&
        (firstRecord.startPosition == secondRecord.startPosition) &&
        (firstRecord.endPosition == secondRecord.endPosition) &&
        (firstRecord.typeMovedPiece == secondRecord.typeMovedPiece) &&
        (firstRecord.isCheck == secondRecord.isCheck) &&
        (firstRecord.isEating == secondRecord.isEating) &&
        (firstRecord.specialMovesType == secondRecord.specialMovesType) &&
        (firstRecord.isMagicPawnTransformation == secondRecord.isMagicPawnTransformation) &&
        (firstRecord.pawnMagicTransfomationPieceType == secondRecord.pawnMagicTransfomationPieceType) &&
        (firstRecord.passantPosition == secondRecord.passantPosition);
}
