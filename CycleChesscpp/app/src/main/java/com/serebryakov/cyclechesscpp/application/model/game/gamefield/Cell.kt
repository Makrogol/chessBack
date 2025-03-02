package com.serebryakov.cyclechesscpp.application.model.game.gamefield

import com.serebryakov.cyclechesscpp.application.model.game.BasePiece
import com.serebryakov.cyclechesscpp.application.model.game.CellColor
import com.serebryakov.cyclechesscpp.application.model.game.PieceColor
import com.serebryakov.cyclechesscpp.application.model.game.PieceType

class Cell(val color: CellColor) {
    var piece: BasePiece? = null

    fun hasPiece() = (piece != null)

    fun hasPieceAnotherColor(color: PieceColor) =
        hasPiece() && (piece!!.color != color)

    fun hasPieceSameColor(color: PieceColor) =
        hasPiece() && (piece!!.color == color)

    fun hasPieceTypeColor(color: PieceColor, type: PieceType) =
        hasPieceSameColor(color) && (piece!!.type == type)

    fun getTypePiece(): PieceType? = piece?.type

    fun getAnotherColor() =
        if (color == CellColor.white) CellColor.black else CellColor.white
}
