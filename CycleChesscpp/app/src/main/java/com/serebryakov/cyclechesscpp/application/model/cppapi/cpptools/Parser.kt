package com.serebryakov.cyclechesscpp.application.model.cppapi.cpptools

import com.serebryakov.cyclechesscpp.application.model.game.GameColor
import com.serebryakov.cyclechesscpp.application.model.game.PieceType
import com.serebryakov.cyclechesscpp.application.model.game.Position
import com.serebryakov.cyclechesscpp.application.model.game.colorToString
import com.serebryakov.cyclechesscpp.application.model.game.pieceTypeToString

class Parser {

    fun positionsToMove(positionFirst: Position, positionSecond: Position): String {
        return "positionsToMove:$positionFirst;$positionSecond"
    }

    fun positionsToMove(positions: Pair<Position, Position>): String {
        return positionsToMove(positions.first, positions.second)
    }


    fun positionToPossibleMove(position: Position): String {
        return position.toString()
    }

    fun positionAndPieceTypeForMagicPawnTransformation(position: Position, pieceType: PieceType): String {
        return "positionAndPieceType:$position;" + pieceTypeToString(pieceType)
    }

    fun color(color: GameColor): String {
        return "color:" + colorToString(color)
    }
}