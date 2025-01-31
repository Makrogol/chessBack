package com.serebryakov.cyclechesscpp.application.model.game.pieces

import com.serebryakov.cyclechesscpp.application.model.game.PieceColor
import com.serebryakov.cyclechesscpp.application.model.game.PieceType
import com.serebryakov.cyclechesscpp.application.model.game.Position
import com.serebryakov.cyclechesscpp.application.model.game.Route
import com.serebryakov.cyclechesscpp.application.model.game.gamefield.Field
import com.serebryakov.cyclechesscpp.application.model.game.gamefield.size

class RookPiece(override var color: PieceColor, override var position: Position) : BasePiece() {
    override var route: Route = emptyRoute()
    override var type: PieceType = PieceType.ROOK

    override fun generatePieceRoute(field: Field): Route {
        route.clear()
        var newPos: Position
        for (i in 1 until size) {
            if ((!canDoStepsOverBoard() && position.isOverBound(i)) || isStepDoCheck(field, position.offset(i)))
                continue
            newPos = position.offset(i)
            if (field[newPos].hasPieceSameColor(color))
                break
            if (field[newPos].hasPieceAnotherColor(color)) {
                route.add(newPos)
                break
            }
            route.add(newPos)
        }
        for (i in 1 until size) {
            if ((!canDoStepsOverBoard() && position.isOverBound(-i)) || isStepDoCheck(field, position.offset(-i)))
                continue
            newPos = position.offset(-i)
            if (field[newPos].hasPieceSameColor(color))
                break
            if (field[newPos].hasPieceAnotherColor(color)) {
                route.add(newPos)
                break
            }
            route.add(newPos)
        }
        for (i in 1 until size) {
            if ((!canDoStepsOverBoard() && position.isOverBound(0, i)) || isStepDoCheck(field, position.offset(0, i)))
                continue
            newPos = position.offset(0, i)
            if (field[newPos].hasPieceSameColor(color))
                break
            if (field[newPos].hasPieceAnotherColor(color)) {
                route.add(newPos)
                break
            }
            route.add(newPos)
        }
        for (i in 1 until size) {
            if ((!canDoStepsOverBoard() && position.isOverBound(0, -i)) || isStepDoCheck(field, position.offset(0, -i)))
                continue
            newPos = position.offset(0, -i)
            if (field[newPos].hasPieceSameColor(color))
                break
            if (field[newPos].hasPieceAnotherColor(color)) {
                route.add(newPos)
                break
            }
            route.add(newPos)
        }
        return route
    }

    override fun generateBrokenCellsRoute(field: Field): Route {
        route.clear()
        var newPos: Position
        for (i in 1 until size) {
            if ((!canDoStepsOverBoard() && position.isOverBound(i)))
                continue
            newPos = position.offset(i)
            if (field[newPos].hasPieceSameColor(color))
                break
            if (field[newPos].hasPieceAnotherColor(color)) {
                route.add(newPos)
                break
            }
            route.add(newPos)
        }
        for (i in 1 until size) {
            if ((!canDoStepsOverBoard() && position.isOverBound(-i)))
                continue
            newPos = position.offset(-i)
            if (field[newPos].hasPieceSameColor(color))
                break
            if (field[newPos].hasPieceAnotherColor(color)) {
                route.add(newPos)
                break
            }
            route.add(newPos)
        }
        for (i in 1 until size) {
            if ((!canDoStepsOverBoard() && position.isOverBound(0, i)))
                continue
            newPos = position.offset(0, i)
            if (field[newPos].hasPieceSameColor(color))
                break
            if (field[newPos].hasPieceAnotherColor(color)) {
                route.add(newPos)
                break
            }
            route.add(newPos)
        }
        for (i in 1 until size) {
            if ((!canDoStepsOverBoard() && position.isOverBound(0, -i)))
                continue
            newPos = position.offset(0, -i)
            if (field[newPos].hasPieceSameColor(color))
                break
            if (field[newPos].hasPieceAnotherColor(color)) {
                route.add(newPos)
                break
            }
            route.add(newPos)
        }
        return route
    }

    private fun generateRookRoute(field: Field) {

    }

    override fun getCopy(): RookPiece {
        val newPiece = RookPiece(color, position.getCopy())
        newPiece.countSteps = countSteps
        newPiece.type = type
        return newPiece
    }
}