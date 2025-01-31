package com.serebryakov.cyclechesscpp.foundation.tools

import kotlin.math.pow
import kotlin.math.roundToInt

fun Float.round(count: Int): Float {
    val roundNumber = (10.0).pow(count)
    return ((this * roundNumber).roundToInt() / roundNumber).toFloat()
}

fun Float.toPercent(): String = ((100 * this).round(2)).toString() + "%"