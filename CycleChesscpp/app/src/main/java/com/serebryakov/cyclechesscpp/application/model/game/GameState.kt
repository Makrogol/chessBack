package com.serebryakov.cyclechesscpp.application.model.game

enum class GameState {
    ON_GOING,
    MATE_FOR_WHITE,
    MATE_FOR_BLACK,
    PATE,
    DRAW,
    CHECK_FOR_WHITE,
    CHECK_FOR_BLACK,
}

fun Int.toGameState(): GameState {
    return when (this) {
        0 -> GameState.ON_GOING
        1 -> GameState.MATE_FOR_WHITE
        2 -> GameState.MATE_FOR_BLACK
        3 -> GameState.PATE
        4 -> GameState.DRAW
        5 -> GameState.CHECK_FOR_WHITE
        6 -> GameState.CHECK_FOR_BLACK
        // TODO добавить сюда какой-то еще тип, чтобы понимать когда проблемы
        else -> GameState.ON_GOING
    }
}
