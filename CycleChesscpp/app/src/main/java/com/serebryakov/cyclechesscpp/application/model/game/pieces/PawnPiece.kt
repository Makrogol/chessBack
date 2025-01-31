package com.serebryakov.cyclechesscpp.application.model.game.pieces

import com.serebryakov.cyclechesscpp.application.model.game.PieceColor
import com.serebryakov.cyclechesscpp.application.model.game.PieceType
import com.serebryakov.cyclechesscpp.application.model.game.Route
import com.serebryakov.cyclechesscpp.application.model.game.Position
import com.serebryakov.cyclechesscpp.application.model.game.toGameColor
import com.serebryakov.cyclechesscpp.application.model.game.gamefield.Field


// Пешка
class PawnPiece(override var color: PieceColor, override var position: Position) : BasePiece() {
    override var route: Route = emptyRoute()
    override var type: PieceType = PieceType.PAWN
    // TODO придумать как отслеживать сделала она один ход уже или нет

    override fun generateBrokenCellsRoute(field: Field): Route {
        route.clear()

        val gameColor = field.getMainColor()
        val iOffset = if (gameColor == color.toGameColor()) -1 else 1

        // TODO реализовать взятие на проходе

        var newPos = position.offset(iOffset, -1)
        if (!field[newPos].hasPieceSameColor(color) && !(!canDoStepsOverBoard() && position.isOverBound(iOffset, -1)))
            route.add(newPos)
        newPos = position.offset(iOffset, 1)
        if (!field[newPos].hasPieceSameColor(color) && !(!canDoStepsOverBoard() && position.isOverBound(iOffset, 1)))
            route.add(newPos)

        return route
    }


    override fun generatePieceRoute(field: Field): Route {
        route.clear()

        val gameColor = field.getMainColor()
        val iOffset = if (gameColor == color.toGameColor()) -1 else 1

        if (!field[position.offset(iOffset)].hasPiece() &&
            !(!canDoStepsOverBoard() && position.isOverBound(iOffset)) &&
            !isStepDoCheck(field, position.offset(iOffset))
        ) {
            route.add(position.offset(iOffset))
            if ((countSteps == 0) && !field[position.offset(2 * iOffset)].hasPiece() &&
                !(!canDoStepsOverBoard() && position.isOverBound(2 * iOffset)) &&
                !isStepDoCheck(field, position.offset(2 * iOffset))
            )
                route.add(position.offset(2 * iOffset))
        }

        if (field[position.offset(iOffset, 1)].hasPieceAnotherColor(color) &&
            !(!canDoStepsOverBoard() && position.isOverBound(iOffset, 1)) &&
            !isStepDoCheck(field, position.offset(iOffset, 1))
        )
            route.add(position.offset(iOffset, 1))
        if (field[position.offset(iOffset, -1)].hasPieceAnotherColor(color) &&
            !(!canDoStepsOverBoard() && position.isOverBound(iOffset, -1)) &&
            !isStepDoCheck(field, position.offset(iOffset, -1))
        )
            route.add(position.offset(iOffset, -1))
        return route
    }

    override fun getCopy(): PawnPiece {
        val newPiece = PawnPiece(color, position.getCopy())
        newPiece.countSteps = countSteps
        newPiece.type = type
        return newPiece
    }
}