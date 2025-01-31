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



Unparser::Strings Unparser::split(const String& splitString, const String& splitCharacter) {
    Unparser::Strings splitStrings;
    int start, end = -1 * splitCharacter.size();
    do {
        start = end + splitCharacter.size();
        end = splitString.find(splitCharacter, start);
        splitStrings.push_back(splitString.substr(start, end - start));
    } while (end != -1);
    return splitStrings;
}

Unparser::TwoPositions Unparser::getPositionsToMove(const String& positionsString) {
    const Unparser::Strings nameAndPositions = split(positionsString, ":");
    const Unparser::Strings positions = split(nameAndPositions[1], ";");
    // if (positions.size() != 3 || positions[0] != "PositionsToMove") {
    //     // TODO бросить исключение
    // }
    return {getPosition(positions[0]), getPosition(positions[1])};
}

Position Unparser::getPosition(const String& positionString) {
    const Unparser::Strings positionElements = split(positionString, ",");
    // TODO тут сделать аналогичные проверки на содержимое
    // __android_log_print(ANDROID_LOG_INFO, "TRACKERS", "%s", "Unparser getPosition");
    // __android_log_print(ANDROID_LOG_INFO, "TRACKERS", "%s", positionElements[0].c_str());
    // __android_log_print(ANDROID_LOG_INFO, "TRACKERS", "%s", positionElements[1].c_str());
    return Position(std::atoi(positionElements[0].c_str()), std::atoi(positionElements[1].c_str()));
}

Position Unparser::getPositionToPossibleMove(const String& positionString) {
    return getPosition(positionString);
}

Unparser::PositionAndPieceType Unparser::getPositionAndPieceTypeForMagicPawnTransformation(const String& positionAndPieceTypeString) {
    const Unparser::Strings nameAndPositionsAndPieceType = split(positionAndPieceTypeString, ":");
    // TODO тут сделать аналогичные проверки на содержимое
    const Unparser::Strings positionAndPieceType = split(nameAndPositionsAndPieceType[1], ";");
    const Position position = getPosition(positionAndPieceType[0]);
    const int pieceTypeInt = std::atoi(positionAndPieceType[1].c_str());
    const PieceType pieceType = static_cast<PieceType>(pieceTypeInt);
    return {position, pieceType};
}


Color Unparser::getColor(const String& color) {
//    __android_log_print(ANDROID_LOG_INFO, "TRACKERS", "%s", "Unparser getMainColor");
//    __android_log_print(ANDROID_LOG_INFO, "TRACKERS", "%s", color.c_str());
    const Unparser::Strings nameAndColor = split(color, ":");
    // TODO тут сделать аналогичные проверки на содержимое
    const int colorInt = std::atoi(nameAndColor[1].c_str());
//    __android_log_print(ANDROID_LOG_INFO, "TRACKERS", "%s", nameAndColor[0].c_str());
//    __android_log_print(ANDROID_LOG_INFO, "TRACKERS", "%s", nameAndColor[1].c_str());
//    __android_log_print(ANDROID_LOG_INFO, "TRACKERS", "%d", colorInt);
    return static_cast<Color>(colorInt);
}
