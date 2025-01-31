package com.serebryakov.cyclechesscpp.foundation

import com.serebryakov.cyclechesscpp.foundation.model.Repository

interface BaseApplication {

    val singletonScopeDependencies: List<Any>
}