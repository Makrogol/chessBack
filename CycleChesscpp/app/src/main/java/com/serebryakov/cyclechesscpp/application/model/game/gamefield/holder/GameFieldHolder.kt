package com.serebryakov.cyclechesscpp.application.model.game.gamefield.holder

import com.serebryakov.cyclechesscpp.application.model.game.gamefield.GameField

interface GameFieldHolder {

    // Warn: use it only if you check has() is true
    fun getStrict(): GameField

    fun get(): GameField?

    fun has(): Boolean

    fun set(gameField: GameField?)
}
