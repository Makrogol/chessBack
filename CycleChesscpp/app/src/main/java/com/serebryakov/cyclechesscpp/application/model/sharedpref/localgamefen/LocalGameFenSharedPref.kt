package com.serebryakov.cyclechesscpp.application.model.sharedpref.localgamefen

interface LocalGameFenSharedPref {
    fun getFen(): String

    fun getIsPlayingWithBot(): Boolean

    fun setIsPlayingWithBot(isPLayingWithBot: Boolean)

    fun setFen(fen: String)
}
