package com.serebryakov.cyclechesscpp.application.repository.sharedprefrepository.localgamefen

import com.serebryakov.cyclechesscpp.application.model.sharedpref.localgamefen.LocalGameFenSharedPref
import com.serebryakov.cyclechesscpp.foundation.model.IoDispatcher
import kotlinx.coroutines.withContext

class LocalGameFenSharedPrefRepositoryImpl(
    private val fenSharedPref: LocalGameFenSharedPref,
    private val ioDispatcher: IoDispatcher
): LocalGameFenSharedPrefRepository {
    override suspend fun getFen(): String = withContext(ioDispatcher.value) {
        return@withContext fenSharedPref.getFen()
    }

    override suspend fun getIsPlayingWithBot(): Boolean = withContext(ioDispatcher.value) {
        return@withContext fenSharedPref.getIsPlayingWithBot()
    }

    override suspend fun setIsPlayingWithBot(isPLayingWithBot: Boolean) = withContext(ioDispatcher.value) {
        fenSharedPref.setIsPlayingWithBot(isPLayingWithBot)
    }

    override suspend fun setFen(fen: String) = withContext(ioDispatcher.value) {
        fenSharedPref.setFen(fen)
    }

    override suspend fun clearFen() = withContext(ioDispatcher.value) {
        setFen("")
        setIsPlayingWithBot(false)
    }
}
