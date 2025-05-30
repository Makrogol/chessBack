package com.serebryakov.cyclechesscpp.application.view.gamescreen.viewutils

import com.serebryakov.cyclechesscpp.application.model.game.PieceColor
import com.serebryakov.cyclechesscpp.application.model.game.Position
import com.serebryakov.cyclechesscpp.application.model.game.Route
import com.serebryakov.cyclechesscpp.application.model.game.GameColor
import com.serebryakov.cyclechesscpp.application.view.gamescreen.OnGameFieldUiCellClick
import com.serebryakov.cyclechesscpp.foundation.viewutils.ViewUtils

interface GameScreenViewUtils: ViewUtils {

    fun createGameFieldUi(onGameFieldUiCellClick: OnGameFieldUiCellClick)

    fun drawGameField()

    fun drawCheckCell(position: Position)

    fun drawRoute(position: Position, route: Route)

    fun clearField()

    fun clearFieldWithoutCheckColor()

    fun clearRoute()

    fun disableGameField()

    fun enableGameField()

    fun getUiGameFieldCell(i: Int, j: Int): UiGameFieldCell

    fun getUiGameFieldCell(position: Position): UiGameFieldCell

    fun getMagicPawnTransformationUiElement(i: Int): UiMagicPawnTransformationElement

    fun showMagicPawnTransformationUi(pieceColor: PieceColor)

    fun hideMagicPawnTransformationUi()

    fun setUsernames(username: String, opponentUsername: String)

    fun setGameResult(gameResult: String)

    fun clearGameResult()

    fun setStartTurnColor(mainColor: GameColor)

    fun changeTurnColor()

    fun setCountPieceSteps(position: Position)

    fun hideCountPieceSteps()

    fun isUserTurn(mainColor: GameColor, turnColor: GameColor): Boolean

    fun drawPieceEmptyRoute(position: Position)

    fun isEmptyGameResult(): Boolean
}
