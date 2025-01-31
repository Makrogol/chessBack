package com.serebryakov.cyclechesscpp.application.model.cppapi

external fun getKingPositionByColorCpp(color: String): String

external fun startGameCpp(mainColorString: String): String

external fun getPossibleMovesForPositionCpp(positionString: String): String

external fun tryDoMoveCpp(positionsString: String): String

external fun getGameStateCpp(): String

external fun tryDoMagicPawnTransformationCpp(positionAndPieceTypeString: String): String

external fun endGameCpp()