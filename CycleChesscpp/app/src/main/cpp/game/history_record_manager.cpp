#include "history_record_manager.h"


HistoryRecordManager::HistoryRecordManager() {
    clearRecord();
}

HistoryRecord HistoryRecordManager::getRecord() {
    HistoryRecord copyRecord = record;
    clearRecord();
    return copyRecord;
}

void HistoryRecordManager::clearRecord() {
    record.color = Color::NO_COLOR;
    record.endPosition = Position();
    record.isCheck = false;
    record.isEating = false;
    record.specialMovesType = MoveType::NOT_SPECIAL;
    record.startPosition = Position();
    record.typeMovedPiece = PieceType::EMPTY;
    record.isMagicPawnTransformation = false;
    record.pawnMagicTransfomationPieceType = PieceType::EMPTY;
}

void HistoryRecordManager::onPieceDoSpecialMove(const MoveType& specialMovesType) {
    record.specialMovesType = specialMovesType;
}

void HistoryRecordManager::onPieceDoEatMove() {
    record.isEating = true;
}

void HistoryRecordManager::onPieceMove(const Position& position, const Position& newPosition) {
    record.startPosition = position;
    record.endPosition = newPosition;
}

void HistoryRecordManager::isPieceDoCheck() {
    record.isCheck = true;
}

void HistoryRecordManager::setPieceTypeAndColor(const PieceType& type, const Color& color) {
    record.color = color;
    record.typeMovedPiece = type;
}

void HistoryRecordManager::setPassantPosition(const Position& position) {
    record.passantPosition = position;
}

void HistoryRecordManager::setMagicPawnTransformationPieceType(const PieceType& type) {
    record.isMagicPawnTransformation = true;
    record.pawnMagicTransfomationPieceType = type;
}

HistoryRecordManager::PHistoryRecordManager HistoryRecordManager::getCopy() const {
    HistoryRecordManager newHistoryRecordManager;
    newHistoryRecordManager.record = record;
    return std::make_shared<HistoryRecordManager>(newHistoryRecordManager);
}
