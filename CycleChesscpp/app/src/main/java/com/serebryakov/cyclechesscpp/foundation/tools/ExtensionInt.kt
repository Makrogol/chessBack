package com.serebryakov.cyclechesscpp.foundation.tools

import android.content.Context

fun Int.toBoolean(): Boolean {
    return this != 0
}

fun Int.toDp(context: Context): Int = (this / context.resources.displayMetrics.density).toInt()
