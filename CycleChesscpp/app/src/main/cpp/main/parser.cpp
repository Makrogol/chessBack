#include "parser.h"

// Position = std::pair<int, int>
// Координаты (целые) разделяются запятой
// В начале НЕ подписывается, что это position
// Пример
// TODO

// Route = std::vector<Position>
// Элементы разделяются точкой с запятой
// В начале подписывается, что это possibleMoves или allPossibleMoves
// Пример
// TODO

// GameState = int
// В начале подписывается, что это gameState
// Пример
// TODO

// Move = std::tuple<Position, Position, PieceType>
// В начале НЕ подписывается, что это move
// Элементы разделяются . (PieceType добавляется только если он не PieceTYpe::Empty)
// Пример
// TODO

// ResultDoMagicTransformation = bool
// В начале дописывается, что это resultDoMagicTransformation
// Пример
// TODO

// ResultCanDoOneMoveAndDrawByFiftyMoves = bool
// В начале дописывается, что это resultCanDoOneMoveAndDrawByFiftyMoves
// Пример
// TODO

// ResultCanDoPassant = bool
// В начале дописывается, что это resultCanDoPassant
// Пример
// TODO

// Color = int
// Просто одно число
// В начале НЕ подписывается, что это color
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


String Parser::moveToString(const Move& move) {
    Strings moveElements;
    moveElements.reserve(3); // По максимуму - 2 позиции и тип для превращения пешки
    moveElements.emplace_back(positionToString(move.positionFirst));
    moveElements.emplace_back(positionToString(move.positionSecond));
    if (move.promotion != PieceType::EMPTY) {
        moveElements.emplace_back(std::to_string(static_cast<int>(move.promotion)));
    }
    return join(".", moveElements);
}

String Parser::positionToString(const Position& position) {
    const String iStr = std::to_string(position.getI());
    const String jStr = std::to_string(position.getJ());
    // мб тут тоже надо подписывать, что мы передаем именно позишион
    return join(",", {iStr, jStr});
}

String Parser::color(const Color color) {
    const String colorStr = std::to_string(static_cast<int>(color));
    return colorStr;
}

// TODO переменовать
String Parser::possibleMoves(const Board::Route& route) {
    Strings positionStrings;
    for (const Position& position : route) {
        positionStrings.push_back(positionToString(position));
    }
    return "possibleMoves:" + join(";", positionStrings);
}

String Parser::allPossibleMoves(const Board::Moves& moves) {
    Strings moveStrings;
    for (const Move& move : moves) {
        moveStrings.push_back(moveToString(move));
    }
    return "allPossibleMoves:" + join(";", moveStrings);
}

String Parser::gameState(const GameState& state) {
    const String stateStr = std::to_string(static_cast<int>(state));
    return "gameState:" + stateStr;
}

String Parser::pieceTypeAndColor(const Board::PieceTypeAndColor& pieceTypeAndColor) {
    // TODO сделать каст всех енумов к строке
    // TODO поменять тут контракт, чтобы он для колоров был одинаковый везде
    String pieceTypeString = std::to_string(static_cast<int>(pieceTypeAndColor.type));
    String colorString = std::to_string(static_cast<int>(pieceTypeAndColor.color));
    return join(",", {pieceTypeString, colorString});
}

String Parser::boardRepresentation(const Board::BoardRepresentation& boardRepresentation) {
    Strings representation;
    for (const Board::LineRepresentation& line : boardRepresentation) {
        for (const Board::PieceTypeAndColor pieceTypeAndColorEl : line) {
            representation.push_back(pieceTypeAndColor(pieceTypeAndColorEl));
        }
    }
    return "boardRepresentation:" + join(";", representation);
}

String Parser::resultDoMagicTransformation(const bool result) {
    return "resultDoMagicTransformation:" + std::to_string(result);
}

String Parser::resultCanDoOneMoveAndDrawByFiftyMoves(const bool result) {
    return "resultCanDoOneMoveAndDrawByFiftyMoves:" + std::to_string(result);
}

String Parser::resultCanDoPassant(const bool result) {
    return "resultCanDoPassant:" + std::to_string(result);
}

String Parser::moveType(const MoveType& moveType) {
    return "moveType:" + std::to_string(static_cast<int>(moveType));
}
