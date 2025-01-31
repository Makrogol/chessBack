package com.serebryakov.cyclechesscpp.application.model.game.gamefield

import com.serebryakov.cyclechesscpp.application.model.game.pieces.BishopPiece
import com.serebryakov.cyclechesscpp.application.model.game.CellColor
import com.serebryakov.cyclechesscpp.application.model.game.GameColor
import com.serebryakov.cyclechesscpp.application.model.game.pieces.KingPiece
import com.serebryakov.cyclechesscpp.application.model.game.pieces.KnightPiece
import com.serebryakov.cyclechesscpp.application.model.game.pieces.PawnPiece
import com.serebryakov.cyclechesscpp.application.model.game.PieceColor
import com.serebryakov.cyclechesscpp.application.model.game.PieceType
import com.serebryakov.cyclechesscpp.application.model.game.Position
import com.serebryakov.cyclechesscpp.application.model.game.pieces.QueenPiece
import com.serebryakov.cyclechesscpp.application.model.game.pieces.RookPiece
import com.serebryakov.cyclechesscpp.application.model.game.getAnotherColor
import com.serebryakov.cyclechesscpp.application.model.game.pieces.BasePiece
import com.serebryakov.cyclechesscpp.application.model.game.toPieceColor
import java.lang.Math.abs

class Field {
    private val field = mutableListOf<MutableList<Cell>>()
    private val brokenCells = mutableListOf<Position>()
    private var gameColor = GameColor.noColor

    operator fun get(position: Position): Cell {
        return field[position.i][position.j]
    }


    fun movePiece(oldPosition: Position, newPosition: Position) {
        this[newPosition].piece = this[oldPosition].piece
        this[oldPosition].piece = null
        field[newPosition.i][newPosition.j].piece?.changePosition(newPosition)
    }

    fun magicPawnTransformation(position: Position, pieceType: PieceType) : Boolean {
        val color = this[position].piece!!.color
        this[position].piece =  when(pieceType) {
            PieceType.KNIGHT -> KnightPiece(color, position)
            PieceType.BISHOP -> BishopPiece(color, position)
            PieceType.ROOK -> RookPiece(color, position)
            PieceType.QUEEN -> QueenPiece(color, position)
            else -> PawnPiece(color, position)
        }
        return this[position].piece!!.type != PieceType.PAWN
    }

    fun doPassant(position: Position, newPosition: Position) {
        this[newPosition].piece = this[position].piece
        this[position].piece = null
        field[newPosition.i][newPosition.j].piece?.changePosition(newPosition)
        // TODO сюда бы какой-то ассерт добавить, что мы убираем именно пешку
        field[position.i][newPosition.j].piece = null
    }

    fun doCastling(position: Position, newPosition: Position) {
        val newKingPosition = Position(position.i, position.j - if (position.j > newPosition.j) 2 else -2)
        val newRookPosition = Position(position.i, newKingPosition.j - if(position.j > newPosition.j) -1 else 1)
        movePiece(position, newKingPosition)
        movePiece(newPosition, newRookPosition)
    }

    fun getPiece(position: Position): BasePiece? {
        return field[position.i][position.j].piece
    }

    fun getPieceForColor(color: PieceColor, type: PieceType): Position {
        for (i in 0 until size) {
            for (j in 0 until size) {
                val currentPiece = field[i][j].piece
                if (currentPiece?.type == type && currentPiece.color == color) {
                    return Position(i, j)
                }
            }
        }
        return Position()
    }

    fun hasPiece(position: Position): Boolean {
        return field[position.i][position.j].hasPiece()
    }

    fun isCheck(currentPayerColor: PieceColor): Boolean {
        val position = getPieceForColor(currentPayerColor, PieceType.KING)
        return isCellBroken(position, currentPayerColor.getAnotherColor())
    }

    fun getMainColor() = gameColor

    fun generateEmptyField() {
        for (i in 0 until size) {
            field.add(mutableListOf())
        }
        field[0].add(Cell(CellColor.white)) // это левое верхнее поле
        for (i in 0 until size) {
            for (j in 0 until size) {
                if (i == 0 && j == 0)
                    continue
                if (j == 0)
                    field[i].add(Cell(field[i - 1][j].getAnotherColor()))
                else
                    field[i].add(Cell(field[i][j - 1].getAnotherColor()))
            }
        }
    }

    fun setPieceToStartPosition(color: GameColor) {
        gameColor = color
        val mainColor = gameColor.toPieceColor()

        setPawns(mainColor)
        setAllFiguresNotPawns(mainColor)
    }

    private fun setPawns(mainColor: PieceColor) {
        println("setPawns mainColor $mainColor")
        val anotherColor = mainColor.getAnotherColor()
        val iMain = size - 2
        val iAnother = 1
        for (j in 0 until size) {
            field[iMain][j].piece = PawnPiece(mainColor, Position(iMain, j))
            field[iAnother][j].piece = PawnPiece(anotherColor, Position(iAnother, j))
        }
    }

    private fun setAllFiguresNotPawns(mainColor: PieceColor) {
        val anotherColor = mainColor.getAnotherColor()
        // TODO возможно это надо переделать, чтобы было без дублирования добавления фигур
        field[size - 1][0].piece = RookPiece(mainColor, Position(size - 1, 0))
        field[size - 1][size - 1].piece = RookPiece(mainColor, Position(size - 1, size - 1))

        field[size - 1][1].piece = KnightPiece(mainColor, Position(size - 1, 1))
        field[size - 1][size - 2].piece = KnightPiece(mainColor, Position(size - 1, size - 2))

        field[size - 1][2].piece = BishopPiece(mainColor, Position(size - 1, 2))
        field[size - 1][size - 3].piece = BishopPiece(mainColor, Position(size - 1, size - 3))

        if (mainColor == PieceColor.white) {
            field[size - 1][3].piece = QueenPiece(mainColor, Position(size - 1, 3))
            field[size - 1][4].piece = KingPiece(mainColor, Position(size - 1, 4))
        } else {
            field[size - 1][3].piece = KingPiece(mainColor, Position(size - 1, 3))
            field[size - 1][4].piece = QueenPiece(mainColor, Position(size - 1, 4))
        }



        field[0][0].piece = RookPiece(anotherColor, Position(0, 0))
        field[0][size - 1].piece = RookPiece(anotherColor, Position(0, size - 1))

        field[0][1].piece = KnightPiece(anotherColor, Position(0, 1))
        field[0][size - 2].piece = KnightPiece(anotherColor, Position(0, size - 2))

        field[0][2].piece = BishopPiece(anotherColor, Position(0, 2))
        field[0][size - 3].piece = BishopPiece(anotherColor, Position(0, size - 3))


        if (mainColor == PieceColor.white) {
            field[0][3].piece = QueenPiece(anotherColor, Position(0, 3))
            field[0][4].piece = KingPiece(anotherColor, Position(0, 4))
        } else {
            field[0][3].piece = KingPiece(anotherColor, Position(0, 3))
            field[0][4].piece = QueenPiece(anotherColor, Position(0, 4))
        }
    }

    // Еще надо передать цвет, для кого мы будем считать битые поля
    fun isCellBroken(position: Position, color: PieceColor): Boolean {
        if (brokenCells.isEmpty())
            generateBrokenCells(color)
        return brokenCells.firstOrNull { it == position } != null
    }

    private fun generateBrokenCells(color: PieceColor) {
        for (i in 0 until size) {
            for (j in 0 until size) {
                if (field[i][j].piece?.color != color)
                    continue
                val currentPieceBrokenCells = field[i][j].piece?.generateBrokenCellsRoute(this)

                // TODO потом надо сделать так, чтобы тут не было повторяющихся элементов
                if (currentPieceBrokenCells != null)
                    brokenCells.addAll(currentPieceBrokenCells)
            }
        }
    }

    fun getCopy(): Field {
        val newField = Field()
        newField.gameColor = gameColor
        newField.generateEmptyField()
        for (i in 0 until size) {
            for (j in 0 until size) {
                newField.field[i][j] = field[i][j].getCopy()
            }
        }
        return newField
    }
}