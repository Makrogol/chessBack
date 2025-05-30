package com.serebryakov.cyclechesscpp.application.model.back.responses

data class CreateUserResponse(
    val token: String,
    val token_type: String,
    val success: Boolean,
    val username: String,
) {
    fun toTokenResponse() : TokenResponse = TokenResponse(
        token = token,
        token_type = token_type,
        success = success,
        username = username,
    )
}

//class CreateUserResponse(
//    override val token: String,
//    override val token_type: String,
//    override val success: Boolean
//) : TokenResponse(token, token_type, success)
