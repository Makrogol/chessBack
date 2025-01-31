package com.serebryakov.cyclechesscpp.application.model.game.pieces

import com.serebryakov.cyclechesscpp.application.model.game.PieceColor
import com.serebryakov.cyclechesscpp.application.model.game.PieceType
import com.serebryakov.cyclechesscpp.application.model.game.Route
import com.serebryakov.cyclechesscpp.application.model.game.Position
import com.serebryakov.cyclechesscpp.application.model.game.getAnotherColor
import com.serebryakov.cyclechesscpp.application.model.game.gamefield.Field

class KingPiece(override var color: PieceColor, override var position: Position) : BasePiece() {
    override var route: Route = emptyRoute()
    override var type: PieceType = PieceType.KING
    private val steps = listOf(
        Pair(1, 0),
        Pair(0, 1),
        Pair(1, 1),
        Pair(-1, 0),
        Pair(0, -1),
        Pair(-1, -1),
        Pair(-1, 1),
        Pair(1, -1)
    )

    override fun generatePieceRoute(field: Field): Route {
        route.clear()

        var newPos: Position
        for (el in steps) {
            if ((!canDoStepsOverBoard() && position.isOverBound(el)) || isStepDoCheck(field, position.offset(el)))
                continue
            newPos = position.offset(el)
            if (field.isCellBroken(newPos, color.getAnotherColor()) ||
                field[newPos].hasPieceSameColor(color) ||
                checkKingNear(field, newPos)
            )
                continue
            route.add(newPos)
        }


        // TODO сделать рокировку
        // Рокировка - возможность сходить на ладью
        // Рокировка с левой ладьей
//        newPos = Position(position.i, 0)
//        if (isNoStepBefore && field[newPos].piece?.isNoStepBefore == true)
//            route.add(newPos)
//
//        // Рокировка с правой ладьей
//        newPos = Position(position.i, size - 1)
//        if (isNoStepBefore && field[newPos].piece?.isNoStepBefore == true)
//            route.add(newPos)

        return route
    }

    override fun generateBrokenCellsRoute(field: Field): Route {
        route.clear()
        for (el in steps) {
            if ((!canDoStepsOverBoard() && position.isOverBound(el)) ||
                field[position.offset(el)].hasPieceSameColor(color)
            )
                continue
            route.add(position.offset(el))
        }
        return route
    }

    // Передаем сюда новый ход, куда хочет сходить король и смотрим на близость другого короля
    private fun checkKingNear(field: Field, newPosition: Position): Boolean {
        for (el in steps) {
            if ((!canDoStepsOverBoard(countSteps + 1) && newPosition.isOverBound(el)))
                continue
            if (field[newPosition.offset(el)].hasPieceTypeColor(color.getAnotherColor(), PieceType.KING))
                return true
        }

        val anotherKingPos = field.getPieceForColor(color.getAnotherColor(), PieceType.KING)
        val anotherKing = field[anotherKingPos].piece!!
        for (el in steps) {
            if ((!anotherKing.canDoStepsOverBoard() && anotherKingPos.isOverBound(el)))
                continue
            if (field[anotherKingPos.offset(el)].hasPieceTypeColor(anotherKing.color.getAnotherColor(), PieceType.KING))
                return true
        }
        return false
    }

    override fun getCopy(): KingPiece {
        val newPiece = KingPiece(color, position.getCopy())
        newPiece.countSteps = countSteps
        newPiece.type = type
        return newPiece
    }
}