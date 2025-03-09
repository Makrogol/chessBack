package com.serebryakov.cyclechesscpp.application.repository.sharedprefrepository.username

import com.serebryakov.cyclechesscpp.foundation.model.Repository

interface UsernameSharedPrefRepository: Repository {
    suspend fun getUsername(): String

    suspend fun setUsername(username: String)

    suspend fun clearUsername()
}
