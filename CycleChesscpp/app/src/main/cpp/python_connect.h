#pragma once

#ifdef  __cplusplus
extern "C" {
#endif

void startGame(char* mainColor);
void endGame();
void startGameWithFen(char* mainColor, char* fen);
char* getCurrentTurn();
char* getPossibleMovesForPosition(char* position);
char* tryDoMove(char* positions);
char* getGameState();
char* tryDoMagicPawnTransformation(char* positionAndPieceType);
char* getKingPositionByColor(char* color);
char* getBoardRepresentation();

#ifdef  __cplusplus
}
#endif
