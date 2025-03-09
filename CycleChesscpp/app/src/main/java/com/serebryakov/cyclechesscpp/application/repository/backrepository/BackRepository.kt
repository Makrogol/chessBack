package com.serebryakov.cyclechesscpp.application.repository.backrepository

import com.serebryakov.cyclechesscpp.application.model.back.responses.CreateUserResponse
import com.serebryakov.cyclechesscpp.application.model.back.responses.UserResponse
import com.serebryakov.cyclechesscpp.application.model.back.responses.ValidateTokenResponse
import com.serebryakov.cyclechesscpp.application.model.back.responses.ValidateUserResponse
import com.serebryakov.cyclechesscpp.application.model.sharedpref.jwttoken.JwtToken
import com.serebryakov.cyclechesscpp.application.model.user.UserData
import com.serebryakov.cyclechesscpp.foundation.model.Repository

interface BackRepository: Repository {

    suspend fun getUsers(): List<UserResponse>

    suspend fun validateUser(userData: UserData): ValidateUserResponse

    suspend fun createUser(userData: UserData): CreateUserResponse

    suspend fun validateToken(token: JwtToken): ValidateTokenResponse
}
