package com.serebryakov.cyclechesscpp.application.model.game.pieces

import com.serebryakov.cyclechesscpp.application.model.game.PieceColor
import com.serebryakov.cyclechesscpp.application.model.game.PieceType
import com.serebryakov.cyclechesscpp.application.model.game.Route
import com.serebryakov.cyclechesscpp.application.model.game.Position
import com.serebryakov.cyclechesscpp.application.model.game.gamefield.Field

class KnightPiece(override var color: PieceColor, override var position: Position) : BasePiece() {
    override var route: Route = emptyRoute()
    override var type: PieceType = PieceType.KNIGHT
    private val steps = listOf(
        Pair(2, 1),
        Pair(2, -1),
        Pair(-2, 1),
        Pair(-2, -1),
        Pair(1, -2),
        Pair(1, 2),
        Pair(-1, 2),
        Pair(-1, -2)
    )

    override fun generatePieceRoute(field: Field): Route {
        route.clear()
        var newPos: Position
        for (el in steps) {
            if ((!canDoStepsOverBoard() && position.isOverBound(el)) || isStepDoCheck(field, position.offset(el)))
                continue
            newPos = position.offset(el)
            if (!field[newPos].hasPieceSameColor(color))
                route.add(newPos)
        }
        return route
    }

    override fun generateBrokenCellsRoute(field: Field): Route {
        route.clear()
        var newPos: Position
        for (el in steps) {
            if ((!canDoStepsOverBoard() && position.isOverBound(el)))
                continue
            newPos = position.offset(el)
            if (!field[newPos].hasPieceSameColor(color))
                route.add(newPos)
        }
        return route
    }

    private fun generateKnightRoute(field: Field) {

    }

    override fun getCopy(): KnightPiece {
        val newPiece = KnightPiece(color, position.getCopy())
        newPiece.countSteps = countSteps
        newPiece.type = type
        return newPiece
    }
}