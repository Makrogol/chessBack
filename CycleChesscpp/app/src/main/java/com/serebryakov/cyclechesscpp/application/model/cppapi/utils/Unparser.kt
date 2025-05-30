package com.serebryakov.cyclechesscpp.application.model.cppapi.utils

import com.serebryakov.cyclechesscpp.application.model.game.GameColor
import com.serebryakov.cyclechesscpp.application.model.game.GameState
import com.serebryakov.cyclechesscpp.application.model.game.Move
import com.serebryakov.cyclechesscpp.application.model.game.MoveType
import com.serebryakov.cyclechesscpp.application.model.game.PieceType
import com.serebryakov.cyclechesscpp.application.model.game.Position
import com.serebryakov.cyclechesscpp.application.model.game.Route
import com.serebryakov.cyclechesscpp.application.model.game.ToMoveType
import com.serebryakov.cyclechesscpp.application.model.game.toGameColor
import com.serebryakov.cyclechesscpp.application.model.game.toGameState
import com.serebryakov.cyclechesscpp.application.model.game.toPieceType
import com.serebryakov.cyclechesscpp.foundation.tools.toBoolean

class Unparser {

    fun getPositionToMove(positionsToMoveString: String): Pair<Position, Position> {
        val nameAndPositionToMove = positionsToMoveString.split(':')
        val positionsToMove = nameAndPositionToMove[1].split(';')
        var positionFirst = Position()
        var positionSecond = Position()
        var isFirstFill = false
        for (positionString in positionsToMove) {
            if (positionString == "") {
                continue
            }
            if (isFirstFill) {
                positionSecond = getPosition(positionString)
            } else {
                isFirstFill = true
                positionFirst = getPosition(positionString)
            }
        }
        return Pair(positionFirst, positionSecond)
    }

    fun getPossibleMoves(possibleMovesString: String): Route {
        val route: Route = mutableListOf()
        val nameAndPossibleMoves = possibleMovesString.split(':')
        // TODO бросать исключения
//        if (nameAndPossibleMoves[0] != "possibleMoves") {
//            return "error"
//        }
        val positionsString = nameAndPossibleMoves[1].split(';')
        for (positionString in positionsString) {
            if (positionString == "") {
                continue
            }
            route.add(getPosition(positionString))
        }
        return route
    }

    fun getColor(colorString: String): GameColor {
        return colorString.toInt().toGameColor()
    }

    fun getMove(moveString: String): Move {
        val moveElement = moveString.split('.')
        val move = Move(getPosition(moveElement[0]), getPosition(moveElement[1]))
        if (moveElement.size > 2) {
            move.promotion = moveElement[2].toInt().toPieceType()
        }
        return move
    }

    fun getPosition(position: String): Position {
        val positionElement = position.split(',')
        return Position(positionElement[0].toInt(), positionElement[1].toInt())
    }

    fun getGameState(gameStateString: String): GameState {
        val nameAndGameState = gameStateString.split(':')
        return nameAndGameState[1].toInt().toGameState()
    }

    fun getResultDoMove(resultDoMoveString: String): Boolean {
        val nameAndResultDoMove = resultDoMoveString.split(':')
        return nameAndResultDoMove[1].toBoolean()
    }

    fun getResultDoMagicTransformation(resultDoMagicTransformationString: String): Boolean {
        val nameAndResultDoMagicTransformation = resultDoMagicTransformationString.split(':')
        return nameAndResultDoMagicTransformation[1].toBoolean()
    }

    fun getMoveType(moveTypeString: String): MoveType {
        val nameAndMoveType = moveTypeString.split(':')
        return nameAndMoveType[1].ToMoveType()
    }
}