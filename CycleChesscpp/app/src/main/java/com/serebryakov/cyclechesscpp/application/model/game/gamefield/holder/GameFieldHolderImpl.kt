package com.serebryakov.cyclechesscpp.application.model.game.gamefield.holder

import com.serebryakov.cyclechesscpp.application.model.game.gamefield.GameField

class GameFieldHolderImpl: GameFieldHolder {
    private var gameField: GameField? = null

    override fun getStrict(): GameField = gameField!!

    override fun get() = gameField

    override fun has() = gameField != null

    override fun set(gameField: GameField?) {
        this.gameField = gameField
    }
}