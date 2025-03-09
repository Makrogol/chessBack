package com.serebryakov.cyclechesscpp.application.repository.backrepository

import com.google.gson.JsonObject
import com.serebryakov.cyclechesscpp.application.model.back.Api
import com.serebryakov.cyclechesscpp.application.model.back.responses.CreateUserResponse
import com.serebryakov.cyclechesscpp.application.model.back.responses.UserResponse
import com.serebryakov.cyclechesscpp.application.model.back.responses.ValidateTokenResponse
import com.serebryakov.cyclechesscpp.application.model.back.responses.ValidateUserResponse
import com.serebryakov.cyclechesscpp.application.model.sharedpref.jwttoken.JwtToken
import com.serebryakov.cyclechesscpp.application.model.user.UserData
import com.serebryakov.cyclechesscpp.foundation.model.IoDispatcher
import kotlinx.coroutines.withContext

class BackRepositoryImpl(
    private val api: Api,
    private val ioDispatcher: IoDispatcher,
): BackRepository {
    override suspend fun getUsers(): List<UserResponse> = withContext(ioDispatcher.value) {
        return@withContext api.getUsers()
    }

    override suspend fun validateUser(userData: UserData): ValidateUserResponse = withContext(ioDispatcher.value) {
        return@withContext api.validateUser(userData.username, userData.password)
    }

    override suspend fun createUser(userData: UserData): CreateUserResponse = withContext(ioDispatcher.value) {
        val createUserBody = JsonObject()
        createUserBody.addProperty("username", userData.username)
        createUserBody.addProperty("password", userData.password)
        return@withContext api.createUser(createUserBody)
    }

    override suspend fun validateToken(token: JwtToken): ValidateTokenResponse = withContext(ioDispatcher.value) {
        return@withContext api.validateToken("Bearer ${token.token}")
    }
}
