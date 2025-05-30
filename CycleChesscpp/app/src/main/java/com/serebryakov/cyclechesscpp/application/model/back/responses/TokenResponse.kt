package com.serebryakov.cyclechesscpp.application.model.back.responses

open class TokenResponse(
    open val token: String,
    open val token_type: String,
    override val success: Boolean,
    open val username: String
) : SuccessResponse(success)
