package com.serebryakov.cyclechesscpp.application.model.back.responses

data class ValidateUserResponse(
    val token: String,
    val token_type: String,
    val success: Boolean
)
