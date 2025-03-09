package com.serebryakov.cyclechesscpp.application.model.sharedpref.jwttoken

interface JwtTokenSharedPref {
// TODO переделать название класса, потому что он уже не только за jwt токены отвечает
    fun getJwtToken(): JwtToken

    fun setJwtToken(token: JwtToken)
}
