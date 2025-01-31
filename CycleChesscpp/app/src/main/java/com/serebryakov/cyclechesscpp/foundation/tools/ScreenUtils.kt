package com.serebryakov.cyclechesscpp.foundation.tools

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager


object ScreenUtils {

    fun getScreenWidth(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        return displayMetrics.widthPixels
    }
}