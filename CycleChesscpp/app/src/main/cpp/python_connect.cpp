#include "python_connect.h"

#include <cstring>

#include "main_factory.h"

char* stringToCharP(std::string str) {
    char* result = new char[str.length() + 1];
    strcpy(result, str.c_str());
    return result;
}

void startGame(char* mainColor) {
    MainFactory::getOrCreateMain()->startGame(std::string(mainColor));
}

void endGame() {
    MainFactory::destroyMain();
}

void startGameWithFen(char* mainColor, char* fen) {
    return MainFactory::getOrCreateMain()->startGameWithFen(std::string(mainColor), std::string(fen));
}

char* getCurrentTurn() {
    return stringToCharP(MainFactory::getOrCreateMain()->getCurrentTurn());
}

char* getPossibleMovesForPosition(char* position) {
    return stringToCharP(MainFactory::getOrCreateMain()->getPossibleMovesForPosition(std::string(position)));
}

char* tryDoMove(char* positions) {
    return stringToCharP(MainFactory::getOrCreateMain()->tryDoMove(std::string(positions)));
}

char* getGameState() {
    return stringToCharP(MainFactory::getOrCreateMain()->getGameState());
}

char* tryDoMagicPawnTransformation(char* positionAndPieceType) {
    return stringToCharP(MainFactory::getOrCreateMain()->tryDoMagicPawnTransformation(std::string(positionAndPieceType)));
}

char* getKingPositionByColor(char* color) {
    return stringToCharP(MainFactory::getOrCreateMain()->getKingPositionByColor(std::string(color)));
}

char* getBoardRepresentation() {
    return stringToCharP(MainFactory::getOrCreateMain()->getBoardRepresentation());
}
