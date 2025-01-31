package com.serebryakov.cyclechesscpp.application.model.game

import com.serebryakov.cyclechesscpp.R

// TODO в результате должен остаться только один колор я думаю

enum class PieceColor {
    noColor,
    white,
    black,
}

enum class CellColor {
    noColor,
    white,
    black,
}

enum class GameColor {
    noColor,
    white,
    black,
}

fun colorToString(color: GameColor): String {
    return when (color) {
        GameColor.noColor -> "0"
        GameColor.white -> "1"
        GameColor.black -> "2"
    }
}

fun PieceColor.getAnotherColor(): PieceColor =
    if (this == PieceColor.white) PieceColor.black else PieceColor.white

fun CellColor.getAnotherColor(): CellColor =
    if (this == CellColor.white) CellColor.black else CellColor.white

fun GameColor.getAnotherColor(): GameColor =
    if (this == GameColor.white) GameColor.black else GameColor.white

fun PieceColor.toGameColor(): GameColor =
    if (this == PieceColor.white) GameColor.white else GameColor.black

fun GameColor.toPieceColor(): PieceColor =
    if (this == GameColor.white) PieceColor.white else PieceColor.black
