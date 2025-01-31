package com.serebryakov.cyclechesscpp.application.view.gamescreen.utils.tagholder

import com.serebryakov.cyclechesscpp.R

class GameScreenTagHolderImpl : GameScreenTagHolder {
    override fun getTagByScreenColor(color: Int): Tag =
        when (color) {
            R.color.white_cell_color -> Tag.WHITE_CELL
            R.color.black_cell_color -> Tag.BLACK_CELL
            R.color.piece_route_cell_color -> Tag.ROUTE_CELL
            R.color.wrong_piece_on_check_color -> Tag.WRONG_PIECE_ON_CHECK_CELL
            R.color.empty_route_color -> Tag.EMPTY_ROUTE_CELL
            R.color.check_cell_color -> Tag.CHECK_CELL
            R.drawable.empty_cell -> Tag.EMPTY_CELL
            else -> Tag.EMPTY_CELL
        }

    override fun isTagInRouteValues(tag: Tag): Boolean = tag in routeTags

    companion object {
        private val routeTags = listOf(
            Tag.ROUTE_CELL,
            Tag.EMPTY_ROUTE_CELL
        )
    }
}