#include "unparser.h"

// #include <android/log.h>
#include <cstring>

// Position = std::pair<int, int>
// Координаты (целые) разделяются запятой
// В начале НЕ подписывается, что это position
// Пример
// TODO

// PieceType = int
// Просто одно число без подписей (мб везде добавить подписей??)
// Пример
// TODO

// Move = std::tuple<Position, Position, PieceType>
// В начале НЕ подписывается, что это move
// Элементы разделяются . (PieceType добавляется только если он не PieceTYpe::Empty)
// Пример
// TODO

// Color = int
// Просто одно число
// В начале подписывается, что это color
// Пример
// TODO

// PositionsToMove = std::pair<Position, Position>
// Элементы разделяются точкой с запятой
// В начале подписывается, что это positionsToMove
// Пример
// TODO

// PositionAndPieceType = std::pair<Position, PieceType>
// Элементы разделяются точкой с запятой
// В начале подписывается, что это positionAndPieceType
// Пример
// TODO


Unparser::TwoPositions Unparser::getPositionsToMove(const String& positionsString) {
    const Strings nameAndPositions = split(positionsString, ":");
    const Strings positions = split(nameAndPositions[1], ";");
    // if (positions.size() != 3 || positions[0] != "PositionsToMove") {
    //     // TODO бросить исключение
    // }
    return {getPosition(positions[0]), getPosition(positions[1])};
}

Position Unparser::getPosition(const String& positionString) {
    const Strings positionElements = split(positionString, ",");
    // TODO тут сделать аналогичные проверки на содержимое
    // __android_log_print(ANDROID_LOG_INFO, "TRACKERS", "%s", "Unparser getPosition");
    // __android_log_print(ANDROID_LOG_INFO, "TRACKERS", "%s", positionElements[0].c_str());
    // __android_log_print(ANDROID_LOG_INFO, "TRACKERS", "%s", positionElements[1].c_str());
    return Position(stringToInt(positionElements[0]), stringToInt(positionElements[1]));
}

Position Unparser::getPositionToPossibleMove(const String& positionString) {
    return getPosition(positionString);
}

Move Unparser::getMove(const String& moveString) {
    const Strings moveElements = split(moveString, ".");
    const Position positionFirst = getPosition(moveElements[0]);
    const Position positionSecond = getPosition(moveElements[1]);
    const PieceType promotionType = 
        moveElements.size() > 2 ? 
            static_cast<PieceType>(stringToInt(moveElements[2])) : 
            PieceType::EMPTY;
    return Move(positionFirst, positionSecond, promotionType);
}

Unparser::PositionAndPieceType Unparser::getPositionAndPieceTypeForMagicPawnTransformation(const String& positionAndPieceTypeString) {
    const Strings nameAndPositionsAndPieceType = split(positionAndPieceTypeString, ":");
    // TODO тут сделать аналогичные проверки на содержимое
    const Strings positionAndPieceType = split(nameAndPositionsAndPieceType[1], ";");
    const Position position = getPosition(positionAndPieceType[0]);
    const int pieceTypeInt = stringToInt(positionAndPieceType[1]);
    const PieceType pieceType = static_cast<PieceType>(pieceTypeInt);
    return {position, pieceType};
}


Color Unparser::getColor(const String& color) {
//    __android_log_print(ANDROID_LOG_INFO, "TRACKERS", "%s", "Unparser getMainColor");
//    __android_log_print(ANDROID_LOG_INFO, "TRACKERS", "%s", color.c_str());
    // const Strings nameAndColor = split(color, ":");
    // TODO тут сделать аналогичные проверки на содержимое
    // const int colorInt = stringToInt(nameAndColor[1]);
//    __android_log_print(ANDROID_LOG_INFO, "TRACKERS", "%s", nameAndColor[0].c_str());
//    __android_log_print(ANDROID_LOG_INFO, "TRACKERS", "%s", nameAndColor[1].c_str());
//    __android_log_print(ANDROID_LOG_INFO, "TRACKERS", "%d", colorInt);
    return static_cast<Color>(stringToInt(color));
}
