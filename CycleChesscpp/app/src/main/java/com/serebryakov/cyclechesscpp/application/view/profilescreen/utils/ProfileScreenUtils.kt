package com.serebryakov.cyclechesscpp.application.view.profilescreen.utils

import android.text.Editable

interface ProfileScreenUtils {

    fun checkLogin(login: Editable): CheckResult

    fun checkPassword(password: Editable): CheckResult

    fun checkAll(login: Editable, password: Editable): CheckResult

}