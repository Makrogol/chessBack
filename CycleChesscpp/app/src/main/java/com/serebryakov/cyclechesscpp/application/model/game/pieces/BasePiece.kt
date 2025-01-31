package com.serebryakov.cyclechesscpp.application.model.game.pieces

import com.serebryakov.cyclechesscpp.application.model.game.PieceColor
import com.serebryakov.cyclechesscpp.application.model.game.PieceType
import com.serebryakov.cyclechesscpp.application.model.game.Position
import com.serebryakov.cyclechesscpp.application.model.game.Route
import com.serebryakov.cyclechesscpp.application.model.game.gamefield.Field

// TODO Надо, когда я буду передавать данные между устройствами о сделанном ходе,
//  конвертить позиции (потому что у меня для белых и черных не одинаковые поля)


open class BasePiece {
    // TODO разобраться с тем, что он не дает сделать геттеры протектед полям
    //  и поэтому я не могу сделать их протектед
    open var position = Position()
    open var color = PieceColor.noColor
    open var route: Route = emptyRoute()
    open var type: PieceType = PieceType.EMPTY
    open var countSteps = 0

    fun changePosition(newPosition: Position) {
        position = newPosition
    }

    open fun generatePieceRoute(field: Field) = route

    open fun generateBrokenCellsRoute(field: Field) = route

    open fun generatePieceRouteOnCheck(field: Field): Route {
        val newRoute = emptyRoute()
        val maxRoute = generatePieceRoute(field)
        var newField: Field
        for (el in maxRoute) {
            newField = field.getCopy()
            newField.movePiece(position, el)
            if (!newField.isCheck(color))
                newRoute.add(el)
        }
        return newRoute
    }

    open fun isStepDoCheck(field: Field, newPosition: Position): Boolean {
        val newField = field.getCopy()
        newField.movePiece(position, newPosition)
        return newField.isCheck(color)
    }

    fun onPieceDoStep() {
        countSteps += 1
    }

    fun canDoStepsOverBoard() = countSteps > 2

    fun canDoStepsOverBoard(countSteps: Int) = countSteps > 2

    fun countSteps() = countSteps

    open fun getCopy(): BasePiece {
        val newPiece = BasePiece()
        newPiece.position = position.getCopy()
        newPiece.color = color
        newPiece.countSteps = countSteps
        newPiece.type = type
        return newPiece
    }
}

fun emptyRoute() = mutableListOf<Position>()
