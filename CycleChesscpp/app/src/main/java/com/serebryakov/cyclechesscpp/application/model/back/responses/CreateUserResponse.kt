package com.serebryakov.cyclechesscpp.application.model.back.responses

data class CreateUserResponse(
    val token: String,
    val token_type: String,
    val success: Boolean
)
