package com.serebryakov.cyclechesscpp.application.model.game.pieces

import com.serebryakov.cyclechesscpp.application.model.game.PieceColor
import com.serebryakov.cyclechesscpp.application.model.game.PieceType
import com.serebryakov.cyclechesscpp.application.model.game.Route
import com.serebryakov.cyclechesscpp.application.model.game.Position
import com.serebryakov.cyclechesscpp.application.model.game.gamefield.Field
import com.serebryakov.cyclechesscpp.application.model.game.gamefield.size

class QueenPiece(override var color: PieceColor, override var position: Position) : BasePiece() {
    override var route: Route = emptyRoute()
    override var type: PieceType = PieceType.QUEEN

    override fun generatePieceRoute(field: Field): Route {
        route.clear()
        // bishop
        val minMaxPositions = getMinMaxPositions()
        var newPos = position
        for (i in 0..7) {
            if (newPos.isOverBound(-1, 1)) {
                if(canDoStepsOverBoard()) {
                    newPos = minMaxPositions[3]
                    for (j in 0..7) {
                        if (isStepDoCheck(field, newPos))
                            continue
                        if (field[newPos].hasPieceSameColor(color))
                            break
                        if (field[newPos].hasPieceAnotherColor(color)) {
                            route.add(newPos)
                            break
                        }
                        newPos = newPos.offset(-1, 1)
                        route.add(newPos)
                    }
                }
                break
            }
            newPos = newPos.offset(-1, 1)
            if (isStepDoCheck(field, newPos))
                continue
            if (field[newPos].hasPieceSameColor(color))
                break
            if (field[newPos].hasPieceAnotherColor(color)) {
                route.add(newPos)
                break
            }
            route.add(newPos)
        }

        newPos = position
        for (i in 0..7) {
            if (newPos.isOverBound(-1, -1)) {
                if(canDoStepsOverBoard()) {
                    newPos = minMaxPositions[2]
                    for (j in 0..7) {
                        if (isStepDoCheck(field, newPos))
                            continue
                        if (field[newPos].hasPieceSameColor(color))
                            break
                        if (field[newPos].hasPieceAnotherColor(color)) {
                            route.add(newPos)
                            break
                        }
                        newPos = newPos.offset(-1, -1)
                        route.add(newPos)
                    }
                }
                break
            }
            newPos = newPos.offset(-1, -1)
            if (isStepDoCheck(field, newPos))
                continue
            if (field[newPos].hasPieceSameColor(color))
                break
            if (field[newPos].hasPieceAnotherColor(color)) {
                route.add(newPos)
                break
            }
            route.add(newPos)
        }

        newPos = position
        for (i in 0..7) {
            if (newPos.isOverBound(1, 1)) {
                if(canDoStepsOverBoard()) {
                    newPos = minMaxPositions[1]
                    for (j in 0..7) {
                        if (isStepDoCheck(field, newPos))
                            continue
                        if (field[newPos].hasPieceSameColor(color))
                            break
                        if (field[newPos].hasPieceAnotherColor(color)) {
                            route.add(newPos)
                            break
                        }
                        newPos = newPos.offset(1, 1)
                        route.add(newPos)
                    }
                }
                break
            }
            newPos = newPos.offset(1, 1)
            if (isStepDoCheck(field, newPos))
                continue
            if (field[newPos].hasPieceSameColor(color))
                break
            if (field[newPos].hasPieceAnotherColor(color)) {
                route.add(newPos)
                break
            }
            route.add(newPos)
        }

        newPos = position
        for (i in 0..7) {
            if (newPos.isOverBound(1, -1)) {
                if(canDoStepsOverBoard()) {
                    newPos = minMaxPositions[0]
                    for (j in 0..7) {
                        if (isStepDoCheck(field, newPos))
                            continue
                        if (field[newPos].hasPieceSameColor(color))
                            break
                        if (field[newPos].hasPieceAnotherColor(color)) {
                            route.add(newPos)
                            break
                        }
                        newPos = newPos.offset(1, -1)
                        route.add(newPos)
                    }
                }
                break
            }
            newPos = newPos.offset(1, -1)
            if (isStepDoCheck(field, newPos))
                continue
            if (field[newPos].hasPieceSameColor(color))
                break
            if (field[newPos].hasPieceAnotherColor(color)) {
                route.add(newPos)
                break
            }
            route.add(newPos)
        }

        // rook
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
        // bishop
        val minMaxPositions = getMinMaxPositions()
        var newPos = position
        for (i in 0..7) {
            if (newPos.isOverBound(-1, 1)) {
                if(canDoStepsOverBoard()) {
                    newPos = minMaxPositions[3]
                    for (j in 0..7) {
                        if (isStepDoCheck(field, newPos))
                            continue
                        if (field[newPos].hasPieceSameColor(color))
                            break
                        if (field[newPos].hasPieceAnotherColor(color)) {
                            route.add(newPos)
                            break
                        }
                        newPos = newPos.offset(-1, 1)
                        route.add(newPos)
                    }
                }
                break
            }
            newPos = newPos.offset(-1, 1)
            if (field[newPos].hasPieceSameColor(color))
                break
            if (field[newPos].hasPieceAnotherColor(color)) {
                route.add(newPos)
                break
            }
            route.add(newPos)
        }

        newPos = position
        for (i in 0..7) {
            if (newPos.isOverBound(-1, -1)) {
                if(canDoStepsOverBoard()) {
                    newPos = minMaxPositions[2]
                    for (j in 0..7) {
                        if (isStepDoCheck(field, newPos))
                            continue
                        if (field[newPos].hasPieceSameColor(color))
                            break
                        if (field[newPos].hasPieceAnotherColor(color)) {
                            route.add(newPos)
                            break
                        }
                        newPos = newPos.offset(-1, -1)
                        route.add(newPos)
                    }
                }
                break
            }
            newPos = newPos.offset(-1, -1)
            if (field[newPos].hasPieceSameColor(color))
                break
            if (field[newPos].hasPieceAnotherColor(color)) {
                route.add(newPos)
                break
            }
            route.add(newPos)
        }

        newPos = position
        for (i in 0..7) {
            if (newPos.isOverBound(1, 1)) {
                if(canDoStepsOverBoard()) {
                    newPos = minMaxPositions[1]
                    for (j in 0..7) {
                        if (isStepDoCheck(field, newPos))
                            continue
                        if (field[newPos].hasPieceSameColor(color))
                            break
                        if (field[newPos].hasPieceAnotherColor(color)) {
                            route.add(newPos)
                            break
                        }
                        newPos = newPos.offset(1, 1)
                        route.add(newPos)
                    }
                }
                break
            }
            newPos = newPos.offset(1, 1)
            if (field[newPos].hasPieceSameColor(color))
                break
            if (field[newPos].hasPieceAnotherColor(color)) {
                route.add(newPos)
                break
            }
            route.add(newPos)
        }

        newPos = position
        for (i in 0..7) {
            if (newPos.isOverBound(1, -1)) {
                if(canDoStepsOverBoard()) {
                    newPos = minMaxPositions[0]
                    for (j in 0..7) {
                        if (isStepDoCheck(field, newPos))
                            continue
                        if (field[newPos].hasPieceSameColor(color))
                            break
                        if (field[newPos].hasPieceAnotherColor(color)) {
                            route.add(newPos)
                            break
                        }
                        newPos = newPos.offset(1, -1)
                        route.add(newPos)
                    }
                }
                break
            }
            newPos = newPos.offset(1, -1)
            if (field[newPos].hasPieceSameColor(color))
                break
            if (field[newPos].hasPieceAnotherColor(color)) {
                route.add(newPos)
                break
            }
            route.add(newPos)
        }

        // rook
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

    private fun generateQueenRoute(field: Field) {

    }

    private fun getMinMaxPositions(): List<Position> {
        // 0 - право верх
        // 1 - лево верх
        // 2 - право низ
        // 3 - лево низ
        val positions = mutableListOf(Position(), Position(), Position(), Position())
        var newPos: Position
        for (i in 0..7) {
            newPos = position.offset(-i, i)
            if (newPos.i == 0 || newPos.j == 7) {
                positions[0] = newPos
                break
            }
        }
        for (i in 0..7) {
            newPos = position.offset(-i, -i)
            if (newPos.i == 0 || newPos.j == 0) {
                positions[1] = newPos
                break
            }
        }
        for (i in 0..7) {
            newPos = position.offset(i, i)
            if (newPos.i == 7 || newPos.j == 7) {
                positions[2] = newPos
                break
            }
        }
        for (i in 0..7) {
            newPos = position.offset(i, -i)
            if (newPos.i == 7 || newPos.j == 0) {
                positions[3] = newPos
                break
            }
        }
        return positions
    }

    override fun getCopy(): QueenPiece {
        val newPiece = QueenPiece(color, position.getCopy())
        newPiece.countSteps = countSteps
        newPiece.type = type
        return newPiece
    }
}