package com.serebryakov.cyclechesscpp.foundation.navigator

import com.serebryakov.cyclechesscpp.foundation.tools.ResourceActions
import com.serebryakov.cyclechesscpp.foundation.views.BaseScreen


//Навигатор для работы на стороне вьюмодели

class IntermediateNavigator : Navigator {

    private val targetNavigator = ResourceActions<Navigator>()

    override fun launch(screen: BaseScreen) = targetNavigator {
        it.launch(screen)
    }

    fun setTarget(navigator: Navigator?) {
        targetNavigator.resource = navigator
    }

    fun clear() {
        targetNavigator.clear()
    }

}