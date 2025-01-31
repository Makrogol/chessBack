#include "parser.h"

// Position = std::pair<int, int>
// Координаты (целые) разделяются запятой
// В начале НЕ подписывается, что это position
// Пример
// TODO

// Route = std::vector<Position>
// Элементы разделяются точкой с запятой
// В начале подписывается, что это possibleMoves
// Пример
// TODO

// GameState = int
// В начале подписывается, что это gameState
// Пример
// TODO

// ResultDoMagicTransformation = bool
// В начале дописывается, что это resultDoMagicTransformation
// Пример
// TODO

// Color = int
// Просто одно число
// В начале подписывается, что это color
// Пример
// TODO

// MoveType = int
// В начале пишется, что это moveType
// Пример
// TODO

// PieceTypeAndColor = struct{PieceType, Color}
// Элементры разделяются запятой
// В начале НЕ пишется, что это PieceTypeAndColor
// Пример
// TODO

// BoardRepresentation = std::vector<std::vector<PieceTypeAndColor>>
// Элементы разделяются точкой с запятой
// В начале пишется, что это boardRepresentation
// Пример
// TODO


Parser::String Parser::positionToString(const Position& position) {
    const String iStr = std::to_string(position.getI());
    const String jStr = std::to_string(position.getJ());
    // мб тут тоже надо подписывать, что мы передаем именно позишион
    return join(",", {iStr, jStr});
}

Parser::String Parser::join(const String& joinCharacter, const Strings& joinStrings) {
    String result;
    for (const String& joinString : joinStrings) {
        result += joinString + joinCharacter;
    }
    const int substrN = result.size() - joinCharacter.size();
    return result.substr(0, substrN);
}

Parser::String Parser::color(const Color color) {
    const String colorStr = std::to_string(static_cast<int>(color));
    return "color:" + colorStr;
}

Parser::String Parser::possibleMoves(const Route& route) {
    Strings positionStrings;
    for (const Position& position : route) {
        positionStrings.push_back(positionToString(position));
    }
    return "possibleMoves:" + join(";", positionStrings);
}

Parser::String Parser::gameState(const GameState& state) {
    const String stateStr = std::to_string(static_cast<int>(state));
    return "gameState:" + stateStr;
}

Parser::String Parser::pieceTypeAndColor(const Board::PieceTypeAndColor& pieceTypeAndColor) {
    // TODO сделать каст всех енумов к строке
    // TODO поменять тут контракт, чтобы он для колоров был одинаковый везде
    String pieceTypeString = std::to_string(static_cast<int>(pieceTypeAndColor.type));
    String colorString = std::to_string(static_cast<int>(pieceTypeAndColor.color));
    return join(",", {pieceTypeString, colorString});
}

Parser::String Parser::boardRepresentation(const Board::BoardRepresentation& boardRepresentation) {
    Strings representation;
    for (const Board::LineRepresentation& line : boardRepresentation) {
        for (const Board::PieceTypeAndColor pieceTypeAndColorEl : line) {
            representation.push_back(pieceTypeAndColor(pieceTypeAndColorEl));
        }
    }
    return "boardRepresentation:" + join(";", representation);
}

Parser::String Parser::resultDoMagicTransformation(const bool result) {
    return "resultDoMagicTransformation:" + std::to_string(result);
}

Parser::String Parser::moveType(const MoveType& moveType) {
    return "moveType:" + std::to_string(static_cast<int>(moveType));
}
