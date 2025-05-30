package com.serebryakov.cyclechesscpp.application.model.cppapi

external fun getKingPositionByColorCpp(color: String): String

external fun startGameCpp(mainColorString: String)

external fun getFenCpp(): String

external fun getCurrentTurnColorCpp(): String

external fun startGameWithFenCpp(mainColorString: String, fenString: String)

external fun startGameWithReversedFenCpp(mainColorString: String, fenString: String)

external fun getPossibleMovesForPositionCpp(positionString: String): String

external fun tryDoMoveCpp(positionsString: String): String

external fun tryDoMoveV2Cpp(moveString: String): String

external fun getGameStateCpp(): String

external fun tryDoMagicPawnTransformationCpp(positionAndPieceTypeString: String): String

external fun endGameCpp()
