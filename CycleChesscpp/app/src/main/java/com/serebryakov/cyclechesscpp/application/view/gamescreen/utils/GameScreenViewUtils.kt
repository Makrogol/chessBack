package com.serebryakov.cyclechesscpp.application.view.gamescreen.utils

import com.serebryakov.cyclechesscpp.application.model.game.Position
import com.serebryakov.cyclechesscpp.application.model.game.Route
import com.serebryakov.cyclechesscpp.application.model.user.StartGameData
import com.serebryakov.cyclechesscpp.application.view.gamescreen.OnGameFieldUiCellClick

interface GameScreenViewUtils {

    fun createGameFieldUi(startGameData: StartGameData, onGameFieldUiCellClick: OnGameFieldUiCellClick)

    fun drawGameField()

    fun drawCheckCell(position: Position)

    fun drawRoute(position: Position, route: Route)

    fun clearAllField()

    fun clearRoute()

    fun disableGameField()

    fun enableGameField()

    fun getUiGameFieldCell(i: Int, j: Int): UiGameFieldCell

    fun getUiGameFieldCell(position: Position): UiGameFieldCell

    fun getMagicPawnTransformationUiElement(i: Int): UiMagicPawnTransformationElement

    fun showMagicPawnTransformationUi()

    fun hideMagicPawnTransformationUi()

    fun setUsernames(username: String, opponentUsername: String)

}