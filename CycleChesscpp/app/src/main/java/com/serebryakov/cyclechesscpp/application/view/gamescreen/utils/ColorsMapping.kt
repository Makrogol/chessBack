package com.serebryakov.cyclechesscpp.application.view.gamescreen.utils

import com.serebryakov.cyclechesscpp.R
import com.serebryakov.cyclechesscpp.application.model.game.CellColor
import com.serebryakov.cyclechesscpp.application.model.game.GameColor

fun GameColor.toScreenColor(): Int =
    if (this == GameColor.white) R.color.white_cell_color else R.color.black_cell_color

fun CellColor.toScreenColor(): Int =
    if (this == CellColor.white) R.color.white_cell_color else R.color.black_cell_color