package com.serebryakov.cyclechesscpp.application.view.gamescreen.utils.tagholder

interface GameScreenTagHolder {

    fun getTagByScreenColor(color: Int): Tag

    fun isTagInRouteValues(tag: Tag): Boolean

}