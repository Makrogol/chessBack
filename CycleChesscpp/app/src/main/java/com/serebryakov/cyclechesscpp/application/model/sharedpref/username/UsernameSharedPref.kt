package com.serebryakov.cyclechesscpp.application.model.sharedpref.username

interface UsernameSharedPref {
    fun getUsername(): String

    fun setUsername(username: String)
}
