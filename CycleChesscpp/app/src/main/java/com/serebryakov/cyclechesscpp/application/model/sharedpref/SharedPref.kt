package com.serebryakov.cyclechesscpp.application.model.sharedpref

interface SharedPref {
    fun set(key: String, value: String)

    fun get(key: String, defaultValue: String): String
}
