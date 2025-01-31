package com.serebryakov.cyclechesscpp.application.model.game.gamefield.holder

import com.serebryakov.cyclechesscpp.application.model.game.gamefield.GameField

interface GameFieldHolder {

    fun get(): GameField

    fun set(gameField: GameField)

}