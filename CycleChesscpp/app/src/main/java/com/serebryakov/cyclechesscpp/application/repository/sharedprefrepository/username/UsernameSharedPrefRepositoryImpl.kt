package com.serebryakov.cyclechesscpp.application.repository.sharedprefrepository.username

import com.serebryakov.cyclechesscpp.application.model.sharedpref.username.UsernameSharedPref
import com.serebryakov.cyclechesscpp.foundation.model.IoDispatcher
import kotlinx.coroutines.withContext

class UsernameSharedPrefRepositoryImpl(
    private val usernameSharedPref: UsernameSharedPref,
    private val ioDispatcher: IoDispatcher
): UsernameSharedPrefRepository {
    override suspend fun getUsername(): String = withContext(ioDispatcher.value) {
        return@withContext usernameSharedPref.getUsername()
    }

    override suspend fun setUsername(username: String) = withContext(ioDispatcher.value) {
        usernameSharedPref.setUsername(username)
    }

    override suspend fun clearUsername() = withContext(ioDispatcher.value) {
        setUsername("")
    }
}
