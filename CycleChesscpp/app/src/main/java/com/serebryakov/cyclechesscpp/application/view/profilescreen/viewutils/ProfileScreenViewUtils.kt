package com.serebryakov.cyclechesscpp.application.view.profilescreen.viewutils

import com.serebryakov.cyclechesscpp.application.model.data.UserData
import com.serebryakov.cyclechesscpp.foundation.viewutils.ViewUtils

interface ProfileScreenViewUtils: ViewUtils {

    fun getUserDataFromEditText(): UserData

    fun loadProfileUI()

    fun loadRegisterAuthorizationUI()

    fun setUsername(username: String)

    fun setLoginPasswordInfo(info: String)
}
