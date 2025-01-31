package com.serebryakov.cyclechesscpp.foundation.uiactions

import android.content.Context
import android.widget.Toast
import com.serebryakov.cyclechesscpp.foundation.uiActions.UiActions

class AndroidUiActions(
    private val appContext: Context
) : UiActions {

    override fun toast(message: String) {
        Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show()
    }

    override fun getString(messageRes: Int, vararg args: Any): String {
        return appContext.getString(messageRes, *args)
    }
}