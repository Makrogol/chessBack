package com.serebryakov.cyclechesscpp.application.model.sharedpref

import android.content.Context

class SharedPrefImpl(
    context: Context
): SharedPref {
    private val settings = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE)
    private val editor = settings.edit()

    override fun set(key: String, value: String) {
        editor.putString(key, value).apply()
    }

    override fun get(key: String, defaultValue: String): String =
        // TODO супер странно, что при наличии defValue он все равно говорит, что возвращается String?
        settings.getString(key, defaultValue) ?: defaultValue

    companion object {
        private const val STORAGE_NAME = "jwt_token_storage"
    }
}
