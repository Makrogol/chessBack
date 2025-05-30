package com.serebryakov.cyclechesscpp.application.view.profilescreen.utils

import android.text.Editable

class ProfileScreenUtilsImpl: ProfileScreenUtils {

    override fun checkLogin(login: Editable): CheckResult {
        with(login) {
            if (isEmpty()) {
                return CheckResult(
                    result = false,
                    message = "Логин не может быть пустым"
                )
            }
            if (length >= 20) {
                return CheckResult(
                    result = false,
                    message = "Логин не может быть\nбольше 20 символов"
                )
            }
            return CheckResult(
                result = true,
                message = ""
            )
        }
    }

    override fun checkPassword(password: Editable): CheckResult {
        with(password) {
            if (isEmpty()) {
                return CheckResult(
                    result = false,
                    message = "Пароль не может быть пустым"
                )
            }
            if (length < 6) {
                return CheckResult(
                    result = false,
                    message = "Пароль не может быть\nменьше 6 символов"
                )
            }
            return CheckResult(
                result = true,
                message = ""
            )
        }
    }

    override fun checkAll(login: Editable, password: Editable): CheckResult {
        val loginResult = checkLogin(login)
        if (!loginResult.result) {
            return loginResult
        }

        return checkPassword(password)
    }
}
