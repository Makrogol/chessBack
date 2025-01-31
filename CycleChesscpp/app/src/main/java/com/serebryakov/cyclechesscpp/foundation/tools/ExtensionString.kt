package com.serebryakov.cyclechesscpp.foundation.tools

fun String.toBoolean(): Boolean {
    return this.toInt().toBoolean()
}