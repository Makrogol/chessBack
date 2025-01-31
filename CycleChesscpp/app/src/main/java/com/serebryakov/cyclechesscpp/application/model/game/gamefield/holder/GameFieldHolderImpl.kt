package com.serebryakov.cyclechesscpp.application.model.game.gamefield.holder

import com.serebryakov.cyclechesscpp.application.model.game.GameColor
import com.serebryakov.cyclechesscpp.application.model.game.gamefield.GameField

class GameFieldHolderImpl: GameFieldHolder {
    private var gameField = GameField(GameColor.black)

    override fun get() = gameField

    override fun set(gameField: GameField) {
        this.gameField = gameField
    }
}