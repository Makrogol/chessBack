package com.serebryakov.cyclechesscpp.foundation.socket

import com.google.gson.JsonElement

fun JsonElement.toTrimmedString(): String {
    return this.toString().trim('\"')
}
