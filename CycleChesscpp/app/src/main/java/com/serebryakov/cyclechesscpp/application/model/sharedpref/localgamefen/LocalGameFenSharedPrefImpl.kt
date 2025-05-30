package com.serebryakov.cyclechesscpp.application.model.sharedpref.localgamefen

import android.content.Context
import com.serebryakov.cyclechesscpp.application.model.data.StartGameData
import com.serebryakov.cyclechesscpp.application.model.sharedpref.SharedPrefImpl

class LocalGameFenSharedPrefImpl(
    context: Context
) : LocalGameFenSharedPref {
    private val sharedPref = SharedPrefImpl(context)

    override fun getFen(): String = sharedPref.get(FEN_KEY, "")

    override fun getIsPlayingWithBot(): Boolean =
        sharedPref.get(IS_PLAY_WITH_BOT_KEY, "false").toBoolean()

    override fun setIsPlayingWithBot(isPLayingWithBot: Boolean) =
        sharedPref.set(IS_PLAY_WITH_BOT_KEY, isPLayingWithBot.toString())

    override fun setFen(fen: String) = sharedPref.set(FEN_KEY, fen)

    companion object {
        private const val USERNAME_KEY = "username"
        private const val OPPONENT_USERNAME_KEY = "opponent_username"
        private const val MAIN_COLOR_KEY = "main_color"
        private const val USE_SOCKET_KEY = "use_socket"
        private const val FEN_KEY = "local_game_fen"
        private const val IS_OPPONENT_TURN_KEY = "is_opponent_turn"
        private const val IS_SWITCHED_COLOR_KEY = "is_switched_color"
        private const val IS_PLAY_WITH_BOT_KEY = "is_play_with_bot"
    }
}
