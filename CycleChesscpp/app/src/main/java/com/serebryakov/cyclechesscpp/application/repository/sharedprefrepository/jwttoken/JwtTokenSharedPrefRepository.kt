package com.serebryakov.cyclechesscpp.application.repository.sharedprefrepository.jwttoken

import com.serebryakov.cyclechesscpp.application.model.sharedpref.jwttoken.JwtToken
import com.serebryakov.cyclechesscpp.foundation.model.Repository

interface JwtTokenSharedPrefRepository: Repository {

    suspend fun getJwtToken(): JwtToken

    suspend fun setJwtToken(token: JwtToken)

    suspend fun clearToken()
}
