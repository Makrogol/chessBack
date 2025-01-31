package com.serebryakov.cyclechesscpp.application.model.back

import com.google.gson.JsonObject
import com.serebryakov.cyclechesscpp.application.model.back.responses.CreateUserResponse
import com.serebryakov.cyclechesscpp.application.model.back.responses.UserResponse
import com.serebryakov.cyclechesscpp.application.model.back.responses.ValidateUserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface Api {
    @GET("/auth/users/")
    suspend fun getUsers(): List<UserResponse>

    @GET("/auth/validate_user/")
    suspend fun validateUser(@Query("username") username: String, @Query("password") password: String): ValidateUserResponse

    @POST("/auth/")
    suspend fun createUser(@Body createUserBody: JsonObject): CreateUserResponse
}