package com.serebryakov.cyclechesscpp.application.model.sharedpref.jwttoken

import android.content.Context
import com.serebryakov.cyclechesscpp.application.model.sharedpref.SharedPrefImpl

class JwtTokenSharedPrefImpl(
    context: Context
): JwtTokenSharedPref {
    private val sharedPref = SharedPrefImpl(context)

    override fun getJwtToken(): JwtToken {
        return JwtToken(
            token = sharedPref.get(JWT_TOKEN_KEY, "")
        )
    }

    override fun setJwtToken(token: JwtToken) {
        // TODO возможно эта штука должна быть супер низкоуровневой и работать со строкой
        //  не нравится мне, что тут token.token, возможно JwtToken должен собирать и разбирать репозиторий
        return sharedPref.set(JWT_TOKEN_KEY, token.token)
    }

    companion object {
        private const val JWT_TOKEN_KEY = "jwt_token"
    }
}
