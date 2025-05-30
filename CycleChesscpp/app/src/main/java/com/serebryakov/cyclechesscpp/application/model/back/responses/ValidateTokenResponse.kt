package com.serebryakov.cyclechesscpp.application.model.back.responses

data class ValidateTokenResponse(
    val success: Boolean,
    val username: String,
)
