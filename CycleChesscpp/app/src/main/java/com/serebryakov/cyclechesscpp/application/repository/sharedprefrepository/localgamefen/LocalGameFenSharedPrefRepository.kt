package com.serebryakov.cyclechesscpp.application.repository.sharedprefrepository.localgamefen

import com.serebryakov.cyclechesscpp.foundation.model.Repository

interface LocalGameFenSharedPrefRepository: Repository {
    suspend fun getFen(): String

    suspend fun getIsPlayingWithBot(): Boolean

    suspend fun setIsPlayingWithBot(isPLayingWithBot: Boolean)

    suspend fun setFen(fen: String)

    suspend fun clearFen()
}
