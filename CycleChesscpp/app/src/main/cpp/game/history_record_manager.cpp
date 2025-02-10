#include "history_record_manager.h"


HistoryRecordManager::HistoryRecordManager() {
    clearRecord();
}

HistoryRecord HistoryRecordManager::getRecord() {
    HistoryRecord copyRecord = record;
    clearRecord();
    return copyRecord;
}

void HistoryRecordManager::setTurnColor(Color color) {
    turnColor = color;
}

void HistoryRecordManager::clearAll() {
    // TODO рефактор
    HistoryMove blackMove = record.blackMove;
    blackMove.color = Color::NO_COLOR;
    blackMove.endPosition = Position();
    blackMove.isCheck = false;
    blackMove.isEating = false;
    blackMove.specialMovesType = MoveType::NOT_SPECIAL;
    blackMove.startPosition = Position();
    blackMove.type = PieceType::EMPTY;
    blackMove.isMagicPawnTransformation = false;
    blackMove.pawnMagicTransfomationPieceType = PieceType::EMPTY;
    blackMove.passantPosition = Position(0, 0);
    record.setMoveByColor(turnColor, std::move(blackMove));

    HistoryMove whiteMove = record.whiteMove;
    whiteMove.color = Color::NO_COLOR;
    whiteMove.endPosition = Position();
    whiteMove.isCheck = false;
    whiteMove.isEating = false;
    whiteMove.specialMovesType = MoveType::NOT_SPECIAL;
    whiteMove.startPosition = Position();
    whiteMove.type = PieceType::EMPTY;
    whiteMove.isMagicPawnTransformation = false;
    whiteMove.pawnMagicTransfomationPieceType = PieceType::EMPTY;
    whiteMove.passantPosition = Position(0, 0);
    record.setMoveByColor(turnColor, std::move(whiteMove));
}

void HistoryRecordManager::clearRecord() {
    HistoryMove move = record.getMoveByColor(turnColor);
    move.color = Color::NO_COLOR;
    move.endPosition = Position();
    move.isCheck = false;
    move.isEating = false;
    move.specialMovesType = MoveType::NOT_SPECIAL;
    move.startPosition = Position();
    move.type = PieceType::EMPTY;
    move.isMagicPawnTransformation = false;
    move.pawnMagicTransfomationPieceType = PieceType::EMPTY;
    move.passantPosition = Position(0, 0);
    record.setMoveByColor(turnColor, std::move(move));
}

void HistoryRecordManager::onPieceDoSpecialMove(const MoveType& specialMovesType) {
    // TODO возможно можно сделать, чтобы getMoveByColor
    // Возвращало ссылку на мув, чтобы не париться с этим
    HistoryMove move = record.getMoveByColor(turnColor);
    move.specialMovesType = specialMovesType;
    record.setMoveByColor(turnColor, std::move(move));
}

void HistoryRecordManager::onPieceDoEatMove() {
    HistoryMove move = record.getMoveByColor(turnColor);
    move.isEating = true;
    record.setMoveByColor(turnColor, std::move(move));
}

void HistoryRecordManager::onPieceMove(const Position& position, const Position& newPosition) {
    HistoryMove move = record.getMoveByColor(turnColor);
    move.startPosition = position;
    move.endPosition = newPosition;
    record.setMoveByColor(turnColor, std::move(move));
}

void HistoryRecordManager::isPieceDoCheck() {
    HistoryMove move = record.getMoveByColor(turnColor);
    move.isCheck = true;
    record.setMoveByColor(turnColor, std::move(move));
}

void HistoryRecordManager::setPieceTypeAndColor(const PieceType& type, const Color& color) {
    HistoryMove move = record.getMoveByColor(turnColor);
    move.color = color;
    move.type = type;
    record.setMoveByColor(turnColor, std::move(move));
}

void HistoryRecordManager::setPassantPosition(const Position& position) {
    HistoryMove move = record.getMoveByColor(turnColor);
    move.passantPosition = position;
    record.setMoveByColor(turnColor, std::move(move));
}

void HistoryRecordManager::setMagicPawnTransformationPieceType(const PieceType& type) {
    HistoryMove move = record.getMoveByColor(turnColor);
    move.isMagicPawnTransformation = true;
    move.pawnMagicTransfomationPieceType = type;
    record.setMoveByColor(turnColor, std::move(move));
}

HistoryRecordManager::PHistoryRecordManager HistoryRecordManager::getCopy() const {
    HistoryRecordManager newHistoryRecordManager;
    newHistoryRecordManager.record = record;
    return std::make_shared<HistoryRecordManager>(newHistoryRecordManager);
}
