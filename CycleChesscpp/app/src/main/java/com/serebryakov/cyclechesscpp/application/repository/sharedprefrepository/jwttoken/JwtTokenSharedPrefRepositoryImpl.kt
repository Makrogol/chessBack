package com.serebryakov.cyclechesscpp.application.repository.sharedprefrepository.jwttoken

import com.serebryakov.cyclechesscpp.application.model.sharedpref.jwttoken.JwtToken
import com.serebryakov.cyclechesscpp.application.model.sharedpref.jwttoken.JwtTokenSharedPref
import com.serebryakov.cyclechesscpp.foundation.model.IoDispatcher
import kotlinx.coroutines.withContext

class JwtTokenSharedPrefRepositoryImpl(
    private val sharedPref: JwtTokenSharedPref,
    private val ioDispatcher: IoDispatcher,
): JwtTokenSharedPrefRepository {
    override suspend fun getJwtToken(): JwtToken = withContext(ioDispatcher.value) {
        return@withContext sharedPref.getJwtToken()
    }

    override suspend fun setJwtToken(token: JwtToken) = withContext(ioDispatcher.value) {
        sharedPref.setJwtToken(token)
    }

    override suspend fun clearToken() = withContext(ioDispatcher.value) {
        setJwtToken(JwtToken(""))
    }
}
