package com.serebryakov.cyclechesscpp.foundation.navigator

import com.serebryakov.cyclechesscpp.foundation.views.BaseScreen

interface Navigator {

    fun launch(screen: BaseScreen)
}