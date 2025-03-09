package com.serebryakov.cyclechesscpp.application.model.sharedpref.username

import android.content.Context
import com.serebryakov.cyclechesscpp.application.model.sharedpref.SharedPrefImpl

class UsernameSharedPrefImpl(
    context: Context
): UsernameSharedPref {
    private val sharedPref = SharedPrefImpl(context)

    override fun getUsername(): String =
        sharedPref.get(USERNAME_KEY, "")

    override fun setUsername(username: String) =
        sharedPref.set(USERNAME_KEY, username)

    companion object {
        private const val USERNAME_KEY = "username"
    }
}
