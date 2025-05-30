package com.serebryakov.cyclechesscpp.application.view.gamescreen.viewutils.tagholder

// TODO он не нужен (по крайней мере в таком виде, либо надо сделать просто отдельные функции,
//  либо сделать класс, от которого будет польза)
interface GameScreenTagHolder {

    fun getTagByScreenColor(color: Int): Tag

    fun isTagInRouteValues(tag: Tag): Boolean
}
