package com.serebryakov.cyclechesscpp.foundation

import com.serebryakov.cyclechesscpp.foundation.model.Repository

interface BaseApplication {

    suspend fun closeSocket()

    val singletonScopeDependencies: List<Any>
}
