package com.serebryakov.cyclechesscpp.application.model.cppapi.utils

import com.serebryakov.cyclechesscpp.application.model.game.GameColor
import com.serebryakov.cyclechesscpp.application.model.game.Move
import com.serebryakov.cyclechesscpp.application.model.game.PieceType
import com.serebryakov.cyclechesscpp.application.model.game.Position
import com.serebryakov.cyclechesscpp.application.model.game.colorToString
import com.serebryakov.cyclechesscpp.application.model.game.pieceTypeToString

// TODO возможно стоит перенести все toString для разных использующихся тут классов сюда, в парсер
//  по идеи это его работа
class Parser {

    fun positionsToMove(positionFirst: Position, positionSecond: Position): String {
        return "positionsToMove:$positionFirst;$positionSecond"
    }

    fun positionsToMove(positions: Pair<Position, Position>): String {
        return positionsToMove(positions.first, positions.second)
    }

    fun move(move: Move): String {
        return move.toString()
    }

    fun positionToPossibleMove(position: Position): String {
        return position.toString()
    }

    fun positionAndPieceTypeForMagicPawnTransformation(position: Position, pieceType: PieceType): String {
        return "positionAndPieceType:$position;" + pieceTypeToString(pieceType)
    }

    fun color(color: GameColor): String {
        return colorToString(color)
    }
}
